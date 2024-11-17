import React, { useState } from "react";
import axios from "axios";
import DeliverOrderUpdate from "./DeliverOrderUpdate";
import PaymentModal from "./PaymentModal";
import VNPayLogo from "../../assets/images/LogoPayments/VNPay.png";
import Cashbysender from "../../assets/images/LogoPayments/Cashbysender.png";
import Cashbyrep from "../../assets/images/LogoPayments/Cashbyrep.png";

export const paymentMethods = [
  { label: "VNPAY", icon: VNPayLogo },
  { label: "Người gửi trả tiền", icon: Cashbysender },
  { label: "Người nhận trả tiền", icon: Cashbyrep },
];

export const getPaymentMethodIcon = (methodLabel) => {
  const method = paymentMethods.find((m) => m.label === methodLabel);
  return method ? method.icon : null;
};

const DeliverOrderModal = ({
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
                             koiQuantity,
                           }) => {
  const [showDetailPopup, setShowDetailPopup] = useState(false);
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(paymentMethod);
  const [remainingTransportationFee, setRemainingTransportationFee] = useState(null);

  // Move koiData and fishStatus state here
  const [koiData, setKoiData] = useState({
    KOI_VIET_NAM: [0, 0, 0],
    KOI_NHAT_BAN: [0, 0, 0],
    KOI_CHAU_AU: [0, 0, 0],
  });
  const [fishStatus, setFishStatus] = useState("HEALTHY");

  const handleSelectPaymentMethod = (methodLabel) => {
    setSelectedPaymentMethod(methodLabel);
  };

  const onSubmitSuccess = (feeData) => {
    setRemainingTransportationFee(feeData.totalFee);
    setShowDetailPopup(false);
  };

  const handleDeliveryClick = async () => {
    if (selectedPaymentMethod === "VNPAY" && remainingTransportationFee > 0) {
      try {
        const token = localStorage.getItem("token");
        const response = await axios.post(
            "http://localhost:8080/api/orders/confirm-payment",
            { paymentMethod: "VNPAY" },
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
        );

        if (response.data.result) {
          window.location.href = response.data.result;
        }
      } catch (error) {
        console.error("Failed to confirm payment:", error);
        alert("Đã xảy ra lỗi khi xác nhận thanh toán.");
      }
    } else if (remainingTransportationFee === 0) {
      alert("Tổng phí phải lớn hơn 0đ để có thể giao hàng.");
    }
  };

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
          Đơn hàng #{orderId}
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

      {/* Existing modal content */}
      <button onClick={() => setShowDetailPopup(true)}>
        <div className="p-4 mt-4 border rounded-lg bg-gray-50">
          <div className="flex justify-between">Chi tiết cá</div>
        </div>
      </button>

      {remainingTransportationFee !== null && (
        <div className="p-4 mt-4 border rounded-lg bg-gray-50">
          <div className="flex justify-between">
            <p>Tổng phí</p>
            <p>{remainingTransportationFee.toLocaleString()} VND</p>
          </div>
        </div>
      )}

      <div className="flex items-center justify-between p-2 mt-4 transition duration-300 bg-white rounded-lg shadow-md hover:shadow-lg">
        <div className="flex-grow sm:pr-4">
          <div
            className="p-2 bg-gray-100 rounded-lg cursor-pointer"
            onClick={() => setShowPaymentModal(true)}
          >
            <label className="block text-xs font-medium text-gray-600">
              Hình thức thanh toán
            </label>
            <div className="flex items-center mt-1 font-semibold text-gray-900">
              {getPaymentMethodIcon(selectedPaymentMethod) && (
                <img
                  src={getPaymentMethodIcon(selectedPaymentMethod)}
                  alt={selectedPaymentMethod}
                  className="w-[35px] h-[35px] mr-2"
                />
              )}
              <p>{selectedPaymentMethod || "N/A"}</p>
            </div>
          </div>
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
      </div>

      <div className="flex-shrink-0 mt-6">
        <button
          onClick={handleDeliveryClick}
          className="w-full p-3 text-base font-semibold text-white transition-all transform bg-blue-500 rounded-lg hover:bg-blue-600"
          disabled={remainingTransportationFee === 0} // Chỉ bật nút khi tổng phí khác 0
        >
          Giao Hàng
        </button>
      </div>

      {showDetailPopup && (
          <DeliverOrderUpdate
              koiQuantity={koiQuantity}
              koiData={koiData} // Pass koiData to the popup
              setKoiData={setKoiData} // Pass setKoiData to allow updates
              fishStatus={fishStatus} // Pass fishStatus to the popup
              setFishStatus={setFishStatus} // Pass setFishStatus to allow updates
              onClose={() => setShowDetailPopup(false)}
              onSubmitSuccess={onSubmitSuccess}
          />
      )}

      {showPaymentModal && (
        <PaymentModal
          onClose={() => setShowPaymentModal(false)}
          onSelectPaymentMethod={handleSelectPaymentMethod}
          currentPaymentMethod={selectedPaymentMethod}
        />
      )}
    </div>
  );
};

export default DeliverOrderModal;
