// src/components/PaymentSuccessful.jsx
import React from "react";
import { Link } from "react-router-dom";

const PaymentSuccessful = () => {
    return (
        <div className="flex items-center justify-center min-h-screen bg-green-50">
            <div className="p-8 bg-white rounded-lg shadow-lg text-center">
                <h1 className="text-3xl font-bold text-green-600 mb-4">
                    Thanh toán thành công!
                </h1>
                <p className="text-lg text-gray-700 mb-6">
                    Cảm ơn bạn đã hoàn tất thanh toán. Đơn hàng của bạn sẽ được xử lý ngay lập tức.
                </p>
                <Link
                    to="/"
                    className="px-6 py-3 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
                >
                    Trở về trang chủ
                </Link>
            </div>
        </div>
    );
};

export default PaymentSuccessful;
