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

const OrderDetail = () => {
  const { orderId } = useParams(); // Extract orderId from the URL
  const location = useLocation(); // Get the location object
  const order = location.state; // Get the passed order from state

  const [pickupLocation, setPickupLocation] = useState(null);
  const [deliveryLocation, setDeliveryLocation] = useState(null);
  const [distance, setDistance] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [retryCount, setRetryCount] = useState(0); // Track retry attempts

  const MAX_RETRIES = 5; // Set a maximum number of retries to avoid infinite loops
  const RETRY_DELAY = 3000; // Retry every 3 seconds

  // Function to get latitude and longitude for an address using LocationIQ API
  const geocodeAddress = async (address) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/search.php", // Example: using LocationIQ's geocode API
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
    if (order && order.originLocation && order.destinationLocation) {
      setIsLoading(true); // Set loading to true while fetching coordinates
      const pickupCoords = await geocodeAddress(order.originLocation);
      const deliveryCoords = await geocodeAddress(order.destinationLocation);
      setPickupLocation(pickupCoords);
      setDeliveryLocation(deliveryCoords);
      setIsLoading(false); // Set loading to false after fetching coordinates
    }
  };

  useEffect(() => {
    const retryFetchLocations = async () => {
      if (pickupLocation && deliveryLocation) {
        // Stop polling when both locations are available
        clearInterval(intervalId);
      } else if (retryCount < MAX_RETRIES) {
        await fetchLocations();
        setRetryCount(retryCount + 1); // Increment retry count after each attempt
      } else {
        console.error("Max retries reached. Could not fetch locations.");
        clearInterval(intervalId); // Stop retrying after max retries
      }
    };

    const intervalId = setInterval(retryFetchLocations, RETRY_DELAY); // Polling logic

    return () => clearInterval(intervalId); // Clean up the interval on unmount
  }, [retryCount, pickupLocation, deliveryLocation]);

  // AutoFitBounds functionality embedded in OrderDetail
  const AutoFitBounds = ({ pickupLocation, deliveryLocation }) => {
    const map = useMap(); // Get the map instance

    useEffect(() => {
      if (pickupLocation && deliveryLocation) {
        // Fit the map bounds to show both pickup and delivery locations
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
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            />
            <Marker position={pickupLocation} />
            <Marker position={deliveryLocation} />
            <RoutingControl
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
              setDistance={setDistance}
            />
            {/* Auto-fit the bounds of the map to the locations */}
            <AutoFitBounds
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
            />
            {/* Add FitBoundsButton for viewing full route */}
            <FitBoundsButton
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
            />
          </LeafletMap>
        ) : (
          <p>Loading map... Retry attempt {retryCount}</p>
        )}
      </div>
    </div>
  );
};

export default OrderDetail;
