package com.koi_express.service.order.builder;

import java.math.BigDecimal;
import java.util.Map;

import com.koi_express.entity.order.Invoice;
import com.koi_express.entity.order.Orders;
import com.koi_express.repository.InvoiceRepository;
import org.springframework.stereotype.Service;

@Service
public class InvoiceBuilder {

    private final InvoiceRepository invoiceRepository;

    public InvoiceBuilder(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    public void updateInvoice(Orders order, Map<String, BigDecimal> fees) {
        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setCustomer(order.getCustomer());
        invoice.setCommitmentFee(order.getOrderDetail().getCommitmentFee());
        invoice.setDistanceFee(order.getOrderDetail().getDistanceFee());
        invoice.setCareFee(order.getOrderDetail().getCareFee());
        invoice.setPackagingFee(order.getOrderDetail().getPackagingFee());
        invoice.setReturnFee(order.getOrderDetail().getReturnFee());
        invoice.setVat(order.getOrderDetail().getVat());
        invoice.setKoiFee(order.getOrderDetail().getKoiFee());
        invoice.setInsuranceFee(order.getOrderDetail().getInsuranceFee());
        invoice.setTotalFee(fees.get("totalPrice"));
        invoice.setPaymentMethod(order.getPaymentMethod());
        invoiceRepository.save(invoice);
    }
}
