import React, { useEffect } from "react";
import { useMap } from "react-leaflet";
import L from "leaflet";
import "leaflet-routing-machine";
import "leaflet/dist/leaflet.css";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";

const RoutingControl = ({ pickupLocation, deliveryLocation, setDistance }) => {
  const map = useMap();

  useEffect(() => {
    if (pickupLocation && deliveryLocation) {
      // Tạo RoutingControl để vẽ tuyến đường mà không có bảng điều khiển chỉ dẫn
      const routingControl = L.Routing.control({
        waypoints: [L.latLng(pickupLocation), L.latLng(deliveryLocation)],
        lineOptions: {
          styles: [{ color: "blue", opacity: 0.6, weight: 4 }],
        },
        createMarker: () => null,
        routeWhileDragging: true,
        addWaypoints: false, // Không cho phép thêm điểm
        show: false, // Không hiển thị bảng chỉ dẫn
        addControl: false, // Tắt bảng điều khiển mặc định
        showAlternatives: false,
      }).addTo(map);

      // Bắt sự kiện khi tìm được tuyến đường để tính toán khoảng cách
      routingControl.on("routesfound", (e) => {
        const route = e.routes[0];
        if (route) {
          const distanceInKm = route.summary.totalDistance / 1000; // Convert distance to km
          setDistance(distanceInKm);
        }
      });

      return () => {
        if (routingControl) {
          map.removeControl(routingControl);
        }
      };
    }
  }, [pickupLocation, deliveryLocation, map, setDistance]);

  return null;
};

export default RoutingControl;
