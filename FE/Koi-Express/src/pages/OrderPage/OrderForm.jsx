import React, { useState, useEffect } from "react";
import axios from "axios";
import ReactDOM from "react-dom";

const OrderForm = ({
  pickupAddress,
  setPickupAddress,
  deliveryAddress,
  setDeliveryAddress,
  pickupSuggestions,
  deliverySuggestions,
  handleAddressChange,
  handleSelect,
  distance,
}) => {
  const [isFormValid, setIsFormValid] = useState(false);
  const [showPickupDetail, setShowPickupDetail] = useState(false);
  const [showDeliveryDetail, setShowDeliveryDetail] = useState(false);
  const [pickupDetail, setPickupDetail] = useState("");
  const [deliveryDetail, setDeliveryDetail] = useState("");
  const [senderName, setSenderName] = useState("");
  const [senderPhone, setSenderPhone] = useState("");
  const [recipientName, setRecipientName] = useState("");
  const [recipientPhone, setRecipientPhone] = useState("");

  useEffect(() => {
    setIsFormValid(
      pickupAddress &&
        deliveryAddress &&
        senderName &&
        senderPhone &&
        recipientName &&
        recipientPhone
    );
  }, [
    pickupAddress,
    deliveryAddress,
    senderName,
    senderPhone,
    recipientName,
    recipientPhone,
  ]);

  const handleOrder = async () => {
    const orderData = {
      pickupAddress,
      pickupDetail,
      deliveryAddress,
      deliveryDetail,
      senderName,
      senderPhone,
      recipientName,
      recipientPhone,
      cost: distance * 10000, // Tính toán chi phí dựa trên khoảng cách
    };

    try {
      const response = await axios.post(
        "http://localhost:8080/api/orders/create",
        orderData
      );
      if (response.status === 201) {
        console.log("Order created successfully", response.data);
      }
    } catch (error) {
      console.error("Error creating order:", error);
    }
  };

  return (
    <div className="w-1/3 p-10 overflow-y-auto bg-white border-r border-gray-200 shadow-lg relative z-20">
      <h2 className="text-xl font-extrabold text-gray-900">Đơn hàng mới</h2>

      {/* Pickup Address Box */}
      <div className="mt-8 border p-4 rounded-lg shadow-inner">
        <label className="block mb-2 font-semibold text-sm text-gray-700">
          Địa chỉ lấy hàng
        </label>
        <input
          type="text"
          value={pickupAddress}
          onChange={(e) => handleAddressChange(e, true)}
          placeholder="Chọn địa điểm"
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
        />
        <div className="mt-2 overflow-y-auto bg-white border rounded-lg shadow-md max-h-60">
          {pickupSuggestions.map((suggestion) => (
            <div
              key={suggestion.place_id}
              className="p-3 transition-colors cursor-pointer hover:bg-gray-100"
              onClick={() => handleSelect(suggestion, true)}
            >
              <span className="text-sm">{suggestion.display_name}</span>
            </div>
          ))}
        </div>
        {/* Thay đổi nút hiển thị chi tiết địa chỉ */}
        {pickupDetail ? (
          <p
            className="text-blue-500 text-sm mt-2 cursor-pointer"
            onClick={() => setShowPickupDetail(true)}
          >
            Chi tiết địa chỉ:{" "}
            <span className="font-semibold">{pickupDetail}</span>
          </p>
        ) : (
          <p
            className="text-blue-500 text-sm mt-2 cursor-pointer"
            onClick={() => setShowPickupDetail(true)}
          >
            + Chi tiết địa chỉ
          </p>
        )}

        {/* Thêm các trường Tên người gửi và Số điện thoại */}
        <div className="mt-4">
          <label className="block mb-2 font-semibold text-sm text-gray-700">
            Tên người gửi và Số điện thoại
          </label>
          <div className="flex space-x-4">
            <input
              type="text"
              value={senderName}
              onChange={(e) => setSenderName(e.target.value)}
              placeholder="Tên người gửi"
              className="w-1/2 p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
            />
            <input
              type="text"
              value={senderPhone}
              onChange={(e) => setSenderPhone(e.target.value)}
              placeholder="Số điện thoại"
              className="w-1/2 p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
            />
          </div>
        </div>
      </div>

      {/* Delivery Address Box */}
      <div className="mt-8 border p-4 rounded-lg shadow-inner">
        <label className="block mb-2 font-semibold text-sm text-gray-700">
          Địa chỉ giao hàng
        </label>
        <input
          type="text"
          value={deliveryAddress}
          onChange={(e) => handleAddressChange(e, false)}
          placeholder="Chọn địa điểm"
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
        />
        <div className="mt-2 overflow-y-auto bg-white border rounded-lg shadow-md max-h-60">
          {deliverySuggestions.map((suggestion) => (
            <div
              key={suggestion.place_id}
              className="p-3 transition-colors cursor-pointer hover:bg-gray-100"
              onClick={() => handleSelect(suggestion, false)}
            >
              <span className="text-sm">{suggestion.display_name}</span>
            </div>
          ))}
        </div>

        {/* Thay đổi nút hiển thị chi tiết địa chỉ */}
        {deliveryDetail ? (
          <p
            className="text-blue-500 text-sm mt-2 cursor-pointer"
            onClick={() => setShowDeliveryDetail(true)}
          >
            Chi tiết địa chỉ:{" "}
            <span className="font-semibold">{deliveryDetail}</span>
          </p>
        ) : (
          <p
            className="text-blue-500 text-sm mt-2 cursor-pointer"
            onClick={() => setShowDeliveryDetail(true)}
          >
            + Chi tiết địa chỉ
          </p>
        )}

        {/* Thêm các trường Tên người nhận và Số điện thoại */}
        <div className="mt-4">
          <label className="block mb-2 font-semibold text-sm text-gray-700">
            Tên người nhận và Số điện thoại
          </label>
          <div className="flex space-x-4">
            <input
              type="text"
              value={recipientName}
              onChange={(e) => setRecipientName(e.target.value)}
              placeholder="Tên người nhận"
              className="w-1/2 p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
            />
            <input
              type="text"
              value={recipientPhone}
              onChange={(e) => setRecipientPhone(e.target.value)}
              placeholder="Số điện thoại"
              className="w-1/2 p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
            />
          </div>
        </div>
      </div>

      {/* Pickup Detail Modal */}
      {showPickupDetail &&
        ReactDOM.createPortal(
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-70 z-[9999]">
            <div className="bg-white p-8 rounded-lg shadow-lg w-1/3 z-[10000]">
              <h3 className="text-lg font-bold mb-4">
                Chi tiết địa chỉ lấy hàng
              </h3>
              <input
                type="text"
                value={pickupDetail}
                onChange={(e) => setPickupDetail(e.target.value)}
                placeholder="Số nhà, hẻm, tòa nhà..."
                className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
              />
              <div className="flex justify-end space-x-4 mt-6">
                <button
                  className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg"
                  onClick={() => setShowPickupDetail(false)}
                >
                  Hủy
                </button>
                <button
                  className="bg-blue-500 text-white px-4 py-2 rounded-lg"
                  onClick={() => {
                    setShowPickupDetail(false);
                  }}
                >
                  Xác nhận
                </button>
              </div>
            </div>
          </div>,
          document.body
        )}

      {/* Delivery Detail Modal */}
      {showDeliveryDetail &&
        ReactDOM.createPortal(
          <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-70 z-[9999]">
            <div className="bg-white p-8 rounded-lg shadow-lg w-1/3 z-[10000]">
              <h3 className="text-lg font-bold mb-4">
                Chi tiết địa chỉ giao hàng
              </h3>
              <input
                type="text"
                value={deliveryDetail}
                onChange={(e) => setDeliveryDetail(e.target.value)}
                placeholder="Số nhà, hẻm, tòa nhà..."
                className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
              />
              <div className="flex justify-end space-x-4 mt-6">
                <button
                  className="bg-gray-300 text-gray-700 px-4 py-2 rounded-lg"
                  onClick={() => setShowDeliveryDetail(false)}
                >
                  Hủy
                </button>
                <button
                  className="bg-blue-500 text-white px-4 py-2 rounded-lg"
                  onClick={() => {
                    setShowDeliveryDetail(false);
                  }}
                >
                  Xác nhận
                </button>
              </div>
            </div>
          </div>,
          document.body
        )}

      {/* Button to Confirm Order */}
      <div className="flex mt-10 space-x-4">
        <button
          className={`w-full p-3 rounded-lg text-base font-semibold transition-all transform ${
            isFormValid
              ? "bg-blue-500 text-white hover:bg-blue-600 hover:scale-105"
              : "bg-gray-300 text-gray-600 cursor-not-allowed"
          }`}
          disabled={!isFormValid}
          onClick={handleOrder}
        >
          Tiếp tục
        </button>
      </div>
    </div>
  );
};

export default OrderForm;
