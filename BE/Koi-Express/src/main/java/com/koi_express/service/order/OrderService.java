package com.koi_express.service.order;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.exception.AppException;
import com.koi_express.exception.ErrorCode;
import com.koi_express.repository.OrderRepository;
import com.koi_express.repository.TransactionLogsRepository;
import com.koi_express.service.manager.ManagerService;
import com.koi_express.service.order.price.TransportationFeeCalculator;
import com.koi_express.service.payment.VNPayService;
import com.koi_express.service.staffAssignment.StaffAssignmentService;
import com.koi_express.service.verification.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ManagerService managerService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderBuilder orderBuilder;

    @Autowired
    private StaffAssignmentService staffAssignmentService;

    @Autowired
    @Lazy
    private VNPayService vnPayService;

    @Autowired
    private TransactionLogsRepository transactionLogsRepository;

    // Create Order with OrderRequest, order with add into database base on customerId in payload of token
    public ApiResponse<Map<String, Object>> createOrder(OrderRequest orderRequest, String token) {

        try {
            String customerId = jwtUtil.extractCustomerId(token);
            Customers customer = managerService.getCustomerById(Long.parseLong(customerId));

            double totalFee = TransportationFeeCalculator.calculateTotalFee(orderRequest.getKilometers());
            double commitmentFee = TransportationFeeCalculator.calculateCommitmentFee(orderRequest.getKilometers());

            Orders orders = orderBuilder.buildOrder(orderRequest, customer);
            orders.getOrderDetail().setDistanceFee(BigDecimal.valueOf(totalFee)); // Set total fee
            orders.getOrderDetail().setCommitmentFee(BigDecimal.valueOf(commitmentFee)); // Set commitment fee

            orders.setStatus(OrderStatus.COMMIT_FEE_PENDING);

            Orders savedOrder = orderRepository.save(orders);
            logger.info("Order created successfully with COMMIT_FEE_PENDING status: {}", savedOrder);

            // Gọi VNPayService để tạo URL thanh toán
            String paymentUrl = vnPayService.createVnPayPayment(savedOrder);

            logger.info("Payment URL generated: {}", paymentUrl);

            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("order", savedOrder); // Thông tin đơn hàng
            responseMap.put("paymentUrl", paymentUrl); // URL thanh toán VNPay

            return new ApiResponse<>(HttpStatus.OK.value(), "Order created, awaiting commit fee payment", responseMap);
        } catch (Exception e) {
            logger.error("Error creating order: ", e);
            throw new AppException(ErrorCode.ORDER_CREATION_FAILED);
        }
    }

    @Transactional
    public ApiResponse<String> confirmCommitFeePayment(long orderId, Map<String, String> vnpParams) {
        try {
            // Xác minh thanh toán
            boolean isPaymentVerified = vnPayService.verifyPayment(vnpParams);

            if (isPaymentVerified) {
                // Tìm đơn hàng và cập nhật trạng thái
                Orders order = orderRepository
                        .findById(orderId)
                        .orElseThrow(() -> new AppException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

                order.setStatus(OrderStatus.PENDING); // Cập nhật trạng thái thành PENDING
                orderRepository.save(order);

                //                TransactionLogs transactionLogs = TransactionLogs.builder()
                //                        .order(order)
                //                        .customer(order.getCustomer())
                //                        .amount(order.getOrderDetail().getCommitmentFee())
                //                        .paymentMethod(PaymentMethod.VNPAY) // Assuming VNPay is used
                //                        .status(TransactionStatus.PAID) // Assuming success
                //                        .build();
                //
                //                transactionLogsRepository.save(transactionLogs);

                // Gửi email xác nhận thanh toán thành công
                emailService.sendOrderConfirmationEmail(order.getCustomer().getEmail(), order);

                return new ApiResponse<>(HttpStatus.OK.value(), "Commit fee payment confirmed successfully", null);
            }
            return new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Payment verification failed.", null);
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

        // Kiểm tra trạng thái đơn hàng có đúng là PENDING không
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

            // Parse dates and status
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
}
