import React, { useState } from "react";
import HeaderOrderForm from "../../components/Header/HeaderOrderForm";
import PaymentModal from "./PaymentModal";
import PlaceOrderModal from "./PlaceOrderModal";
import { getPaymentMethodIcon } from "./IconsData";
import axios from "axios";

const OrderForm2 = ({
                        handleBack,
                        basePrice,
                        pickupAddress,
                        deliveryAddress,
                        pickupDetail,
                        deliveryDetail,
                        senderName,
                        senderPhone,
                        recipientName,
                        recipientPhone,
                        isPickupConfirmed,
                        isDeliveryConfirmed,
                        distance,
                        distanceFee, // Use passed distanceFee
                        commitmentFee, // Use passed commitmentFee
                    }) => {
    const [koiQuantity, setKoiQuantity] = useState(0);
    const [useInsurance, setUseInsurance] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState("VNPAY");
    const [showPaymentModal, setShowPaymentModal] = useState(false);
    const [showPlaceOrderModal, setShowPlaceOrderModal] = useState(false);
    const [paymentUrl, setPaymentUrl] = useState("");

    const handleConfirmOrder = async () => {
        if (
            !senderName ||
            !senderPhone ||
            !recipientName ||
            !recipientPhone ||
            !pickupAddress ||
            !deliveryAddress
        ) {
            alert("Please fill in all required fields.");
            return;
        }

        try {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("No authentication token found. Please login again.");
                return;
            }

            const orderData = {
                senderName: senderName.trim(),
                senderPhone: senderPhone.trim(),
                recipientName: recipientName.trim(),
                recipientPhone: recipientPhone.trim(),
                koiQuantity: parseInt(koiQuantity, 10) || 0,
                originLocation: pickupAddress.trim(),
                destinationLocation: deliveryAddress.trim(),
                originDetail: pickupDetail?.trim() || "",
                destinationDetail: deliveryDetail?.trim() || "",
                paymentMethod: paymentMethod || "VNPAY",
                insuranceSelected: !!useInsurance,
                kilometers: distance,
            };

            const response = await axios.post(
                "http://localhost:8080/api/orders/create",
                orderData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (response.data && response.data.result && response.data.result.paymentUrl) {
                setPaymentUrl(response.data.result.paymentUrl);
            } else {
                console.error("Payment URL not found in response:", response.data);
            }

            setShowPlaceOrderModal(true);
        } catch (error) {
            if (error.response) {
                console.error("Error Response:", error.response.data);
                alert(`Error: ${error.response.data.message}`);
            } else {
                console.error("Error:", error.message);
                alert("There was an error creating your order. Please try again.");
            }
        }
    };

    const paymentMethodIcon = getPaymentMethodIcon(paymentMethod);

    const handlePaymentMethodSelect = (method) => {
        const validMethods = ["VNPAY"];
        if (validMethods.includes(method)) {
            setPaymentMethod(method);
        } else {
            alert("Invalid payment method selected.");
        }
    };

    const handleKoiQuantityChange = (e) => {
        const value = e.target.value;

        // If the input is empty, set the quantity to 0 for display
        if (value === "") {
            setKoiQuantity("0");
        } else {
            // Parse the integer value, ensuring no leading zeros and a minimum of 0
            const parsedValue = Math.max(parseInt(value, 10), 0);
            setKoiQuantity(parsedValue.toString());
        }
    };



    return (
        <div className="relative z-20 flex flex-col w-1/3 h-full p-6 bg-white border-r border-gray-200 shadow-lg">
            <HeaderOrderForm />
            <div className="flex flex-col justify-between flex-grow space-y-4">
                <div>
                    <button
                        className="text-gray-500 transition duration-200 text-md hover:text-gray-700"
                        onClick={handleBack}
                    >
                        ← Quay về
                    </button>
                </div>

                <div className="mb-2 border-b border-gray-300 shadow-sm"></div>

                <div className="text-lg font-semibold text-gray-800">Chi tiết giao hàng</div>

                <div className="p-2 transition duration-300 bg-white rounded-lg shadow-md hover:shadow-lg">
                    <label className="block text-xs font-medium text-gray-600">
                        Số lượng cá koi
                    </label>
                    <input
                        type="number"
                        value={koiQuantity}
                        onChange={handleKoiQuantityChange} // Use the new handler
                        className="w-full p-2 mt-1 transition duration-200 bg-gray-100 rounded-lg outline-none focus:ring focus:ring-orange-300"
                    />
                </div>

                <div
                    className="flex items-center p-2 space-x-4 transition duration-300 bg-white rounded-lg shadow-md hover:shadow-lg">
                    <input
                        type="checkbox"
                        checked={useInsurance}
                        onChange={() => setUseInsurance(!useInsurance)}
                        className="w-4 h-4 text-orange-500 border-gray-300 rounded form-checkbox"
                    />
                    <span className="text-xs text-gray-600">Sử dụng bảo hiểm</span>
                </div>

                <div className="mb-2 border-b border-gray-300 shadow-sm"></div>

                <div className="h-[110px]"></div>

                <div className="flex items-center justify-between p-2 transition duration-300 bg-white rounded-lg shadow-md hover:shadow-lg">
                    <div className="flex-grow sm:pr-4">
                        <div
                            className="p-2 bg-gray-100 rounded-lg cursor-pointer"
                            onClick={() => setShowPaymentModal(true)}
                        >
                            <label className="block text-xs font-medium text-gray-600">
                                Hình thức thanh toán
                            </label>
                            <div className="flex items-center mt-1 font-semibold text-gray-900">
                                {paymentMethodIcon && (
                                    <img
                                        src={paymentMethodIcon}
                                        alt={paymentMethod}
                                        className="w-[35px] h-[35px] mr-2"
                                    />
                                )}
                                <p>{paymentMethod}</p>
                            </div>
                        </div>
                    </div>
                </div>

                {showPaymentModal && (
                    <PaymentModal
                        onClose={() => setShowPaymentModal(false)}
                        onSelectPaymentMethod={handlePaymentMethodSelect}
                        currentPaymentMethod={paymentMethod}
                    />
                )}

                <div className="flex items-center justify-between p-2 transition duration-300 bg-white rounded-lg shadow-md sm:space-x-10 hover:shadow-lg">
                    <div className="w-1/2 text-center">
                        <h2 className="text-lg font-semibold text-gray-800">Tiền vận chuyển</h2>
                        <p className="text-2xl font-bold text-orange-500">
                            {Number(distanceFee).toLocaleString("vi-VN", {
                                style: "currency",
                                currency: "VND",
                            })}
                        </p>
                    </div>
                    <div className="w-1/2 text-center">
                        <h2 className="text-lg font-semibold text-gray-800">Tiền cam kết</h2>
                        <p className="text-2xl font-bold text-orange-500">
                            {Number(commitmentFee).toLocaleString("vi-VN", {
                                style: "currency",
                                currency: "VND",
                            })}
                        </p>
                    </div>
                </div>
            </div>
            <button
                className="w-full p-3 mt-4 text-white bg-blue-500 rounded-lg shadow-lg hover:bg-blue-600"
                onClick={handleConfirmOrder}
                disabled={!isPickupConfirmed || !isDeliveryConfirmed}
            >
                Đặt đơn
            </button>

            {showPlaceOrderModal && (
                <PlaceOrderModal
                    onClose={() => setShowPlaceOrderModal(false)}
                    commitmentFee={commitmentFee}
                    paymentUrl={paymentUrl}
                />
            )}
        </div>
    );
};

export default OrderForm2;
