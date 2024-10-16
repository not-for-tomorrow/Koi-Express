package com.koi_express.controller.payment;

import java.util.Map;
import java.util.stream.Collectors;

import com.koi_express.dto.payment.PaymentDTO;
import com.koi_express.dto.response.ApiResponse;
import com.koi_express.dto.response.ResponseObject;
import com.koi_express.entity.order.Orders;
import com.koi_express.service.order.OrderService;
import com.koi_express.service.payment.VNPayService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class VNPayController {

    private final VNPayService vnPayService;

    @Autowired
    private OrderService orderService;

    @GetMapping("/vn-pay")
    public ResponseObject<PaymentDTO.VNPayResponse> pay(@RequestParam("orderId") Long orderId) {
        // Tìm đơn hàng dựa trên orderId
        Orders order = orderService.findOrderById(orderId);

        // Tạo URL thanh toán từ đối tượng Orders
        String paymentUrl = vnPayService.createVnPayPayment(order);

        PaymentDTO.VNPayResponse paymentResponse = PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();

        return new ResponseObject<>(HttpStatus.OK, "Success", paymentResponse);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseObject<String> payCallbackHandler(HttpServletRequest request) {
        // Lấy tất cả các tham số từ request và chuyển thành Map
        Map<String, String> vnpParams = request.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue()[0]));

        // Lấy orderId từ tham số "vnp_TxnRef"
        long orderId = Long.parseLong(vnpParams.get("vnp_TxnRef"));

        // Xác nhận thanh toán commit fee cho đơn hàng
        ApiResponse<String> response = orderService.confirmCommitFeePayment(orderId, vnpParams);

        if (response.getCode() == HttpStatus.OK.value()) {
            return new ResponseObject<>(HttpStatus.OK, "Thanh toán thành công", "Payment Success");
        } else {
            return new ResponseObject<>(
                    HttpStatus.BAD_REQUEST, "Xác minh thanh toán thất bại", "Payment Verification Failed");
        }
    }
}
