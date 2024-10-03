
import React, { useState } from 'react';
import axios from 'axios';
import OrderForm from './OrderForm';
import MapComponent from './MapContainer';

const OrderPage = () => {
  const [pickupAddress, setPickupAddress] = useState('');
  const [deliveryAddress, setDeliveryAddress] = useState('');
  const [recipientName, setRecipientName] = useState('');
  const [phoneNumber, setPhoneNumber] = useState('');
  const [additionalInfo, setAdditionalInfo] = useState('');
  const [pickupLocation, setPickupLocation] = useState({ lat: 10.8231, lng: 106.6297 });
  const [deliveryLocation, setDeliveryLocation] = useState({ lat: 10.8231, lng: 106.6297 });
  const [pickupSuggestions, setPickupSuggestions] = useState([]);
  const [deliverySuggestions, setDeliverySuggestions] = useState([]);
  const [distance, setDistance] = useState(0);

  const fetchSuggestions = async (inputAddress, setSuggestions) => {
    try {
      const response = await axios.get('https://nominatim.openstreetmap.org/search', {
        params: {
          q: inputAddress,
          format: 'json',
          addressdetails: 1,
          limit: 5,
        },
      });
      setSuggestions(response.data);
    } catch (error) {
      console.error('Error fetching address suggestions:', error);
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
    const location = { lat: parseFloat(suggestion.lat), lng: parseFloat(suggestion.lon) };

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
        <MapComponent pickupLocation={pickupLocation} deliveryLocation={deliveryLocation} />
      </div>
    </div>
  );
};

export default OrderPage;
