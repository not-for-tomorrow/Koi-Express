import { MapContainer as LeafletMap, TileLayer, Marker } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import FitBoundsButton from "./FitBoundsButton";
import RoutingControl from "./RoutingControl";

const MapComponent = ({ pickupLocation, deliveryLocation }) => {
  return (
    <LeafletMap
      center={pickupLocation}
      zoom={13}
      className="map-container"
      style={{ width: "100%", height: "100%", position: "relative", zIndex: 1 }}
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
      />
      <FitBoundsButton
        pickupLocation={pickupLocation}
        deliveryLocation={deliveryLocation}
      />
    </LeafletMap>
  );
};

export default MapComponent;
