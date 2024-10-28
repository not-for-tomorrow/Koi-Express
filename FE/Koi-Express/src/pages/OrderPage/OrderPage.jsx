import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import OrderForm from "./OrderForm";
import OrderForm2 from "./OrderForm2";
import ChatWindow from "../chat/ChatWindow.jsx";
import ChatButton from "../chat/ChatButton.jsx";
import { MapContainer as LeafletMap, TileLayer, Marker } from "react-leaflet";
import RoutingControl from "./RoutingControl";
import FitBoundsButton from "./FitBoundsButton";
import { LOCATIONIQ_KEY } from "../../koi/api/api";

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
  const mapRef = useRef(null);

  const [isChatOpen, setIsChatOpen] = useState(false); // Manage full chat window visibility
  const [showText, setShowText] = useState(true);

  // Toggle the full chat window
  const toggleChat = () => {
    console.log("Chat button clicked");
    setIsChatOpen((prev) => !prev);
  };

  // Automatically show chat bubble on page load
  useEffect(() => {
    console.log("OrderPage loaded and chat bubble should appear");
    const timer = setTimeout(() => {
      setShowText(false);
    }, 10000); // 5000 ms = 5 seconds

    return () => clearTimeout(timer); // Clear the timer on component unmount
  }, []);

  // Function to reverse geocode lat/lng to an address
  const reverseGeocode = async (lat, lng) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/reverse.php",
        {
          params: {
            key: LOCATIONIQ_KEY,
            lat: lat,
            lon: lng,
            format: "json",
          },
        }
      );
      return response.data.display_name;
    } catch (error) {
      console.error("Error reverse geocoding:", error);
      return "";
    }
  };

  useEffect(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        async (position) => {
          const currentLocation = {
            lat: position.coords.latitude,
            lng: position.coords.longitude,
          };
          setGpsLocation(currentLocation);
          setPickupLocation(currentLocation);

          const address = await reverseGeocode(
            currentLocation.lat,
            currentLocation.lng
          );
          setPickupAddress(address);
        },
        (error) => {
          console.error("Error getting current GPS location:", error);
        }
      );
    } else {
      console.error("Geolocation is not supported by this browser.");
    }
  }, []);

  const handleContinue = (calculatedPrice) => {
    setTotalPrice(calculatedPrice);
    setCurrentStep(2);
  };

  const handleBack = () => {
    setCurrentStep(1);
  };

  const fetchSuggestions = async (inputAddress, setSuggestions) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/search.php",
        {
          params: {
            key: LOCATIONIQ_KEY,
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
      setGpsLocation(null);
      setPickupSuggestions([]);
    } else {
      setDeliveryAddress(suggestion.display_name);
      setDeliveryLocation(location);
      setGpsLocation(null);
      setDeliverySuggestions([]);
    }
  };

  useEffect(() => {
    if (mapRef.current) {
      const map = mapRef.current;
      let bounds = [];
      if (gpsLocation && !pickupLocation && !deliveryLocation) {
        bounds.push([gpsLocation.lat, gpsLocation.lng]);
      }
      if (pickupLocation && !deliveryLocation) {
        bounds.push([pickupLocation.lat, pickupLocation.lng]);
      }
      if (deliveryLocation && !pickupLocation) {
        bounds.push([deliveryLocation.lat, deliveryLocation.lng]);
      }
      if (pickupLocation && deliveryLocation) {
        bounds.push([pickupLocation.lat, pickupLocation.lng]);
        bounds.push([deliveryLocation.lat, deliveryLocation.lng]);
      }
      if (bounds.length > 0) {
        map.fitBounds(bounds);
      }
    }
  }, [pickupLocation, deliveryLocation]);

  return (
    <div className="relative flex h-screen">
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
        <OrderForm2
          handleBack={handleBack}
          basePrice={totalPrice}
          pickupAddress={pickupAddress}
          deliveryAddress={deliveryAddress}
          pickupDetail={pickupDetail}
          deliveryDetail={deliveryDetail}
          senderName={senderName}
          senderPhone={senderPhone}
          recipientName={recipientName}
          recipientPhone={recipientPhone}
          isPickupConfirmed={isPickupConfirmed}
          isDeliveryConfirmed={isDeliveryConfirmed}
        />
      )}

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
            whenCreated={(mapInstance) => {
              mapRef.current = mapInstance;
            }}
          >
            <TileLayer
              url={`https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=${LOCATIONIQ_KEY}`}
              attribution='&copy; <a href="https://locationiq.com">LocationIQ</a> contributors'
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

      <div className="fixed bottom-10 right-5 flex items-center z-[9999]">
        {/* Animated Text on the Left Side */}
        {showText && (
          <span className="px-3 py-1 mr-2 text-gray-800 bg-white rounded-lg shadow-lg animate-fade-slide-in">
            Hi there, welcome! 😊
          </span>
        )}

        {/* Chat Button */}
        <button
          onClick={() => {
            toggleChat();
          }}
          className="flex items-center justify-center w-12 h-12 text-white bg-blue-900 rounded-full shadow-lg"
        >
          <span className="text-lg">💬</span>
        </button>
      </div>

      {/* Full Chat Window */}
      {isChatOpen && <ChatWindow onClose={toggleChat} />}
      <ChatButton onClick={() => setIsChatOpen(true)} />
    </div>
  );
};

export default OrderPage;
