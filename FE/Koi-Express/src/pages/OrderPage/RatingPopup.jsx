import React, { useState } from "react";

const RatingPopup = ({ isOpen, onClose, onSubmit }) => {
  const [selectedRating, setSelectedRating] = useState(0);
  const [comment, setComment] = useState("");
  const [selectedFeedback, setSelectedFeedback] = useState([]);

  const feedbackOptions = [
    { id: 1, label: "Nhiệt tình", icon: "🔥" },
    { id: 2, label: "Nhanh chóng", icon: "⏱️" },
    { id: 3, label: "Cẩn thận", icon: "📦" },
    { id: 4, label: "Dễ thương", icon: "😊" },
    { id: 5, label: "Tuyệt vời", icon: "👍" },
    { id: 6, label: "Lịch sự", icon: "⭐" },
  ];

  const handleFeedbackSelect = (id) => {
    setSelectedFeedback((prev) =>
      prev.includes(id) ? prev.filter((item) => item !== id) : [...prev, id]
    );
  };

  const handleSubmit = () => {
    if (comment.length >= 6) {
      onSubmit({ rating: selectedRating, feedback: selectedFeedback, comment });
      onClose();
    } else {
      alert("Vui lòng nhập ít nhất 6 ký tự cho bình luận.");
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-30">
      <div className="relative w-full max-w-md p-6 bg-white rounded-lg shadow-lg">
        {/* Close Button */}
        <button
          className="absolute text-2xl text-gray-500 top-4 right-4 hover:text-gray-700"
          onClick={onClose}
        >
          ✖️
        </button>

        <h2 className="mb-4 text-xl font-semibold">Đánh giá</h2>

        {/* Star Rating */}
        <div className="flex items-center space-x-1 text-3xl">
          {[...Array(5)].map((_, index) => (
            <span
              key={index}
              className={`cursor-pointer ${
                index < selectedRating ? "text-yellow-500" : "text-gray-400"
              }`}
              onClick={() => setSelectedRating(index + 1)}
            >
              ⭐
            </span>
          ))}
        </div>

        {/* Feedback Options */}
        <p className="mt-4 text-gray-500">
          Ưu tiên giao đơn hàng tiếp theo với tài xế mà bạn yêu thích
        </p>
        <button className="flex items-center justify-center px-4 py-2 mt-2 text-sm text-gray-600 border border-gray-300 rounded-md">
          ❤️ Thêm tài xế yêu thích
        </button>

        <div className="mt-4 text-lg font-semibold">Dịch vụ hoàn hảo</div>
        <div className="flex flex-wrap mt-2">
          {feedbackOptions.map((option) => (
            <div
              key={option.id}
              onClick={() => handleFeedbackSelect(option.id)}
              className={`flex flex-col items-center justify-center w-1/3 p-2 cursor-pointer ${
                selectedFeedback.includes(option.id)
                  ? "text-blue-500"
                  : "text-gray-600"
              }`}
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
        />

        <p className="mt-1 text-sm text-gray-400">
          Vui lòng nhập ít nhất 6 ký tự
        </p>

        {/* Submit Button */}
        <button
          onClick={handleSubmit}
          className="w-full py-2 mt-4 font-semibold text-white bg-orange-500 rounded-md hover:bg-orange-600"
        >
          Gửi
        </button>
      </div>
    </div>
  );
};

export default RatingPopup;
