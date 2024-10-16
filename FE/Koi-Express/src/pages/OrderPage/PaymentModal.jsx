import React, { useState, useEffect } from "react";
import { paymentMethods, cashPaymentMethods } from "./IconsData"; // Import your icons data

const PaymentModal = ({ onClose }) => {
  const [selectedMethod, setSelectedMethod] = useState(() => {
    // Retrieve the selected method from local storage (on initial render)
    const savedMethod = localStorage.getItem("selectedPaymentMethod");
    return savedMethod || ""; // Default to empty string if none found
  });

  // Save the selected method to localStorage whenever it changes
  useEffect(() => {
    if (selectedMethod) {
      localStorage.setItem("selectedPaymentMethod", selectedMethod);
    }
  }, [selectedMethod]);

  const handlePaymentMethodSelect = (methodLabel) => {
    setSelectedMethod(methodLabel); // Update state with selected method
    onClose(); // Optionally close modal when method is selected
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
      <div className="w-full max-w-[500px] bg-white rounded-lg shadow-lg">
        {/* Title row */}
        <div className="flex items-center justify-between p-4">
          <h2 className="w-full text-lg font-semibold text-center text-gray-900">
            Chọn phương thức thanh toán
          </h2>
          <button
            onClick={onClose}
            className="absolute text-gray-500 hover:text-gray-700 right-4 top-4"
          >
            ✕
          </button>
        </div>

        {/* Full-width horizontal line */}
        <hr className="w-full border-gray-300" />

        <div className="space-y-0">
          {/* Non-cash payments */}
          <div className="p-4 text-lg font-semibold text-gray-900">
            Thanh toán không dùng tiền mặt
          </div>

          {/* Full-width horizontal line */}
          <hr className="w-full border-gray-300" />

          {paymentMethods.map((method) => (
            <div
              key={method.label}
              className={`flex items-center justify-between w-full h-[72px] border-b border-gray-300 cursor-pointer hover:bg-gray-100 ${
                selectedMethod === method.label ? "border-t border-b border-orange-500" : ""
              }`}
              onClick={() => handlePaymentMethodSelect(method.label)}
            >
              <span className="flex items-center pl-4">
                {method.icon && (
                  <img
                    src={method.icon}
                    alt={method.label}
                    className="w-[35px] h-[35px] mr-2"
                  />
                )}
                {method.label}
              </span>
              <div className="pr-7">
                <input
                  type="radio"
                  checked={selectedMethod === method.label}
                  onChange={() => handlePaymentMethodSelect(method.label)}
                  className="w-[28px] h-[28px]"
                />
              </div>
            </div>
          ))}

          {/* Cash payments */}
          <div className="p-4 mt-4 text-sm font-semibold text-gray-700">
            Thanh toán bằng tiền mặt
          </div>

          {/* Full-width horizontal line */}
          <hr className="w-full border-gray-300" />

          {cashPaymentMethods.map((method) => (
            <div
              key={method.label}
              className={`flex items-center justify-between w-full h-[72px] border-b border-gray-300 cursor-pointer hover:bg-gray-100 ${
                selectedMethod === method.label ? "border-t border-b border-orange-500" : ""
              }`}
              onClick={() => handlePaymentMethodSelect(method.label)}
            >
              <span className="pl-4">{method.label}</span>
              <div className="pr-7">
                <input
                  type="radio"
                  checked={selectedMethod === method.label}
                  onChange={() => handlePaymentMethodSelect(method.label)}
                  className="w-[28px] h-[28px]"
                />
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

export default PaymentModal;
