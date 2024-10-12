package com.koi_express.enums;

public enum OrderStatus {
    PENDING("Chờ xác nhận"), // Đơn hàng đang chờ xác nhận từ sales-staff
    ACCEPTED("Đã xác nhận"), // Đơn hàng đã được sales-staff xác nhận
    ASSIGNED("Đã phân công"), // Đơn hàng đã được gán cho nhân viên giao hàng
    PICKING_UP("Chuẩn bị lấy hàng"), // Nhân viên đang chuẩn bị đến lấy hàng
    IN_TRANSIT("Đang giao"), // Đơn hàng đang trong quá trình vận chuyển
    DELIVERED("Hoàn thành"), // Đơn hàng đã được giao
    CANCELED("Đã hủy");

    private final String vietnameseStatus;

    OrderStatus(String vietnameseStatus) {
        this.vietnameseStatus = vietnameseStatus;
    }

    public String getVietnameseStatus() {
        return vietnameseStatus;
    }
}
