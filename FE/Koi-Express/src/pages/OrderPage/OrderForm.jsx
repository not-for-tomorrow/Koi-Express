import React, { useState, useEffect } from "react";
import axios from "axios";
import HeaderOrderForm from "../../components/Header/HeaderOrderForm";

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
  handleContinue,
  pickupDetail,
  setPickupDetail,
  deliveryDetail,
  setDeliveryDetail,
  senderName,
  setSenderName,
  senderPhone,
  setSenderPhone,
  recipientName,
  setRecipientName,
  recipientPhone,
  setRecipientPhone,
  isPickupConfirmed,
  setIsPickupConfirmed,
  isDeliveryConfirmed,
  setIsDeliveryConfirmed,
}) => {
  const roundedCost = Math.ceil((distance * 20000) / 1000) * 1000;
  const [isFormValid, setIsFormValid] = useState(false);
  const [showPickupDetail, setShowPickupDetail] = useState(false);
  const [showDeliveryDetail, setShowDeliveryDetail] = useState(false);
  const [pickupCollapsed, setPickupCollapsed] = useState(isPickupConfirmed);
  const [deliveryCollapsed, setDeliveryCollapsed] =
    useState(isDeliveryConfirmed);

  useEffect(() => {
    setIsFormValid(
      pickupAddress &&
        deliveryAddress &&
        senderName &&
        senderPhone &&
        recipientName &&
        recipientPhone &&
        isPickupConfirmed &&
        isDeliveryConfirmed
    );
  }, [
    pickupAddress,
    deliveryAddress,
    senderName,
    senderPhone,
    recipientName,
    recipientPhone,
    isPickupConfirmed,
    isDeliveryConfirmed,
  ]);

  const onContinueClick = () => {
    handleContinue(roundedCost); // Pass the calculated price to OrderPage
  };

  const handlePickupConfirm = () => {
    setPickupCollapsed(true);
    setIsPickupConfirmed(true);
  };

  const handleDeliveryConfirm = () => {
    setDeliveryCollapsed(true);
    setIsDeliveryConfirmed(true);
  };

  const togglePickupCollapsed = () => {
    if (pickupCollapsed) {
      // Khi mở rộng (expanded), trạng thái "Xác nhận" sẽ bị đặt lại thành false
      setIsPickupConfirmed(false);
    }
    setPickupCollapsed(!pickupCollapsed);
  };

  const toggleDeliveryCollapsed = () => {
    if (deliveryCollapsed) {
      // Khi mở rộng (expanded), trạng thái "Xác nhận" sẽ bị đặt lại thành false
      setIsDeliveryConfirmed(false);
    }
    setDeliveryCollapsed(!deliveryCollapsed);
  };

  return (
    <div className="relative z-20 flex flex-col w-1/3 h-full p-6 bg-white border-r border-gray-200 shadow-lg">
      <HeaderOrderForm />
      <div className="flex-grow pr-4 overflow-y-auto">
        {deliveryCollapsed && distance > 0 && (
          <p className="mt-4 text-sm font-semibold text-gray-700">
            Lộ trình điểm giao: {distance.toFixed(2)} km
          </p>
        )}
        {/* Pickup Address Box */}
        <div
          className="p-4 mt-4 border rounded-lg shadow-inner cursor-pointer"
          onClick={
            pickupCollapsed && pickupAddress ? togglePickupCollapsed : undefined
          }
        >
          <label className="block mb-2 text-sm font-semibold text-gray-700">
            Địa chỉ lấy hàng
          </label>
          {pickupCollapsed ? (
            <>
              <p className="font-semibold">{pickupAddress.split(",")[0]}</p>
              <p>{pickupAddress.split(",").slice(1).join(", ")}</p>
            </>
          ) : (
            <>
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

              {pickupDetail ? (
                <p
                  className="mt-2 text-sm text-blue-500 cursor-pointer"
                  onClick={() => setShowPickupDetail(true)}
                >
                  Chi tiết địa chỉ:{" "}
                  <span className="font-semibold">{pickupDetail}</span>
                </p>
              ) : (
                <p
                  className="mt-2 text-sm text-blue-500 cursor-pointer"
                  onClick={() => setShowPickupDetail(true)}
                >
                  + Chi tiết địa chỉ
                </p>
              )}

              <div className="mt-4">
                <label className="block mb-2 text-sm font-semibold text-gray-700">
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
              <div className="flex justify-end mt-4 space-x-4">
                <button
                  className={`bg-blue-500 text-white px-4 py-2 rounded-lg ${
                    !pickupAddress ? "opacity-50 cursor-not-allowed" : ""
                  }`}
                  disabled={!pickupAddress}
                  onClick={handlePickupConfirm}
                >
                  Xác nhận
                </button>
              </div>
            </>
          )}
        </div>

        {/* Delivery Address Box */}
        <div
          className="p-4 mt-8 border rounded-lg shadow-inner cursor-pointer"
          onClick={
            deliveryCollapsed && deliveryAddress
              ? toggleDeliveryCollapsed
              : undefined
          }
        >
          <label className="block mb-2 text-sm font-semibold text-gray-700">
            Địa chỉ giao hàng
          </label>
          {deliveryCollapsed ? (
            <>
              <p className="font-semibold">{deliveryAddress.split(",")[0]}</p>
              <p>{deliveryAddress.split(",").slice(1).join(", ")}</p>
            </>
          ) : (
            <>
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

              {deliveryDetail ? (
                <p
                  className="mt-2 text-sm text-blue-500 cursor-pointer"
                  onClick={() => setShowDeliveryDetail(true)}
                >
                  Chi tiết địa chỉ:{" "}
                  <span className="font-semibold">{deliveryDetail}</span>
                </p>
              ) : (
                <p
                  className="mt-2 text-sm text-blue-500 cursor-pointer"
                  onClick={() => setShowDeliveryDetail(true)}
                >
                  + Chi tiết địa chỉ
                </p>
              )}

              <div className="mt-4">
                <label className="block mb-2 text-sm font-semibold text-gray-700">
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
              <div className="flex justify-end mt-4 space-x-4">
                <button
                  className={`bg-blue-500 text-white px-4 py-2 rounded-lg ${
                    !deliveryAddress ? "opacity-50 cursor-not-allowed" : ""
                  }`}
                  disabled={!deliveryAddress}
                  onClick={handleDeliveryConfirm}
                >
                  Xác nhận
                </button>
              </div>
            </>
          )}
        </div>
      </div>

      {distance > 0 && (
        <p className="mb-4 text-lg font-semibold text-center text-gray-900">
          {`Chi phí: ${new Intl.NumberFormat("vi-VN").format(roundedCost)} VND`}
        </p>
      )}

      {showPickupDetail && (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black bg-opacity-50">
          <div className="w-1/3 p-6 bg-white rounded-lg shadow-lg">
            <h2 className="mb-4 text-lg font-semibold text-gray-900">
              Chi tiết địa chỉ lấy hàng
            </h2>
            <input
              type="text"
              value={pickupDetail}
              onChange={(e) => setPickupDetail(e.target.value)}
              placeholder="Số nhà, hẻm, tòa nhà...."
              className="w-full p-3 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-4 focus:ring-blue-500"
            />
            <div className="flex justify-end mt-4 space-x-4">
              <button
                onClick={() => setShowPickupDetail(false)}
                className="px-4 py-2 text-gray-700 bg-gray-300 rounded-lg"
              >
                Hủy
              </button>
              <button
                onClick={() => setShowPickupDetail(false)}
                className="px-4 py-2 text-white bg-blue-500 rounded-lg"
              >
                Xác nhận
              </button>
            </div>
          </div>
        </div>
      )}

      {showDeliveryDetail && (
        <div className="fixed inset-0 z-[9999] flex items-center justify-center bg-black bg-opacity-50">
          <div className="w-1/3 p-6 bg-white rounded-lg shadow-lg">
            <h2 className="mb-4 text-lg font-semibold text-gray-900">
              Chi tiết địa chỉ giao hàng
            </h2>
            <input
              type="text"
              value={deliveryDetail}
              onChange={(e) => setDeliveryDetail(e.target.value)}
              placeholder="Số nhà, hẻm, tòa nhà...."
              className="w-full p-3 bg-white border border-gray-300 rounded-lg focus:outline-none focus:ring-4 focus:ring-blue-500"
            />
            <div className="flex justify-end mt-4 space-x-4">
              <button
                onClick={() => setShowDeliveryDetail(false)}
                className="px-4 py-2 text-gray-700 bg-gray-300 rounded-lg"
              >
                Hủy
              </button>
              <button
                onClick={() => setShowDeliveryDetail(false)}
                className="px-4 py-2 text-white bg-blue-500 rounded-lg"
              >
                Xác nhận
              </button>
            </div>
          </div>
        </div>
      )}

      <button
        className={`w-full p-3 rounded-lg text-base font-semibold transition-all transform ${
          isFormValid
            ? "bg-blue-500 text-white hover:bg-blue-600 hover:scale-105"
            : "bg-gray-300 text-gray-600 cursor-not-allowed"
        }`}
        disabled={!isFormValid}
        onClick={onContinueClick}
      >
        Tiếp tục
      </button>
    </div>
  );
};

export default OrderForm;
