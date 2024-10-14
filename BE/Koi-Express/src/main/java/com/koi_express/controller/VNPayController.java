package com.koi_express.controller;

import com.koi_express.dto.request.PaymentRequest;
import com.koi_express.service.payment.VNPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin("http://localhost:5173")
@RestController
@RequestMapping("/api/v1/vnpay")
public class VNPayController {

    @Autowired
    private VNPayService vnPayService;

    @PostMapping("/create-payment")
    public ResponseEntity<Map<String, String>> createPayment(@RequestBody PaymentRequest request) {
        Long commitmentAmount = Math.round(request.getTotalAmount() * 30 / 100.0);

        String paymentUrl = vnPayService.createPaymentUrl(request.getOrderId(), commitmentAmount, request.getCustomerName(), request.getBankCode());

        Map<String, String> response = new HashMap<>();
        response.put("paymentUrl", paymentUrl);
        return ResponseEntity.ok(response);
    }
}
