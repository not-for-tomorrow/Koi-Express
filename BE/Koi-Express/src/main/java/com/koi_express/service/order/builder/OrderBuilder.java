package com.koi_express.service.order.builder;

import java.math.BigDecimal;

import com.koi_express.dto.request.OrderRequest;
import com.koi_express.entity.customer.Customers;
import com.koi_express.entity.order.OrderDetail;
import com.koi_express.entity.order.Orders;
import com.koi_express.enums.OrderStatus;
import com.koi_express.repository.OrderRepository;
import com.koi_express.service.order.price.TransportationFeeCalculator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderBuilder {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransportationFeeCalculator transportationFeeCalculator;

    public Orders buildOrder(OrderRequest orderRequest, Customers customer) {

        BigDecimal distanceFee = transportationFeeCalculator.calculateTotalFee(orderRequest.getKilometers());

        BigDecimal commitmentFee = transportationFeeCalculator.calculateCommitmentFee(orderRequest.getKilometers());

        Orders orders = Orders.builder()
                .customer(customer)
                .originLocation(orderRequest.getOriginLocation())
                .originDetail(orderRequest.getOriginDetail())
                .destinationLocation(orderRequest.getDestinationLocation())
                .destinationDetail(orderRequest.getDestinationDetail())
                .status(OrderStatus.COMMIT_FEE_PENDING)
                .paymentMethod(orderRequest.getPaymentMethod())
                .build();

        OrderDetail orderDetail = OrderDetail.builder()
                .order(orders)
                .senderName(orderRequest.getSenderName())
                .senderPhone(orderRequest.getSenderPhone())
                .recipientName(orderRequest.getRecipientName())
                .recipientPhone(orderRequest.getRecipientPhone())
                .koiQuantity(orderRequest.getKoiQuantity())
                .distanceFee(distanceFee)
                .commitmentFee(commitmentFee)
                .paymentMethod(orderRequest.getPaymentMethod())
                .insurance(orderRequest.isInsuranceSelected())
                .build();

        orders.setOrderDetail(orderDetail);
        return orders;
    }
}
