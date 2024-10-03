import React, { useEffect } from 'react';
import { useMap } from 'react-leaflet';
import L from 'leaflet';
import 'leaflet-routing-machine';

const RoutingControl = ({ pickupLocation, deliveryLocation }) => {
  const map = useMap();

  useEffect(() => {
    if (pickupLocation && deliveryLocation) {
      const routingControl = L.Routing.control({
        waypoints: [L.latLng(pickupLocation), L.latLng(deliveryLocation)],
        lineOptions: {
          styles: [{ color: 'blue', opacity: 0.6, weight: 4 }],
        },
        createMarker: () => null,
        routeWhileDragging: true,
        addControl: false, // Disable the turn-by-turn instructions control
        showAlternatives: false, // Hide alternative routes
      }).addTo(map);

      // Manually remove any remaining instruction boxes
      const removeInstructionElements = () => {
        const leafletRoutingContainer = document.querySelector('.leaflet-routing-container');
        if (leafletRoutingContainer) {
          leafletRoutingContainer.style.display = 'none';
        }
      };

      // Call the function to hide the instruction elements
      removeInstructionElements();

      return () => {
        map.removeControl(routingControl);
      };
    }
  }, [pickupLocation, deliveryLocation, map]);

  return null;
};

export default RoutingControl;
