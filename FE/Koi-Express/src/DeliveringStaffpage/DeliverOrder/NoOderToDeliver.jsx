// NoOrderToDeliver.jsx
import React from "react";

const NoOrderToDeliver = () => {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="text-center p-6 bg-white rounded-lg shadow-lg">
        <h2 className="text-2xl font-semibold text-gray-700">
          Bạn chưa có đơn hàng đang giao nào
        </h2>
        <p className="text-gray-500 mt-2">Vui lòng kiểm tra lại sau!</p>
      </div>
    </div>
  );
};

export default NoOrderToDeliver;
