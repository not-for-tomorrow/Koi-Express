
import React, { useState, useEffect } from "react";
import { useLocation, useParams } from "react-router-dom";
import OrderDetailModal from "./OrderDetailModal";
import LoadingSpinner from "../../components/LoadingSpinner/LoadingSpinner";
import { fetchOrderData, fetchCoordinates } from "/src/koi/api/api.js";
import { initializeMap } from "/src/koi/utils/mapUtils.js";

const MAX_RETRIES = 5;
const RETRY_DELAY_MS = 2000;

const OrderDetail = () => {
  const location = useLocation();
  const { orderId } = useParams();
  const orderIdFromLocation = location.state?.orderId || orderId;
  const [orderData, setOrderData] = useState(null);
  const [distance, setDistance] = useState("");
  const [map, setMap] = useState(null);
  const [retryCount, setRetryCount] = useState(0);
  const [loadingMap, setLoadingMap] = useState(true);

  const token = localStorage.getItem("token");

  useEffect(() => {
    const fetchOrderDetails = async () => {
      try {
        const data = await fetchOrderData(orderIdFromLocation, token);
        setOrderData(data);
      } catch (error) {
        console.error("Failed to fetch order data:", error);
      }
    };

    fetchOrderDetails();
  }, [orderIdFromLocation, token]);

  const setupMap = async () => {
    if (!orderData?.order?.originLocation || !orderData?.order?.destinationLocation) return;

    if (map) map.remove();

    try {
      const pickup = await fetchCoordinates(orderData.order.originLocation);
      const delivery = await fetchCoordinates(orderData.order.destinationLocation);
      const newMap = initializeMap(pickup, delivery, setDistance, retryMapLoad);
      setMap(newMap);
      setLoadingMap(false);
    } catch (error) {
      console.error("Failed to fetch coordinates:", error);
      retryMapLoad();
    }
  };

  const retryMapLoad = () => {
    if (retryCount < MAX_RETRIES) {
      setTimeout(() => {
        setRetryCount(retryCount + 1);
        setupMap();
      }, RETRY_DELAY_MS);
    } else {
      setLoadingMap(false);
      console.error("Failed to load the map after multiple attempts.");
    }
  };

  useEffect(() => {
    setLoadingMap(true);
    setRetryCount(0);
    setupMap();
  }, [orderData]);

  if (!orderData) {
    return (
        <div className="flex items-center justify-center min-h-screen">
          <LoadingSpinner />
        </div>
    );
  }

  const {
    order: {
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
    },
    customer: { fullName },
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
              onClick={() => map.fitBounds(map.getBounds())}
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
