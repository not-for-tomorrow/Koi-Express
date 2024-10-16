import React, { useState, useEffect } from "react";
import axios from "axios";
import OrderForm from "./OrderForm";
import OrderForm2 from "./OrderForm2"; // Import OrderForm2
import { MapContainer as LeafletMap, TileLayer, Marker } from "react-leaflet";
import RoutingControl from "./RoutingControl";
import FitBoundsButton from "./FitBoundsButton";

const OrderPage = () => {
  const [pickupAddress, setPickupAddress] = useState("");
  const [deliveryAddress, setDeliveryAddress] = useState("");
  const [pickupLocation, setPickupLocation] = useState(null);
  const [deliveryLocation, setDeliveryLocation] = useState(null);
  const [pickupSuggestions, setPickupSuggestions] = useState([]);
  const [deliverySuggestions, setDeliverySuggestions] = useState([]);
  const [distance, setDistance] = useState(0);
  const [gpsLocation, setGpsLocation] = useState(null);
  const [currentStep, setCurrentStep] = useState(1);
  const [totalPrice, setTotalPrice] = useState(0);
  const [pickupDetail, setPickupDetail] = useState("");
  const [deliveryDetail, setDeliveryDetail] = useState("");
  const [senderName, setSenderName] = useState("");
  const [senderPhone, setSenderPhone] = useState("");
  const [recipientName, setRecipientName] = useState("");
  const [recipientPhone, setRecipientPhone] = useState("");
  const [isPickupConfirmed, setIsPickupConfirmed] = useState(false);
  const [isDeliveryConfirmed, setIsDeliveryConfirmed] = useState(false);


  // Function to reverse geocode lat/lng to an address
  const reverseGeocode = async (lat, lng) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/reverse.php", // Example: using LocationIQ's reverse geocode API
        {
          params: {
            key: "pk.57eb525ef1bdb7826a61cf49564f8a86", // Replace with your API key
            lat: lat,
            lon: lng,
            format: "json",
          },
        }
      );
      return response.data.display_name; // Extract the address from the response
    } catch (error) {
      console.error("Error reverse geocoding:", error);
      return "";
    }
  };

  useEffect(() => {
    // Get the current GPS location of the user's device
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const currentLocation = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          };
          // Set initial GPS location
          setGpsLocation(currentLocation);
          setPickupLocation(currentLocation);

          // Reverse geocode the GPS location to get the address
          const address = await reverseGeocode(
            currentLocation.lat,
            currentLocation.lng
          );
          setPickupAddress(address); // Set the pickup address to the reverse geocoded address
        },
        (error) => {
          console.error("Error getting current GPS location:", error);
        }
      );
    } else {
      console.error("Geolocation is not supported by this browser.");
    }
  }, []);

  // Function to navigate to the next form (OrderForm2)
  const handleContinue = (calculatedPrice) => {
    setTotalPrice(calculatedPrice); // Set the total price based on the calculation from OrderForm
    setCurrentStep(2); // Move to the next step (OrderForm2)
  };

  const handleBack = () => {
    setCurrentStep(1); // Go back to OrderForm
  };

  // Fetch suggestions for addresses (already implemented)
  const fetchSuggestions = async (inputAddress, setSuggestions) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/search.php",
        {
          params: {
            key: "pk.57eb525ef1bdb7826a61cf49564f8a86",
            q: inputAddress,
            format: "json",
            limit: 5,
          },
        }
      );
      setSuggestions(response.data);
    } catch (error) {
      console.error("Error fetching address suggestions:", error);
    }
  };

  const handleAddressChange = async (e, isPickup) => {
    const inputAddress = e.target.value;

    if (isPickup) {
      setPickupAddress(inputAddress);
      if (inputAddress.length > 3) {
        await fetchSuggestions(inputAddress, setPickupSuggestions);
      } else {
        setPickupSuggestions([]);
      }
    } else {
      setDeliveryAddress(inputAddress);
      if (inputAddress.length > 3) {
        await fetchSuggestions(inputAddress, setDeliverySuggestions);
      } else {
        setDeliverySuggestions([]);
      }
    }
  };

  const handleSelect = (suggestion, isPickup) => {
    const location = {
      lat: parseFloat(suggestion.lat),
      lng: parseFloat(suggestion.lon),
    };

    if (isPickup) {
      setPickupAddress(suggestion.display_name);
      setPickupLocation(location);
      setGpsLocation(null); // Remove GPS marker after setting the pickup location
      setPickupSuggestions([]);
    } else {
      setDeliveryAddress(suggestion.display_name);
      setDeliveryLocation(location);
      setGpsLocation(null); // Remove GPS marker after setting the delivery location
      setDeliverySuggestions([]);
    }
  };

  return (
    <div className="flex h-screen">
      {/* Sidebar Order Form Section */}
      {currentStep === 1 ? (
        <OrderForm
        pickupAddress={pickupAddress}
          setPickupAddress={setPickupAddress}
          deliveryAddress={deliveryAddress}
          setDeliveryAddress={setDeliveryAddress}
          pickupSuggestions={pickupSuggestions}
          deliverySuggestions={deliverySuggestions}
          handleAddressChange={handleAddressChange}
          handleSelect={handleSelect}
          distance={distance}
          handleContinue={handleContinue}
          pickupDetail={pickupDetail}
          setPickupDetail={setPickupDetail}
          deliveryDetail={deliveryDetail}
          setDeliveryDetail={setDeliveryDetail}
          senderName={senderName}
          setSenderName={setSenderName}
          senderPhone={senderPhone}
          setSenderPhone={setSenderPhone}
          recipientName={recipientName}
          setRecipientName={setRecipientName}
          recipientPhone={recipientPhone}
          setRecipientPhone={setRecipientPhone}
          isPickupConfirmed={isPickupConfirmed}
          setIsPickupConfirmed={setIsPickupConfirmed}
          isDeliveryConfirmed={isDeliveryConfirmed}
          setIsDeliveryConfirmed={setIsDeliveryConfirmed}
        />
      ) : (
        <OrderForm2 handleBack={handleBack} basePrice={totalPrice || 0} /> // Pass totalPrice as basePrice to OrderForm2
      )}

      {/* Full-Screen Map Section */}
      <div className="relative w-2/3 h-screen" style={{ zIndex: 1 }}>
        {(pickupLocation || deliveryLocation) && (
          <LeafletMap
            center={pickupLocation || { lat: 10.8231, lng: 106.6297 }}
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
            {gpsLocation && <Marker position={gpsLocation} />}
            {pickupLocation && !gpsLocation && (
              <Marker position={pickupLocation} />
            )}
            {deliveryLocation && !gpsLocation && (
              <Marker position={deliveryLocation} />
            )}
            <RoutingControl
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
              setDistance={setDistance}
            />
            <FitBoundsButton
              pickupLocation={pickupLocation}
              deliveryLocation={deliveryLocation}
            />
          </LeafletMap>
        )}
      </div>
    </div>
  );
};

export default OrderPage;
