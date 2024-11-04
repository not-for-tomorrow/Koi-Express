// src/components/OrderPage/PaymentSuccessful.jsx
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const PaymentSuccessful = () => {
    const navigate = useNavigate();
    const [countdown, setCountdown] = useState(5);

    useEffect(() => {
        if (countdown === 0) {
            navigate("/deliveringstaffpage");
        }
        const timer = setInterval(() => setCountdown(prev => prev - 1), 1000);
        return () => clearInterval(timer);
    }, [countdown, navigate]);

    const handleBackToHome = () => {
        navigate("/deliveringstaffpage");
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-green-50">
            <div className="p-8 text-center bg-white rounded-lg shadow-lg">
                <h1 className="mb-4 text-3xl font-bold text-green-600">Thanh toán thành công!</h1>
                <p className="mb-6 text-lg text-gray-700">
                    Cảm ơn bạn đã hoàn tất thanh toán. Đơn hàng của bạn sẽ được xử lý ngay lập tức.
                </p>
                <p>Tự động chuyển về trang chủ sau {countdown} giây.</p>

                <button
                    onClick={handleBackToHome}
                    className="px-6 py-2 mt-4 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
                >
                    Trở về trang chủ
                </button>
            </div>
        </div>
    );
};

export default PaymentSuccessful;
