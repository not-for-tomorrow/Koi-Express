import React from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";

const FitBoundsButton = ({ pickupLocation, deliveryLocation }) => {
  const map = useMap();

  const fitBounds = () => {
    const bounds = L.latLngBounds([pickupLocation, deliveryLocation]);
    map.fitBounds(bounds, { padding: [50, 50] });
  };

  return (
    <button
      onClick={fitBounds}
      style={{
        position: "absolute",
        top: "10px",
        right: "10px",
        zIndex: 1000,
        padding: "10px",
        backgroundColor: "#007bff",
        color: "white",
        border: "none",
        borderRadius: "5px",
        cursor: "pointer",
      }}
    >
      Xem toàn bộ tuyến đường
    </button>
  );
};

export default FitBoundsButton;
