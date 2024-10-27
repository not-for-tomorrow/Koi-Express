import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AcceptOrder = () => {
  const [isTimeFilterExpanded, setIsTimeFilterExpanded] = useState(false);
  const [selectedTimeFilter, setSelectedTimeFilter] = useState("all");
  const [tempSelectedTimeFilter, setTempSelectedTimeFilter] = useState("all");
  const [customDateRange, setCustomDateRange] = useState({ from: "", to: "" });
  const [displayDateRange, setDisplayDateRange] = useState("");
  const [selectedTab, setSelectedTab] = useState("Tất cả");
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem("token");
        if (!token) {
          throw new Error("Token not found. Please log in.");
        }

        const response = await axios.get(
          "http://localhost:8080/api/sales/orders/pending", // API for pending orders
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setOrders(response.data.content || []); // Update orders from API response
        setLoading(false);
      } catch (err) {
        setError(err.message || "Failed to fetch orders");
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  const goToOrderDetail = (order) => {
    if (order && order.orderId) {
      navigate(`/salepage/accept/detail/${order.orderId}`, {
        state: order,
      });
    } else {
      console.error("Order or Order ID is missing");
    }
  };

  const filterOrders = () => {
    let filteredOrders = Array.isArray(orders) ? orders : [];

    // Filter by selected tab (status)
    if (selectedTab !== "Tất cả") {
      filteredOrders = filteredOrders.filter(
        (order) => getVietnameseStatus(order.status) === selectedTab
      );
    }

    // Filter by search query
    if (searchQuery) {
      filteredOrders = filteredOrders.filter((order) => {
        const orderId = order.orderId ? order.orderId.toString() : "";
        const originLocation = order.originLocation
          ? order.originLocation.toLowerCase()
          : "";
        const destinationLocation = order.destinationLocation
          ? order.destinationLocation.toLowerCase()
          : "";
        const fullName = order.orderDetail.senderName
          ? order.orderDetail.senderName.toLowerCase()
          : "";

        return (
          orderId.includes(searchQuery) ||
          originLocation.includes(searchQuery.toLowerCase()) ||
          destinationLocation.includes(searchQuery.toLowerCase()) ||
          fullName.includes(searchQuery.toLowerCase())
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
    CANCELED: "Đã hủy",
    COMMIT_FEE_PENDING: "Chờ thanh toán cam kết",
  };

  const getVietnameseStatus = (status) =>
    vietnameseStatusMapping[status] || status;

  const statusColors = {
    "Tất cả": { background: "rgba(59, 130, 246, 0.1)", text: "#1E3A8A" },
    "Chờ xác nhận": { background: "rgba(254, 240, 138, 0.2)", text: "#854D0E" },
    "Đã xác nhận": { background: "rgba(233, 213, 255, 0.2)", text: "#2c2c54" },
    "Đã phân công": { background: "rgba(253, 230, 138, 0.2)", text: "#CA8A04" },
    "Chuẩn bị lấy hàng": {
      background: "rgba(153, 246, 228, 0.2)",
      text: "#0D9488",
    },
    "Đang giao": { background: "rgba(191, 219, 254, 0.2)", text: "#1E3A8A" },
    "Hoàn thành": { background: "rgba(187, 247, 208, 0.2)", text: "#065F46" },
    "Đã hủy": { background: "rgba(254, 202, 202, 0.2)", text: "#c0392b" },
    "Chờ thanh toán cam kết": {
      background: "rgba(255, 199, 199, 0.2)",
      text: "#C0392B",
    },
  };

  const defaultStatusColor = { background: "#f0f0f0", text: "#000" };

  return (
    <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
      {loading ? (
        <div className="text-sm text-center">Loading...</div>
      ) : error ? (
        <div className="text-sm text-center text-red-500">{error}</div>
      ) : (
        <div className="p-8 text-sm bg-white rounded-lg shadow-lg">
          <div className="sticky top-0 z-20 bg-white">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-2xl font-bold text-gray-800">
                Đơn hàng đang chờ xác nhận
              </h1>
            </div>

            <div className="flex items-center mb-6 space-x-6">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Tìm kiếm đơn hàng..."
                className="w-full max-w-md p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="overflow-auto max-h-[63.5vh] text-sm">
            {filterOrders().length === 0 ? (
              <div className="text-center text-gray-500">
                Không tìm thấy đơn hàng
              </div>
            ) : (
              <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                <thead className="sticky top-0 z-10 bg-blue-100">
                  <tr className="text-blue-900 border-b border-blue-200">
                    <th className="p-2 font-semibold w-1/8">Mã đơn hàng</th>
                    <th className="w-1/5 p-2 font-semibold">Người gửi</th>
                    <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/3 p-2 font-semibold">Điểm giao hàng</th>
                    <th className="p-2 font-semibold w-1/10">Thời gian tạo</th>
                    <th className="p-2 font-semibold w-1/9">Tổng phí</th>
                    <th className="p-2 font-semibold text-center w-1/9">
                      Trạng thái
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filterOrders().map((order, index) => {
                    const statusColor =
                      statusColors[getVietnameseStatus(order.status)] ||
                      defaultStatusColor;

                    return (
                      <tr
                        key={index}
                        className="transition duration-300 border-b border-gray-200 cursor-pointer hover:bg-blue-50"
                        onClick={() => goToOrderDetail(order)} // Correctly pass the entire order object
                      >
                        <td className="p-2 font-semibold text-blue-600">
                          {order.orderId}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {order.orderDetail.senderName}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {order.originLocation}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {order.destinationLocation}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {new Date(order.createdAt).toLocaleString("vi-VN")}
                        </td>
                        <td className="p-2 text-sm font-medium text-blue-600">
                          {order.totalFee !== null
                            ? `₫ ${order.totalFee.toLocaleString("vi-VN")}`
                            : "Chưa tính"}
                        </td>
                        <td className="p-2 text-center">
                          <span
                            className="inline-block px-4 py-2 text-xs font-semibold rounded-full"
                            style={{
                              backgroundColor: statusColor.background,
                              color: statusColor.text,
                              minWidth: "120px", // Ensure minimum size for consistent button size
                              textAlign: "center",
                              padding: "6px 12px",
                              whiteSpace: "nowrap", // Prevents text wrapping in the status
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

export default AcceptOrder;
