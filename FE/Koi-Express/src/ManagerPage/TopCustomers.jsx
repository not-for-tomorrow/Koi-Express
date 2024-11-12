import React, {useEffect, useState} from "react";
import {Link} from "react-router-dom";

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
                    const customerOrderDetails = {};

                    data.result.forEach(orderItem => {
                        const {customerId, fullName} = orderItem.customer;
                        const orderStatus = orderItem.status;
                        const orderTotal = orderItem.totalAmount;

                        if (!customerOrderDetails[customerId]) {
                            customerOrderDetails[customerId] = {name: fullName, count: 0, totalAmount: 0};
                        }

                        if (orderStatus === "DELIVERED") {
                            customerOrderDetails[customerId].count += 1;
                            customerOrderDetails[customerId].totalAmount += orderTotal;
                        }
                    });

                    const sortedCustomers = Object.entries(customerOrderDetails)
                        .map(([id, {name, count, totalAmount}]) => ({id, name, count, totalAmount}))
                        .sort((a, b) => b.totalAmount - a.totalAmount);

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
        <div className="bg-white px-4 pt-3 pb-4 rounded-lg border-2 border-gray-400 w-[18.5rem]">
            <strong className="font-medium text-gray-700">Top Customers</strong>
            <div className="flex flex-col gap-3 mt-4">
                {topCustomers.map((customer, index) => (
                    <Link key={customer.id} to={`/managerpage/customeraccount/${customer.id}`}>
                        <div className="flex items-center justify-between">
                            <div className="text-sm text-gray-800 font-semibold">
                                {index + 1}. {customer.name}
                            </div>
                            <div className="text-xs">
                                Total: ${customer.totalAmount ? customer.totalAmount.toFixed(2) : '0.00'}
                            </div>
                        </div>
                        <div className="text-xs text-gray-600">
                            Orders: {customer.count}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default TopCustomers;
