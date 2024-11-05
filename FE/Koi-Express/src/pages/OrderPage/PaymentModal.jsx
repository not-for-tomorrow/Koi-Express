import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { paymentMethods } from "./IconsData";

const PaymentModal = ({
                        onClose,
                        onSelectPaymentMethod,
                        currentPaymentMethod,
                      }) => {
  const navigate = useNavigate();
  const [selectedMethod, setSelectedMethod] = useState(
      currentPaymentMethod || localStorage.getItem("selectedPaymentMethod") || ""
  );

  useEffect(() => {
    if (selectedMethod) {
      localStorage.setItem("selectedPaymentMethod", selectedMethod);
    }
  }, [selectedMethod]);

  const handlePaymentMethodSelect = (methodLabel) => {
    setSelectedMethod(methodLabel);
    onSelectPaymentMethod(methodLabel);
  };

  const handlePaymentSuccess = () => {
    // Redirect to the payment success page
    navigate("/payment-successful");
  };

  const handleConfirmPayment = async () => {
    try {
      // Here, you would typically make an API call or initiate the payment process
      // Assuming the payment is successful, we call handlePaymentSuccess
      handlePaymentSuccess();
    } catch (error) {
      console.error("Payment failed:", error);
      alert("Payment failed, please try again.");
    }
  };

  return (
      <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="fixed inset-0 z-40 bg-black bg-opacity-50" onClick={onClose}></div>

        <div className="relative z-50 w-[450px] max-h-[660px] bg-white rounded-lg shadow-lg overflow-y-auto">
          <div className="flex items-center justify-between p-4">
            <h2 className="w-full text-lg font-semibold text-center text-gray-900">
              Chọn phương thức thanh toán
            </h2>
            <button onClick={onClose} className="absolute text-gray-500 hover:text-gray-700 right-4 top-4">
              ✕
            </button>
          </div>
          <hr className="w-full border-gray-300" />

          <div className="space-y-0">
            <div className="p-4 text-lg font-semibold text-gray-900">
              Thanh toán không dùng tiền mặt
            </div>
            <hr className="w-full border-gray-300" />

            {paymentMethods.map((method) => (
                <div
                    key={method.label}
                    className={`flex items-center justify-between w-full h-[72px] border-b cursor-pointer hover:bg-gray-100 ${
                        selectedMethod === method.label ? "border-t border-b border-orange-500" : "border-gray-300"
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
                        checked={selectedMethod === method.label}
                        onChange={() => handlePaymentMethodSelect(method.label)}
                        className="w-[22px] h-[22px]"
                    />
                  </div>
                </div>
            ))}

            <div className="flex justify-end p-4">
              <button
                  onClick={handleConfirmPayment}
                  className="px-6 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
              >
                Thanh toán
              </button>
            </div>
          </div>
        </div>
      </div>
  );
};

export default PaymentModal;