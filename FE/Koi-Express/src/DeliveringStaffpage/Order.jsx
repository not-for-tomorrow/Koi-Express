import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const Order = () => {
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
          "http://localhost:8080/api/delivering/orders/assigned-orders",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setOrders(response.data.result || []);
        setLoading(false);
      } catch (err) {
        console.error(
          "Failed to fetch orders: ",
          err.response ? err.response.data : err.message
        );
        setError(err.message || "Failed to fetch orders");
        setLoading(false);
      }
    };

    fetchOrders();
  }, []);

  const goToOrderDetail = (order) => {
    if (order && order.orderId) {
      navigate(`/deliveringstaffpage/Order/detail/${order.orderId}`, {
        state: {
          orderId: order.orderId,
        },
      });
    } else {
      console.error("Order or Order ID is missing");
    }
  };
  

  const filterOrders = () => {
    return orders.filter((order) => {
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
  };

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
                Danh sách đơn hàng được phân công
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
                    <th className="p-2 font-semibold w-1/8">
                      Tên nhân viên giao hàng
                    </th>
                    <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/3 p-2 font-semibold">Điểm giao hàng</th>
                    <th className="p-2 font-semibold w-1/10">Thời gian tạo</th>
                    <th className="w-1/12 p-2 font-semibold">Phí cam kết</th>
                  </tr>
                </thead>
                <tbody>
                  {filterOrders().map((order, index) => {
                    const deliveringStaff = order.deliveringStaff;
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
                          {deliveringStaff.fullName}
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
                        <td className="p-2 text-sm font-medium text-center text-blue-600 align-middle">
                          {order.totalFee !== null
                            ? `₫ ${order.totalFee.toLocaleString("vi-VN")}`
                            : order.orderDetail.commitmentFee !== null
                            ? `₫ ${order.orderDetail.commitmentFee.toLocaleString(
                                "vi-VN"
                              )}`
                            : "N/A"}
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
