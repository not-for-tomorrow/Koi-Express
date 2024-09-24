package com.koi_express.enums;

public enum OrderStatus {
    PENDING,            // Đơn hàng mới được tạo
    ACCEPTED,           // Đơn hàng đã được chấp nhận
    PREPARING,          // Đang chuẩn bị đơn hàng (chưa đưa vào quá trình vận chuyển)
    IN_TRANSIT,         // Đang trên đường vận chuyển
    DELIVERED,          // Đã giao hàng (chưa hoàn thành)
    COMPLETED,          // Đơn hàng đã hoàn thành và xác nhận
    CANCELED,           // Đơn hàng bị hủy
    RETURNED            // Đơn hàng bị trả lại

}
