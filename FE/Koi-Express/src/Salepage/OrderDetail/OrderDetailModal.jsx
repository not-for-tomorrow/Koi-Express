import React from "react";
import { Link } from "react-router-dom";

const OrderDetailModal = ({
  orderId,
  fullName,
  originLocation,
  destinationLocation,
  senderName,
  senderPhone,
  recipientName,
  recipientPhone,
  distance,
  status,
  paymentMethod,
  distanceFee,
  commitmentFee,
  deliveringStaff, 
}) => {
  const statusMapping = {
    PENDING: "Chờ xác nhận",
    ACCEPTED: "Đã xác nhận",
    ASSIGNED: "Đã phân công",
    PICKING_UP: "Chuẩn bị lấy hàng",
    IN_TRANSIT: "Đang giao",
    DELIVERED: "Hoàn thành",
    CANCELED: "Đã hủy",
    COMMIT_FEE_PENDING: "Chờ thanh toán cam kết",
  };

  const statusColors = {
    "Chờ xác nhận": { background: "rgba(254, 240, 138, 0.2)", text: "#854D0E" },
    "Đã xác nhận": { background: "rgba(233, 213, 255, 0.2)", text: "#2c2c54" },
    "Đã phân công": { background: "rgba(253, 230, 138, 0.2)", text: "#CA8A04" },
    "Chuẩn bị lấy hàng": {
      background: "rgba(153, 246, 228, 0.2)",
      text: "#0D9488",
    },
    "Đang giao": { background: "rgba(191, 219, 254, 0.2)", text: "#1E3A8A" },
    "Hoàn thành": { background: "rgba(187, 247, 208, 0.2)", text: "#065F46" },
    "Đã hủy": { background: "rgba(254, 202, 202, 0.2)", text: "#c0392b" },
    "Chờ thanh toán cam kết": {
      background: "rgba(255, 199, 199, 0.2)",
      text: "#C0392B",
    },
  };

  const translatedStatus = statusMapping[status] || "Unknown";
  const statusColor = statusColors[translatedStatus] || {};

  return (
    <div className="relative z-20 flex flex-col w-full h-full max-w-lg p-6 bg-white border border-gray-200 shadow-lg">
      <div className="flex-grow">
        <div className="mb-4 text-2xl font-bold text-gray-800">
          Đơn hàng #{orderId} của {fullName}
        </div>

        {/* Driver Information Section */}
        {deliveringStaff && (
          <div className="p-4 mb-4 border rounded-lg bg-gray-50">
            <h3 className="mb-2 text-lg font-semibold">Thông tin tài xế</h3>
            <p>
              <strong>Họ tên:</strong> {deliveringStaff.fullName}
            </p>
            <p>
              <strong>Số điện thoại:</strong> {deliveringStaff.phoneNumber}
            </p>
            <p>
              <strong>Email:</strong> {deliveringStaff.email}
            </p>
          </div>
        )}

        <div className="mb-6 text-sm">
          <strong className="text-gray-600">Lộ trình:</strong> {distance}
        </div>

        <div className="mt-6">
          {/* Sender Information */}
          <div className="flex items-start space-x-2">
            <div className="w-4 h-4 mt-1 bg-blue-500 rounded-full"></div>
            <div>
              <p className="text-lg font-bold">
                {senderName} • <span>{senderPhone}</span>
              </p>
              <p className="text-sm text-gray-500">{originLocation}</p>
            </div>
          </div>

          {/* Recipient Information */}
          <div className="flex items-start mt-6 space-x-2">
            <div className="w-4 h-4 mt-1 bg-green-500 rounded-full"></div>
            <div>
              <p className="text-lg font-bold">
                {recipientName} • <span>{recipientPhone}</span>
              </p>
              <p className="text-sm text-gray-500">{destinationLocation}</p>
            </div>
          </div>
        </div>
      </div>

      <div className="p-4 mt-4 border rounded-lg bg-gray-50">
        <div className="flex justify-between">
          <p>Phí vận chuyển</p>
          <p>{distanceFee.toLocaleString()} VND</p>
        </div>
        <div className="flex justify-between mt-2">
          <p>Phí cam kết</p>
          <p>{commitmentFee.toLocaleString()} VND</p>
        </div>
      </div>

      <div className="mt-4 text-sm text-gray-600">
        <p>
          <strong>Trạng thái:</strong>{" "}
          <span
            style={{
              backgroundColor: statusColor.background,
              color: statusColor.text,
              padding: "2px 8px",
              borderRadius: "4px",
            }}
          >
            {translatedStatus}
          </span>
        </p>
        <p className="mt-3">
          <strong>Phương thức thanh toán:</strong> {paymentMethod || "N/A"}
        </p>
      </div>

      <div className="flex-shrink-0 mt-6">
        <button className="w-full p-3 text-base font-semibold text-white transition-all transform bg-blue-500 rounded-lg hover:bg-blue-600">
          <Link to="/salepage/allorder">Đóng</Link>
        </button>
      </div>
    </div>
  );
};

export default OrderDetailModal;
