package com.koi_express.enums;

public enum OrderStatus {
    PENDING,            // Đơn hàng mới được tạo
    ACCEPTED,           // Đơn hàng đã được chấp nhận
    IN_TRANSIT,         // Đang trên đường vận chuyển
    COMPLETED,          // Đơn hàng đã hoàn thành và xác nhận
    CANCELED,           // Đơn hàng bị hủy

}
