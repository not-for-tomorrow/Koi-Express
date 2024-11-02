package com.koi_express.service.order.builder;

import java.math.BigDecimal;
import java.util.Map;

import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.KoiType;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailBuilder {

    public void updateOrderDetails(Orders order, Map<String, BigDecimal> fees, KoiType koiType, BigDecimal koiSize) {
        OrderDetail orderDetail = order.getOrderDetail();
        orderDetail.setKoiType(koiType);
        orderDetail.setKoiSize(koiSize);
        orderDetail.setDistanceFee(fees.getOrDefault("remainingTransportationFee", BigDecimal.ZERO));
        orderDetail.setKoiFee(fees.getOrDefault("koiFee", BigDecimal.ZERO));
        orderDetail.setCareFee(fees.getOrDefault("careFee", BigDecimal.ZERO));
        orderDetail.setPackagingFee(fees.getOrDefault("packagingFee", BigDecimal.ZERO));
        orderDetail.setInsuranceFee(fees.getOrDefault("insuranceFee", BigDecimal.ZERO));
        orderDetail.setVat(fees.getOrDefault("vat", BigDecimal.ZERO));
        order.setTotalFee(fees.getOrDefault("totalFee", BigDecimal.ZERO));
    }
}
