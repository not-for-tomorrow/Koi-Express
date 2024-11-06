import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import RatingPopup from "./RatingPopup"; // Import the RatingPopup component

const OrderDetailModal = ({ orderId, distance }) => {
  const [order, setOrder] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isExpanded, setIsExpanded] = useState(false);
  const [isRatingPopupOpen, setIsRatingPopupOpen] = useState(false); // State to toggle RatingPopup

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

  useEffect(() => {
    const fetchOrderDetails = async () => {
      const token = localStorage.getItem("token");

      try {
        const response = await axios.get(
          `http://localhost:8080/api/orders/${orderId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setOrder(response.data.order);
      } catch (err) {
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchOrderDetails();
  }, [orderId]);

  const handleStarClick = () => {
    setIsRatingPopupOpen(true); // Open the rating popup
  };

  const handleRatingSubmit = (data) => {
    // Handle the submitted rating data here
    console.log("Submitted Rating:", data);
    setIsRatingPopupOpen(false); // Close the popup after submission
  };

  const handleRatingClose = () => {
    setIsRatingPopupOpen(false); // Close the popup without submission
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  if (error || !order) {
    return <div>Error: {error || "Order not found or missing data."}</div>;
  }

  const {
    orderDetail,
    status,
    paymentMethod,
    originLocation,
    destinationLocation,
    driverName, 
    driverRating, 
  } = order;

  const formattedDistanceFee = new Intl.NumberFormat("vi-VN").format(
    orderDetail?.distanceFee || 0
  );
  const formattedCommitmentFee = new Intl.NumberFormat("vi-VN").format(
    orderDetail?.commitmentFee || 0
  );

  const translatedStatus = statusMapping[status] || status;
  const statusColor = statusColors[translatedStatus] || {
    background: "#fff",
    text: "#000",
  };

  const toggleExpand = () => {
    setIsExpanded(!isExpanded);
  };

  return (
      <div className="relative z-20 flex flex-col w-full h-full max-w-lg p-6 bg-white border border-gray-200 shadow-lg">
        <div className="flex-grow">
          {/* Order Info Section */}
          <div className="mb-4">
            <div className="mb-4 text-2xl font-bold text-gray-800">
              Đơn hàng #{orderId}
            </div>

            <div className="p-4 mb-4 border rounded-lg bg-gray-50">
              <div className="flex justify-between">
                <p>
                  Tài xế{" "}
                  <span className="font-bold text-red-500">
    {order.deliveringStaff.fullName || "N/A"}
  </span>{" "}
                  đã hoàn tất đơn hàng. Hãy cho chúng tôi biết đánh giá của bạn nhé!
                </p>

              </div>

              <div className="flex justify-center mt-2 space-x-1 text-3xl">
                {[...Array(5)].map((_, index) => (
                    <span
                        key={index}
                        className="text-yellow-500 cursor-pointer"
                        onClick={handleStarClick} // Open the rating popup on star click
                    >
                  ⭐
                </span>
                ))}
              </div>
            </div>

            <div className="text-sm ">
              <strong className="text-gray-600">Lộ trình:</strong>{" "}
              {distance?.toFixed(2)} km
            </div>

            <div className="mt-6">
              {/* Sender Information */}
              <div className="flex items-start space-x-2">
                <div className="flex-shrink-0">
                  <div className="w-4 h-4 mt-1 bg-blue-500 rounded-full"></div>
                </div>
                <div>
                  <p className="text-lg font-bold">
                    {orderDetail?.senderName || "N/A"} •{" "}
                    <span className="text-base font-normal">
                    {orderDetail?.senderPhone || "N/A"}
                  </span>
                  </p>
                  <p className="text-sm text-gray-500">
                    {originLocation || "N/A"}
                  </p>
                </div>
              </div>

              {/* Recipient Information */}
              <div className="flex items-start mt-6 space-x-2">
                <div className="flex-shrink-0">
                  <div className="w-4 h-4 mt-1 bg-green-500 rounded-full"></div>
                </div>
                <div>
                  <p className="text-lg font-bold">
                    {orderDetail?.recipientName || "N/A"} •{" "}
                    <span className="text-base font-normal">
                    {orderDetail?.recipientPhone || "N/A"}
                  </span>
                  </p>
                  <p className="text-sm text-gray-500">
                    {destinationLocation || "N/A"}
                  </p>
                </div>
              </div>
            </div>
          </div>
        </div>

        {/* Koi Quantity and Size Section */}
        {orderDetail?.koiQuantity > 0 && (
            <div className="p-4 mt-4 border rounded-lg bg-gray-50">
              <div className="flex justify-between">
                <p>Số lượng cá</p>
                <p>{orderDetail?.koiQuantity}</p>
              </div>
              {/* Koi size is only displayed if it's a valid number greater than 0 */}
              {orderDetail?.koiSize > 0 && (
                  <div className="flex justify-between mt-2">
                    <p>Kích cỡ cá</p>
                    <p>{orderDetail?.koiSize}</p>
                  </div>
              )}
            </div>
        )}

        {/* Fee Breakdown Section */}
        <div className="p-4 mt-4 border rounded-lg bg-gray-50">
          {status === "DELIVERED" ? (
              // Display only totalFee when the status is DELIVERED
              <div className="flex justify-between">
                <p>Tổng phí</p>
                <p>{new Intl.NumberFormat("vi-VN").format(order.totalFee || 0)} VND</p>
              </div>
          ) : (
              // Otherwise, display both distance fee and commitment fee
              <>
                <div className="flex justify-between">
                  <p>Phí vận chuyển</p>
                  <p>{formattedDistanceFee} VND</p>
                </div>
                <div className="flex justify-between mt-2">
                  <p>Phí cam kết</p>
                  <p>{formattedCommitmentFee} VND</p>
                </div>
              </>
          )}
        </div>


        {/* Status & Payment Section */}
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

        {/* Button at the bottom */}
        <div className="flex-shrink-0 mt-6">
          <button
              className="w-full p-3 text-base font-semibold text-white transition-all transform bg-blue-500 rounded-lg hover:bg-blue-600">
            <Link to="/appkoiexpress/history">Đóng</Link>
          </button>
        </div>
        {/* Include the RatingPopup component and pass required props */}
        <RatingPopup
            isOpen={isRatingPopupOpen}
            onClose={handleRatingClose}
            onSubmit={handleRatingSubmit}
            orderId={orderId}
        />
      </div>
  );
};

export default OrderDetailModal;
