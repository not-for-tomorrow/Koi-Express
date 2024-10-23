package com.koi_express.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum OrderStatus {
    PENDING("Chờ xác nhận"), // Đơn hàng đang chờ xác nhận từ sales-staff
    ACCEPTED("Đã xác nhận"), // Đơn hàng đã được sales-staff xác nhận
    ASSIGNED("Đã phân công"), // Đơn hàng đã được gán cho nhân viên giao hàng
    PICKING_UP("Chuẩn bị lấy hàng"), // Nhân viên đang chuẩn bị đến lấy hàng
    IN_TRANSIT("Đang giao"), // Đơn hàng đang trong quá trình vận chuyển
    DELIVERED("Hoàn thành"), // Đơn hàng đã được giao
    CANCELED("Đã hủy"),

    COMMIT_FEE_PENDING("Chờ xác nhận phí cam kết"),
    COMMIT_FEE_PAID("Đã thanh toán phí cam kết"),
    PAYMENT_PENDING("Chờ xác nhận thanh toán");

    private final String vietnameseStatus;

    OrderStatus(String vietnameseStatus) {
        this.vietnameseStatus = vietnameseStatus;
    }

    public String getVietnameseStatus() {
        return vietnameseStatus;
    }

    // Return an OrderStatus by its Vietnamese translation
    public static OrderStatus fromVietnameseStatus(String vietnameseStatus) {
        return Arrays.stream(OrderStatus.values())
                .filter(status -> status.getVietnameseStatus().equalsIgnoreCase(vietnameseStatus))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No matching status for: " + vietnameseStatus));
    }

    // Get a list of all statuses with their Vietnamese descriptions
    public static List<String> getAllVietnameseStatuses() {
        return Arrays.stream(OrderStatus.values())
                .map(OrderStatus::getVietnameseStatus)
                .collect(Collectors.toList());
    }
}
