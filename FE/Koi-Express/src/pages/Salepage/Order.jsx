import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Order = () => {
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
    // Static test data for orders with all possible statuses
    const testOrders = [
      {
        orderId: 1,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-19T14:00:32",
        totalFee: 500000,
        status: "PENDING",
      },
      {
        orderId: 2,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-19T14:25:16",
        totalFee: 750000,
        status: "ACCEPTED",
      },
      {
        orderId: 3,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-19T14:37:57",
        totalFee: 300000,
        status: "ASSIGNED",
      },
      {
        orderId: 4,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-19T22:08:36",
        totalFee: 1000000,
        status: "PICKING_UP",
      },
      {
        orderId: 5,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-19T22:14:07",
        totalFee: 2000000,
        status: "IN_TRANSIT",
      },
      {
        orderId: 6,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-19T22:20:16",
        totalFee: 0,
        status: "DELIVERED",
      },
      {
        orderId: 7,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-20T22:32:11",
        totalFee: 1500000,
        status: "CANCELED",
      },
      {
        orderId: 8,
        originLocation: "Phường Tăng Nhơn Phú B, Thủ Đức, Ho Chi Minh City",
        destinationLocation: "Nhà thờ Tân Phú, Nguyễn Hậu, Tan Thanh Ward",
        createdAt: "2024-10-20T22:37:05",
        totalFee: 400000,
        status: "COMMIT_FEE_PENDING",
      }
    ];
  
    // Set static orders data
    setOrders(testOrders);
    setLoading(false); // Set loading to false after data is ready
  }, []);
  

  const goToOrderDetail = (order) => {
    if (order && order.orderId) {
      navigate(`/apphomepage/history/detail/${order.orderId}`, {
        state: order,
      });
    } else {
      console.error("Order or Order ID is missing");
    }
  };

  const handleTimeFilterClick = () => {
    setTempSelectedTimeFilter(selectedTimeFilter);
    setIsTimeFilterExpanded(!isTimeFilterExpanded);
  };

  const handleTimeFilterSelect = (filter) => {
    setTempSelectedTimeFilter(filter);
    if (filter !== "custom") {
      setCustomDateRange({ from: "", to: "" });
    }
  };

  const handleApplyFilter = () => {
    setSelectedTimeFilter(tempSelectedTimeFilter);
    setIsTimeFilterExpanded(false);

    const now = new Date();
    let fromDate, toDate;
    switch (tempSelectedTimeFilter) {
      case "today":
        fromDate = toDate = now.toLocaleDateString("vi-VN");
        setDisplayDateRange(`Ngày: ${fromDate}`);
        break;
      case "this-week":
        const startOfWeek = new Date(now);
        startOfWeek.setDate(now.getDate() - now.getDay());
        const endOfWeek = new Date(now);
        endOfWeek.setDate(now.getDate() + (6 - now.getDay()));
        fromDate = startOfWeek.toLocaleDateString("vi-VN");
        toDate = endOfWeek.toLocaleDateString("vi-VN");
        setDisplayDateRange(`Tuần này: ${fromDate} - ${toDate}`);
        break;
      case "this-month":
        const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
        const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        fromDate = startOfMonth.toLocaleDateString("vi-VN");
        toDate = endOfMonth.toLocaleDateString("vi-VN");
        setDisplayDateRange(`Tháng này: ${fromDate} - ${toDate}`);
        break;
      case "custom":
        if (customDateRange.from && customDateRange.to) {
          fromDate = new Date(customDateRange.from).toLocaleDateString("vi-VN");
          toDate = new Date(customDateRange.to).toLocaleDateString("vi-VN");
          setDisplayDateRange(`Tùy chỉnh: ${fromDate} - ${toDate}`);
        }
        break;
      default:
        setDisplayDateRange("Tất cả");
        break;
    }
  };

  const handleCloseFilter = () => {
    setIsTimeFilterExpanded(false);
  };

  const handleCustomDateChange = (field, value) => {
    setCustomDateRange({ ...customDateRange, [field]: value });
  };

  const filterOrders = () => {
    let filteredOrders = Array.isArray(orders) ? orders : [];

    // Filter by selected tab (status)
    if (selectedTab !== "Tất cả") {
      filteredOrders = filteredOrders.filter(
        (order) => getVietnameseStatus(order.status) === selectedTab
      );
    }

    // Filter by selected time filter (today, this week, etc.)
    if (selectedTimeFilter !== "all") {
      filteredOrders = filteredOrders.filter((order) => {
        const orderDate = new Date(order.createdAt);
        if (selectedTimeFilter === "today") {
          const today = new Date();
          return orderDate.toDateString() === today.toDateString();
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
          if (customDateRange.from && customDateRange.to) {
            const from = new Date(customDateRange.from);
            const to = new Date(customDateRange.to);
            return orderDate >= from && orderDate <= to;
          }
          return false;
        }
        return true;
      });
    }

    // Filter by search query
    if (searchQuery) {
      filteredOrders = filteredOrders.filter((order) => {
        const orderId = order.orderId ? order.orderId.toString() : ""; // Convert orderId to a string
        const originLocation = order.originLocation
          ? order.originLocation.toLowerCase()
          : "";
        const destinationLocation = order.destinationLocation
          ? order.destinationLocation.toLowerCase()
          : "";

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
                Lịch sử đơn hàng
              </h1>
            </div>

            <div className="flex mb-6 space-x-4 overflow-x-auto">
              {Object.keys(statusColors).map((tab, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedTab(tab)}
                  className={`px-4 py-2 rounded-full transition duration-300 text-sm ${
                    selectedTab === tab
                      ? "font-bold shadow-md bg-blue-100 text-blue-900"
                      : "text-blue-700 bg-transparent"
                  }`}
                  style={{
                    backgroundColor:
                      selectedTab === tab
                        ? statusColors[tab].background
                        : "transparent",
                    color:
                      selectedTab === tab ? statusColors[tab].text : "black",
                  }}
                >
                  {tab}
                </button>
              ))}
            </div>

            <div className="flex items-center mb-6 space-x-6">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Tìm kiếm đơn hàng..."
                className="w-full max-w-md p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />

              <div className="relative">
                <button
                  onClick={handleTimeFilterClick}
                  className={`flex items-center p-2 text-sm transition duration-300 rounded-lg shadow-sm ${
                    isTimeFilterExpanded
                      ? "bg-blue-100 text-blue-900"
                      : "text-blue-700 bg-transparent"
                  }`}
                >
                  <span>{displayDateRange || "Tất cả"}</span>
                </button>
                {isTimeFilterExpanded && (
                  <div className="absolute left-0 w-64 p-4 mt-2 bg-white border border-blue-300 rounded-lg shadow-lg">
                    {["all", "today", "this-week", "this-month", "custom"].map(
                      (filter) => (
                        <div key={filter} className="mb-2">
                          <label className="flex items-center text-sm">
                            <input
                              type="radio"
                              name="timeFilter"
                              value={filter}
                              checked={tempSelectedTimeFilter === filter}
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
                      )
                    )}
                    {tempSelectedTimeFilter === "custom" && (
                      <div className="flex flex-col mt-4 space-y-4">
                        <div className="relative">
                          <label className="block mb-1 text-sm font-medium text-gray-600">
                            Từ ngày:
                          </label>
                          <input
                            type="date"
                            value={customDateRange.from}
                            max={new Date().toISOString().split("T")[0]}
                            onChange={(e) =>
                              handleCustomDateChange("from", e.target.value)
                            }
                            className="w-full p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                        <div className="relative">
                          <label className="block mb-1 text-sm font-medium text-gray-600">
                            Đến ngày:
                          </label>
                          <input
                            type="date"
                            value={customDateRange.to}
                            min={customDateRange.from}
                            max={new Date().toISOString().split("T")[0]}
                            onChange={(e) =>
                              handleCustomDateChange("to", e.target.value)
                            }
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
            {filterOrders().length === 0 ? (
              <div className="text-center text-gray-500">
                Không tìm thấy đơn hàng
              </div>
            ) : (
              <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                <thead className="sticky top-0 z-10 bg-blue-100">
                  <tr className="text-blue-900 border-b border-blue-200">
                    <th className="p-2 font-semibold w-1/8">Mã đơn hàng</th>
                    <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/3 p-2 font-semibold">Điểm giao hàng</th>
                    <th className="p-2 font-semibold w-1/10">Thời gian tạo</th>
                    <th className="w-1/12 p-2 font-semibold">Tổng COD</th>
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
                            : "N/A"}
                        </td>
                        <td className="p-2 text-center">
                          <span
                            className="inline-block px-4 py-2 text-xs font-semibold rounded-full"
                            style={{
                              backgroundColor: statusColor.background,
                              color: statusColor.text,
                              minWidth: "120px", // Đảm bảo kích thước tối thiểu cho trạng thái đồng nhất
                              textAlign: "center", // Căn giữa văn bản
                              padding: "6px 12px", // Đồng bộ padding
                              whiteSpace: "nowrap", // Ngăn trạng thái xuống dòng
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

export default Order;
