package com.koi_express.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.koi_express.jwt.JwtUtil;
import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.service.manager.ManagerService;
import com.koi_express.service.order.builder.InvoiceBuilder;
import com.koi_express.service.order.builder.OrderBuilder;
import com.koi_express.service.order.builder.OrderDetailBuilder;
import com.koi_express.service.order.price.TransportationFeeCalculator;
import com.koi_express.service.payment.VNPayService;
import com.koi_express.service.staff_assignment.StaffAssignmentService;
import com.koi_express.service.verification.EmailService;
import com.koi_express.store.TemporaryStorage;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private static final String ORDER_NOT_FOUND_MESSAGE = "Order not found";

    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;
    private final ManagerService managerService;
    private final EmailService emailService;
    private final OrderBuilder orderBuilder;
    private final StaffAssignmentService staffAssignmentService;
    private final VNPayService vnPayService;
    private final TransportationFeeCalculator transportationFeeCalculator;
    private final InvoiceBuilder invoiceBuilder;
    private final OrderDetailBuilder orderDetailBuilder;


    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            JwtUtil jwtUtil,
            ManagerService managerService,
            EmailService emailService,
            OrderBuilder orderBuilder,
            StaffAssignmentService staffAssignmentService,
            @Lazy VNPayService vnPayService,
            TransportationFeeCalculator transportationFeeCalculator,
            InvoiceBuilder invoiceBuilder,
            OrderDetailBuilder orderDetailBuilder) {
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
        this.managerService = managerService;
        this.emailService = emailService;
        this.orderBuilder = orderBuilder;
        this.staffAssignmentService = staffAssignmentService;
        this.vnPayService = vnPayService;
        this.transportationFeeCalculator = transportationFeeCalculator;
        this.invoiceBuilder = invoiceBuilder;
        this.orderDetailBuilder = orderDetailBuilder;
    }

    public ApiResponse<Map<String, Object>> createOrder(OrderRequest orderRequest, String token) {

        try {
            Customers customer = extractCustomerFromToken(token);
            Orders orders = prepareOrder(orderRequest, customer);
            Orders savedOrder = orderRepository.save(orders);

            ApiResponse<String> paymentResponse = vnPayService.createVnPayPayment(savedOrder);
            String paymentUrl = paymentResponse.getResult();

            scheduleOrderCancellation(savedOrder.getOrderId());

            return prepareSuccessResponse(savedOrder, paymentUrl);
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }
    }

    private Customers extractCustomerFromToken(String token) {
        String customerId = jwtUtil.extractCustomerId(token);
        return managerService.getCustomerById(Long.parseLong(customerId));
    }

    private Orders prepareOrder(OrderRequest orderRequest, Customers customer) {
        BigDecimal totalFee = transportationFeeCalculator.calculateTotalFee(orderRequest.getKilometers());
        BigDecimal commitmentFee = transportationFeeCalculator.calculateCommitmentFee(orderRequest.getKilometers());

        Orders orders = orderBuilder.buildOrder(orderRequest, customer);
        orders.getOrderDetail().setDistanceFee(totalFee);
        orders.getOrderDetail().setCommitmentFee(commitmentFee);
        orders.setStatus(OrderStatus.COMMIT_FEE_PENDING);

        return orders;
    }

    private ApiResponse<Map<String, Object>> prepareSuccessResponse(Orders savedOrder, String paymentUrl) {
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("order", savedOrder);
        responseMap.put("distanceFee", savedOrder.getOrderDetail().getDistanceFee());
        responseMap.put("commitmentFee", savedOrder.getOrderDetail().getCommitmentFee());
        responseMap.put("paymentUrl", paymentUrl);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order created, awaiting commit fee payment", responseMap);
    }

    private void scheduleOrderCancellation(Long orderId) {
        scheduler.schedule(
                () -> {
                    Orders order = orderRepository.findById(orderId).orElse(null);
                    if (order != null && order.getStatus() == OrderStatus.COMMIT_FEE_PENDING) {
                        order.setStatus(OrderStatus.CANCELED);
                        orderRepository.save(order);
                        logger.info("Order ID {} has been canceled due to unpaid commit fee", orderId);
                    }
                },
                10,
                TimeUnit.MINUTES);
    }

    @Transactional
    public ApiResponse<String> confirmCommitFeePayment(long orderId, Map<String, String> vnpParams) {
        try {
            boolean isPaymentVerified = vnPayService.verifyPayment(vnpParams);
            String responseCode = vnpParams.get("vnp_ResponseCode");

            Orders order = orderRepository
                    .findById(orderId)
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, ORDER_NOT_FOUND_MESSAGE));

            if (isPaymentVerified && "00".equals(responseCode)) {
                order.setStatus(OrderStatus.PENDING);
                orderRepository.save(order);

                emailService.sendOrderConfirmationEmail(order.getCustomer().getEmail(), order);

                return new ApiResponse<>(HttpStatus.OK.value(), "Commit fee payment confirmed successfully", null);
            } else {
                order.setStatus(OrderStatus.CANCELED);
                orderRepository.save(order);
                logger.info("Payment failed or canceled for order ID: {}", orderId);
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Payment verification failed.", null);
            }
        } catch (Exception e) {
            logger.error("Error during commit fee payment confirmation: ", e);
            return new ApiResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), "Payment processing error.", e.getMessage());
        }
    }

    //    Cancel Order
    public ApiResponse<String> cancelOrder(Long orderId) {
        Orders orders =
                orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (orders.getStatus() == OrderStatus.CANCELED || orders.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order has already been processed");
        }

        orders.setStatus(OrderStatus.CANCELED);
        orderRepository.save(orders);

        logger.info("Order with ID {} has been canceled", orderId);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order canceled successfully", null);
    }

    //    Delivered Order
    public ApiResponse<String> deliveredOrder(Long orderId) {
        Orders orders =
                orderRepository.findById(orderId).orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND));

        if (orders.getStatus() == OrderStatus.CANCELED || orders.getStatus() == OrderStatus.DELIVERED) {
            throw new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order has already been processed");
        }

        orders.setStatus(OrderStatus.DELIVERED);
        orderRepository.save(orders);

        logger.info("Order with ID {} has been delivered", orderId);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order delivered successfully", null);
    }

    //    Get All Orders
    public ApiResponse<List<OrderWithCustomerDTO>> getAllOrders() {
        try {
            List<OrderWithCustomerDTO> ordersWithCustomers = orderRepository.findAllWithCustomerAndShipment();
            if (ordersWithCustomers.isEmpty()) {
                return new ApiResponse<>(HttpStatus.OK.value(), "No orders found", null);
            }
            return new ApiResponse<>(HttpStatus.OK.value(), "Orders retrieved successfully", ordersWithCustomers);
        } catch (Exception e) {
            logger.error("Error retrieving orders: ", e);
            throw new AppException(ErrorCode.ORDER_RETRIEVAL_FAILED, "Error retrieving orders");
        }
    }

    // Accept Order
    public ApiResponse<String> acceptOrder(Long orderId) {
        logger.info("Attempting to accept order with ID: {}", orderId);

        Optional<Orders> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            logger.error("Order with ID {} not found in the database", orderId);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND, ORDER_NOT_FOUND_MESSAGE);
        }

        Orders order = optionalOrder.get();
        logger.info("Order found: {}", order);

        if (order.getStatus() != OrderStatus.PENDING) {
            logger.error("Order with ID {} is not in PENDING status, current status: {}", orderId, order.getStatus());
            throw new AppException(ErrorCode.ORDER_ALREADY_PROCESSED, "Order is not in PENDING status");
        }

        order.setStatus(OrderStatus.ACCEPTED);
        orderRepository.save(order);

        logger.info("Order with ID {} has been accepted", orderId);

        try {
            String message = staffAssignmentService.assignOrder(order.getOrderId());
            logger.info(message);
        } catch (Exception e) {
            logger.error("Failed to assign staff to order ID: {}", order.getOrderId(), e);
            throw new AppException(
                    ErrorCode.STAFF_ASSIGNMENT_FAILED, "Staff assignment failed for order ID: " + order.getOrderId());
        }

        return new ApiResponse<>(HttpStatus.OK.value(), "Order accepted and staff assigned successfully", null);
    }

    @Transactional
    public ApiResponse<List<OrderWithCustomerDTO>> getOrderHistoryByFilters(
            String token, String status, String fromDate, String toDate) {
        try {
            String customerId = jwtUtil.extractCustomerId(token);
            logger.info("Customer ID extracted from token: {}", customerId);

            LocalDate from = (fromDate != null) ? LocalDate.parse(fromDate) : null;
            LocalDate to = (toDate != null) ? LocalDate.parse(toDate) : null;
            OrderStatus orderStatus =
                    (status != null && !status.isEmpty()) ? OrderStatus.valueOf(status.toUpperCase()) : null;

            List<OrderWithCustomerDTO> orders =
                    orderRepository.findOrdersWithFilters(Long.parseLong(customerId), orderStatus, from, to);
            if (orders.isEmpty()) {
                logger.info("No orders found for customer with filters");
                return new ApiResponse<>(HttpStatus.OK.value(), "No orders found", null);
            }
            logger.info("Order history retrieved successfully for customer with filters");
            return new ApiResponse<>(HttpStatus.OK.value(), "Order history retrieved successfully", orders);
        } catch (Exception e) {
            logger.error("Error retrieving order history: ", e);
            throw new AppException(ErrorCode.ORDER_HISTORY_RETRIEVAL_FAILED);
        }
    }

    public Orders findOrderById(Long orderId) {
        return orderRepository
                .findById(orderId)
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, ORDER_NOT_FOUND_MESSAGE));
    }

    public OrderWithCustomerDTO getOrderWithDetails(Long orderId, HttpServletRequest request) {
        return orderRepository.findOrderWithCustomerAndShipment(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));
    }

    @Transactional
    public ApiResponse<String> confirmPaymentFromStorage(Long userId) {
        Map<String, Object> sessionData = TemporaryStorage.getInstance().retrieveData(userId);

        if (sessionData == null || !sessionData.containsKey("totalFee") || !sessionData.containsKey("orderId")) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Thông tin thanh toán không tìm thấy.", null);
        }

        try {
            BigDecimal totalFee = (BigDecimal) sessionData.get("totalFee");
            Long orderId = (Long) sessionData.get("orderId");
            Orders order = findOrderById(orderId);
            if (order == null) {
                logger.error("Không tìm thấy đơn hàng với ID: {}", orderId);
                return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đơn hàng.", null);
            }

            PaymentMethod paymentMethod = order.getPaymentMethod();
            Map<String, BigDecimal> calculationData = new HashMap<>();
            calculationData.put("totalFee", totalFee);

            ApiResponse<String> paymentResponse = handlePaymentMethod(order, paymentMethod, totalFee, calculationData);

            if (paymentResponse.getCode() == HttpStatus.OK.value() ) {
                order.setStatus(OrderStatus.IN_TRANSIT);
                order.setPaymentConfirmed(true);
                orderRepository.save(order);
                return new ApiResponse<>(HttpStatus.OK.value(), "Thanh toán xác nhận thành công", paymentResponse.getResult());
            } else {
                return paymentResponse;
            }

        } catch (AppException e) {
            logger.error("Order not found for userId {}: {}", userId, e.getMessage());
            return new ApiResponse<>(HttpStatus.NOT_FOUND.value(), "Không tìm thấy đơn hàng.", e.getMessage());
        } catch (Exception e) {
            logger.error("Error confirming payment from storage for userId {}: ", userId, e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error processing payment", e.getMessage());
        }
    }


    public ApiResponse<String> handlePaymentMethod(Orders order, PaymentMethod paymentMethod, BigDecimal totalFee, Map<String, BigDecimal> calculationData) {
        return switch (paymentMethod) {
            case VNPAY -> processVnPayPayment(order, totalFee, calculationData);

            case CASH_BY_RECEIVER, CASH_BY_SENDER -> processCashPayment(order, calculationData);
        };
    }

    private ApiResponse<String> processVnPayPayment(Orders order, BigDecimal totalFee, Map<String, BigDecimal> calculationData) {
        try {
            ApiResponse<String> paymentLinkResponse = vnPayService.createVnPayPaymentWithTotalFee(order, totalFee);
            if (paymentLinkResponse.getCode() != HttpStatus.OK.value()) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Failed to create VNPay payment link", null);
            }

            if (OrderStatus.PICKING_UP.equals(order.getStatus())) {
                order.setStatus(OrderStatus.IN_TRANSIT);
            }
            order.setPaymentConfirmed(true);

            orderDetailBuilder.updateOrderDetails(order, calculationData, null, null);
            invoiceBuilder.updateInvoice(order, calculationData);

            orderRepository.save(order);

            return new ApiResponse<>(HttpStatus.OK.value(), "Payment link sent to email", paymentLinkResponse.getResult());

        } catch (Exception e) {
            logger.error("Error creating VNPay payment link: ", e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create VNPay payment link", e.getMessage());
        }
    }

    private ApiResponse<String> processCashPayment(Orders order, Map<String, BigDecimal> calculationData) {
        if (OrderStatus.PICKING_UP.equals(order.getStatus())) {
            order.setStatus(OrderStatus.IN_TRANSIT);
        }
        order.setPaymentConfirmed(true);

        orderDetailBuilder.updateOrderDetails(order, calculationData, null, null);
        invoiceBuilder.updateInvoice(order, calculationData);

        orderRepository.save(order);
        return new ApiResponse<>(HttpStatus.OK.value(), "Order is in-transit but not yet paid", null);
    }
}