import React, { useState, useEffect, useCallback } from "react";
import axios from "axios";

const Report = ({ isOpen, onClose, orderId }) => {
  const reportReasons = [
    "",
    "Tôi đã đợi cả ngày nhưng chưa có nhân viên giao hàng đến nhận đơn.",
    "Nhân viên giao hàng không liên hệ được",
    "Đơn hàng bị chậm trễ",
    "Khác",
  ];
  const [reportReason, setReportReason] = useState("");
  const [reportDescription, setReportDescription] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [existingReport, setExistingReport] = useState(null);

  // Khóa localStorage riêng cho mỗi đơn hàng
  const LOCAL_STORAGE_KEY = `support_request_${orderId}`;

  // Lưu báo cáo vào localStorage
  const saveReportToLocalStorage = useCallback(
    (reportData) => {
      try {
        localStorage.setItem(
          LOCAL_STORAGE_KEY,
          JSON.stringify({
            ...reportData,
            savedAt: new Date().toISOString(),
          })
        );
      } catch (error) {
        console.error("Error saving report to localStorage:", error);
      }
    },
    [LOCAL_STORAGE_KEY]
  );

  // Khôi phục báo cáo từ localStorage
  const restoreReportFromLocalStorage = useCallback(() => {
    try {
      const savedReport = localStorage.getItem(LOCAL_STORAGE_KEY);
      if (savedReport) {
        return JSON.parse(savedReport);
      }
      return null;
    } catch (error) {
      console.error("Error restoring report from localStorage:", error);
      return null;
    }
  }, [LOCAL_STORAGE_KEY]);

  const handleClose = () => {
    // Reset các trạng thái nếu cần
    setReportReason("");
    setReportDescription("");
    setExistingReport(null);

    // Gọi hàm onClose được truyền từ component cha
    onClose();
  };

  // Kiểm tra báo cáo đã tồn tại
  const checkExistingReport = useCallback(async () => {
    const token = localStorage.getItem("token");
    try {
      // Thử lấy báo cáo từ localStorage trước
      const localReport = restoreReportFromLocalStorage();

      const response = await axios.get(
        `http://localhost:8080/api/support-request/order/${orderId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (response.data && response.data.length > 0) {
        // Lấy báo cáo mới nhất
        const latestReport = response.data[0];

        // Lưu báo cáo vào localStorage
        saveReportToLocalStorage(latestReport);

        setExistingReport(latestReport);

        // Điền thông tin vào form nếu báo cáo chưa được xử lý
        if (latestReport.supportRequestsStatus === "PENDING") {
          setReportReason(latestReport.description);
          setReportDescription(latestReport.additionalDetails || "");
        }
      } else if (localReport) {
        // Nếu không có báo cáo từ server nhưng có trong localStorage
        setExistingReport(localReport);
      }
    } catch (error) {
      console.error("Error checking existing reports:", error);

      // Thử lấy báo cáo từ localStorage nếu gọi API thất bại
      const localReport = restoreReportFromLocalStorage();
      if (localReport) {
        setExistingReport(localReport);
      }
    }
  }, [orderId, saveReportToLocalStorage, restoreReportFromLocalStorage]);

  // Kiểm tra báo cáo khi mở popup
  useEffect(() => {
    if (isOpen) {
      checkExistingReport();
    }
  }, [isOpen, checkExistingReport]);

  const handleSubmitReport = async () => {
    const token = localStorage.getItem("token");
    setIsSubmitting(true);

    try {
      const response = await axios.post(
        `http://localhost:8080/api/support-request/create/${orderId}`,
        {
          description: reportReason,
          additionalDetails: reportDescription,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      alert("Yêu cầu hỗ trợ của bạn đã được gửi thành công!");

      // Lưu báo cáo mới vào localStorage
      saveReportToLocalStorage(response.data);

      // Cập nhật báo cáo hiện tại
      setExistingReport(response.data);

      onClose();
      // Reset form
      setReportReason("");
      setReportDescription("");
    } catch (error) {
      console.error("Lỗi khi gửi yêu cầu hỗ trợ:", error);
      alert("Có lỗi xảy ra. Vui lòng thử lại.");
    } finally {
      setIsSubmitting(false);
    }
  };

  // Hiển thị thông tin báo cáo đã gửi
  const renderExistingReportInfo = () => {
    if (!existingReport) return null;

    const statusMapping = {
      PENDING: "Đang chờ xử lý",
      IN_PROGRESS: "Đang xử lý",
      RESOLVED: "Đã giải quyết",
      CLOSED: "Đã đóng",
    };

    return (
      <div className="mb-4 p-4 bg-yellow-50 border border-yellow-200 rounded-lg">
        <h3 className="font-semibold text-lg mb-2">Thông tin yêu cầu hỗ trợ</h3>
        <div className="space-y-2">
          <p>
            <strong>Mã yêu cầu:</strong> #{existingReport.requestId}
          </p>
          <p>
            <strong>Ngày tạo:</strong>{" "}
            {new Date(existingReport.createdAt).toLocaleString()}
          </p>
          <p>
            <strong>Trạng thái:</strong>{" "}
            <span className="font-bold text-yellow-600">
              {statusMapping[existingReport.supportRequestsStatus] ||
                existingReport.supportRequestsStatus}
            </span>
          </p>
          <p>
            <strong>Nội dung:</strong> {existingReport.description}
          </p>
          {existingReport.additionalDetails && (
            <p>
              <strong>Chi tiết:</strong> {existingReport.additionalDetails}
            </p>
          )}
        </div>
      </div>
    );
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50 p-4">
      <div className="bg-white p-6 rounded-2xl w-full max-w-md shadow-2xl border border-gray-200">
        <div className="flex items-center mb-6">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            className="h-8 w-8 text-red-500 mr-3"
            fill="none"
            viewBox="0 0 24 24"
            stroke="currentColor"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              strokeWidth={2}
              d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
            />
          </svg>
          <h2 className="text-2xl font-bold text-gray-800">Yêu cầu hỗ trợ</h2>
        </div>

        {/* Hiển thị thông tin báo cáo đã tồn tại */}
        {renderExistingReportInfo()}

        {/* Chỉ hiển thị form báo cáo nếu không có báo cáo hoặc báo cáo đang ở trạng thái PENDING */}
        {(!existingReport ||
          (existingReport &&
            existingReport.supportRequestsStatus === "PENDING")) && (
          <div>
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Lý do báo cáo <span className="text-red-500">*</span>
              </label>
              <select
                value={reportReason}
                onChange={(e) => setReportReason(e.target.value)}
                className={`w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500 transition duration-300 
          ${
            existingReport ? "bg-gray-100 cursor-not-allowed text-gray-500" : ""
          }`}
                required
                disabled={!!existingReport}
              >
                {reportReasons.map((reason, index) => (
                  <option key={index} value={reason}>
                    {reason || "Chọn lý do báo cáo"}
                  </option>
                ))}
              </select>
            </div>

            <div className="mb-6">
              <label className="block text-sm font-medium text-gray-700 mb-2">
                Mô tả chi tiết (Tùy chọn)
              </label>
              <textarea
                value={reportDescription}
                onChange={(e) => setReportDescription(e.target.value)}
                rows={4}
                className={`w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-red-500 transition duration-300
          ${
            existingReport ? "bg-gray-100 cursor-not-allowed text-gray-500" : ""
          }`}
                placeholder="Bạn có thể bổ sung thêm thông tin chi tiết để chúng tôi hỗ trợ tốt hơn"
                disabled={!!existingReport}
              />
            </div>

            <div className="flex justify-between space-x-4">
              <button
                onClick={handleClose}
                className="flex-1 px-4 py-2 bg-gray-100 text-gray-800 rounded-md hover:bg-gray-200 transition duration-300 border"
                disabled={isSubmitting}
              >
                Hủy
              </button>
              <button
                onClick={handleSubmitReport}
                disabled={!reportReason || isSubmitting || !!existingReport}
                className={`flex-1 px-4 py-2 rounded-md transition duration-300 
          ${
            !reportReason || isSubmitting || existingReport
              ? "bg-gray-300 text-gray-500 cursor-not-allowed"
              : "bg-red-500 text-white hover:bg-red-600"
          } 
          disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center`}
              >
                {isSubmitting ? (
                  <svg
                    className="animate-spin h-5 w-5 mr-2"
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                  >
                    <circle
                      className="opacity-25"
                      cx="12"
                      cy="12"
                      r="10"
                      stroke="currentColor"
                      strokeWidth="4"
                    ></circle>
                    <path
                      className="opacity-75"
                      fill="currentColor"
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    ></path>
                  </svg>
                ) : null}
                {isSubmitting ? "Đang gửi..." : "Gửi yêu cầu"}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default Report;
