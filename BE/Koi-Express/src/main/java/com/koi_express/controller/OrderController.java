package com.koi_express.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
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
    public ResponseEntity<Orders> getOrderWithDetails(@PathVariable Long orderId, HttpSession session, HttpServletRequest request) {
        logger.info("Fetching order with details for orderId: {}", orderId);

        String token = request.getHeader("Authorization").substring(7);  // Extract the token
        String role = jwtUtil.extractRole(token);

        logger.info("Extracted role: {}", role);

        // Extract the appropriate ID based on the role
        String userId = jwtUtil.extractUserId(token, role);
        logger.info("Extracted userId: {}", userId);

        Orders order = orderService.getOrderWithDetails(orderId);
        logger.info("Order details retrieved for orderId: {}", orderId);

        // Store data in session using the appropriate key based on role
        switch (role) {
            case "CUSTOMER":
                logger.info("Storing session data for customer with ID: {}", userId);
                session.setAttribute("customer_" + userId, Map.of(
                        "koiQuantity", order.getOrderDetail().getKoiQuantity(),
                        "distanceFee", order.getOrderDetail().getDistanceFee(),
                        "commitmentFee", order.getOrderDetail().getCommitmentFee(),
                        "role", role
                ));
                break;
            case "DELIVERING_STAFF":
                logger.info("Storing session data for delivering staff with ID: {}", userId);
                session.setAttribute("staff_" + userId, Map.of(
                        "koiQuantity", order.getOrderDetail().getKoiQuantity(),
                        "distanceFee", order.getOrderDetail().getDistanceFee(),
                        "commitmentFee", order.getOrderDetail().getCommitmentFee(),
                        "role", role
                ));
                break;
            case "SALES_STAFF":
            case "MANAGER":
                logger.info("Storing session data for account (sales staff or manager) with ID: {}", userId);
                session.setAttribute("account_" + userId, Map.of(
                        "koiQuantity", order.getOrderDetail().getKoiQuantity(),
                        "distanceFee", order.getOrderDetail().getDistanceFee(),
                        "commitmentFee", order.getOrderDetail().getCommitmentFee(),
                        "role", role
                ));
                break;
            default:
                logger.error("Invalid role: {}", role);
                throw new IllegalArgumentException("Invalid role: " + role);
        }

        return ResponseEntity.ok(order);
    }

    @PostMapping("/calculate-total-fee")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> calculateTotalFee(
            @RequestParam KoiType koiType,
            @RequestParam BigDecimal koiSize,
            HttpSession session, HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);  // Extract the token
        String role = jwtUtil.extractRole(token);  // Extract role from token
        String userId = jwtUtil.extractUserId(token, role);  // Extract userId based on role

        // Retrieve stored values using the appropriate session key based on role
        Map<String, Object> sessionData;
        switch (role) {
            case "CUSTOMER":
                sessionData = (Map<String, Object>) session.getAttribute("customer_" + userId);
                break;
            case "DELIVERING_STAFF":
                sessionData = (Map<String, Object>) session.getAttribute("staff_" + userId);
                break;
            case "SALES_STAFF":
            case "MANAGER":
                sessionData = (Map<String, Object>) session.getAttribute("account_" + userId);
                break;
            default:
                return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid role", null), HttpStatus.BAD_REQUEST);
        }

        if (sessionData == null) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Session data not found for user", null), HttpStatus.BAD_REQUEST);
        }

        // Extract stored data
        Integer koiQuantity = (Integer) sessionData.get("koiQuantity");
        BigDecimal distanceFee = (BigDecimal) sessionData.get("distanceFee");
        BigDecimal commitmentFee = (BigDecimal) sessionData.get("commitmentFee");

        if (koiQuantity == null || distanceFee == null || commitmentFee == null) {
            return new ResponseEntity<>(new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), "Invalid session data", null), HttpStatus.BAD_REQUEST);
        }

        // Calculate the total price using the KoiInvoiceCalculator and get the full breakdown
        ApiResponse<Map<String, BigDecimal>> response = koiInvoiceCalculator.calculateTotalPrice(koiType, koiQuantity, koiSize, distanceFee, commitmentFee);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}