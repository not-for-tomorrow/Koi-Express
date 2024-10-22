// OrderDetailModal.js
import React from "react";

const OrderDetailModal = ({ orderId, order, distance }) => {
  if (!order) {
    return <div>Error: Order not found or missing data.</div>;
  }

  return (
    <div className="w-1/3 p-6 bg-gray-100">
      <h1 className="text-2xl font-bold">Order Details for #{orderId}</h1>
      <p>
        <strong>Điểm lấy hàng (Pickup Location):</strong>{" "}
        {order.originLocation || "N/A"}
      </p>
      <p>
        <strong>Điểm giao hàng (Delivery Location):</strong>{" "}
        {order.destinationLocation || "N/A"}
      </p>
      <p>
        <strong>Total Distance:</strong> {distance.toFixed(2)} km
      </p>
    </div>
  );
};

export default OrderDetailModal;
