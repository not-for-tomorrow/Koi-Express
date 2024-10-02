package com.koi_express.enums;

public enum TransactionStatus {
    PAID,               // Giao dịch đã được thanh toán
    PENDING,            // Giao dịch đang chờ xử lý
    FAILED,             // Giao dịch thất bại
    REFUNDED,           // Giao dịch đã được hoàn trả
    CANCELED            // Giao dịch đã bị hủy
}
