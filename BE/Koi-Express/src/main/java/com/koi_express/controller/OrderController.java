package com.koi_express.controller;

import java.util.List;

import com.koi_express.JWT.JwtUtil;
import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.order.Orders;
import com.koi_express.service.order.OrderService;
import jakarta.servlet.http.HttpServletRequest;
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

    @Autowired
    private OrderService orderService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/create")
    public ApiResponse<Orders> createOrder(
            @RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest) {

        String token = extractToken(httpServletRequest);

        return orderService.createOrder(orderRequest, token);
    }

    @PostMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponse<String>> cancelOrder(@PathVariable Long orderId) {

        ApiResponse<String> response = orderService.cancelOrder(orderId);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/deliver/{orderId}")
    public ResponseEntity<ApiResponse<String>> deliverOrder(@PathVariable Long orderId) {

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
}
