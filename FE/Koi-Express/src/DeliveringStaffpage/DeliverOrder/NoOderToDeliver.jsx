// NoOrderToDeliver.jsx
import React from "react";

const NoOrderToDeliver = () => {
  return (
    <div className="flex items-center justify-center min-h-screen bg-gray-100">
      <div className="p-6 text-center bg-white rounded-lg shadow-lg">
        <h2 className="text-2xl font-semibold text-gray-700">
          Bạn chưa có đơn hàng nào mới cập nhật
        </h2>
        <p className="mt-2 text-gray-500">Vui lòng kiểm tra lại sau!</p>
      </div>
    </div>
  );
};

export default NoOrderToDeliver;
