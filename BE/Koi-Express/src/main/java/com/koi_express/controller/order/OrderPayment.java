package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.enums.PaymentMethod;
import com.koi_express.repository.OrderRepository;
import com.koi_express.service.order.OrderService;
import com.koi_express.service.order.builder.InvoiceBuilder;
import com.koi_express.service.order.builder.OrderDetailBuilder;
import com.koi_express.service.payment.VNPayService;
import com.koi_express.service.verification.EmailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderPayment {

    private static final Logger logger = LoggerFactory.getLogger(OrderPayment.class);

    private final OrderService orderService;
    private final OrderSessionManager sessionManager;
    private final JwtUtil jwtUtil;
    private final OrderDetailBuilder orderDetailBuilder;
    private final InvoiceBuilder invoiceBuilder;
    private final VNPayService vnPayService;
    private final EmailService emailService;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderPayment(
            OrderService orderService,
            OrderSessionManager sessionManager,
            JwtUtil jwtUtil,
            OrderDetailBuilder orderDetailBuilder,
            InvoiceBuilder invoiceBuilder,
            VNPayService vnPayService,
            EmailService emailService,
            OrderRepository orderRepository) {
        this.orderService = orderService;
        this.sessionManager = sessionManager;
        this.jwtUtil = jwtUtil;
        this.orderDetailBuilder = orderDetailBuilder;
        this.invoiceBuilder = invoiceBuilder;
        this.vnPayService = vnPayService;
        this.emailService = emailService;
        this.orderRepository = orderRepository;
    }

    @PostMapping("/payment/commit-fee/callback")
    public ResponseEntity<ApiResponse<String>> confirmCommitFeePayment(HttpServletRequest request) {
        Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        if (!vnpParams.containsKey("vnp_TxnRef")) {
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Missing transaction reference", null),
                    HttpStatus.BAD_REQUEST);
        }

        long orderId = Long.parseLong(vnpParams.get("vnp_TxnRef"));
        logger.info("Processing commit fee payment callback for order ID: {}", orderId);

        ApiResponse<String> response = orderService.confirmCommitFeePayment(orderId, vnpParams);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<ApiResponse<String>> confirmVnPayPayment(HttpServletRequest request, HttpSession session) {

        String role = sessionManager.getRoleFromSession(session);
        String userId = sessionManager.getUserIdFromSession(session);

        // Lấy dữ liệu từ session
        Map<String, Object> sessionData = sessionManager.retrieveSessionData(session, role, userId);
        if (sessionData == null || !sessionData.containsKey("orderId")) {
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Order ID not found in session", null),
                    HttpStatus.BAD_REQUEST);
        }
        Long orderId = (Long) sessionData.get("orderId");

        // Lấy dữ liệu từ session thứ hai (chứa totalFee và các biến liên quan)
        Map<String, BigDecimal> calculationData = sessionManager.retrieveCalculationSessionData(session, role, userId);
        if (calculationData == null || !calculationData.containsKey("totalFee")) {
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Calculation data missing", null),
                    HttpStatus.BAD_REQUEST);
        }

        BigDecimal totalFee = calculationData.get("totalFee");

        // Lấy thông tin đơn hàng
        Orders order = orderService.findOrderById(orderId);

        // Xử lý phương thức thanh toán
        PaymentMethod paymentMethod = order.getPaymentMethod();
        if (request.getParameter("paymentMethod") != null) {
            paymentMethod =
                    PaymentMethod.valueOf(request.getParameter("paymentMethod").toUpperCase());
        }

        // Nếu người dùng chọn phương thức VNPay
        if (paymentMethod == PaymentMethod.VNPAY) {
            try {
                // Tạo link thanh toán VNPay
                ApiResponse<String> paymentLinkResponse = vnPayService.createVnPayPaymentWithTotalFee(order, totalFee);
                if (paymentLinkResponse.getCode() != HttpStatus.OK.value()) {
                    return new ResponseEntity<>(
                            new ApiResponse<>(
                                    HttpStatus.BAD_REQUEST.value(), "Failed to create VNPay payment link", null),
                            HttpStatus.BAD_REQUEST);
                }

                // Gửi link thanh toán qua email
                String email = (String) sessionData.get("email");
                emailService.sendPaymentLink(email, paymentLinkResponse.getResult(), order);

                return new ResponseEntity<>(
                        new ApiResponse<>(
                                HttpStatus.OK.value(), "Payment link sent to email", paymentLinkResponse.getResult()),
                        HttpStatus.OK);

            } catch (Exception e) {
                logger.error("Error creating VNPay payment link: ", e);
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                                "Failed to create VNPay payment link",
                                e.getMessage()),
                        HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        // Nếu thanh toán qua phương thức khác
        if (paymentMethod == PaymentMethod.CASH_BY_RECEIVER || paymentMethod == PaymentMethod.CASH_BY_SENDER) {
            order.setStatus(OrderStatus.IN_TRANSIT);
            order.setPaymentConfirmed(false);

            // Cập nhật OrderDetail và Invoice
            orderDetailBuilder.updateOrderDetails(order, calculationData, null, null);
            invoiceBuilder.updateInvoice(order, calculationData);

            orderRepository.save(order);
            return new ResponseEntity<>(
                    new ApiResponse<>(HttpStatus.OK.value(), "Order is in-transit but not yet paid", null),
                    HttpStatus.OK);
        }

        // Chuyển đổi request parameters từ Map<String, String[]> sang Map<String, String> để tương thích với
        // verifyPayment
        Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        try {
            // Xác minh thanh toán VNPay
            if (vnPayService.verifyPayment(vnpParams)) {
                order.setStatus(OrderStatus.IN_TRANSIT);
                order.setPaymentConfirmed(true);

                // Cập nhật OrderDetail và Invoice
                orderDetailBuilder.updateOrderDetails(order, calculationData, null, null);
                invoiceBuilder.updateInvoice(order, calculationData);

                orderRepository.save(order);
                return new ResponseEntity<>(
                        new ApiResponse<>(HttpStatus.OK.value(), "Payment confirmed, order is in-transit", null),
                        HttpStatus.OK);
            } else {
                return new ResponseEntity<>(
                        new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Payment verification failed", null),
                        HttpStatus.BAD_REQUEST);
            }

        } catch (Exception e) {
            logger.error("Error verifying VNPay payment: ", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(), "Payment verification error", e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
