import React, { useState, useEffect } from 'react';
import axios from 'axios';

const OrderForm = ({ 
  pickupAddress, setPickupAddress, 
  deliveryAddress, setDeliveryAddress, 
  recipientName, setRecipientName, 
  phoneNumber, setPhoneNumber, 
  additionalInfo, setAdditionalInfo, 
  pickupSuggestions, deliverySuggestions, 
  handleAddressChange, handleSelect, distance 
}) => {

  const [isFormValid, setIsFormValid] = useState(false);

  useEffect(() => {
    setIsFormValid(pickupAddress && deliveryAddress && recipientName && phoneNumber);
  }, [pickupAddress, deliveryAddress, recipientName, phoneNumber]);

  return (
    <div className="w-1/3 p-10 overflow-y-auto bg-white border-r border-gray-200 shadow-lg">
      <h2 className="text-xl font-extrabold text-gray-900">Đơn hàng mới</h2> {/* Reduced from text-3xl to text-xl */}

      {/* Pickup Address Input */}
      <div className="mt-8">
        <label className="block mb-2 font-semibold text-sm text-gray-700">Địa chỉ lấy hàng</label> {/* Reduced font size */}
        <input
          type="text"
          value={pickupAddress}
          onChange={(e) => handleAddressChange(e, true)}
          placeholder="Chọn địa điểm"
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
        />
        {/* Suggestions List */}
        <div className="mt-2 overflow-y-auto bg-white border rounded-lg shadow-md max-h-60">
          {pickupSuggestions.map((suggestion) => (
            <div
              key={suggestion.place_id}
              className="p-3 transition-colors cursor-pointer hover:bg-gray-100"
              onClick={() => handleSelect(suggestion, true)}
            >
              <span className="text-sm">{suggestion.display_name}</span> {/* Reduced font size */}
            </div>
          ))}
        </div>
      </div>

      {/* Delivery Address Input */}
      <div className="mt-8">
        <label className="block mb-2 font-semibold text-sm text-gray-700">Địa chỉ giao hàng</label> {/* Reduced font size */}
        <input
          type="text"
          value={deliveryAddress}
          onChange={(e) => handleAddressChange(e, false)}
          placeholder="Chọn địa điểm"
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
        />
        {/* Suggestions List */}
        <div className="mt-2 overflow-y-auto bg-white border rounded-lg shadow-md max-h-60">
          {deliverySuggestions.map((suggestion) => (
            <div
              key={suggestion.place_id}
              className="p-3 transition-colors cursor-pointer hover:bg-gray-100"
              onClick={() => handleSelect(suggestion, false)}
            >
              <span className="text-sm">{suggestion.display_name}</span> {/* Reduced font size */}
            </div>
          ))}
        </div>
      </div>

      {/* Cost Calculation */}
      <div className="mt-8">
        <p className="font-semibold text-sm text-gray-700">Chi phí: {distance * 10000} đồng</p> {/* Reduced font size */}
      </div>

      {/* Recipient Information */}
      <div className="mt-8">
        <label className="block mb-2 font-semibold text-sm text-gray-700">Tên người nhận</label> {/* Reduced font size */}
        <input
          type="text"
          value={recipientName}
          onChange={(e) => setRecipientName(e.target.value)}
          placeholder="Nhập tên người nhận"
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
        />
      </div>

      <div className="mt-8">
        <label className="block mb-2 font-semibold text-sm text-gray-700">Số điện thoại</label> {/* Reduced font size */}
        <input
          type="text"
          value={phoneNumber}
          onChange={(e) => setPhoneNumber(e.target.value)}
          placeholder="Nhập số điện thoại"
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
        />
      </div>

      <div className="mt-8">
        <label className="block mb-2 font-semibold text-sm text-gray-700">Thông tin thêm</label> {/* Reduced font size */}
        <textarea
          value={additionalInfo}
          onChange={(e) => setAdditionalInfo(e.target.value)}
          placeholder="Nhập các yêu cầu đặc biệt về giao hàng..."
          className="w-full p-3 mt-1 transition-all bg-white border border-gray-300 rounded-lg shadow-inner focus:outline-none focus:ring-4 focus:ring-blue-500"
          rows="4"
        />
      </div>

      <div className="flex mt-10 space-x-4">
        <button
          className={`w-full p-3 rounded-lg text-base font-semibold transition-all transform ${
            isFormValid
              ? 'bg-blue-500 text-white hover:bg-blue-600 hover:scale-105'
              : 'bg-gray-300 text-gray-600 cursor-not-allowed'
          }`}
          disabled={!isFormValid}
        >
          Tiếp tục
        </button>
      </div>
    </div>
  );
};

export default OrderForm;
