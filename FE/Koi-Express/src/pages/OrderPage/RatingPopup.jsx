import React, { useState, useEffect } from "react";
import jwt_decode from "jwt-decode";

const RatingPopup = ({ isOpen, onClose, onSubmit, orderId }) => { // Nhận thêm prop orderId
  const [selectedRating, setSelectedRating] = useState(0);
  const [comment, setComment] = useState("");
  const [selectedFeedback, setSelectedFeedback] = useState([]);
  const [loading, setLoading] = useState(false);
  const [customerId, setCustomerId] = useState(null);

  const feedbackOptions = [
    { id: 1, label: "Nhiệt tình", icon: "🔥", value: "NHIET_TINH" },
    { id: 2, label: "Nhanh chóng", icon: "⏱️", value: "NHANH_CHONG" },
    { id: 3, label: "Cẩn thận", icon: "📦", value: "CAN_THAN" },
    { id: 4, label: "Dễ thương", icon: "😊", value: "DE_THUONG" },
    { id: 5, label: "Tuyệt vời", icon: "👍", value: "TUYET_VOI" },
    { id: 6, label: "Lịch sự", icon: "⭐", value: "LICH_SU" },
];


  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const decoded = jwt_decode(token);
        setCustomerId(decoded.customerId); 
      } catch (error) {
        console.error("Error decoding token:", error);
      }
    }
  }, []);

  const handleFeedbackSelect = (id) => {
    setSelectedFeedback((prev) =>
        prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleSubmit = async () => {
    if (selectedRating === 0) {
      alert("Vui lòng chọn số sao đánh giá.");
      return;
    }
    if (comment.length < 6) {
      alert("Vui lòng nhập ít nhất 6 ký tự cho bình luận.");
      return;
    }

    const feedbackData = {
      rating: selectedRating,
      tags: selectedFeedback.map(
          (id) => feedbackOptions.find((option) => option.id === id).value
      ),
      comments: comment,
      customerId: customerId,
      orderId: orderId,
  };
  

    try {
      setLoading(true);
      const token = localStorage.getItem("token");
      const response = await fetch("http://localhost:8080/api/feedback/submitFeedback", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(feedbackData),
      });

      if (response.ok) {
        const result = await response.json();
        console.log("Feedback submitted successfully:", result);
        alert("Phản hồi đã được gửi thành công!");
        onClose();
        setSelectedRating(0);
        setComment("");
        setSelectedFeedback([]);
      } else {
        const errorText = await response.text();
        console.error("Failed to submit feedback:", errorText);
        alert(`Không thể gửi phản hồi. Lỗi: ${errorText}`);
      }
    } catch (error) {
      console.error("Error:", error);
      alert("Đã xảy ra lỗi khi gửi phản hồi.");
    } finally {
      setLoading(false);
    }
  };


  if (!isOpen) return null;

  return (
      <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30">
        <div className="relative w-full max-w-md p-6 bg-white rounded-lg shadow-lg">
          <button
              className="absolute text-2xl text-gray-500 top-4 right-4 hover:text-gray-700"
              onClick={onClose}
              aria-label="Close"
          >
            ✖️
          </button>

          <h2 className="mb-4 text-xl font-semibold">Đánh giá</h2>

          {/* Star Rating */}
          <div className="flex items-center space-x-1 text-3xl" aria-label="Rating">
            {[...Array(5)].map((_, index) => (
                <span
                    key={index}
                    role="button"
                    aria-label={`Rate ${index + 1} stars`}
                    className={`cursor-pointer ${index < selectedRating ? "text-yellow-500" : "text-gray-400"}`}
                    onClick={() => setSelectedRating(index + 1)}
                >
              ⭐
            </span>
            ))}
          </div>

          {/* Feedback Options */}
          <p className="mt-4 text-gray-500">Ưu tiên giao đơn hàng tiếp theo với tài xế mà bạn yêu thích</p>
          <button
              className="flex items-center justify-center px-4 py-2 mt-2 text-sm text-gray-600 border border-gray-300 rounded-md"
          >
            ❤️ Thêm tài xế yêu thích
          </button>

          <div className="mt-4 text-lg font-semibold">Dịch vụ hoàn hảo</div>
          <div className="flex flex-wrap mt-2">
            {feedbackOptions.map((option) => (
                <div
                    key={option.id}
                    onClick={() => handleFeedbackSelect(option.id)}
                    className={`flex flex-col items-center justify-center w-1/3 p-2 cursor-pointer ${
                        selectedFeedback.includes(option.id) ? "text-blue-500" : "text-gray-600"
                    }`}
                    role="button"
                    aria-pressed={selectedFeedback.includes(option.id)}
                >
                  <span className="text-2xl">{option.icon}</span>
                  <span className="mt-1 text-xs">{option.label}</span>
                </div>
            ))}
          </div>

          {/* Comment Input */}
          <textarea
              value={comment}
              onChange={(e) => setComment(e.target.value)}
              placeholder="Nhập bình luận của bạn"
              className="w-full p-2 mt-4 border rounded-md"
              minLength="6"
              aria-label="Comment"
          />

          <p className="mt-1 text-sm text-gray-400">Vui lòng nhập ít nhất 6 ký tự</p>

          {/* Submit Button */}
          <button
              onClick={handleSubmit}
              className={`w-full py-2 mt-4 font-semibold text-white rounded-md ${
                  loading || selectedRating === 0 || comment.length < 6
                      ? "bg-blue-300 cursor-not-allowed"
                      : "bg-blue-500 hover:bg-blue-600 cursor-pointer"
              }`}
              disabled={loading || selectedRating === 0 || comment.length < 6}
          >
            {loading ? "Đang gửi..." : "Gửi"}
          </button>
        </div>
      </div>
  );
};

export default RatingPopup;
