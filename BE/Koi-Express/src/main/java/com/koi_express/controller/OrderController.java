package com.koi_express.controller;

import com.koi_express.dto.request.OrderRequest;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.entity.Orders;
import com.koi_express.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
