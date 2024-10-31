import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

function TopCustomers() {
    const [topCustomers, setTopCustomers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');

        const fetchOrders = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/orders/all-orders", {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });
                
                const data = await response.json();

                if (data.code === 200) {
                    const customerOrderCounts = {};

                    data.result.forEach(orderItem => {
                        const { customerId, fullName } = orderItem.customer;
                        const orderStatus = orderItem.status;

                        if (!customerOrderCounts[customerId]) {
                            customerOrderCounts[customerId] = { name: fullName, count: 0 };
                        }

                        if (orderStatus === "DELIVERED") {
                            customerOrderCounts[customerId].count += 1;
                        }
                    });

                    const sortedCustomers = Object.entries(customerOrderCounts)
                        .map(([id, { name, count }]) => ({ id, name, count }))
                        .sort((a, b) => b.count - a.count);

                    setTopCustomers(sortedCustomers);
                } else {
                    console.error("Failed to fetch orders:", data.message);
                }
            } catch (error) {
                console.error("Error fetching orders:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchOrders();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="bg-white px-4 pt-3 pb-4 rounded-sm border border-gray-200 w-[20rem]">
            <strong className="font-medium text-gray-700">Top Customers</strong>
            <div className="flex flex-col gap-3 mt-4">
                {topCustomers.map((customer, index) => (
                    <Link key={index} to={`/managerpage/customeraccount/${customer.id}`}>
                        <div className="flex-1 ml-4">
                            <p className="text-sm text-gray-800">
                                {index + 1}. {customer.name}
                            </p>
                        </div>
                        <div className="text-xs">
                            Orders: {customer.count}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default TopCustomers;
