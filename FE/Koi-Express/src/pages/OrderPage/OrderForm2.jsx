  import React, { useState } from "react";
  import HeaderOrderForm from "../../components/Header/HeaderOrderForm";

  const OrderForm2 = ({ handleBack, basePrice }) => {
    const [koiQuantity, setKoiQuantity] = useState(0);
    const [useInsurance, setUseInsurance] = useState(false);
    const [paymentMethod, setPaymentMethod] = useState("cash");
    const [selectedDate, setSelectedDate] = useState("Giao ngay");
    const [promoCode, setPromoCode] = useState("");
    const [note, setNote] = useState("");

    const totalPrice = useInsurance ? basePrice * 1.05 : basePrice; // Calculate price with or without insurance

    console.log("Base price in OrderForm2:", basePrice);

    const handleDateChange = () => {
      const chosenDate = prompt("Chọn ngày: (dd/mm/yyyy)", "01/01/2024");
      if (chosenDate) {
        setSelectedDate(chosenDate);
      }
    };

    return (
      <div className="relative z-20 flex flex-col w-1/3 h-full p-6 bg-white border-r border-gray-200 shadow-lg">
        {/* Container chính với flex-grow để phân phối không gian đồng đều */}
        <div className="p-4">
          <HeaderOrderForm />
        </div>
        <div className="flex flex-col justify-between flex-grow space-y-4">
          {/* Hàng 2: Nút quay về */}
          <div>
            <button
              className="text-gray-500 transition duration-200 text-md hover:text-gray-700"
              onClick={handleBack}
            >
              ← Quay về
            </button>
          </div>

          {/* Hàng 3: Gạch xám và shadow */}
          <div className="mb-2 border-b border-gray-300 shadow-sm"></div>

          {/* Hàng 4: Chi tiết giao hàng */}
          <div className="text-lg font-semibold text-gray-800">
            Chi tiết giao hàng
          </div>

          {/* Hàng 5: Nhập số lượng cá Koi */}
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

          {/* Hàng 6: Tích chọn bảo hiểm */}
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

          {/* Hàng 7: Gạch xám và shadow */}
          <div className="mb-2 border-b border-gray-300 shadow-sm"></div>

          {/* Hàng 8-9: Thời gian lấy hàng, mã khuyến mãi và ghi chú cho tài xế */}
          <div className="grid grid-cols-1 gap-2 sm:grid-cols-2">
            <div className="flex flex-col justify-between">
              <div className="flex items-center justify-between p-2 transition duration-300 bg-gray-100 rounded-lg shadow-md hover:shadow-lg">
                {/* Thời gian lấy hàng */}
                <div className="flex flex-col">
                  <label className="block text-xs font-medium text-gray-500">
                    Thời gian lấy hàng
                  </label>
                  <span className="mt-1 font-semibold text-gray-800">
                    {selectedDate}
                  </span>
                </div>
                <div>
                  <span
                    className="text-gray-500 cursor-pointer"
                    onClick={handleDateChange}
                  >
                    📅
                  </span>
                </div>
              </div>

              <div className="relative p-2 mt-2 transition duration-300 bg-gray-100 rounded-lg shadow-md hover:shadow-lg">
                {/* Mã khuyến mãi */}
                <label className="block text-xs font-medium text-gray-500">
                  Mã khuyến mãi
                </label>
                <input
                  type="text"
                  value={promoCode}
                  onChange={(e) => setPromoCode(e.target.value)}
                  className="w-full mt-1 text-gray-500 bg-gray-100 outline-none"
                  placeholder="Nhập mã"
                />
                <div className="absolute text-orange-500 top-6 right-4">🎟️</div>
              </div>
            </div>

            <div className="p-2 transition duration-300 bg-gray-100 rounded-lg shadow-md hover:shadow-lg">
              {/* Ghi chú cho tài xế */}
              <label className="block text-xs font-medium text-gray-600">
                Ghi chú cho tài xế
              </label>
              <textarea
                value={note}
                onChange={(e) => setNote(e.target.value)}
                className="w-full h-12 mt-1 bg-gray-100 outline-none"
                placeholder="Nhập ghi chú"
              ></textarea>
            </div>
          </div>

          {/* Hàng 10: Hình thức thanh toán */}
          <div className="text-lg font-semibold text-gray-800">
            Hình thức thanh toán
          </div>

          {/* Hàng 11: Chọn hình thức thanh toán */}
          <div className="flex flex-col items-center justify-between p-2 transition duration-300 bg-white rounded-lg shadow-md sm:flex-row hover:shadow-lg">
            {/* Hình thức thanh toán */}
            <div className="flex-grow sm:pr-4">
              <div className="p-2 bg-gray-100 rounded-lg">
                <label className="block text-xs font-medium text-gray-600">
                  Hình thức thanh toán
                </label>
                <select
                  value={paymentMethod}
                  onChange={(e) => setPaymentMethod(e.target.value)}
                  className="w-full p-2 mt-1 transition duration-200 bg-gray-100 rounded-lg outline-none focus:ring focus:ring-orange-300"
                >
                  <option value="cash">Trả tiền mặt</option>
                  <option value="momo">Momo</option>
                  <option value="vnpay">VN Pay</option>
                </select>
              </div>
            </div>

            {/* Tổng tiền */}
            <div className="pl-0 mt-3 text-right sm:pl-4 sm:mt-0">
              <h2 className="text-lg font-semibold text-gray-800">Tổng tiền</h2>
              <p className="text-2xl font-bold text-orange-500">
                {Number(totalPrice).toLocaleString("vi-VN", {
                  style: "currency",
                  currency: "VND",
                })}
              </p>
            </div>
          </div>
        </div>
        {/* Submit Button */}
        <button className="w-full p-3 mt-4 text-white bg-blue-500 rounded-lg shadow-lg hover:bg-blue-600">
          Đặt đơn
        </button>
      </div>
    );
  };

  export default OrderForm2;
