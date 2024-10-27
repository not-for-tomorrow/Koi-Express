export const statusMapping = {
    PENDING: "Chờ xác nhận",
    ACCEPTED: "Đã xác nhận",
    ASSIGNED: "Đã phân công",
    PICKING_UP: "Chuẩn bị lấy hàng",
    IN_TRANSIT: "Đang giao",
    DELIVERED: "Hoàn thành",
    CANCELED: "Đã hủy",
    COMMIT_FEE_PENDING: "Chờ thanh toán cam kết",
};

export const statusColors = {
    "Chờ xác nhận": { background: "rgba(254, 240, 138, 0.2)", text: "#854D0E" },
    "Đã xác nhận": { background: "rgba(233, 213, 255, 0.2)", text: "#2c2c54" },
    "Đã phân công": { background: "rgba(253, 230, 138, 0.2)", text: "#CA8A04" },
    "Chuẩn bị lấy hàng": { background: "rgba(153, 246, 228, 0.2)", text: "#0D9488" },
    "Đang giao": { background: "rgba(191, 219, 254, 0.2)", text: "#1E3A8A" },
    "Hoàn thành": { background: "rgba(187, 247, 208, 0.2)", text: "#065F46" },
    "Đã hủy": { background: "rgba(254, 202, 202, 0.2)", text: "#c0392b" },
    "Chờ thanh toán cam kết": { background: "rgba(255, 199, 199, 0.2)", text: "#C0392B" },
};

export const getTranslatedStatus = (status) => statusMapping[status] || "Unknown";
export const getStatusColor = (translatedStatus) => statusColors[translatedStatus] || {};
