import React from "react";
import {Link, useNavigate} from "react-router-dom";

const InTransitOrderModal = ({
                                 orderId,
                                 originLocation,
                                 destinationLocation,
                                 senderName,
                                 senderPhone,
                                 recipientName,
                                 recipientPhone,
                                 distance,
                                 onClose, // Prop để đóng modal
                             }) => {
    const navigate = useNavigate(); // Khởi tạo điều hướng

    // Hàm gọi API cập nhật trạng thái
    const handleUpdateStatus = async () => {
        const token = localStorage.getItem("token");
        try {
            const response = await fetch(
                `http://localhost:8080/api/delivering/orders/complete-order/${orderId}`,
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                }
            );
            if (response.ok) {
                console.log("Trạng thái đơn hàng đã được cập nhật.");
                onClose(); // Đóng modal sau khi cập nhật thành công
                navigate("/deliveringstaffpage"); // Điều hướng về trang chính
            } else {
                console.error("Cập nhật trạng thái thất bại.");
            }
        } catch (error) {
            console.error("Có lỗi xảy ra:", error);
        }
    };

    return (
        <div className="relative z-20 flex flex-col w-full h-full max-w-lg p-6 bg-white border border-gray-200 shadow-lg">
            <div className="flex-grow">
                <div className="mb-4 text-2xl font-bold text-gray-800">
                    Đơn hàng #{orderId}
                </div>
                <div className="mb-6 text-sm">
                    <strong className="text-gray-600">Lộ trình:</strong> {distance}
                </div>

                <div className="mt-6">
                    <div className="flex items-start space-x-2">
                        <div className="w-4 h-4 mt-1 bg-blue-500 rounded-full"></div>
                        <div>
                            <p className="text-lg font-bold">
                                {senderName} • <span>{senderPhone}</span>
                            </p>
                            <p className="text-sm text-gray-500">{originLocation}</p>
                        </div>
                    </div>

                    <div className="flex items-start mt-6 space-x-2">
                        <div className="w-4 h-4 mt-1 bg-green-500 rounded-full"></div>
                        <div>
                            <p className="text-lg font-bold">
                                {recipientName} • <span>{recipientPhone}</span>
                            </p>
                            <p className="text-sm text-gray-500">{destinationLocation}</p>
                        </div>
                    </div>
                </div>
            </div>

            {/* Nút đóng */}
            <div className="flex-shrink-0 mt-6">
                <button
                    onClick={handleUpdateStatus}
                    className="w-full p-3 text-base font-semibold text-white transition-all transform bg-blue-500 rounded-lg hover:bg-blue-600"
                >
                    <Link to="/deliveringstaffpage">Đóng</Link>
                </button>
            </div>

            {/* Nút cập nhật trạng thái */}
            <div className="flex-shrink-0 mt-4">
                <button
                    onClick={handleUpdateStatus}
                    className="w-full p-3 text-base font-semibold text-white transition-all transform bg-green-500 rounded-lg hover:bg-green-600"
                >
                    <Link to="/deliveringstaffpage">Giao hàng thành công</Link>
                </button>
            </div>
        </div>
    );
};

export default InTransitOrderModal;
