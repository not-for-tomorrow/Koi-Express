import React, { useState } from "react";

const DeliverOrderUpdate = ({ koiQuantity, onClose }) => {
  const [koiType, setKoiType] = useState("");
  const [fishStatus, setFishStatus] = useState("Khỏe mạnh");

  // State for each type and quantity
  const [vnKoi1, setVnKoi1] = useState(0);
  const [vnKoi2, setVnKoi2] = useState(0);
  const [vnKoi3, setVnKoi3] = useState(0);

  const [jpKoi1, setJpKoi1] = useState(0);
  const [jpKoi2, setJpKoi2] = useState(0);
  const [jpKoi3, setJpKoi3] = useState(0);

  const [euKoi1, setEuKoi1] = useState(0);
  const [euKoi2, setEuKoi2] = useState(0);
  const [euKoi3, setEuKoi3] = useState(0);

  const increment = (setter) => setter((prev) => prev + 1);
  const decrement = (setter) => setter((prev) => (prev > 0 ? prev - 1 : 0));

  const handleKoiTypeSelect = (type) => {
    setKoiType(type);
  };

  return (
    <div
      className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
      style={{ zIndex: 1000 }}
    >
      <div className="bg-white p-6 rounded-lg shadow-lg w-96 relative">
        <h2 className="text-lg font-bold mb-4">Chi tiết cá</h2>
        <div className="mb-2">Số lượng: {koiQuantity}</div>
        <div className="mb-4">
          <strong>Loại cá:</strong>
          <div className="flex space-x-4 mt-2">
            {/* Koi VN Type */}
            <div className="flex flex-col items-center">
              <button
                onClick={() => handleKoiTypeSelect("Koi VN")}
                className={`p-2 rounded w-20 ${
                  koiType === "Koi VN"
                    ? "bg-blue-500 text-white"
                    : "bg-gray-200"
                }`}
              >
                Koi VN
              </button>
              {koiType === "Koi VN" && (
                <>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setVnKoi1)}>-</button>
                    <input
                      type="text"
                      value={vnKoi1}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setVnKoi1)}>+</button>
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setVnKoi2)}>-</button>
                    <input
                      type="text"
                      value={vnKoi2}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setVnKoi2)}>+</button>
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setVnKoi3)}>-</button>
                    <input
                      type="text"
                      value={vnKoi3}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setVnKoi3)}>+</button>
                  </div>
                </>
              )}
            </div>

            {/* Koi JP Type */}
            <div className="flex flex-col items-center">
              <button
                onClick={() => handleKoiTypeSelect("Koi JP")}
                className={`p-2 rounded w-20 ${
                  koiType === "Koi JP"
                    ? "bg-blue-500 text-white"
                    : "bg-gray-200"
                }`}
              >
                Koi JP
              </button>
              {koiType === "Koi JP" && (
                <>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setJpKoi1)}>-</button>
                    <input
                      type="text"
                      value={jpKoi1}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setJpKoi1)}>+</button>
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setJpKoi2)}>-</button>
                    <input
                      type="text"
                      value={jpKoi2}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setJpKoi2)}>+</button>
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setJpKoi3)}>-</button>
                    <input
                      type="text"
                      value={jpKoi3}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setJpKoi3)}>+</button>
                  </div>
                </>
              )}
            </div>

            {/* Koi EU Type */}
            <div className="flex flex-col items-center">
              <button
                onClick={() => handleKoiTypeSelect("Koi EU")}
                className={`p-2 rounded w-20 ${
                  koiType === "Koi EU"
                    ? "bg-blue-500 text-white"
                    : "bg-gray-200"
                }`}
              >
                Koi EU
              </button>
              {koiType === "Koi EU" && (
                <>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setEuKoi1)}>-</button>
                    <input
                      type="text"
                      value={euKoi1}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setEuKoi1)}>+</button>
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setEuKoi2)}>-</button>
                    <input
                      type="text"
                      value={euKoi2}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setEuKoi2)}>+</button>
                  </div>
                  <div className="flex space-x-2 mt-2">
                    <button onClick={() => decrement(setEuKoi3)}>-</button>
                    <input
                      type="text"
                      value={euKoi3}
                      readOnly
                      className="w-12 text-center"
                    />
                    <button onClick={() => increment(setEuKoi3)}>+</button>
                  </div>
                </>
              )}
            </div>
          </div>
        </div>

        <div className="mt-4">
          <label>Tình trạng cá:</label>
          <select
            value={fishStatus}
            onChange={(e) => setFishStatus(e.target.value)}
            className="w-full mt-1 p-2 border rounded"
          >
            <option>Khỏe mạnh</option>
            <option>Bị thương</option>
            <option>Chết</option>
          </select>
        </div>
        <button
          className="mt-4 w-full bg-blue-500 text-white p-2 rounded"
          onClick={onClose}
        >
          Xác nhận
        </button>
      </div>
    </div>
  );
};

export default DeliverOrderUpdate;
