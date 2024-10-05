import { MapContainer as LeafletMap, TileLayer, Marker } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import FitBoundsButton from "./FitBoundsButton";
import RoutingControl from "./RoutingControl";

const MapContainer = ({ pickupLocation, deliveryLocation }) => {
  return (
    <LeafletMap
      center={[10.8231, 106.6297]} // Set a default center point for the map
      zoom={13}
      className="map-container"
      style={{ width: "100%", height: "100%" }}
    >
      <TileLayer
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      />
      {/* Only render markers if locations are defined */}
      {pickupLocation && <Marker position={pickupLocation} />}
      {deliveryLocation && <Marker position={deliveryLocation} />}
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

export default MapContainer;
