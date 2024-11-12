import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { fetchPendingOrders } from "/src/koi/api/api.js";

const AcceptOrder = () => {
  const [selectedTab] = useState("Tất cả");
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [orders, setOrders] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchOrders = async () => {
      try {
        setLoading(true);
        const ordersData = await fetchPendingOrders();
        setOrders(ordersData);
      } catch (err) {
        setError(err.message || "Failed to fetch orders");
      } finally {
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

    if (selectedTab !== "Tất cả") {
      filteredOrders = filteredOrders.filter(
        (order) => getVietnameseStatus(order.status) === selectedTab
      );
    }

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

  const truncateAddress = (address) => {
    const parts = address.split(",");
    if (parts.length <= 2) return address;
    return `${parts.slice(0, 2).join(", ")}...`;
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
                    <th className="p-2 font-semibold w-1/8">Tên khách hàng</th>
                    <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/4 p-2 font-semibold">Điểm giao hàng</th>
                    <th className="p-2 font-semibold w-1/10">Thời gian tạo</th>
                    <th className="w-1/12 p-2 font-semibold">Phí cam kết</th>
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
                        onClick={() => goToOrderDetail(order)}
                      >
                        <td className="p-2 font-semibold text-blue-600">
                          {order.orderId}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {order.orderDetail.senderName}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {truncateAddress(order.originLocation)}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {truncateAddress(order.destinationLocation)}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {new Date(order.createdAt).toLocaleString("vi-VN")}
                        </td>
                        <td className="p-2 text-sm font-medium text-center text-blue-600 align-middle">
                          {order.totalFee !== null
                            ? `₫ ${order.totalFee.toLocaleString("vi-VN")}`
                            : order.orderDetail.commitmentFee !== null
                            ? `₫ ${order.orderDetail.commitmentFee.toLocaleString(
                                "vi-VN"
                              )}`
                            : "N/A"}
                        </td>

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
