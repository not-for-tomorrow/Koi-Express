import axios from "axios";

const BASE_URL = "http://localhost:8080/api";
export const LOCATIONIQ_KEY = "pk.a60137f2b88e6f24357a349bd80babf3";

export const fetchSalesStaffAPI = async () => {
    const token = localStorage.getItem("token");
    const response = await fetch(`${BASE_URL}/manager/sales-staff`, {
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
    const response = await fetch(`${BASE_URL}/api/manager/create-sales-staff`, {
        method: "POST",
        headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
        },
        body: JSON.stringify({...staff, role: "SALES_STAFF", active: true}),
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
            console.error("Unauthorized - Token may have expired.");
            throw new Error("Please log in again.");
        }
        throw new Error("Failed to fetch customer accounts");
    }
};

export const acceptOrderAPI = async (orderId) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.put(
        `${BASE_URL}/sales/accept/${orderId}`,
        null,
        {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        }
    );

    if (response.status !== 200) throw new Error("Failed to accept order");

    return response.data;
};

export const pickupOrderAPI = async (orderId) => {
    const token = localStorage.getItem("token");
    if (!token) throw new Error("Token not found. Please log in.");

    const response = await axios.put(
        `${BASE_URL}/delivering/orders/pickup/${orderId}`,
        null,
        {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        }
    );

    if (response.status !== 200) throw new Error("Failed to pickup order");

    return response.data;
};

export const createBlogAPI = async (title, content, imageFile) => {
    const token = localStorage.getItem("token");

    const formData = new FormData();
    formData.append("title", title);
    formData.append("content", content);
    formData.append("status", "DRAFT");

    // Use the correct key for the image file as expected by the API
    if (imageFile instanceof File) {
        formData.append("imageUrl", imageFile); // Change key to 'imageUrl'
    } else {
        console.error("Image file is not a valid File object:", imageFile);
        throw new Error("Invalid image file.");
    }

    // Logging to verify content in formData
    console.log("Form Data Entries:");
    for (let pair of formData.entries()) {
        console.log(`${pair[0]}: ${pair[1]}`);
    }

    try {
        const response = await axios.post(
            `${BASE_URL}/blogs/create-blog`,
            formData,
            {
                headers: {
                    Authorization: `Bearer ${token}`, // No 'Content-Type' needed
                },
            }
        );
        console.log("API Response:", response.data);
        return response.data;
    } catch (error) {
        console.error("API Error:", error.response?.data || error.message);
        throw new Error("Error creating blog");
    }
};

//fetch dữ liệu blog dựa trên status
export const fetchBlogDataByStatus = async (status) => {
    const token = localStorage.getItem("token");
    try {
        const response = await fetch(`${BASE_URL}/blogs/all-blogs/${status}`, {
            headers: {
                Authorization: `Bearer ${token}`,
            },
        });
        if (!response.ok) throw new Error("Failed to fetch blog data");
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Error fetching blog data:", error);
        return [];
    }
};

//load blog có sẵn về trang chính cho guest
export const loadBlogData = async () => {
    try {
        const response = await fetch("http://localhost:8080/api/blogs/all", {
            method: "GET",
            headers: {
                "Content-Type": "application/json",
            },
        });

        if (!response.ok) {
            throw new Error("Không thể tải dữ liệu blog.");
        }

        const data = await response.json();
        return data;
    } catch (error) {
        console.error("Lỗi khi fetch blog data:", error);
        throw error;
    }
};