import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AcceptSupport = () => {
    const navigate = useNavigate();

    const [state, setState] = useState({
        loading: true,
        error: null,
        supportRequests: []
    });

    const goToOrderDetail = (request) => {
        if (request && request.order && request.order.orderId) {
            navigate(`/salepage/acceptsupport/detail/${request.order.orderId}`, {
                state: {
                    requestId: request.requestId,
                    orderData: request.order,
                    supportRequestData: request
                },
            });
        } else {
            console.error("Order hoặc Order ID bị thiếu");
            alert("Không thể mở chi tiết đơn hàng");
        }
    };

    useEffect(() => {
        const fetchSupportRequests = async () => {
            try {
                setState((prev) => ({ ...prev, loading: true }));
                const token = localStorage.getItem("token");
                if (!token) {
                    throw new Error("Token not found. Please log in.");
                }

                const response = await axios.get("http://localhost:8080/api/sales/support/pending", {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setState((prev) => ({ 
                    ...prev, 
                    supportRequests: response.data.content || [], 
                    loading: false 
                }));
            } catch (err) {
                setState((prev) => ({ 
                    ...prev, 
                    error: err.message || "Failed to fetch support requests", 
                    loading: false 
                }));
            }
        };

        fetchSupportRequests();
    }, []);

    return (
        <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
            {state.loading ? (
                <div className="text-sm text-center">Đang tải...</div>
            ) : state.error ? (
                <div className="text-sm text-center text-red-500">{state.error}</div>
            ) : (
                <div className="p-8 text-sm bg-white rounded-lg shadow-lg">
                    <h1 className="text-2xl font-bold text-gray-800 mb-6">Xác nhận hỗ trợ</h1>

                    <div className="overflow-auto max-h-[63.5vh] text-sm">
                        {state.supportRequests.length === 0 ? (
                            <div className="text-center text-gray-500">Không có yêu cầu hỗ trợ</div>
                        ) : (
                            <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                                <thead className="sticky top-0 z-10 bg-blue-100">
                                    <tr className="text-blue-900 border-b border-blue-200">
                                        <th className="p-2 font-semibold w-1/8">Mã yêu cầu</th>
                                        <th className="w-1/6 p-2 font-semibold">Tên khách hàng</th>
                                        <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                                        <th className="w-1/4 p-2 font-semibold">Điểm giao hàng</th>
                                        <th className="p-2 font-semibold w-1/10">Ngày tạo</th>
                                        <th className="w-1/4 p-2 font-semibold">Nội dung</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {state.supportRequests.map((request, index) => (
                                        <tr 
                                            key={request.requestId} 
                                            className="transition duration-300 border-b border-gray-200 hover:bg-blue-50 cursor-pointer"
                                            onClick={() => goToOrderDetail(request)} 
                                        >
                                            <td className="p-2 font-semibold text-blue-600">
                                                {request.requestId}
                                            </td>
                                            <td className="p-2 text-sm text-gray-700">
                                                {request.customer?.fullName || 'Không xác định'}
                                            </td>
                                            <td className="p-2 text-sm text-gray-700">
                                                {request.order?.originLocation}
                                            </td>
                                            <td className="p-2 text-sm text-gray-700">
                                                {request.order?.destinationLocation}
                                            </td>
                                            <td className="p-2 text-sm text-gray-700">
                                                {new Date(request.createdAt).toLocaleString("vi-VN")}
                                            </td>
                                            <td className="p-2 text-sm text-gray-700">
                                                {request.description}
                                            </td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default AcceptSupport;