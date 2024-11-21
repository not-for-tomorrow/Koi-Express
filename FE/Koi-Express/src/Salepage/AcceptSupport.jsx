import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";

const AcceptSupport = () => {
  const [state, setState] = useState({
    loading: true,
    error: null,
    supportRequests: [],
    selectedStatus: "Tất cả",
  });

  const navigate = useNavigate();

  const statusColors = {
    PENDING: { background: "rgba(254, 240, 138, 0.2)", text: "#854D0E" },
    IN_PROGRESS: { background: "rgba(191, 219, 254, 0.2)", text: "#1E3A8A" },
    RESOLVED: { background: "rgba(187, 247, 208, 0.2)", text: "#065F46" },
  };

  const vietnameseStatusMapping = {
    PENDING: "Đang chờ",
    IN_PROGRESS: "Đang xử lý",
    RESOLVED: "Đã giải quyết",
  };

  useEffect(() => {
    const fetchSupportRequests = async () => {
      try {
        setState((prev) => ({ ...prev, loading: true }));
        const token = localStorage.getItem("token");
        if (!token) {
          throw new Error("Token not found. Please log in.");
        }

        const response = await axios.get(
          "http://localhost:8080/api/support-request/all",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setState((prev) => ({
          ...prev,
          supportRequests: response.data.result || [],
          loading: false,
        }));
      } catch (err) {
        setState((prev) => ({
          ...prev,
          error: err.message || "Failed to fetch support requests",
          loading: false,
        }));
      }
    };

    fetchSupportRequests();
  }, []);

  const filterSupportRequests = () => {
    const { supportRequests, selectedStatus } = state;

    return supportRequests.filter(
      (request) =>
        selectedStatus === "Tất cả" ||
        vietnameseStatusMapping[request.supportRequestsStatus] ===
          selectedStatus
    );
  };

  const filteredRequests = filterSupportRequests();

  const goToOrderDetail = (request) => {
    if (request && request.order && request.order.orderId) {
      navigate(`/salepage/acceptsupport/detail/${request.order.orderId}`, {
        state: {
          requestId: request.requestId,
          orderData: request.order,
          supportRequestData: request,
          supportRequestsStatus: request.supportRequestsStatus, // Pass the status here
        },
      });
    } else {
      console.error("Order hoặc Order ID bị thiếu");
      alert("Không thể mở chi tiết đơn hàng");
    }
  };

  return (
    <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
      {state.loading ? (
        <div className="text-sm text-center">Loading...</div>
      ) : state.error ? (
        <div className="text-sm text-center text-red-500">{state.error}</div>
      ) : (
        <div className="p-8 text-sm bg-white rounded-lg shadow-lg">
          <h1 className="text-2xl font-bold text-gray-800 mb-6">
            Lịch sử hỗ trợ
          </h1>

          {/* Status Filter */}
          <div className="flex mb-6 space-x-4 overflow-x-auto">
            {["Tất cả", ...Object.values(vietnameseStatusMapping)].map(
              (status, index) => (
                <button
                  key={index}
                  onClick={() =>
                    setState((prev) => ({ ...prev, selectedStatus: status }))
                  }
                  className={`px-4 py-2 rounded-full transition duration-300 text-sm ${
                    state.selectedStatus === status
                      ? "font-bold shadow-md bg-blue-100 text-blue-900"
                      : "text-blue-700 bg-transparent"
                  }`}
                >
                  {status}
                </button>
              )
            )}
          </div>

          {/* Table */}
          <div className="overflow-auto max-h-[63.5vh] text-sm">
            {filteredRequests.length === 0 ? (
              <div className="text-center text-gray-500">
                Không tìm thấy yêu cầu hỗ trợ
              </div>
            ) : (
              <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                <thead className="sticky top-0 z- 10 bg-blue-100">
                  <tr className="text-blue-900 border-b border-blue-200">
                    <th className="p-2 font-semibold w-1/10">Mã yêu cầu</th>
                    <th className="p-2 font-semibold w-1/8">Mã đơn</th>
                    <th className="w-1/4 p-2 font-semibold">Điểm lấy hàng</th>
                    <th className="w-1/4 p-2 font-semibold">Điểm giao hàng</th>
                    <th className="p-2 font-semibold w-1/10">Ngày tạo</th>
                    <th className="w-1/4 p-2 font-semibold">Nội dung</th>
                    <th className="p-2 font-semibold text-center w-1/10">
                      Trạng thái
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filteredRequests.map((request, index) => {
                    const statusColor = statusColors[
                      request.supportRequestsStatus
                    ] || {
                      background: "#f0f0f0",
                      text: "#000",
                    };

                    return (
                      <tr
                        key={index}
                        className="transition duration-300 border-b border-gray-200 hover:bg-blue-50"
                        onClick={() => goToOrderDetail(request)}
                      >
                        <td className="p-2 font-semibold text-blue-600">
                          {request.requestId}
                        </td>
                        <td className="p-2 font-semibold text-blue-600">
                          {request.order.orderId}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {request.order.originLocation}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {request.order.destinationLocation}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {new Date(request.createdAt).toLocaleString("vi-VN")}
                        </td>
                        <td className="p-2 text-sm text-gray-700">
                          {request.description}
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
                            {
                              vietnameseStatusMapping[
                                request.supportRequestsStatus
                              ]
                            }
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

export default AcceptSupport;