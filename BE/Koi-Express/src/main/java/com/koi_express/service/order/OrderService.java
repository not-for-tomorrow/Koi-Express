package com.koi_express.service.order;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.controller.order.OrderSessionManager;
import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.KoiType;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.service.manager.ManagerService;
import com.koi_express.service.order.builder.InvoiceBuilder;
import com.koi_express.service.order.builder.OrderBuilder;
import com.koi_express.service.order.builder.OrderDetailBuilder;
import com.koi_express.service.order.price.KoiInvoiceCalculator;
import com.koi_express.service.order.price.TransportationFeeCalculator;
import com.koi_express.service.payment.VNPayService;
import com.koi_express.service.staffAssignment.StaffAssignmentService;
import com.koi_express.service.verification.EmailService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;
    private final ManagerService managerService;
    private final EmailService emailService;
    private final OrderBuilder orderBuilder;
    private final StaffAssignmentService staffAssignmentService;
    private final VNPayService vnPayService;
    private final KoiInvoiceCalculator koiInvoiceCalculator;
    private final TransportationFeeCalculator transportationFeeCalculator;
    private final InvoiceBuilder invoiceBuilder;
    private final OrderDetailBuilder orderDetailBuilder;
    private final OrderSessionManager sessionManager;

    @Autowired
    public OrderService(
            OrderRepository orderRepository,
            JwtUtil jwtUtil,
            ManagerService managerService,
            EmailService emailService,
            OrderBuilder orderBuilder,
            StaffAssignmentService staffAssignmentService,
            @Lazy VNPayService vnPayService,
            KoiInvoiceCalculator koiInvoiceCalculator,
            TransportationFeeCalculator transportationFeeCalculator,
            InvoiceBuilder invoiceBuilder,
            OrderDetailBuilder orderDetailBuilder,
            OrderSessionManager sessionManager
    ) {
        this.orderRepository = orderRepository;
        this.jwtUtil = jwtUtil;
        this.managerService = managerService;
        this.emailService = emailService;
        this.orderBuilder = orderBuilder;
        this.staffAssignmentService = staffAssignmentService;
        this.vnPayService = vnPayService;
        this.koiInvoiceCalculator = koiInvoiceCalculator;
        this.transportationFeeCalculator = transportationFeeCalculator;
        this.invoiceBuilder = invoiceBuilder;
        this.orderDetailBuilder = orderDetailBuilder;
        this.sessionManager = sessionManager;
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
                    .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

            if (isPaymentVerified && "00".equals(responseCode)) {
                order.setStatus(OrderStatus.PENDING);
                orderRepository.save(order);

                // Gửi email xác nhận thanh toán thành công
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
    public Page<Orders> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    // Accept Order
    public ApiResponse<String> acceptOrder(Long orderId) {
        logger.info("Attempting to accept order with ID: {}", orderId);

        Optional<Orders> optionalOrder = orderRepository.findById(orderId);
        if (!optionalOrder.isPresent()) {
            logger.error("Order with ID {} not found in the database", orderId);
            throw new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found");
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
    public ApiResponse<List<Orders>> getOrderHistoryByFilters(
            String token, String status, String fromDate, String toDate) {
        try {
            String customerId = jwtUtil.extractCustomerId(token);
            logger.info("Customer ID extracted from token: {}", customerId);

            LocalDate from = (fromDate != null) ? LocalDate.parse(fromDate) : null;
            LocalDate to = (toDate != null) ? LocalDate.parse(toDate) : null;
            OrderStatus orderStatus =
                    (status != null && !status.isEmpty()) ? OrderStatus.valueOf(status.toUpperCase()) : null;

            List<Orders> orders =
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
                .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));
    }

    public OrderWithCustomerDTO getOrderWithDetails(Long orderId) {
        Orders order = orderRepository
                .findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found with ID: " + orderId));

        Customers customer = order.getCustomer();

        OrderWithCustomerDTO response = new OrderWithCustomerDTO(order, customer);

        return response;
    }

    public ApiResponse<Map<String, BigDecimal>> calculateTotalFee(Long orderId, KoiType koiType, BigDecimal koiSize) {
        try {
            Orders order = findOrderById(orderId);

            int koiQuantity = order.getOrderDetail().getKoiQuantity();
            if (koiQuantity <= 0) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid koi quantity", null);
            }

            BigDecimal distanceFee = order.getOrderDetail().getDistanceFee();
            BigDecimal commitmentFee = order.getOrderDetail().getCommitmentFee();

            if (distanceFee == null || commitmentFee == null) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid fees in order details", null);
            }

            logger.info("Calculating total fee for koiType: {}, koiQuantity: {}, koiSize: {}, distanceFee: {}, commitmentFee: {}",
                    koiType, koiQuantity, koiSize, distanceFee, commitmentFee);

            ApiResponse<Map<String, BigDecimal>> response = koiInvoiceCalculator.calculateTotalPrice(
                    koiType, koiQuantity, koiSize, distanceFee, commitmentFee);

            if (response.getCode() != HttpStatus.OK.value()) {
                logger.error("Error calculating total fee: {}", response.getMessage());
            }

            return response;
        } catch (Exception e) {
            logger.error("Error during fee calculation: ", e);
            return new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error calculating total fee");
        }
    }

    public ApiResponse<String> processOrderPayment(Long orderId, PaymentMethod paymentMethod) {
        Orders order = this.findOrderById(orderId);

        ApiResponse<Map<String, BigDecimal>> totalFeeResponse = this.calculateTotalFee(orderId, null, null);
        if (totalFeeResponse.getCode() != 200) {
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Không thể tính toán tổng chi phí", null);
        }

        BigDecimal totalFee = totalFeeResponse.getResult().get("totalPrice");

        if (paymentMethod == PaymentMethod.VNPAY) {
            ApiResponse<String> paymentLinkResponse = vnPayService.createVnPayPayment(order);
            if (paymentLinkResponse.getCode() != 200) {
                return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Không thể tạo liên kết thanh toán VNPay", null);
            }

            String paymentLink = paymentLinkResponse.getResult();

            String customerEmail = order.getCustomer().getEmail();
            emailService.sendPaymentLink(customerEmail, paymentLink, order);

            return new ApiResponse<>(HttpStatus.OK.value(), "Đã gửi liên kết thanh toán VNPay đến email của khách hàng", paymentLink);
        } else if (paymentMethod == PaymentMethod.CASH_BY_RECEIVER || paymentMethod == PaymentMethod.CASH_BY_SENDER) {
            order.setStatus(OrderStatus.IN_TRANSIT);
            order.setPaymentConfirmed(false);
            orderRepository.save(order);

            return new ApiResponse<>(HttpStatus.OK.value(), "Thanh toán bằng tiền mặt. Đơn hàng đang vận chuyển (chưa thanh toán).", null);
        }

        return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Phương thức thanh toán không hợp lệ", null);
    }

    @Transactional
    public ResponseEntity<ApiResponse<String>> confirmPayment(HttpSession session, HttpServletRequest request) {
        // Lấy role và userId từ session
        String role = sessionManager.getRoleFromSession(session);
        String userId = sessionManager.getUserIdFromSession(session);

        // Lấy dữ liệu từ session đầu tiên (chứa orderId và các biến liên quan)
        Map<String, Object> sessionData = sessionManager.retrieveSessionData(session, role, userId);
        if (sessionData.isEmpty() || !sessionData.containsKey("orderId")) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Order ID not found in session", null), HttpStatus.BAD_REQUEST);
        }

        Long orderId = (Long) sessionData.get("orderId");

        // Lấy dữ liệu từ session thứ hai (chứa totalFee và các biến tính toán)
        Map<String, BigDecimal> calculationData = sessionManager.retrieveCalculationSessionData(session, role, userId);
        if (calculationData.isEmpty() || !calculationData.containsKey("totalFee")) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Calculation data missing", null), HttpStatus.BAD_REQUEST);
        }

        BigDecimal totalFee = calculationData.get("totalFee");

        // Tìm thông tin đơn hàng theo orderId
        Orders order = this.findOrderById(orderId);

        // Lấy phương thức thanh toán từ order và cho phép người dùng thay đổi nếu cần
        PaymentMethod paymentMethod = order.getPaymentMethod();
        if (request.getParameter("paymentMethod") != null) {
            paymentMethod = PaymentMethod.valueOf(request.getParameter("paymentMethod").toUpperCase());
        }

        if (paymentMethod == PaymentMethod.VNPAY) {
            try {
                ApiResponse<String> paymentLinkResponse = vnPayService.createVnPayPaymentWithTotalFee(order, totalFee);
                if (paymentLinkResponse.getCode() != HttpStatus.OK.value()) {
                    return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Failed to create VNPay payment link", null), HttpStatus.BAD_REQUEST);
                }

                String email = (String) sessionData.get("email");
                emailService.sendPaymentLink(email, paymentLinkResponse.getResult(), order);

                orderDetailBuilder.updateOrderDetails(order, calculationData, null, null);
                invoiceBuilder.updateInvoice(order, calculationData);

                return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Payment link sent to email", paymentLinkResponse.getResult()), HttpStatus.OK);

            } catch (Exception e) {
                logger.error("Error creating VNPay payment link: ", e);
                return new ResponseEntity<>(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Failed to create VNPay payment link", e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (paymentMethod == PaymentMethod.CASH_BY_RECEIVER || paymentMethod == PaymentMethod.CASH_BY_SENDER) {
            order.setStatus(OrderStatus.IN_TRANSIT);
            order.setPaymentConfirmed(false);

            orderDetailBuilder.updateOrderDetails(order, calculationData, null, null);
            invoiceBuilder.updateInvoice(order, calculationData);

            orderRepository.save(order);
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.OK.value(), "Order is in-transit but not yet paid", null), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid payment method", null), HttpStatus.BAD_REQUEST);
    }

}