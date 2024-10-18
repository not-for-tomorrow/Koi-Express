import React, { useState } from "react";
import HeaderOrderForm from "../../components/Header/HeaderOrderForm";
import PaymentModal from "./PaymentModal"; // Import the PaymentModal component
import { getPaymentMethodIcon, cashPaymentMethods } from "./IconsData"; // Add this import

const OrderForm2 = ({ handleBack, basePrice }) => {
  const [koiQuantity, setKoiQuantity] = useState(0);
  const [useInsurance, setUseInsurance] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState("cash");
  const [selectedDate, setSelectedDate] = useState("Giao ngay");
  const [promoCode, setPromoCode] = useState("");
  const [note, setNote] = useState("");
  const [showPaymentModal, setShowPaymentModal] = useState(false); // Modal state

  // Calculate total price, considering insurance
  const commitmentFee = basePrice * 0.3;
  const totalPrice = useInsurance ? basePrice * 1.3 * 1.05 : basePrice * 1.3;

  // Function to handle payment method selection
  const handlePaymentMethodSelect = (method) => {
    setPaymentMethod(method); // Update payment method
    setShowPaymentModal(false); // Close modal
  };

  const paymentMethodIcon = getPaymentMethodIcon(paymentMethod);

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
                {paymentMethodIcon ? (
                  <img
                    src={paymentMethodIcon}
                    alt={paymentMethod}
                    className="w-[35px] h-[35px] mr-2" // Adjust size as needed
                  />
                ) : paymentMethod === "cash" ? (
                  <img
                    src={cashPaymentMethods[0].icon} // Get the icon for "Người gửi trả tiền mặt"
                    alt={cashPaymentMethods[0].label}
                    className="w-[35px] h-[35px] mr-2" // Adjust size as needed
                  />
                ) : null}
                <p>
                  {paymentMethod === "cash"
                    ? cashPaymentMethods[0].label // Display "Người gửi trả tiền mặt" by default
                    : paymentMethod}
                </p>
              </div>
            </div>
          </div>
        </div>

        {/* Payment Method Modal */}
        {showPaymentModal && (
          <PaymentModal
            onClose={() => setShowPaymentModal(false)}
            onSelectPaymentMethod={handlePaymentMethodSelect} // Pass callback to get the value from the popup
            currentPaymentMethod={paymentMethod} // Pass the current selected payment method
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
      <button className="w-full p-3 mt-4 text-white bg-blue-500 rounded-lg shadow-lg hover:bg-blue-600">
        Đặt đơn
      </button>
    </div>
  );
};

export default OrderForm2;
