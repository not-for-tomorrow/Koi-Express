import React, { useState, useEffect } from "react";
import axios from "axios";
import { useLocation, useParams } from "react-router-dom";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-routing-machine";
import OrderDetailModal from "./OrderDetailModal";
import LoadingSpinner from "../../components/LoadingSpinner/LoadingSpinner";
import { LOCATIONIQ_KEY } from "../../koi/api/api";

const MAX_RETRIES = 5;
const RETRY_DELAY_MS = 2000;

const OrderDetail = () => {
  const location = useLocation();
  const { orderId } = useParams();
  const orderIdFromLocation = location.state?.orderId || orderId;
  const [orderData, setOrderData] = useState(null);
  const [distance, setDistance] = useState("");
  const [map, setMap] = useState(null);
  const [routeBounds, setRouteBounds] = useState(null);
  const [loadingMap, setLoadingMap] = useState(true);
  const [retryCount, setRetryCount] = useState(0);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchOrderData = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/api/delivering/orders/assigned-orders`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        // Extracting assigned orders from the result array based on orderId
        const assignedOrder = response.data.result.find(order => order.orderId === parseInt(orderIdFromLocation));
        setOrderData(assignedOrder);
      } catch (error) {
        console.error("Failed to fetch order data:", error);
      }
    };

    fetchOrderData();
  }, [orderIdFromLocation, token]);

  const initializeMap = async () => {
    if (!orderData?.originLocation || !orderData?.destinationLocation) return;

    if (map) {
      map.remove();
    }

    const { originLocation, destinationLocation } = orderData;

    try {
      const pickupResponse = await axios.get(
        `https://us1.locationiq.com/v1/search.php?key=${LOCATIONIQ_KEY}&q=${originLocation}&format=json`
      );
      const deliveryResponse = await axios.get(
        `https://us1.locationiq.com/v1/search.php?key=${LOCATIONIQ_KEY}&q=${destinationLocation}&format=json`
      );

      const pickup = pickupResponse.data[0];
      const delivery = deliveryResponse.data[0];

      const newMap = L.map("map").setView([pickup.lat, pickup.lon], 10);
      setMap(newMap);

      L.tileLayer(
        `https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=${LOCATIONIQ_KEY}`,
        {
          attribution: '&copy; <a href="https://locationiq.com">LocationIQ</a> contributors',
        }
      ).addTo(newMap);

      L.marker([pickup.lat, pickup.lon]).addTo(newMap);
      L.marker([delivery.lat, delivery.lon]).addTo(newMap);

      const routingService = L.Routing.osrmv1();
      routingService.route(
        [
          L.Routing.waypoint(L.latLng(pickup.lat, pickup.lon)),
          L.Routing.waypoint(L.latLng(delivery.lat, delivery.lon)),
        ],
        (err, routes) => {
          if (!err && routes && routes[0]) {
            const route = routes[0];
            setDistance((route.summary.totalDistance / 1000).toFixed(2) + " km");

            const routePolyline = L.polyline(route.coordinates, {
              color: "blue",
              weight: 4,
            }).addTo(newMap);
            const bounds = routePolyline.getBounds();
            setRouteBounds(bounds);

            newMap.fitBounds(bounds);
            setLoadingMap(false);
          } else {
            console.error("Failed to calculate route:", err);
            retryMapLoad();
          }
        }
      );
    } catch (error) {
      console.error("Failed to fetch coordinates:", error);
      retryMapLoad();
    }
  };

  const retryMapLoad = () => {
    if (retryCount < MAX_RETRIES) {
      setTimeout(() => {
        setRetryCount(retryCount + 1);
        initializeMap();
      }, RETRY_DELAY_MS);
    } else {
      setLoadingMap(false);
      console.error("Failed to load the map after multiple attempts.");
    }
  };

  useEffect(() => {
    setLoadingMap(true);
    setRetryCount(0);
    initializeMap();
  }, [orderData]);

  const handleFitBounds = () => {
    if (map && routeBounds) {
      map.fitBounds(routeBounds);
    }
  };

  if (!orderData) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <LoadingSpinner />
      </div>
    );
  }

  const {
    originLocation,
    destinationLocation,
    status,
    paymentMethod,
    orderDetail: {
      senderName,
      senderPhone,
      recipientName,
      recipientPhone,
      distanceFee,
      commitmentFee,
    },
    deliveringStaff: { fullName },
  } = orderData;

  return (
    <div className="flex min-h-screen bg-white">
      <div className="w-1/3">
        <OrderDetailModal
          orderId={orderIdFromLocation}
          fullName={fullName}
          originLocation={originLocation}
          destinationLocation={destinationLocation}
          senderName={senderName}
          senderPhone={senderPhone}
          recipientName={recipientName}
          recipientPhone={recipientPhone}
          distance={distance}
          status={status}
          paymentMethod={paymentMethod}
          distanceFee={distanceFee}
          commitmentFee={commitmentFee}
        />
      </div>

      <div className="relative w-2/3">
        <div id="map" className="absolute top-0 left-0 w-full h-full"></div>
        {loadingMap && (
          <div className="absolute top-0 left-0 z-50 flex items-center justify-center w-full h-full bg-white bg-opacity-75">
            <LoadingSpinner />
          </div>
        )}
        <button
          onClick={handleFitBounds}
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
      </div>
    </div>
  );
};

export default OrderDetail;
