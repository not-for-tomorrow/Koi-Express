import React from "react";
import { Link } from "react-router-dom";
import {
  getTranslatedStatus,
  getStatusColor,
} from "/src/koi/utils/statusUtils.js";
import { acceptOrderAPI } from "/src/koi/api/api.js";
import { useNavigate } from "react-router-dom";

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
  onClose,
}) => {
  const navigate = useNavigate();
  const translatedStatus = getTranslatedStatus(status);
  const statusColor = getStatusColor(translatedStatus);

  const handleAcceptOrder = async () => {
    try {
      await acceptOrderAPI(orderId);
      alert("Order accepted successfully!");
      navigate("/salepage");
      if (onClose) onClose(); // Optional: Close modal if onClose is passed
    } catch (error) {
      alert("Failed to accept the order. Please try again.");
      console.error(error);
    }
  };

  const handleCloseModal = () => {
    navigate("/salepage"); // Sử dụng navigate để chuyển trang
  };

  return (
    <div className="relative z-20 flex flex-col w-full h-full max-w-lg p-6 bg-white border border-gray-200 shadow-lg">
      <div className="flex-grow">
        <div className="mb-4 text-2xl font-bold text-gray-800">
          Đơn hàng #{orderId} của {fullName}
        </div>

        <div className="mb-6 text-sm">
          <strong className="text-gray-600">Lộ trình:</strong> {distance}
        </div>

        <div className="mt-6">
          <div className="flex items-start space-x-2">
            <div className="w-4 h-4 mt-1 bg-blue-500 rounded-full"></div>
            <div>
              <p className="text-lg font-bold">
                {senderName} • <span>{senderPhone}</span>
              </p>
              <p className="text-sm text-gray-500">{originLocation}</p>
            </div>
          </div>

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

      <div className="flex flex-shrink-0 mt-6 space-x-2">
        <button
          onClick={handleAcceptOrder}
          className="w-1/2 p-3 text-base font-semibold text-white transition-all transform bg-green-500 rounded-lg hover:bg-green-600"
        >
          Duyệt Đơn
        </button>

        <button
          onClick={handleCloseModal}
          className="w-1/2 p-3 text-base font-semibold text-white transition-all transform bg-blue-500 rounded-lg hover:bg-blue-600"
        >
          Đóng
        </button>
      </div>
    </div>
  );
};

export default OrderDetailModal;
