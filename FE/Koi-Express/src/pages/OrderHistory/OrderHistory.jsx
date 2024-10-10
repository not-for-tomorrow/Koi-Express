import React, { useState, useEffect } from "react";
import axios from "axios";

const OrderHistory = () => {
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

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const token = localStorage.getItem("token");
        if (!token) {
          throw new Error("Token not found. Please log in.");
        }

        const response = await axios.get("http://localhost:8080/api/orders/history", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        console.log("Order history response:", response.data);

        setOrders(response.data.result || []); // Cập nhật orders từ API
        console.log("Orders state after set:", response.data.data);
        setLoading(false);
      } catch (err) {
        setError(err.message || "Failed to fetch orders");
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

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
    let filteredOrders = orders;

    if (selectedTab !== "Tất cả") {
      filteredOrders = filteredOrders.filter(
        (order) => order.status === selectedTab
      );
    }

    if (selectedTimeFilter !== "all") {
      filteredOrders = filteredOrders.filter((order) => {
        const orderDate = new Date(order.date);
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

    if (searchQuery) {
      filteredOrders = filteredOrders.filter((order) => {
        return (
          order.id.includes(searchQuery) ||
          order.pickup.toLowerCase().includes(searchQuery.toLowerCase()) ||
          order.delivery.toLowerCase().includes(searchQuery.toLowerCase())
        );
      });
    }

    return Array.isArray(filteredOrders) ? filteredOrders : [];
  };

  // Define color mapping for status buttons
  const statusColors = {
    "Tất cả": { background: "#3B82F6", text: "#FFFFFF", darkText: "#1E3A8A" }, // Blue
    "PENDING": { background: "#FEF08A", text: "#1F2937", darkText: "#854D0E" }, // Yellow
    "ACCEPTED": { background: "#E9D5FF", text: "#1F2937", darkText: "#5B21B6" }, // Purple
    "PICKING_UP": { background: "#99F6E4", text: "#1F2937", darkText: "#0D9488" }, // Teal
    "IN_TRANSIT": { background: "#BFDBFE", text: "#1F2937", darkText: "#1E3A8A" }, // Light Blue
    "DELIVERED": { background: "#BBF7D0", text: "#1F2937", darkText: "#065F46" }, // Light Green
    "CANCELED": { background: "#FECACA", text: "#1F2937", darkText: "#991B1B" }, // Light Red
  };

  return (
    <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
      {loading ? (
        <div className="text-center">Loading...</div> // Hiển thị khi đang tải dữ liệu
      ) : error ? (
        <div className="text-center text-red-500">{error}</div> // Hiển thị khi có lỗi
      ) : (
        <div className="p-8 bg-white rounded-lg shadow-lg">
          {/* Header Section */}
          <div className="sticky top-0 z-20 bg-white">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-3xl font-bold text-gray-800">
                Lịch sử đơn hàng
              </h1>
            </div>

            {/* Tabs Section */}
            <div className="flex mb-6 space-x-4 overflow-x-auto">
              {Object.keys(statusColors).map((tab, index) => (
                <button
                  key={index}
                  onClick={() => setSelectedTab(tab)}
                  className={`px-5 py-2 rounded-full transition duration-300 ${
                    selectedTab === tab
                      ? `${statusColors[tab].background} text-white shadow-md`
                      : "bg-blue-100 text-blue-700"
                  }`}
                  style={{
                    backgroundColor: selectedTab === tab ? statusColors[tab].background : "transparent",
                    color: selectedTab === tab ? statusColors[tab].darkText : "black",
                    fontWeight: selectedTab === tab ? "bold" : "normal",
                  }}
                  onMouseEnter={(e) => {
                    if (selectedTab !== tab) {
                      e.currentTarget.style.backgroundColor = statusColors[tab].background;
                      e.currentTarget.style.color = statusColors[tab].darkText;
                      e.currentTarget.style.fontWeight = "bold";
                    }
                  }}
                  onMouseLeave={(e) => {
                    if (selectedTab !== tab) {
                      e.currentTarget.style.backgroundColor = "transparent";
                      e.currentTarget.style.color = "black";
                      e.currentTarget.style.fontWeight = "normal";
                    }
                  }}
                >
                  {tab}
                </button>
              ))}
            </div>

            {/* Search and Filter Section */}
            <div className="flex items-center mb-6 space-x-6">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Tìm kiếm đơn hàng..."
                className="w-full max-w-md p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />

             
              <div className="relative">
                <button
                  onClick={handleTimeFilterClick}
                  className="flex items-center p-3 text-blue-700 transition duration-300 bg-blue-100 rounded-lg shadow-sm hover:bg-blue-200"
                >
                  <span>{displayDateRange || "Tất cả"}</span>
                </button>
                {isTimeFilterExpanded && (
                  <div className="absolute left-0 w-64 p-4 mt-2 bg-white border border-blue-300 rounded-lg shadow-lg">
                    {["all", "today", "this-week", "this-month", "custom"].map(
                      (filter) => (
                        <div key={filter} className="mb-2">
                          <label className="flex items-center">
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
                          <label className="block mb-1 font-medium text-gray-600">
                            Từ ngày:
                          </label>
                          <input
                            type="date"
                            value={customDateRange.from}
                            max={new Date().toISOString().split("T")[0]}
                            onChange={(e) =>
                              handleCustomDateChange("from", e.target.value)
                            }
                            className="w-full p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                        <div className="relative">
                          <label className="block mb-1 font-medium text-gray-600">
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
                            className="w-full p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                          />
                        </div>
                      </div>
                    )}
                    <div className="flex justify-end mt-4 space-x-3">
                      <button
                        onClick={handleCloseFilter}
                        className="px-4 py-2 text-sm font-semibold text-gray-700 transition duration-300 bg-gray-100 rounded-lg hover:bg-gray-200"
                      >
                        Đóng
                      </button>
                      <button
                        onClick={handleApplyFilter}
                        className="px-4 py-2 text-sm font-semibold text-white transition duration-300 bg-blue-500 rounded-lg hover:bg-blue-600"
                      >
                        Áp dụng
                      </button>
                    </div>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* Orders Table */}
          <div className="overflow-auto max-h-[63.5vh]">
            {orders.length === 0 ? (
              <div className="text-center text-gray-500">
                Không tìm thấy đơn hàng
              </div> // Hiển thị khi không có đơn hàng
            ) : (
              <table
                className="w-full text-left border-collapse shadow-md table-auto"
                style={{ tableLayout: "fixed" }}
              >
                <thead className="sticky top-0 z-10 bg-blue-100">
                  <tr className="text-blue-900 border-b border-blue-200">
                    <th className="p-4 font-semibold w-1/8">Mã đơn hàng</th>
                    <th className="w-1/4 p-4 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/3 p-4 font-semibold">Điểm giao hàng</th>
                    <th className="p-4 font-semibold w-1/10">Thời gian tạo</th>
                    <th className="w-1/12 p-4 font-semibold">Tổng COD</th>
                    <th className="p-4 font-semibold text-center w-1/9">
                      Trạng thái
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filterOrders().map((order, index) => (
                    <tr
                      key={index}
                      className="transition duration-300 border-b border-gray-200 hover:bg-blue-50"
                    >
                      <td className="p-4 font-semibold text-blue-600">
                        {order.orderId}
                      </td>
                      <td className="p-4 text-sm text-gray-700">{order.originLocation}</td>
                      <td className="p-4 text-sm text-gray-700">{order.destinationLocation}</td>
                      <td className="p-4 text-sm text-gray-700">
                        {new Date(order.createdAt).toLocaleString("vi-VN")}
                      </td>
                      <td className="p-4 text-sm font-medium text-blue-600">
                      đ {order.totalFee.toLocaleString("vi-VN")}
                      </td>
                      <td className="p-4 text-center whitespace-nowrap">
                        <span
                          className="inline-block px-4 py-2 text-sm font-semibold rounded-full"
                          style={{
                            backgroundColor:
                              order.status === "DELIVERED"
                                ? "#BBF7D0"
                                : order.status === "CANCELED"
                                ? "#FECACA"
                                : order.status === "IN_TRANSIT"
                                ? "#BFDBFE"
                                : order.status === "PICKING_UP"
                                ? "#99F6E4"
                                : order.status === "PENDING"
                                ? "#FEF08A"
                                : order.status === "ACCEPTED"
                                ? "#E9D5FF"
                                : "#E5E7EB",
                            color:
                              order.status === "DELIVERED"
                                ? "#065F46"
                                : order.status === "CANCELED"
                                ? "#991B1B"
                                : order.status === "IN_TRANSIT"
                                ? "#1E3A8A"
                                : order.status === "PICKING_UP"
                                ? "#0D9488"
                                : order.status === "PENDING"
                                ? "#854D0E"
                                : order.status === "ACCEPTED"
                                ? "#5B21B6"
                                : "#374151",
                          }}
                        >
                          {order.status}
                        </span>
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

export default OrderHistory;
