import React, { useState } from "react";
import axios from "axios";
import OrderForm from "./OrderForm";
import { MapContainer as LeafletMap, TileLayer, Marker } from "react-leaflet";
import RoutingControl from "./RoutingControl";
import FitBoundsButton from "./FitBoundsButton";

const OrderPage = () => {
  const [pickupAddress, setPickupAddress] = useState("");
  const [deliveryAddress, setDeliveryAddress] = useState("");
  const [recipientName, setRecipientName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [additionalInfo, setAdditionalInfo] = useState("");
  const [pickupLocation, setPickupLocation] = useState({
    lat: 10.8231,
    lng: 106.6297,
  });
  const [deliveryLocation, setDeliveryLocation] = useState({
    lat: 10.8231,
    lng: 106.6297,
  });
  const [pickupSuggestions, setPickupSuggestions] = useState([]);
  const [deliverySuggestions, setDeliverySuggestions] = useState([]);
  const [distance, setDistance] = useState(0);

  const fetchSuggestions = async (inputAddress, setSuggestions) => {
    try {
      const response = await axios.get(
        "https://us1.locationiq.com/v1/search.php",
        {
          params: {
            key: "pk.57eb525ef1bdb7826a61cf49564f8a86", // Thay bằng API Key của bạn
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
      setPickupSuggestions([]);
    } else {
      setDeliveryAddress(suggestion.display_name);
      setDeliveryLocation(location);
      setDeliverySuggestions([]);
    }
  };

  return (
    <div className="flex h-screen">
      {/* Sidebar Order Form Section */}
      <OrderForm
        pickupAddress={pickupAddress}
        setPickupAddress={setPickupAddress}
        deliveryAddress={deliveryAddress}
        setDeliveryAddress={setDeliveryAddress}
        recipientName={recipientName}
        setRecipientName={setRecipientName}
        phoneNumber={phoneNumber}
        setPhoneNumber={setPhoneNumber}
        additionalInfo={additionalInfo}
        setAdditionalInfo={setAdditionalInfo}
        pickupSuggestions={pickupSuggestions}
        deliverySuggestions={deliverySuggestions}
        handleAddressChange={handleAddressChange}
        handleSelect={handleSelect}
        distance={distance}
      />

      {/* Full-Screen Map Section */}
      <div className="relative w-2/3 h-screen">
        <LeafletMap
          center={pickupLocation}
          zoom={13}
          style={{ width: "100%", height: "100%" }}
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
            setDistance={setDistance} // Truyền setDistance vào RoutingControl để cập nhật khoảng cách
          />
          <FitBoundsButton
            pickupLocation={pickupLocation}
            deliveryLocation={deliveryLocation}
          />
        </LeafletMap>
      </div>
    </div>
  );
};

export default OrderPage;
