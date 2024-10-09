package com.koi_express.enums;

public enum OrderStatus {
    PENDING,          // Đơn hàng đang chờ xác nhận từ sales-staff
    ACCEPTED,         // Đơn hàng đã được sales-staff xác nhận
    ASSIGNED,         // Đơn hàng đã được gán cho nhân viên giao hàng
    PICKING_UP,       // Nhân viên đang chuẩn bị đến lấy hàng
    IN_TRANSIT,       // Đơn hàng đang trong quá trình vận chuyển
    DELIVERED,        // Đơn hàng đã được giao
    CANCELED
}