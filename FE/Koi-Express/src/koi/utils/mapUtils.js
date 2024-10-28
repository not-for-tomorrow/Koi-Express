// utils/mapUtils.js

import L from "leaflet";
import "leaflet-routing-machine";

const LOCATIONIQ_KEY = "pk.57eb525ef1bdb7826a61cf49564f8a86";

export const initializeMap = (pickup, delivery, setDistance, retryMapLoad) => {
    const map = L.map("map").setView([pickup.lat, pickup.lon], 10);

    L.tileLayer(
        `https://{s}-tiles.locationiq.com/v3/streets/r/{z}/{x}/{y}.png?key=${LOCATIONIQ_KEY}`,
        {
            attribution: '&copy; <a href="https://locationiq.com">LocationIQ</a> contributors',
        }
    ).addTo(map);

    L.marker([pickup.lat, pickup.lon]).addTo(map);
    L.marker([delivery.lat, delivery.lon]).addTo(map);

    const routingService = L.Routing.osrmv1();
    routingService.route(
        [
            L.Routing.waypoint(L.latLng(pickup.lat, pickup.lon)),
            L.Routing.waypoint(L.latLng(delivery.lat, delivery.lon)),
        ],
        (err, routes) => {
            if (!err && routes && routes[0]) {
                const route = routes[0];
                setDistance((route.summary.totalDistance / 1000).toFixed(2) + " km");

                const routePolyline = L.polyline(route.coordinates, {
                    color: "blue",
                    weight: 4,
                }).addTo(map);

                const bounds = routePolyline.getBounds();
                map.fitBounds(bounds);
            } else {
                console.error("Failed to calculate route:", err);
                retryMapLoad();
            }
        }
    );

    return map;
};
