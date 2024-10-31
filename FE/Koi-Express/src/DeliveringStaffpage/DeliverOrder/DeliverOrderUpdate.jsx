import React, { useState } from "react";

const DeliverOrderUpdate = ({ koiQuantity, onClose }) => {
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

  return (
    <div
      className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
      style={{ zIndex: 1000 }}
    >
      <div className="bg-white p-6 rounded-lg shadow-lg w-full w-[400px] relative">
        <h2 className="mb-4 text-lg font-bold">Chi tiết cá</h2>
        <div className="mb-2">Số lượng: {koiQuantity}</div>

        <div className="grid grid-cols-3 gap-4">
          {/* Koi VN Type */}
          <div className="mb-4">
            <strong>Koi VN:</strong>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setVnKoi1)}>-</button>
              <input
                type="text"
                value={vnKoi1}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setVnKoi1)}>+</button>
            </div>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setVnKoi2)}>-</button>
              <input
                type="text"
                value={vnKoi2}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setVnKoi2)}>+</button>
            </div>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setVnKoi3)}>-</button>
              <input
                type="text"
                value={vnKoi3}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setVnKoi3)}>+</button>
            </div>
          </div>

          {/* Koi JP Type */}
          <div className="mb-4">
            <strong>Koi JP:</strong>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setJpKoi1)}>-</button>
              <input
                type="text"
                value={jpKoi1}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setJpKoi1)}>+</button>
            </div>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setJpKoi2)}>-</button>
              <input
                type="text"
                value={jpKoi2}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setJpKoi2)}>+</button>
            </div>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setJpKoi3)}>-</button>
              <input
                type="text"
                value={jpKoi3}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setJpKoi3)}>+</button>
            </div>
          </div>

          {/* Koi EU Type */}
          <div className="mb-4">
            <strong>Koi EU:</strong>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setEuKoi1)}>-</button>
              <input
                type="text"
                value={euKoi1}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setEuKoi1)}>+</button>
            </div>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setEuKoi2)}>-</button>
              <input
                type="text"
                value={euKoi2}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setEuKoi2)}>+</button>
            </div>
            <div className="flex mt-2 space-x-2">
              <button onClick={() => decrement(setEuKoi3)}>-</button>
              <input
                type="text"
                value={euKoi3}
                readOnly
                className="w-12 text-center"
              />
              <button onClick={() => increment(setEuKoi3)}>+</button>
            </div>
          </div>
        </div>

        {/* Display fish status options as buttons */}
        <div className="mt-4">
          <label>Tình trạng cá:</label>
          <select
            value={fishStatus}
            onChange={(e) => setFishStatus(e.target.value)}
            className="w-full p-2 mt-1 border rounded"
          >
            <option>Khỏe mạnh</option>
            <option>Bị thương</option>
            <option>Chết</option>
          </select>
        </div>

        <button
          className="w-full p-2 mt-4 text-white bg-blue-500 rounded"
          onClick={onClose}
        >
          Xác nhận
        </button>
      </div>
    </div>
  );
};

export default DeliverOrderUpdate;