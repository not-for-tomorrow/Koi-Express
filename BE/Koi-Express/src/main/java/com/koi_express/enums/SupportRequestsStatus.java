package com.koi_express.enums;

public enum SupportRequestsStatus {
    PENDING,             // Yêu cầu đang chờ xử lý
    IN_PROGRESS,         // Yêu cầu đang được xử lý
    ESCALATED,           // Yêu cầu đã được nâng cấp để xử lý bởi cấp cao hơn
    COMPLETED,           // Yêu cầu đã hoàn tất
    REJECTED,            // Yêu cầu bị từ chối
    CANCELED             // Yêu cầu đã bị hủy
}
