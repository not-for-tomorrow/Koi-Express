package com.koi_express.service.order;

import com.koi_express.dto.request.OrderRequest;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class OrderBuilder {

    public Orders buildOrder(OrderRequest orderRequest, Customers customer) {

        double distanceFee = TransportationFeeCalculator.calculateTotalFee(orderRequest.getKilometers());

        double commitmentFee = TransportationFeeCalculator.calculateCommitmentFee(orderRequest.getKilometers());

        Orders orders = Orders.builder()
                .customer(customer)
                .originLocation(orderRequest.getOriginLocation())
                .originDetail(orderRequest.getOriginDetail())
                .destinationLocation(orderRequest.getDestinationLocation())
                .destinationDetail(orderRequest.getDestinationDetail())
                .status(OrderStatus.PENDING)
                .paymentMethod(orderRequest.getPaymentMethod())
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .order(orders)
                .senderName(orderRequest.getSenderName())
                .senderPhone(orderRequest.getSenderPhone())
                .recipientName(orderRequest.getRecipientName())
                .recipientPhone(orderRequest.getRecipientPhone())
                .koiQuantity(orderRequest.getKoiQuantity())
                .distanceFee(BigDecimal.valueOf(distanceFee))
                .commitmentFee(BigDecimal.valueOf(commitmentFee))
                .paymentMethod(orderRequest.getPaymentMethod())
                .insurance(orderRequest.isInsuranceSelected())
                .build();

        orders.setOrderDetail(orderDetail);
        return orders;
    }
}
