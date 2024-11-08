import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchAllOrders } from "/src/koi/api/api.js";
import { getDateRangeDisplay } from "/src/koi/utils/timeFilterUtils.js";
import {
  getTranslatedStatus,
  getStatusColor,
  statusColors,
} from "/src/koi/utils/statusUtils.js";
import "/src/css/order.css";

const Order = () => {
  const [isTimeFilterExpanded, setIsTimeFilterExpanded] = useState(false);
  const [selectedTimeFilter, setSelectedTimeFilter] = useState("all");
  const [tempSelectedTimeFilter, setTempSelectedTimeFilter] = useState("all");
  const [customDateRange, setCustomDateRange] = useState({ from: "", to: "" });
  const [displayDateRange, setDisplayDateRange] = useState("");
  const [selectedTab, setSelectedTab] = useState("Chờ xác nhận");
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [orders, setOrders] = useState([]);
  const [showDeliveringStaffColumn, setShowDeliveringStaffColumn] =
    useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const ordersData = await fetchAllOrders();
        setOrders(ordersData);

        // Cập nhật biến hiển thị cột "Người giao hàng"
        const hasDeliveringStaff = ordersData.some(
          (orderWrapper) => orderWrapper.order.deliveringStaff
        );
        setShowDeliveringStaffColumn(hasDeliveringStaff);
      } catch (err) {
        console.error("Failed to fetch orders:", err.message);
        setError(err.message);
      } finally {
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
    setDisplayDateRange(
      getDateRangeDisplay(tempSelectedTimeFilter, customDateRange)
    );
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
        (orderWrapper) =>
          getTranslatedStatus(orderWrapper.order.status) === selectedTab
      );
    }

    // Filter by search query
    if (searchQuery) {
      filteredOrders = filteredOrders.filter((orderWrapper) => {
        const order = orderWrapper.order;
        const orderId = order.orderId ? order.orderId.toString() : "";
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

  const truncateAddress = (address) => {
    const parts = address.split(",");
    if (parts.length <= 2) return address;
    return `${parts.slice(0, 2).join(", ")}...`;
  };

  const totalFeeHeaderTitle = ["Chờ xác nhận", "Đã xác nhận", "Chuẩn bị lấy hàng"].includes(selectedTab)
      ? "Phí cam kết"
      : "Tổng tiền";

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
                Danh sách đơn hàng
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
                        <div>
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
                        <div>
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
                        onClick={() => setIsTimeFilterExpanded(false)}
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
                    <th className="p-2 font-semibold w-1/7">Mã đơn hàng</th>
                    <th className="p-4 font-semibold w-1/8">Tên khách hàng</th>
                    <th className="w-1/4 p-4 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/4 p-4 font-semibold">Điểm giao hàng</th>
                    <th className="p-2 font-semibold w-1/10">Thời gian tạo</th>
                    <th className="w-1/4 p-4 font-semibold text-center w-1/10"> {totalFeeHeaderTitle}</th>
                    {showDeliveringStaffColumn && (
                      <th className="w-1/4 p-2 font-semibold  w-1/10">
                        Người giao hàng
                      </th>
                    )}
                    <th className="p-2 font-semibold text-center w-1/9">
                      Trạng thái
                    </th>
                  </tr>
                </thead>

                <tbody>
                  {filterOrders().map((orderWrapper, index) => {
                    const order = orderWrapper.order;
                    const customer = orderWrapper.customer;
                    const deliveringStaff = order.deliveringStaff;
                    const statusColor = getStatusColor(
                      getTranslatedStatus(order.status)
                    );

                    return (
                      <tr
                        key={index}
                        className="transition duration-300 border-b border-gray-200 cursor-pointer hover:bg-blue-50"
                        onClick={() =>
                          navigate(
                            `/salepage/allorder/detail/${order.orderId}`,
                            {
                              state: { orderId: order.orderId },
                            }
                          )
                        }
                      >
                        <td className="p-2 font-semibold text-blue-600">
                          {order.orderId}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {customer.fullName}
                        </td>
                        <td className="w-1/4 p-2 text-sm text-gray-700">
                          <div className="tooltip">
                            {truncateAddress(order.originLocation)}
                            <span className="tooltip-text">
                              {order.originLocation}
                            </span>
                          </div>
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          <div className="tooltip">
                            {truncateAddress(order.destinationLocation)}
                            <span className="tooltip-text">
                              {order.destinationLocation}
                            </span>
                          </div>
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {new Date(order.createdAt).toLocaleString("vi-VN")}
                        </td>
                        <td className="p-2 text-sm font-medium text-center text-blue-600 align-middle">
                          {order.totalFee !== null
                            ? `₫ ${order.totalFee.toLocaleString("vi-VN")}`
                            : order.orderDetail &&
                              order.orderDetail.commitmentFee
                            ? `₫ ${order.orderDetail.commitmentFee.toLocaleString(
                                "vi-VN"
                              )}`
                            : "N/A"}
                        </td>

                        {showDeliveringStaffColumn && (
                          <td className="p-2 text-sm text-gray-700 text-center">
                            {deliveringStaff ? deliveringStaff.fullName : ""}
                          </td>
                        )}
                        <td className="p-2 text-center">
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
                            {getTranslatedStatus(order.status)}
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
