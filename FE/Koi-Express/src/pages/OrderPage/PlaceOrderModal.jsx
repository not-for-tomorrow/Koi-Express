import React from "react";

const PlaceOrderModal = ({ onClose, commitmentFee }) => {
  const handleConfirm = () => {
    // Open the payment page in a new tab
    window.open(
      "https://sandbox.vnpayment.vn/paymentv2/Payment/Error.html?code=15",
      "_blank"
    );
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      <div className="absolute inset-0 bg-gray-800 opacity-50"></div>
      <div className="relative z-10 p-6 bg-white rounded-lg shadow-lg">
        <h2 className="mb-4 text-xl font-semibold text-gray-800">
          Để đặt đơn thành công bạn phải thanh toán tiền cam kết
        </h2>
        <p className="mb-6 text-2xl font-bold text-orange-500">
          {Number(commitmentFee).toLocaleString("vi-VN", {
            style: "currency",
            currency: "VND",
          })}
        </p>
        <div className="flex justify-end space-x-4">
          <button
            onClick={onClose}
            className="px-4 py-2 text-gray-700 bg-gray-200 rounded-lg hover:bg-gray-300"
          >
            Hủy
          </button>
          <button
            onClick={handleConfirm}
            className="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
          >
            Thanh toán
          </button>
        </div>
      </div>
    </div>
  );
};

export default PlaceOrderModal;
