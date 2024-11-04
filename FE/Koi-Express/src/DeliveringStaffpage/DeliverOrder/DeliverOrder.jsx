// DeliverOrder.jsx
import React, { useState, useEffect } from "react";
import axios from "axios";
import L from "leaflet";
import "leaflet/dist/leaflet.css";
import "leaflet-routing-machine";
import DeliverOrderModal from "./DeliverOrderModal";
import InTransitOrderModal from "./InTransitOrderModal";
import LoadingSpinner from "../../components/LoadingSpinner/LoadingSpinner";
import NoOrderToDeliver from "./NoOderToDeliver";
import { LOCATIONIQ_KEY } from "../../koi/api/api";

const DeliverOrder = () => {
  const [orderData, setOrderData] = useState(null);
  const [distance, setDistance] = useState("");
  const [map, setMap] = useState(null);
  const [routeBounds, setRouteBounds] = useState(null);
  const [loadingMap, setLoadingMap] = useState(true);
  const [mapInitialized, setMapInitialized] = useState(false);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchOrderData = async () => {
      try {
        // Thử gọi API pickup-orders trước
        let response = await axios.get(
          `http://localhost:8080/api/delivering/orders/pickup-orders`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );

        // Nếu không có đơn hàng nào trong pickup-orders, gọi API intransit-orders
        if (!response.data.result.length) {
          response = await axios.get(
            `http://localhost:8080/api/delivering/orders/intransit-orders`,
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
        }

        const order = response.data.result.length
          ? response.data.result[0]
          : null;
        setOrderData(order);

        if (order) {
          console.log("Fetched order data:", order);
          const staffId = order.deliveringStaff.staffId;
          const sessionKey = `delivering_staff_${staffId}_pickupOrder`;

          sessionStorage.setItem(
            sessionKey,
            JSON.stringify({
              koiQuantity: order.orderDetail.koiQuantity,
              orderId: order.orderId,
              distanceFee: order.orderDetail.distanceFee,
              commitmentFee: order.orderDetail.commitmentFee,
            })
          );
        } else {
          console.warn("No orders available for delivery.");
        }
      } catch (error) {
        console.error("Failed to fetch order data:", error);
      }
    };

    fetchOrderData();
  }, [token]);

  // Kiểm tra dữ liệu trước khi load bản đồ
  useEffect(() => {
    if (
      !orderData ||
      !orderData.originLocation ||
      !orderData.destinationLocation ||
      mapInitialized
    )
      return;

    const initializeMap = async () => {
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
        setMapInitialized(true); // Set flag to true after initializing

        L.tileLayer(
          `https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=${LOCATIONIQ_KEY}`,
          {
            attribution:
              '&copy; <a href="https://locationiq.com">LocationIQ</a> contributors',
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
              setDistance(
                (route.summary.totalDistance / 1000).toFixed(2) + " km"
              );

              const routePolyline = L.polyline(route.coordinates, {
                color: "blue",
                weight: 4,
              }).addTo(newMap);
              const bounds = routePolyline.getBounds();
              setRouteBounds(bounds);

              newMap.fitBounds(bounds);
              setLoadingMap(false); // Map loaded successfully
            } else {
              console.error("Failed to calculate route:", err);
              setLoadingMap(false);
            }
          }
        );
      } catch (error) {
        console.error("Failed to fetch coordinates:", error);
        setLoadingMap(false);
      }
    };

    setLoadingMap(true);
    initializeMap();
  }, [orderData, mapInitialized]);

  const handleFitBounds = () => {
    if (map && routeBounds) {
      map.fitBounds(routeBounds);
    }
  };

  if (!orderData) {
    return <NoOrderToDeliver />;
  }

  const ModalComponent =
    orderData.status === "IN_TRANSIT" ? InTransitOrderModal : DeliverOrderModal;

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
        <ModalComponent
          orderId={orderData.orderId}
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
          koiQuantity={orderData.orderDetail.koiQuantity}
        />
      </div>
      <div className="relative w-2/3">
        <div
          id="map"
          className="absolute top-0 left-0 w-full h-full"
          style={{ zIndex: 10 }}
        ></div>
        {loadingMap && (
          <div
            className="absolute top-0 left-0 flex items-center justify-center w-full h-full bg-white bg-opacity-75"
            style={{ zIndex: 15 }}
          >
            <LoadingSpinner />
          </div>
        )}
        <button
          onClick={handleFitBounds}
          style={{
            position: "absolute",
            top: "10px",
            right: "10px",
            zIndex: 20, // Set button above the map and loading spinner, but below popup
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

export default DeliverOrder;
