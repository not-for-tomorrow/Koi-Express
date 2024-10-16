// package com.koi_express.service.order;
//
// import com.koi_express.entity.order.Invoice;
// import com.koi_express.entity.order.Orders;
// import com.koi_express.enums.InvoiceStatus;
// import com.koi_express.enums.PaymentMethod;
// import com.koi_express.repository.InvoiceRepository;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Component;
//
// @Component
// public class CreateInvoice {
//
//    @Autowired
//    private InvoiceRepository invoiceRepository;
//
//    public void createInvoice(Orders order) {
//
//        Invoice invoice = Invoice.builder()
//                .order(order)
//                .customer(order.getCustomer())
//                .commitmentFee(order.getOrderDetail().getCommitmentFee())
//                .distanceFee(order.getOrderDetail().getDistanceFee())
//                .careFee(order.getOrderDetail().getCareFee())
//                .tollFee(order.getOrderDetail().getTollFee())
//                .weightFee(order.getOrderDetail().getWeightFee())
//                .packingFee(order.getOrderDetail().getPackingFee())
//                .storageFee(order.getOrderDetail().getStorageFee())
//                .returnFee(order.getOrderDetail().getReturnFee())
//                .vat(order.getOrderDetail().getVat())
//                .fuelFee(order.getOrderDetail().getFuelFee())
//                .insuranceFee(order.getOrderDetail().getInsuranceFee())
//                .totalAmount(order.getOrderDetail().calculateTotalAmount())
//                .status(InvoiceStatus.PAID)
//                .paymentMethod(PaymentMethod.VNPAY)
//                .build();
//
//        invoiceRepository.save(invoice);
//
//    }
// }
