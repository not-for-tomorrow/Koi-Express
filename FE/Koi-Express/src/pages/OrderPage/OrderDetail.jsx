import React, { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router-dom";
import {
  MapContainer as LeafletMap,
  TileLayer,
  Marker,
  useMap,
} from "react-leaflet";
import RoutingControl from "./RoutingControl";
import FitBoundsButton from "./FitBoundsButton";
import axios from "axios";
import OrderDetailModal from "./OrderDetailModal";
import LoadingSpinner from "../../components/LoadingSpinner/LoadingSpinner";

const OrderDetail = () => {
  const { orderId } = useParams(); // Extract orderId from the URL
  const [order, setOrder] = useState(null); // Store the order data fetched from API
  const [pickupLocation, setPickupLocation] = useState(null);
  const [deliveryLocation, setDeliveryLocation] = useState(null);
  const [distance, setDistance] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [retryCount, setRetryCount] = useState(0); // Track retry attempts

  const MAX_RETRIES = 5; // Set a maximum number of retries to avoid infinite loops
  const RETRY_DELAY = 3000; // Retry every 3 seconds

  // Fetch order details from API
  useEffect(() => {
    const fetchOrderDetails = async () => {
      const token = localStorage.getItem("token"); // Retrieve the token from localStorage
      try {
        const response = await axios.get(
          `http://localhost:8080/api/orders/${orderId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`, // Include the token in the request headers
            },
          }
        );
        setOrder(response.data.order); // Set order data from API response
      } catch (error) {
        console.error("Error fetching order details:", error);
      }
    };
    fetchOrderDetails();
  }, [orderId]);

  // Function to get latitude and longitude for an address using LocationIQ API
  const geocodeAddress = async (address) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/search.php",
        {
          params: {
            key: "pk.57eb525ef1bdb7826a61cf49564f8a86", // Replace with your API key
            q: address,
            format: "json",
            limit: 1,
          },
        }
      );
      if (response.data && response.data.length > 0) {
        const location = response.data[0];
        return { lat: parseFloat(location.lat), lng: parseFloat(location.lon) };
      }
      return null;
    } catch (error) {
      console.error("Error geocoding address:", error);
      return null;
    }
  };

  const fetchLocations = async () => {
    if (!pickupLocation && order?.originLocation) {
      const pickupCoords = await geocodeAddress(order.originLocation);
      setPickupLocation(pickupCoords);
    }
    if (!deliveryLocation && order?.destinationLocation) {
      const deliveryCoords = await geocodeAddress(order.destinationLocation);
      setDeliveryLocation(deliveryCoords);
    }
    setIsLoading(false);
  };

  useEffect(() => {
    const retryFetchLocations = async () => {
      if (pickupLocation && deliveryLocation) {
        clearInterval(intervalId);
      } else if (retryCount < MAX_RETRIES) {
        await fetchLocations();
        setRetryCount(retryCount + 1);
      } else {
        console.error("Max retries reached. Could not fetch locations.");
        clearInterval(intervalId);
      }
    };

    const intervalId = setInterval(retryFetchLocations, RETRY_DELAY);

    return () => clearInterval(intervalId);
  }, [retryCount, pickupLocation, deliveryLocation, order]);

  const AutoFitBounds = ({ pickupLocation, deliveryLocation }) => {
    const map = useMap();

    useEffect(() => {
      if (pickupLocation && deliveryLocation) {
        const bounds = [
          [pickupLocation.lat, pickupLocation.lng],
          [deliveryLocation.lat, deliveryLocation.lng],
        ];
        map.fitBounds(bounds, { padding: [50, 50] });
      }
    }, [pickupLocation, deliveryLocation, map]);

    return null;
  };

  return (
    <div className="flex h-screen">
      {/* Sidebar Order Details Section */}
      <OrderDetailModal orderId={orderId} order={order} distance={distance} />

      {/* Full-Screen Map Section */}
      <div className="relative w-2/3 h-screen" style={{ zIndex: 1 }}>
        {!isLoading && pickupLocation && deliveryLocation ? (
          <LeafletMap
            center={pickupLocation}
            zoom={13}
            style={{
              width: "100%",
              height: "100%",
              position: "relative",
              zIndex: 1,
            }}
          >
            <TileLayer
              url="https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=pk.57eb525ef1bdb7826a61cf49564f8a86"
              attribution='&copy; <a href="https://locationiq.com">LocationIQ</a> contributors'
            />
            <Marker position={pickupLocation} />
            <Marker position={deliveryLocation} />
            <RoutingControl
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
              setDistance={setDistance}
            />
            <AutoFitBounds
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
            />
            <FitBoundsButton
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
            />
          </LeafletMap>
        ) : (
          <div className="flex items-center justify-center min-h-screen">
  <LoadingSpinner />
</div>

        )}
      </div>
    </div>
  );
};

export default OrderDetail;
