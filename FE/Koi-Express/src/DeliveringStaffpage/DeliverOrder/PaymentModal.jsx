import React, {useState, useEffect} from "react";
import {cashPaymentMethods, nonCashPaymentMethods} from "./IconsData";

const PaymentModal = ({
                          onClose,
                          onSelectPaymentMethod,
                          currentPaymentMethod,
                      }) => {
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
        onClose();
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
                <hr className="w-full border-gray-300"/>

                <div className="space-y-0">
                    <div className="p-4 text-lg font-semibold text-gray-900">
                        Thanh toán không dùng tiền mặt
                    </div>
                    <hr className="w-full border-gray-300"/>

                    {nonCashPaymentMethods.map((method) => (
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

                    <div className="p-4 text-lg font-semibold text-gray-900">
                        Thanh toán dùng tiền mặt
                    </div>
                    <hr className="w-full border-gray-300"/>

                    {cashPaymentMethods.map((method) => (
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
                </div>
            </div>
        </div>
    );
};

export default PaymentModal;
