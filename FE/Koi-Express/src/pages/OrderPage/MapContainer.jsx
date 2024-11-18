import {MapContainer as LeafletMap, TileLayer, Marker} from "react-leaflet";
import "leaflet/dist/leaflet.css";
import FitBoundsButton from "./FitBoundsButton";
import RoutingControl from "./RoutingControl";
import {LOCATIONIQ_KEY} from "../../koi/api/api";

const MapContainer = ({pickupLocation, deliveryLocation}) => {
    return (
        <LeafletMap
            center={[10.8231, 106.6297]}
            zoom={13}
            className="map-container"
            style={{width: "100%", height: "100%"}}
        >
            <TileLayer
                url={`https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=${LOCATIONIQ_KEY}`}
                attribution='&copy; <a href="https://locationiq.com">LocationIQ</a> contributors'
            />
            {/* Only render markers if locations are defined */}
            {pickupLocation && <Marker position={pickupLocation}/>}
            {deliveryLocation && <Marker position={deliveryLocation}/>}
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
