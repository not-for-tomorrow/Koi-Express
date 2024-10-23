package com.koi_express.service.order.builder;

import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class OrderDetailBuilder {

    public void updateOrderDetails(Orders order, Map<String, BigDecimal> fees, KoiType koiType, BigDecimal koiSize) {
        OrderDetail orderDetail = order.getOrderDetail();
        orderDetail.setKoiType(koiType);
        orderDetail.setKoiSize(koiSize);
        orderDetail.setDistanceFee(fees.get("remainingTransportationFee"));
        orderDetail.setKoiFee(fees.get("fishPrice"));
        orderDetail.setCareFee(fees.get("careFee"));
        orderDetail.setPackagingFee(fees.get("packagingFee"));
        orderDetail.setInsuranceFee(fees.get("insuranceFee"));
        orderDetail.setVat(fees.get("vat"));
        order.setTotalFee(fees.get("totalPrice"));
    }
}
