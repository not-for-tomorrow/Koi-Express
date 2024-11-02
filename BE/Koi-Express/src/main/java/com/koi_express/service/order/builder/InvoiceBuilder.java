package com.koi_express.service.order.builder;

import java.math.BigDecimal;
import java.util.Map;

import com.koi_express.entity.order.Invoice;
import com.koi_express.entity.order.Orders;
import com.koi_express.repository.InvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceBuilder.class);
    private final InvoiceRepository invoiceRepository;

    public void updateInvoice(Orders order, Map<String, BigDecimal> fees) {
        if (order == null || order.getOrderDetail() == null || fees == null) {
            logger.error("Invalid order details or fees provided. Invoice update aborted.");
            throw new IllegalArgumentException("Order, order details, and fees must not be null.");
        }

        Invoice invoice = buildInvoice(order, fees);
        invoiceRepository.save(invoice);
        logger.info("Invoice for order ID {} has been updated.", order.getOrderId());
    }

    private Invoice buildInvoice(Orders order, Map<String, BigDecimal> fees) {
        return Invoice.builder()
                .order(order)
                .customer(order.getCustomer())
                .commitmentFee(order.getOrderDetail().getCommitmentFee())
                .distanceFee(fees.getOrDefault("remainingTransportationFee", BigDecimal.ZERO))
                .careFee(fees.getOrDefault("careFee", BigDecimal.ZERO))
                .packagingFee(fees.getOrDefault("packagingFee", BigDecimal.ZERO))
                .returnFee(order.getOrderDetail().getReturnFee())
                .vat(fees.getOrDefault("vat", BigDecimal.ZERO))
                .koiFee(fees.getOrDefault("koiFee", BigDecimal.ZERO))
                .insuranceFee(fees.getOrDefault("insuranceFee", BigDecimal.ZERO))
                .totalFee(fees.getOrDefault("totalFee", BigDecimal.ZERO))
                .paymentMethod(order.getPaymentMethod())
                .build();
    }
}
