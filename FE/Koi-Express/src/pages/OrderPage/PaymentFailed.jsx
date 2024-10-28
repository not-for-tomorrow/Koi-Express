// src/components/OrderPage/PaymentFailed.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const PaymentFailed = () => {
    const navigate = useNavigate();
    const [countdown, setCountdown] = useState(5);

    useEffect(() => {
        if (countdown === 0) {
            navigate("/");
        }
        const timer = setInterval(() => setCountdown(prev => prev - 1), 1000);
        return () => clearInterval(timer);
    }, [countdown, navigate]);

    const handleBackToHome = () => {
        navigate("/appkoiexpress"); // Redirect immediately to homepage
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-red-50">
            <div className="p-8 bg-white rounded-lg shadow-lg text-center">
                <h1 className="text-3xl font-bold text-red-600 mb-4">Đặt đơn thất bại</h1>
                <p className="text-lg text-gray-700 mb-6">
                    Rất tiếc, thanh toán của bạn không thành công. Vui lòng thử lại hoặc liên hệ hỗ trợ.
                </p>
                <p>Tự động chuyển về trang chủ sau {countdown} giây.</p>

                <button
                    onClick={handleBackToHome}
                    className="mt-4 px-6 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600"
                >
                    Trở về trang chủ
                </button>
            </div>
        </div>
    );
};

export default PaymentFailed;
