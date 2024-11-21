import React, { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import {
  getTranslatedStatus,
  getStatusColor,
} from "/src/koi/utils/statusUtils.js";

const OrderDetailModal = () => {
  const navigate = useNavigate();
  const [supportRequests, setSupportRequests] = useState([]);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  // Fetch danh sách yêu cầu hỗ trợ
  const fetchSupportRequests = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(
        "http://localhost:8080/api/sales/support/pending",
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );

      // Kiểm tra và set danh sách yêu cầu
      if (response.data.content && response.data.content.length > 0) {
        setSupportRequests(response.data.content);
        // Tự động chọn request đầu tiên
        setSelectedRequest(response.data.content[0]);
      }
    } catch (error) {
      console.error("Lỗi tải danh sách yêu cầu hỗ trợ:", error);
      alert("Không thể tải danh sách yêu cầu hỗ trợ");
    }
  };

  // Gọi API chấp nhận yêu cầu hỗ trợ
  const handleAcceptSupport = async () => {
    if (!selectedRequest) return;

    const confirmAccept = window.confirm(
      `Bạn chắc chắn muốn chấp nhận yêu cầu hỗ trợ #${selectedRequest.requestId}?`
    );
    if (!confirmAccept) return;

    try {
      setIsLoading(true);
      const token = localStorage.getItem("token");

      const response = await axios.put(
        `http://localhost:8080/api/sales/support/accept/${selectedRequest.requestId}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      alert("Chấp nhận yêu cầu hỗ trợ thành công!");
      navigate("/salepage/acceptsupport");
    } catch (error) {
      console.error("Lỗi chấp nhận yêu cầu hỗ trợ:", error);
      alert(
        error.response?.data?.message ||
          "Không thể chấp nhận yêu cầu hỗ trợ. Vui lòng thử lại."
      );
    } finally {
      setIsLoading(false);
    }
  };

  // Fetch danh sách yêu cầu khi component mount
  useEffect(() => {
    fetchSupportRequests();
  }, []);

  // Nếu không có yêu cầu hỗ trợ
  if (!selectedRequest) {
    return (
      <div className="text-center p-6">
        Không có yêu cầu hỗ trợ nào để xử lý.
      </div>
    );
  }

  // Destructure dữ liệu
  const {
    requestId,
    customer,
    order,
    subject,
    description,
    supportRequestsStatus,
    createdAt,
  } = selectedRequest;

  const orderDetail = order?.orderDetail || {};
  const translatedStatus = getTranslatedStatus(supportRequestsStatus);
  const statusColor = getStatusColor(translatedStatus);

  return (
    <div className="relative z-20 flex flex-col w-full h-full max-w-lg p-6 bg-white border border-gray-200 shadow-lg">
      <div className="flex-grow">
        <div className="mb-4 text-2xl font-bold text-gray-800">
          Yêu cầu hỗ trợ #{requestId} của {customer?.fullName || "Khách hàng"}
        </div>

        <div className="p-4 mb-4 border rounded-lg bg-gray-50">
          <h3 className="mb-2 text-lg font-semibold">Thông tin tài xế</h3>
          <p>
            <strong>Họ tên:</strong> {order.deliveringStaff.fullName}
          </p>
          <p>
            <strong>Số điện thoại:</strong> {order.deliveringStaff.phoneNumber}
          </p>
          <p>
            <strong>Email:</strong> {order.deliveringStaff.email}
          </p>
        </div>

        {/* Phần hiển thị thông tin chi tiết đơn hàng */}
        <div className="mt-6">
          <div className="flex items-start space-x-2">
            <div className="w-4 h-4 mt-1 bg-blue-500 rounded-full"></div>
            <div>
              <p className="text-lg font-bold">
                {orderDetail.senderName} •{" "}
                <span>{orderDetail.senderPhone}</span>
              </p>
              <p className="text-sm text-gray-500">{order.originLocation}</p>
            </div>
          </div>

          <div className="flex items-start mt-6 space-x-2">
            <div className="w-4 h-4 mt-1 bg-green-500 rounded-full"></div>
            <div>
              <p className="text-lg font-bold">
                {orderDetail.recipientName} •{" "}
                <span>{orderDetail.recipientPhone}</span>
              </p>
              <p className="text-sm text-gray-500">
                {order.destinationLocation}
              </p>
            </div>
          </div>
        </div>
      </div>

      <div className="mb-6">
        <strong className="text-gray-600 block mb-2">Tiêu đề:</strong>
        <p className="text-sm">{subject}</p>
      </div>

      <div className="mb-6">
        <strong className="text-gray-600 block mb-2">Mô tả:</strong>
        <p className="text-sm">{description}</p>
      </div>

      {/* Các phần khác của component giữ nguyên */}
      <div className="flex flex-shrink-0 mt-6 space-x-2">
        <button
          onClick={handleAcceptSupport}
          disabled={isLoading}
          className={`w-1/2 p-3 text-base font-semibold text-white transition-all transform rounded-lg 
            ${
              isLoading
                ? "bg-gray-400 cursor-not-allowed"
                : "bg-green-500 hover:bg-green-600"
            }`}
        >
          {isLoading ? "Đang xử lý..." : "Chấp nhận hỗ trợ"}
        </button>

        <button
          onClick={() => navigate("/salepage/acceptsupport")}
          disabled={isLoading}
          className="w-1/2 p-3 text-base font-semibold text-white transition-all transform bg-blue-500 rounded-lg hover:bg-blue-600"
        >
          Đóng
        </button>
      </div>
    </div>
  );
};

export default OrderDetailModal;
