import React, { useState } from "react";
import HeaderOrderForm from "../../components/Header/HeaderOrderForm";
import PaymentModal from "./PaymentModal";
import PlaceOrderModal from "./PlaceOrderModal";
import { getPaymentMethodIcon } from "./IconsData";
import axios from "axios";

// Nhận props bao gồm senderName, senderPhone, recipientName, recipientPhone từ OrderPage
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
  isPickupConfirmed, // Ensure it's received here
  isDeliveryConfirmed, // Ensure it's received here
}) => {
  const [koiQuantity, setKoiQuantity] = useState(0);
  const [useInsurance, setUseInsurance] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("VNPAY");
  const [showPaymentModal, setShowPaymentModal] = useState(false);
  const [showPlaceOrderModal, setShowPlaceOrderModal] = useState(false);
  const [paymentUrl, setPaymentUrl] = useState("");

  const commitmentFee = basePrice * 0.3;
  const totalPrice = useInsurance ? basePrice * 1.3 * 1.05 : basePrice * 1.3;

  const handleConfirmOrder = async () => {
    // Validate required fields before making the API call, but address details are optional
    if (
      !senderName ||
      !senderPhone ||
      !recipientName ||
      !recipientPhone ||
      !pickupAddress ||
      !deliveryAddress
    ) {
      alert("Please fill in all required fields.");
      return; // Stop execution if any required field is empty
    }

    try {
      // Get the token from localStorage
      const token = localStorage.getItem("token");

      // Check if the token exists
      if (!token) {
        console.error("No authentication token found. Please login again.");
        return;
      }

      // Prepare order data - mapping the addresses as per your requirement
      const orderData = {
        senderName: senderName.trim(),
        senderPhone: senderPhone.trim(),
        recipientName: recipientName.trim(),
        recipientPhone: recipientPhone.trim(),
        koiQuantity: parseInt(koiQuantity, 10) || 0,
        originLocation: pickupAddress.trim(), // Pickup address as originLocation
        destinationLocation: deliveryAddress.trim(), // Delivery address as destinationLocation
        originDetail: pickupDetail?.trim() || "", // Optional field, defaults to empty string if not provided
        destinationDetail: deliveryDetail?.trim() || "", // Optional field, defaults to empty string if not provided
        paymentMethod: paymentMethod || "VNPAY", // default to VNPAY
        insuranceSelected: !!useInsurance, // Ensure it's a boolean
        kilometers: basePrice / 20000, // Assuming 20,000 VND/km pricing
      };

      console.log("Order Data being sent to API:", orderData); // Debugging to check the payload

      // Make the API request to create the order
      const response = await axios.post(
        "http://localhost:8080/api/orders/create",
        orderData,
        {
          headers: {
            Authorization: `Bearer ${token}`, // Add token to Authorization header
            "Content-Type": "application/json",
          },
        }
      );

      // Check if the payment URL exists in the response
      if (
        response.data &&
        response.data.result &&
        response.data.result.paymentUrl
      ) {
        setPaymentUrl(response.data.result.paymentUrl);
      } else {
        console.error("Payment URL not found in response:", response.data);
      }

      // Set the modal to show order confirmation
      setShowPlaceOrderModal(true);
      console.log("Order created successfully:", response.data);
    } catch (error) {
      if (error.response) {
        console.error("Error Response:", error.response.data);
        if (error.response.status === 500) {
          alert("Server error. Please check the server logs.");
        } else if (error.response.status === 401) {
          alert("Unauthorized. Please log in again.");
        } else {
          alert(`Error: ${error.response.data.message}`);
        }
      } else {
        console.error("Error:", error.message);
        alert("There was an error creating your order. Please try again.");
      }
    }
  };

  const paymentMethodIcon = getPaymentMethodIcon(paymentMethod);

  const handlePaymentMethodSelect = (method) => {
    const validMethods = [
      "VNPAY",
      "PAYPAL",
      "MOMO",
      "BANK_TRANSFER",
      "ZALO_PAY",
      "CASH",
    ];

    if (validMethods.includes(method)) {
      setPaymentMethod(method);
    } else {
      alert("Invalid payment method selected.");
    }
  };

  return (
    <div className="relative z-20 flex flex-col w-1/3 h-full p-6 bg-white border-r border-gray-200 shadow-lg">
      <HeaderOrderForm />
      <div className="flex flex-col justify-between flex-grow space-y-4">
        {/* Go back button */}
        <div>
          <button
            className="text-gray-500 transition duration-200 text-md hover:text-gray-700"
            onClick={handleBack}
          >
            ← Quay về
          </button>
        </div>

        <div className="mb-2 border-b border-gray-300 shadow-sm"></div>

        <div className="text-lg font-semibold text-gray-800">
          Chi tiết giao hàng
        </div>

        <div className="p-2 transition duration-300 bg-white rounded-lg shadow-md hover:shadow-lg">
          <label className="block text-xs font-medium text-gray-600">
            Số lượng cá koi
          </label>
          <input
            type="number"
            value={koiQuantity}
            onChange={(e) => setKoiQuantity(e.target.value)}
            className="w-full p-2 mt-1 transition duration-200 bg-gray-100 rounded-lg outline-none focus:ring focus:ring-orange-300"
          />
        </div>

        <div className="flex items-center p-2 space-x-4 transition duration-300 bg-white rounded-lg shadow-md hover:shadow-lg">
          <input
            type="checkbox"
            checked={useInsurance}
            onChange={() => setUseInsurance(!useInsurance)}
            className="w-4 h-4 text-orange-500 border-gray-300 rounded form-checkbox"
          />
          <span className="text-xs text-gray-600">
            Sử dụng bảo hiểm (+5% giá trị đơn hàng)
          </span>
        </div>

        <div className="mb-2 border-b border-gray-300 shadow-sm"></div>

        <div className="h-[110px]"></div>

        {/* Payment section */}
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

        {/* Payment Method Modal */}
        {showPaymentModal && (
          <PaymentModal
            onClose={() => setShowPaymentModal(false)}
            onSelectPaymentMethod={handlePaymentMethodSelect}
            currentPaymentMethod={paymentMethod}
          />
        )}

        <div className="flex items-center justify-between p-2 transition duration-300 bg-white rounded-lg shadow-md sm:space-x-10 hover:shadow-lg">
          <div className="w-1/2 text-center">
            <h2 className="text-lg font-semibold text-gray-800">Tổng tiền</h2>
            <p className="text-2xl font-bold text-orange-500">
              {Number(totalPrice).toLocaleString("vi-VN", {
                style: "currency",
                currency: "VND",
              })}
            </p>
          </div>
          <div className="w-1/2 text-center">
            <h2 className="text-lg font-semibold text-gray-800">
              Tiền cam kết
            </h2>
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
        disabled={!isPickupConfirmed || !isDeliveryConfirmed} // Ensure both addresses are confirmed
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
