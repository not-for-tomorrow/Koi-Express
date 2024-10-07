package com.koi_express.enums;

public enum ShipmentStatus {
    PENDING, // Đơn hàng đang được xử lý
    PREPARING, // Đang chuẩn bị giao hàng
    DISPATCHED, // Đã được gửi đi từ kho
    IN_TRANSIT, // Đang trên đường vận chuyển
    OUT_FOR_DELIVERY, // Đang được giao đến khách hàng
    DELIVERED, // Đã giao hàng thành công
    FAILED_DELIVERY, // Giao hàng không thành công
    RETURNED // Hàng đã được trả lại
}
