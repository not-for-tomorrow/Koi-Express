import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

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

function RecentOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");

    const fetchOrders = async () => {
      try {
        const response = await fetch(
          "http://localhost:8080/api/orders/all-orders",
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );

        const data = await response.json();

        if (data.code === 200) {
          setOrders(data.result);
        } else {
          console.error("Failed to fetch orders:", data.message);
        }
      } catch (error) {
        console.error("Error fetching orders:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div className="flex-1 px-4 pt-3 pb-4 bg-white border border-gray-200 rounded-sm">
      <strong className="font-medium text-gray-700">Recent Orders</strong>
      <div className="mt-3 overflow-y-auto max-h-80">
        {" "}
        {/* Scrollable area */}
        <table className="w-full text-gray-700 border-separate border-spacing-0">
          <thead className="sticky top-0 bg-white">
            <tr>
              <th className="p-2 text-left border-b border-gray-300">ID</th>
              <th className="p-2 text-left border-b border-gray-300">
                Tên Khách Hàng
              </th>
              <th className="p-2 text-left border-b border-gray-300">
                Ngày đặt hàng
              </th>
              <th className="p-2 text-left border-b border-gray-300">
                Tổng giá tiền
              </th>
              <th className="p-2 text-left border-b border-gray-300">
                Địa chỉ lấy hàng
              </th>
              <th className="p-2 text-left border-b border-gray-300">
                Địa chỉ giao hàng
              </th>
              <th className="p-2 text-left border-b border-gray-300">
                Trạng Thái
              </th>
              <th className="p-2 text-left border-b border-gray-300">
              </th>
            </tr>
          </thead>
          <tbody>
            {orders.map((order) => {
              const status = getVietnameseStatus(order.order.status);
              const { background, text } = statusColors[status] || {
                background: "#FFFFFF",
                text: "#000000",
              };

              return (
                <tr key={order.order.orderId}>
                  <td className="p-2">
                    <Link to={`/managerpage/recentorder/detail/${order.order.orderId}`}>
                      {order.order.orderId}
                    </Link>
                  </td>
                  <td className="p-2">{order.customer.fullName}</td>
                  <td className="p-2">
                    {new Date(order.order.createdAt).toLocaleDateString()}
                  </td>
                  <td className="p-2">
                    {order.order.totalFee ? `$${order.order.totalFee}` : "N/A"}
                  </td>
                  <td className="p-2 max-w-[200px] truncate">
                    {order.order.originLocation}
                  </td>
                  <td className="p-2 max-w-[200px] truncate">
                    {order.order.destinationLocation}
                  </td>
                  <td className="p-2">
                    <span
                      className="px-2 py-1 rounded"
                      style={{
                        backgroundColor: background,
                        color: text,
                      }}
                    >
                      {status}
                    </span>
                  </td>
                  <td className="p-2 text-center">
                    <Link
                      to={`/managerpage/recentorder/detail/${order.order.orderId}`}
                      className="text-blue-500 hover:underline"
                    >
                      Xem Chi Tiết
                    </Link>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </div>
  );
}

export default RecentOrders;
