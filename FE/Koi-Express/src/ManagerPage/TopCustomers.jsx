import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

function TopCustomers() {
    const [topCustomers, setTopCustomers] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const token = localStorage.getItem('token');

        const fetchTopCustomers = async () => {
            try {
                const response = await fetch("http://localhost:8080/api/manager/top-spenders", {
                    headers: {
                        "Authorization": `Bearer ${token}`,
                        "Content-Type": "application/json"
                    }
                });

                const data = await response.json();
                console.log(data);

                if (response.ok) {
                    setTopCustomers(data);
                } else {
                    console.error("Failed to fetch top customers:", data.message);
                }
            } catch (error) {
                console.error("Error fetching top customers:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchTopCustomers();
    }, []);

    if (loading) {
        return <div>Loading...</div>;
    }

    return (
        <div className="bg-white px-4 pt-3 pb-4 rounded-lg border-2 border-gray-400 w-[18.5rem]">
            <strong className="font-medium text-gray-700">Top Customers</strong>
            <div className="flex flex-col gap-3 mt-4">
                {topCustomers.map((customer, index) => (
                    <Link key={customer.customerId} to={`/managerpage/customeraccount/${customer.customerId}`}>
                        <div className="flex items-center justify-between">
                            <div className="text-sm text-gray-800 font-semibold">
                                {index + 1}. {customer.fullName}
                            </div>
                            <div className="text-xs">
                                Total: ${customer.totalSpent ? customer.totalSpent.toFixed(2) : '0.00'}
                            </div>
                        </div>
                        <div className="text-xs text-gray-600">
                            Orders: {customer.orderCount}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    );
}

export default TopCustomers;
