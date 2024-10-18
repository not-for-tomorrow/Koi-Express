import React, { useState, useEffect } from "react";
import { paymentMethods, cashPaymentMethods } from "./IconsData"; // Import your icons data

const PaymentModal = ({
  onClose,
  onSelectPaymentMethod,
  currentPaymentMethod,
}) => {
  const [selectedMethod, setSelectedMethod] = useState(
    currentPaymentMethod || ""
  ); // Use the passed value or default to an empty string

  useEffect(() => {
    if (selectedMethod) {
      localStorage.setItem("selectedPaymentMethod", selectedMethod); // Save the selected method in local storage
    }
  }, [selectedMethod]);

  const handlePaymentMethodSelect = (methodLabel) => {
    setSelectedMethod(methodLabel); // Update the selected method in the modal
    onSelectPaymentMethod(methodLabel); // Pass the selected method to the parent component
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Full-page dark background */}
      <div
        className="fixed inset-0 z-40 bg-black bg-opacity-50"
        onClick={onClose}
      ></div>

      {/* Modal content */}
      <div className="relative z-50 w-[500px] h-max-[660px] bg-white rounded-lg shadow-lg overflow-y-auto">
        {" "}
        {/* Set exact size */}
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
        <hr className="w-full border-gray-300" />
        <div className="space-y-0">
          <div className="p-4 text-lg font-semibold text-gray-900">
            Thanh toán không dùng tiền mặt
          </div>

          <hr className="w-full border-gray-300" />

          {/* Payment Methods */}
          {paymentMethods.map((method) => (
            <div
              key={method.label}
              className={`flex items-center justify-between w-full h-[72px] border-b border-gray-300 cursor-pointer hover:bg-gray-100 ${
                selectedMethod === method.label
                  ? "border-t border-b border-orange-500"
                  : ""
              }`}
              onClick={() => handlePaymentMethodSelect(method.label)}
            >
              <span className="flex items-center pl-4 font-semibold">
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
                  name="payment-method"
                  value={method.label}
                  checked={selectedMethod === method.label} // Ensure checked is tied to selectedMethod
                  onChange={() => handlePaymentMethodSelect(method.label)}
                  className="w-[22px] h-[22px]"
                />
              </div>
            </div>
          ))}

          <div className="p-4 mt-4 text-lg font-semibold text-gray-900">
            Thanh toán bằng tiền mặt
          </div>

          <hr className="w-full border-gray-300" />

          {/* Cash Payment Methods */}
          {cashPaymentMethods.map((method) => (
            <div
              key={method.label}
              className={`flex items-center justify-between w-full h-[72px] border-b border-gray-300 cursor-pointer hover:bg-gray-100 ${
                selectedMethod === method.label
                  ? "border-t border-b border-orange-500"
                  : ""
              }`}
              onClick={() => handlePaymentMethodSelect(method.label)}
            >
              <span className="flex items-center pl-4 font-semibold">
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
                  name="payment-method"
                  value={method.label}
                  checked={selectedMethod === method.label} // Ensure checked is tied to selectedMethod
                  onChange={() => handlePaymentMethodSelect(method.label)}
                  className="w-[22px] h-[22px]"
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
