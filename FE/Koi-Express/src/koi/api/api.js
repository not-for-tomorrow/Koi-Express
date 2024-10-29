import axios from "axios";

const BASE_URL = "http://localhost:8080/api";
export const LOCATIONIQ_KEY = "pk.6a63f388fbd716914de899f77dfb04c6";

export const fetchSalesStaffAPI = async () => {
    const token = localStorage.getItem("token");
    const response = await fetch("${BASE_URL}/manager/sales-staff", {
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    });
    if (!response.ok) throw new Error("Failed to fetch sales staff data");
    return response.json();
};

export const createSalesStaffAPI = async (staff) => {
    const token = localStorage.getItem("token");
    const response = await fetch("${BASE_URL}/api/manager/create-sales-staff", {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({ ...staff, role: "SALES_STAFF", active: true }),
    });
    if (!response.ok) throw new Error("Failed to create sales staff");
    return response.json();
};

export const fetchPendingOrders = async () => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.get(`${BASE_URL}/sales/orders/pending`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response || response.status !== 200) {
        throw new Error("Failed to fetch orders");
    }

    return response.data.content || [];
};

export const fetchOrderData = async (orderId, token) => {
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.get(`${BASE_URL}/orders/${orderId}`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response || response.status !== 200) {
        throw new Error("Failed to fetch order data");
    }

    return response.data;
};

export const fetchCoordinates = async (location) => {
    const response = await axios.get(
        `https://us1.locationiq.com/v1/search.php?key=${LOCATIONIQ_KEY}&q=${location}&format=json`
    );

    if (!response || response.status !== 200) {
        throw new Error("Failed to fetch coordinates");
    }

    return response.data[0];
};

export const fetchAllOrders = async () => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.get(`${BASE_URL}/orders/all-orders`, {
        headers: {
            Authorization: `Bearer ${token}`,
        },
    });

    if (!response || response.status !== 200) {
        throw new Error("Failed to fetch orders");
    }

    return response.data.result || [];
};

export const fetchCustomerAccounts = async () => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    try {
        const response = await axios.get(`${BASE_URL}/sales/customers`, {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });
        return response.data;
    } catch (error) {
        if (error.response && error.response.status === 401) {
            // Handle unauthorized access, maybe redirect to login
            console.error("Unauthorized - Token may have expired.");
            throw new Error("Please log in again.");
        }
        throw new Error("Failed to fetch customer accounts");
    }
};

export const acceptOrderAPI = async (orderId) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.put(`${BASE_URL}/sales/accept/${orderId}`, null, {
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    });

    if (response.status !== 200) throw new Error("Failed to accept order");

    return response.data;
};

export const pickupOrderAPI = async (orderId) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.put(`${BASE_URL}/delivering/orders/pickup/${orderId}`, null, {
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
    });

    if (response.status !== 200) throw new Error("Failed to pickup order");

    return response.data;
};