package com.koi_express.controller.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.OrderWithCustomerDTO;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.KoiType;
import com.koi_express.service.order.OrderService;
import com.koi_express.service.order.price.KoiInvoiceCalculator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private KoiInvoiceCalculator koiInvoiceCalculator;

    @PostMapping("/create")
    public ApiResponse<Map<String, Object>> createOrder(
            @Valid @RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest) {

        String token = extractToken(httpServletRequest);
        logger.info("Processing order creation for token: {}", token);

        ApiResponse<Map<String, Object>> response = orderService.createOrder(orderRequest, token);

        logger.info("Response sent to client: {}", response);

        return response;
    }

    @PreAuthorize("hasRole('MANAGER') or hasRole('CUSTOMER')")
    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long orderId) {
        logger.info("Canceling order with ID: {}", orderId);

        ApiResponse<String> response = orderService.cancelOrder(orderId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<ApiResponse<String>> deliverOrder(@PathVariable Long orderId) {
        logger.info("Marking order as delivered with ID: {}", orderId);

        ApiResponse<String> response = orderService.deliveredOrder(orderId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    //    ử dụng jwt có role cúa managerể tuy cập vào api này
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping(value = "/all", produces = "application/json")
    public ResponseEntity<Page<Orders>> getAllOrders(
            HttpServletRequest httpServletRequest,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        logger.info("Fetching all orders with page: {} and size: {}", page, size);

        Pageable paging = PageRequest.of(page, size);
        Page<Orders> ordersPage = orderService.getAllOrders(paging);

        return new ResponseEntity<>(ordersPage, HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<ApiResponse<List<Orders>>> getOrderHistory(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "fromDate", required = false) String fromDate,
            @RequestParam(value = "toDate", required = false) String toDate) {

        logger.info("Fetching order history with status: {}, fromDate: {}, toDate: {}", status, fromDate, toDate);

        String token = authorizationHeader.substring(7);
        ApiResponse<List<Orders>> response = orderService.getOrderHistoryByFilters(token, status, fromDate, toDate);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String extractToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.substring(7);
        } else {
            throw new IllegalArgumentException("Authorization header must be provided");
        }
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

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderWithCustomerDTO> getOrderWithDetails(@PathVariable Long orderId, HttpSession session, HttpServletRequest request) {
        logger.info("Fetching order with details for orderId: {}", orderId);

        String token = request.getHeader("Authorization").substring(7);  // Extract the token
        String role = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token, role);

        logger.info("Extracted role: {}, Extracted userId: {}", role, userId);

        Orders order = orderService.getOrderWithDetails(orderId).getOrder();
        Customers customer = order.getCustomer();

        // Store data in session based on the role
        storeSessionData(session, role, userId, order);

        OrderWithCustomerDTO response = OrderWithCustomerDTO.builder()
                .order(order)
                .customer(customer)
                .build();

        return ResponseEntity.ok(response);
    }


    @PostMapping("/calculate-total-fee")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> calculateTotalFee(
            @RequestParam KoiType koiType,
            @RequestParam BigDecimal koiSize,
            HttpSession session, HttpServletRequest request) {

        if (session == null) {
            logger.error("Session is null. Cannot proceed.");
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Session is null", null), HttpStatus.INTERNAL_SERVER_ERROR);
        }

        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token, role);

        Map<String, Object> sessionData = retrieveSessionData(session, role, userId);
        if (sessionData == null || !sessionData.containsKey("koiQuantity")
                || !sessionData.containsKey("distanceFee")
                || !sessionData.containsKey("commitmentFee")) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Session data missing", null), HttpStatus.BAD_REQUEST);
        }

        Integer koiQuantity = (Integer) sessionData.get("koiQuantity");
        BigDecimal distanceFee = (BigDecimal) sessionData.get("distanceFee");
        BigDecimal commitmentFee = (BigDecimal) sessionData.get("commitmentFee");

        if (koiQuantity == null || distanceFee == null || commitmentFee == null) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid session data", null), HttpStatus.BAD_REQUEST);
        }

        ApiResponse<Map<String, BigDecimal>> response = koiInvoiceCalculator.calculateTotalPrice(koiType, koiQuantity, koiSize, distanceFee, commitmentFee);

        storeCalculationSessionData(session, role, userId, response.getResult());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/confirm-payment")
    public ResponseEntity<ApiResponse<String>> confirmVnPayPayment(
            HttpServletRequest request,
            @RequestParam Map<String, String> vnpParams,
            @RequestParam KoiType koiType,
            @RequestParam BigDecimal koiSize,
            HttpSession session) {

        String token = request.getHeader("Authorization").substring(7);
        String role = jwtUtil.extractRole(token);
        String userId = jwtUtil.extractUserId(token, role);

        Map<String, Object> sessionData = retrieveSessionData(session, role, userId);

        if (sessionData == null || sessionData.get("orderId") == null) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Order ID not found in session", null), HttpStatus.BAD_REQUEST);
        }

        Long orderId = (Long) sessionData.get("orderId");

        ApiResponse<String> response = orderService.confirmVnPayPayment(orderId, vnpParams, koiType, koiSize);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private String getSessionKey(String role, String userId) {
        switch (role) {
            case "CUSTOMER":
                return "customer_" + userId;
            case "DELIVERING_STAFF":
                return "staff_" + userId;
            case "SALES_STAFF":
            case "MANAGER":
                return "account_" + userId;
            default:
                throw new IllegalArgumentException("Invalid role: " + role);
        }
    }

    private void storeSessionData(HttpSession session, String role, String userId, Orders order) {
        String sessionKey = getSessionKey(role, userId);

        Customers customer = order.getCustomer();

        Map<String, Object> sessionData = Map.of(
                "koiQuantity", order.getOrderDetail().getKoiQuantity(),
                "distanceFee", order.getOrderDetail().getDistanceFee(),
                "commitmentFee", order.getOrderDetail().getCommitmentFee(),
                "orderId", order.getOrderId(),
                "customerId", customer.getCustomerId(),
                "email", customer.getEmail()
        );

        session.setAttribute(sessionKey, sessionData);

        // Log the stored session data
        logger.info("Session data stored for key '{}': {}", sessionKey, sessionData);
    }

    private Map<String, Object> retrieveSessionData(HttpSession session, String role, String userId) {
        String sessionKey = getSessionKey(role, userId);
        Map<String, Object> sessionData = (Map<String, Object>) session.getAttribute(sessionKey);

        // Log the retrieved session data
        if (sessionData != null) {
            logger.info("Session data retrieved for key '{}': {}", sessionKey, sessionData);
        } else {
            logger.info("No session data found for key '{}'", sessionKey);
        }

        return sessionData;
    }

    private void storeCalculationSessionData(HttpSession session, String role, String userId, Map<String, BigDecimal> calculationData) {
        String sessionKey = getSessionKey(role, userId) + "_calculation";

        // Store the calculation data
        session.setAttribute(sessionKey, calculationData);

        // Log the stored session data
        logger.info("Calculation session data stored for key '{}': {}", sessionKey, calculationData);
    }


    private Map<String, Object> retrieveCalculationSessionData(HttpSession session, String role, String userId) {
        String sessionKey = getSessionKey(role, userId) + "_calculation";
        return (Map<String, Object>) session.getAttribute(sessionKey);
    }


}