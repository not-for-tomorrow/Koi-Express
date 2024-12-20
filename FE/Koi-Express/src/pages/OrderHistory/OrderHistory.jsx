import React, {useState, useEffect} from "react";
import axios from "axios";
import {useNavigate} from "react-router-dom";

const OrderHistory = () => {
    const [state, setState] = useState({
        isTimeFilterExpanded: false,
        selectedTimeFilter: "all",
        tempSelectedTimeFilter: "all",
        customDateRange: {from: "", to: ""},
        displayDateRange: "",
        selectedTab: "Chờ xác nhận",
        searchQuery: "",
        loading: true,
        error: null,
        orders: [],
    });

    const navigate = useNavigate();

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                setState((prev) => ({...prev, loading: true}));
                const token = localStorage.getItem("token");
                if (!token) {
                    throw new Error("Token not found. Please log in.");
                }

                const response = await axios.get("http://localhost:8080/api/orders/history", {
                    headers: {Authorization: `Bearer ${token}`},
                });
                setState((prev) => ({...prev, orders: response.data.result || [], loading: false}));
            } catch (err) {
                setState((prev) => ({...prev, error: err.message || "Failed to fetch orders", loading: false}));
            }
        };

        fetchOrders();
    }, []);

    const goToOrderDetail = (order) => {
        if (order?.orderId) {
            navigate(`/appkoiexpress/history/detail/${order.orderId}`, {
                state: {orderId: order.orderId},
            });
        } else {
            console.error("Order or Order ID is missing");
        }
    };

    const handleTimeFilterClick = () => {
        setState((prev) => ({
            ...prev,
            tempSelectedTimeFilter: prev.selectedTimeFilter,
            isTimeFilterExpanded: !prev.isTimeFilterExpanded
        }));
    };

    const handleTimeFilterSelect = (filter) => {
        setState((prev) => ({
            ...prev,
            tempSelectedTimeFilter: filter,
            ...(filter !== "custom" && {customDateRange: {from: "", to: ""}}),
        }));
    };

    const handleApplyFilter = () => {
        const now = new Date();
        let fromDate, toDate;
        const {tempSelectedTimeFilter, customDateRange} = state;

        switch (tempSelectedTimeFilter) {
            case "today":
                fromDate = toDate = now.toLocaleDateString("vi-VN");
                setState((prev) => ({...prev, displayDateRange: `Ngày: ${fromDate}`}));
                break;
            case "this-week":
                const startOfWeek = new Date(now);
                startOfWeek.setDate(now.getDate() - now.getDay());
                const endOfWeek = new Date(now);
                endOfWeek.setDate(now.getDate() + (6 - now.getDay()));
                fromDate = startOfWeek.toLocaleDateString("vi-VN");
                toDate = endOfWeek.toLocaleDateString("vi-VN");
                setState((prev) => ({...prev, displayDateRange: `Tuần này: ${fromDate} - ${toDate}`}));
                break;
            case "this-month":
                const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
                const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
                fromDate = startOfMonth.toLocaleDateString("vi-VN");
                toDate = endOfMonth.toLocaleDateString("vi-VN");
                setState((prev) => ({...prev, displayDateRange: `Tháng này: ${fromDate} - ${toDate}`}));
                break;
            case "custom":
                if (customDateRange.from && customDateRange.to) {
                    fromDate = new Date(customDateRange.from).toLocaleDateString("vi-VN");
                    toDate = new Date(customDateRange.to).toLocaleDateString("vi-VN");
                    setState((prev) => ({...prev, displayDateRange: `Tùy chỉnh: ${fromDate} - ${toDate}`}));
                }
                break;
            default:
                setState((prev) => ({...prev, displayDateRange: "Chờ xác nhận"}));
                break;
        }

        setState((prev) => ({...prev, selectedTimeFilter: tempSelectedTimeFilter, isTimeFilterExpanded: false}));
    };

    const handleCloseFilter = () => {
        setState((prev) => ({...prev, isTimeFilterExpanded: false}));
    };

    const handleCustomDateChange = (field, value) => {
        setState((prev) => ({...prev, customDateRange: {...prev.customDateRange, [field]: value}}));
    };


    const filterOrders = () => {
        const {orders, selectedTab, selectedTimeFilter, searchQuery} = state;
        let filteredOrders = Array.isArray(orders) ? orders : [];

        if (selectedTab !== "Tất cả") {
            filteredOrders = filteredOrders.filter(
                (order) =>
                    getVietnameseStatus(order.order.status) === selectedTab &&
                    order.order.status !== "ASSIGNED" &&
                    order.order.status !== "COMMIT_FEE_PENDING"
            );
        }

        if (selectedTimeFilter !== "all") {
            filteredOrders = filteredOrders.filter((order) => {
                const orderDate = new Date(order.createdAt);
                if (selectedTimeFilter === "today") {
                    return orderDate.toDateString() === new Date().toDateString();
                } else if (selectedTimeFilter === "this-week") {
                    const now = new Date();
                    const startOfWeek = new Date(now);
                    startOfWeek.setDate(now.getDate() - now.getDay());
                    const endOfWeek = new Date(now);
                    endOfWeek.setDate(now.getDate() + (6 - now.getDay()));
                    return orderDate >= startOfWeek && orderDate <= endOfWeek;
                } else if (selectedTimeFilter === "this-month") {
                    const now = new Date();
                    const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
                    const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
                    return orderDate >= startOfMonth && orderDate <= endOfMonth;
                } else if (selectedTimeFilter === "custom") {
                    if (state.customDateRange.from && state.customDateRange.to) {
                        const from = new Date(state.customDateRange.from);
                        const to = new Date(state.customDateRange.to);
                        return orderDate >= from && orderDate <= to;
                    }
                    return false;
                }
                return true;
            });
        }

        if (searchQuery) {
            filteredOrders = filteredOrders.filter((order) => {
                const orderId = order.order.orderId ? order.order.orderId.toString() : "";
                const originLocation = order.order.originLocation ? order.order.originLocation.toLowerCase() : "";
                const destinationLocation = order.order.destinationLocation ? order.order.destinationLocation.toLowerCase() : "";

                return (
                    orderId.includes(searchQuery) ||
                    originLocation.includes(searchQuery.toLowerCase()) ||
                    destinationLocation.includes(searchQuery.toLowerCase())
                );
            });
        }

        return filteredOrders;
    };

    const vietnameseStatusMapping = {
        PENDING: "Chờ xác nhận",
        ACCEPTED: "Đã xác nhận",
        ASSIGNED: "Đã phân công",
        PICKING_UP: "Chuẩn bị lấy hàng",
        IN_TRANSIT: "Đang giao",
        DELIVERED: "Hoàn thành",
        COMPLETED: "Hoàn tất đơn hàng",
        CANCELED: "Đã hủy",
        COMMIT_FEE_PENDING: "Chờ thanh toán cam kết",
    };

    const getVietnameseStatus = (status) => vietnameseStatusMapping[status] || status;

    const statusColors = {
        "Chờ xác nhận": {background: "rgba(254, 240, 138, 0.2)", text: "#854D0E"},
        "Đã xác nhận": {background: "rgba(233, 213, 255, 0.2)", text: "#2c2c54"},
        "Chuẩn bị lấy hàng": {background: "rgba(153, 246, 228, 0.2)", text: "#0D9488"},
        "Đang giao": {background: "rgba(191, 219, 254, 0.2)", text: "#1E3A8A"},
        "Hoàn thành": {background: "rgba(187, 247, 208, 0.2)", text: "#065F46"},
        "Hoàn tất đơn hàng": {background: "rgba(187, 247, 208, 0.2)", text: "#065F46"},
        "Đã hủy": {background: "rgba(254, 202, 202, 0.2)", text: "#c0392b"},
    };

    const totalFeeHeaderTitle = ["Chờ xác nhận", "Đã xác nhận", "Chuẩn bị lấy hàng"].includes(state.selectedTab)
        ? "Phí cam kết"
        : "Tổng tiền";

    const filteredOrders = filterOrders();

    return (
        <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
            {state.loading ? (
                <div className="text-sm text-center">Loading...</div>
            ) : state.error ? (
                <div className="text-sm text-center text-red-500">{state.error}</div>
            ) : (
                <div className="p-8 text-sm bg-white rounded-lg shadow-lg">
                    <div className="sticky top-0 z-20 bg-white">
                        <div className="flex items-center justify-between mb-6">
                            <h1 className="text-2xl font-bold text-gray-800">Lịch sử đơn hàng</h1>
                        </div>

                        <div className="flex mb-6 space-x-4 overflow-x-auto">
                            {Object.keys(statusColors).map((tab, index) => (
                                <button
                                    key={index}
                                    onClick={() => setState((prev) => ({...prev, selectedTab: tab}))}
                                    className={`px-4 py-2 rounded-full transition duration-300 text-sm ${
                                        state.selectedTab === tab
                                            ? "font-bold shadow-md bg-blue-100 text-blue-900"
                                            : "text-blue-700 bg-transparent"
                                    }`}
                                    style={{
                                        backgroundColor: state.selectedTab === tab ? statusColors[tab].background : "transparent",
                                        color: state.selectedTab === tab ? statusColors[tab].text : "black",
                                    }}
                                >
                                    {tab}
                                </button>
                            ))}
                        </div>

                        <div className="flex items-center mb-6 space-x-6">
                            <input
                                type="text"
                                value={state.searchQuery}
                                onChange={(e) => setState((prev) => ({...prev, searchQuery: e.target.value}))}
                                placeholder="Tìm kiếm đơn hàng..."
                                className="w-full max-w-md p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />

                            <div className="relative">
                                <button
                                    onClick={handleTimeFilterClick}
                                    className={`flex items-center p-2 text-sm transition duration-300 rounded-lg shadow-sm ${
                                        state.isTimeFilterExpanded
                                            ? "bg-blue-100 text-blue-900"
                                            : "text-blue-700 bg-transparent"
                                    }`}
                                >
                                    <span>{state.displayDateRange || "Tất cả"}</span>
                                </button>
                                {state.isTimeFilterExpanded && (
                                    <div
                                        className="absolute left-0 w-64 p-4 mt-2 bg-white border border-blue-300 rounded-lg shadow-lg">
                                        {["all", "today", "this-week", "this-month", "custom"].map((filter) => (
                                            <div key={filter} className="mb-2">
                                                <label className="flex items-center text-sm">
                                                    <input
                                                        type="radio"
                                                        name="timeFilter"
                                                        value={filter}
                                                        checked={state.tempSelectedTimeFilter === filter}
                                                        onChange={() => handleTimeFilterSelect(filter)}
                                                        className="mr-2"
                                                    />
                                                    {filter === "all"
                                                        ? "Tất cả"
                                                        : filter === "today"
                                                            ? "Hôm nay"
                                                            : filter === "this-week"
                                                                ? "Tuần này"
                                                                : filter === "this-month"
                                                                    ? "Tháng này"
                                                                    : "Tùy chỉnh"}
                                                </label>
                                            </div>
                                        ))}
                                        {state.tempSelectedTimeFilter === "custom" && (
                                            <div className="flex flex-col mt-4 space-y-4">
                                                <div className="relative">
                                                    <label className="block mb-1 text-sm font-medium text-gray-600">Từ
                                                        ngày:</label>
                                                    <input
                                                        type="date"
                                                        value={state.customDateRange.from}
                                                        max={new Date().toISOString().split("T")[0]}
                                                        onChange={(e) => handleCustomDateChange("from", e.target.value)}
                                                        className="w-full p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus ```javascript
                                                        .outline-none focus:ring-2 focus:ring-blue-500"
                                                    />
                                                </div>
                                                <div className="relative">
                                                    <label className="block mb-1 text-sm font-medium text-gray-600">Đến
                                                        ngày:</label>
                                                    <input
                                                        type="date"
                                                        value={state.customDateRange.to}
                                                        min={state.customDateRange.from}
                                                        max={new Date().toISOString().split("T")[0]}
                                                        onChange={(e) => handleCustomDateChange("to", e.target.value)}
                                                        className="w-full p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                                                    />
                                                </div>
                                            </div>
                                        )}
                                        <div className="flex justify-end mt-4 space-x-3">
                                            <button
                                                onClick={handleCloseFilter}
                                                className="px-4 py-2 text-xs font-semibold text-gray-700 transition duration-300 bg-gray-100 rounded-lg hover:bg-gray-200"
                                            >
                                                Đóng
                                            </button>
                                            <button
                                                onClick={handleApplyFilter}
                                                className="px-4 py-2 text-xs font-semibold text-white transition duration-300 bg-blue-500 rounded-lg hover:bg-blue-600"
                                            >
                                                Áp dụng
                                            </button>
                                        </div>
                                    </div>
                                )}
                            </div>
                        </div>
                    </div>

                    <div className="overflow-auto max-h-[63.5vh] text-sm">
                        {filteredOrders.length === 0 ? (
                            <div className="text-center text-gray-500">Không tìm thấy đơn hàng</div>
                        ) : (
                            <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                                <thead className="sticky top-0 z-10 bg-blue-100">
                                <tr className="text-blue-900 border-b border-blue-200">
                                    <th className="p-2 font-semibold w-1/8">Mã đơn hàng</th>
                                    <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                                    <th className="w-1/3 p-2 font-semibold">Điểm giao hàng</th>
                                    <th className="p-2 font-semibold w-1/10">Thời gian tạo</th>
                                    {filteredOrders.some(orderData => orderData.order.status === "PICKING_UP") && (
                                        <th className="p-2 font-semibold w-1/10">Thời gian lấy hàng</th>
                                    )}
                                    <th className="w-1/4 p-4 font-semibold text-center w-1/10">Người giao hàng</th>
                                    <th className="w-1/4 p-4 font-semibold text-center w-1/10">{totalFeeHeaderTitle}</th>
                                    <th className="p-4 font-semibold text-center w-1/10">Trạng thái</th>
                                </tr>
                                </thead>

                                <tbody>
                                {filteredOrders.map((orderData, index) => {
                                    const order = orderData.order;
                                    const deliveringStaff = order.deliveringStaff;
                                    const statusColor = statusColors[getVietnameseStatus(order.status)] || {
                                        background: "#f0f0f0",
                                        text: "#000"
                                    };

                                    return (
                                        <tr
                                            key={index}
                                            className="transition duration-300 border-b border-gray-200 cursor-pointer hover:bg-blue-50"
                                            onClick={() => goToOrderDetail(order)}
                                        >
                                            <td className="p-2 font-semibold text-blue-600">{order.orderId}</td>
                                            <td className="p-2 text-sm text-gray-700">{order.originLocation}</td>
                                            <td className="p-2 text-sm text-gray-700">{order.destinationLocation}</td>
                                            <td className="p-2 text-sm text-gray-700">{new Date(order.createdAt).toLocaleString("vi-VN")}</td>
                                            {order.status === "PICKING_UP" && (
                                                <td className="p-2 text-sm text-gray-700">
                                                    {orderData.shipments?.estimatedPickupTime
                                                        ? new Date(orderData.shipments.estimatedPickupTime).toLocaleString("vi-VN")
                                                        : "N/A"}
                                                </td>
                                            )}
                                            <td className="p-2 text-sm text-center text-gray-700">
                                                {deliveringStaff ? deliveringStaff.fullName : "Chưa có"}
                                            </td>
                                            <td className="p-2 text-sm font-medium text-center text-blue-600">
                                                {["PENDING", "ACCEPTED", "PICKING_UP"].includes(order.status)
                                                    ? order.orderDetail?.commitmentFee != null
                                                        ? `₫ ${order.orderDetail.commitmentFee.toLocaleString("vi-VN")}`
                                                        : "N/A"
                                                    : order.totalFee != null
                                                        ? `₫ ${order.totalFee.toLocaleString("vi-VN")}`
                                                        : "N/A"}
                                            </td>
                                            <td className="p-2 text-center" style={{width: "120px"}}>
                                                    <span
                                                        className="inline-block px-4 py-2 text-xs font-semibold rounded-full"
                                                        style={{
                                                            backgroundColor: statusColor.background,
                                                            color: statusColor.text,
                                                            minWidth: "120px",
                                                            textAlign: "center",
                                                            padding: "6px 12px",
                                                            whiteSpace: "nowrap",
                                                        }}
                                                    >
                                                        {getVietnameseStatus(order.status)}
                                                    </span>
                                            </td>
                                        </tr>
                                    );
                                })}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default OrderHistory;