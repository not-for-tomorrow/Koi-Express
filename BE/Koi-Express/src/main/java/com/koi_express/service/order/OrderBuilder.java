package com.koi_express.service.order;

import com.koi_express.dto.request.OrderRequest;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderBuilder {

    @Autowired
    private OrderFeeCalculator orderFeeCalculator;

    public Orders buildOrder(OrderRequest orderRequest, Customers customer) {

        Orders orders = Orders.builder()
                .customer(customer)
                .originLocation(orderRequest.getOriginLocation())
                .originDetail(orderRequest.getOriginDetail())
                .destinationLocation(orderRequest.getDestinationLocation())
                .destinationDetail(orderRequest.getDestinationDetail())
                .status(OrderStatus.PENDING)
                .totalFee(orderFeeCalculator.calculateTotalFee(orderRequest))
                .paymentMethod(orderRequest.getPaymentMethod())
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .order(orders)
                .senderName(orderRequest.getSenderName())
                .senderPhone(orderRequest.getSenderPhone())
                .recipientName(orderRequest.getRecipientName())
                .recipientPhone(orderRequest.getRecipientPhone())
                .koiType(orderRequest.getKoiType())
                .koiQuantity(orderRequest.getKoiQuantity())
                .kilometers(orderRequest.getKilometers() != null ? orderRequest.getKilometers() : 0)
                .packingMethod(orderRequest.getPackingMethod())
                .paymentMethod(orderRequest.getPaymentMethod())
                .insurance(orderRequest.isInsurance())
                .specialCare(orderRequest.isSpecialCare())
                .healthCheck(orderRequest.isHealthCheck())
                .build();

        orders.setOrderDetail(orderDetail);
        return orders;
    }
}
