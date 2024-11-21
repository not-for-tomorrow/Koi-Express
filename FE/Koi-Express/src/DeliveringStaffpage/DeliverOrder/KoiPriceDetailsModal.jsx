import React, { useEffect, useState } from "react";
import axios from "axios";

const KoiPriceDetailsModal = ({ koiData, koiQuantity, onClose }) => {
    const [feeDetails, setFeeDetails] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        const calculateFees = async () => {
            const koiList = [
                { koiType: "KOI_VIET_NAM", quantity: koiData.KOI_VIET_NAM[0], koiSize: 20, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_VIET_NAM", quantity: koiData.KOI_VIET_NAM[1], koiSize: 40, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_VIET_NAM", quantity: koiData.KOI_VIET_NAM[2], koiSize: 60, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_NHAT_BAN", quantity: koiData.KOI_NHAT_BAN[0], koiSize: 20, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_NHAT_BAN", quantity: koiData.KOI_NHAT_BAN[1], koiSize: 40, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_NHAT_BAN", quantity: koiData.KOI_NHAT_BAN[2], koiSize: 60, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_CHAU_AU", quantity: koiData.KOI_CHAU_AU[0], koiSize: 20, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_CHAU_AU", quantity: koiData.KOI_CHAU_AU[1], koiSize: 40, shipmentCondition: "HEALTHY" },
                { koiType: "KOI_CHAU_AU", quantity: koiData.KOI_CHAU_AU[2], koiSize: 60, shipmentCondition: "HEALTHY" },
            ].filter(item => item.quantity > 0);

            const koiDataPayload = { koiList };

            try {
                const token = localStorage.getItem("token");
                const response = await axios.post(
                    "http://localhost:8080/api/orders/calculate-total-fee",
                    koiDataPayload,
                    {
                        headers: {
                            "Content-Type": "application/json",
                            Authorization: `Bearer ${token}`,
                        },
                    }
                );

                if (response.data.code === 200) {
                    setFeeDetails(response.data.result);
                } else {
                    setError("Failed to fetch fee details.");
                }
            } catch (error) {
                setError("Error fetching fee details: " + error.message);
            }
        };

        calculateFees();
    }, [koiData]);

    if (error) {
        return (
            <div
                className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
                style={{ zIndex: 1000 }}
            >
                <div className="relative w-full max-w-3xl p-6 bg-white rounded-lg shadow-lg">
                    <button
                        onClick={onClose}
                        className="absolute text-gray-500 top-3 right-3 hover:text-gray-700 focus:outline-none"
                    >
                        &#10005;
                    </button>
                    <h2 className="mb-4 text-lg font-bold">Lỗi</h2>
                    <p className="text-red-500">{error}</p>
                    <button
                        className="w-full p-2 mt-4 text-white bg-blue-500 rounded"
                        onClick={onClose}
                    >
                        Đóng
                    </button>
                </div>
            </div>
        );
    }

    if (!feeDetails) {
        return <div>Loading...</div>;
    }

    return (
        <div
            className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
            style={{ zIndex: 1000 }}
        >
            <div className="relative w-full max-w-md p-6 bg-white rounded-lg shadow-lg">
                <button
                    onClick={onClose}
                    className="absolute text-gray-500 top-3 right-3 hover:text-gray-700 focus:outline-none"
                >
                    &#10005;
                </button>

                <h2 className="mb-4 text-lg font-bold">Chi tiết phí</h2>

                <div className="flex flex-col gap-2">
                    <div className="flex justify-between">
                        <span className="font-medium">Số lượng cá:</span>
                        <span >{koiQuantity}</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="font-medium">Tổng phí cá:</span>
                        <span>{feeDetails.koiFee.toLocaleString()} VND</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="font-medium">Phí bảo hiểm:</span>
                        <span>{feeDetails.insuranceFee.toLocaleString()} VND</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="font-medium">Phí đóng gói:</span>
                        <span>{feeDetails.packagingFee.toLocaleString()} VND</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="font-medium">Phí chăm sóc:</span>
                        <span>{feeDetails.careFee.toLocaleString()} VND</span>
                    </div>
                    <div className="flex justify-between">
                        <span className="font-medium">VAT:</span>
                        <span>{feeDetails.vat.toLocaleString()} VND</span>
                    </div>
                    <div className="flex justify-between font-bold">
                        <span>Tổng phí:</span>
                        <span>{feeDetails.totalFee.toLocaleString()} VND</span>
                    </div>
                </div>

                <div className="mt-4">
                    <button
                        className="w-full p-2 mt-4 text-white bg-blue-500 rounded"
                        onClick={onClose}
                    >
                        Đóng
                    </button>
                </div>
            </div>
        </div>
    );
};

export default KoiPriceDetailsModal;