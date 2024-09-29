package com.koi_express.controller;

import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Orders;
import com.koi_express.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    public ApiResponse<Orders> createOrder(@RequestBody OrderRequest orderRequest, HttpServletRequest httpServletRequest ) {
        String token = httpServletRequest.getHeader("Authorization").substring(7);

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

}
