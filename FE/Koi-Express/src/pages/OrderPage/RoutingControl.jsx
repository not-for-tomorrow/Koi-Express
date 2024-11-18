import React, {useEffect} from "react";
import {useMap} from "react-leaflet";
import L from "leaflet";
import "leaflet-routing-machine";
import "leaflet/dist/leaflet.css";
import "leaflet-routing-machine/dist/leaflet-routing-machine.css";

const RoutingControl = ({pickupLocation, deliveryLocation, setDistance}) => {
    const map = useMap();

    useEffect(() => {
        if (pickupLocation && deliveryLocation) {
            const routingControl = L.Routing.control({
                waypoints: [L.latLng(pickupLocation), L.latLng(deliveryLocation)],
                lineOptions: {
                    styles: [{color: "blue", opacity: 0.6, weight: 4}],
                },
                createMarker: () => null,
                routeWhileDragging: true,
                addWaypoints: false,
                show: false,
                addControl: false,
                showAlternatives: false,
            }).addTo(map);

            routingControl.on("routesfound", (e) => {
                const route = e.routes[0];
                if (route) {
                    const distanceInKm = route.summary.totalDistance / 1000;
                    setDistance(distanceInKm);
                }
            });

            return () => {
                if (routingControl) {
                    map.removeControl(routingControl);
                }
            };
        }
    }, [pickupLocation, deliveryLocation, map, setDistance]);

    return null;
};

export default RoutingControl;
