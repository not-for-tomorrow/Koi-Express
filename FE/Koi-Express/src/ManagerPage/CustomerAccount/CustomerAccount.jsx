import React, {useEffect, useState} from "react";
import {useNavigate} from "react-router-dom";

const CustomerAccount = () => {
    const [searchQuery, setSearchQuery] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [customers, setCustomers] = useState([]);
    const navigate = useNavigate();

    const getToken = () => localStorage.getItem("token");

    useEffect(() => {
        const fetchCustomers = async () => {
            setLoading(true);
            setError(null);

            const token = getToken();

            if (!token) {
                setError("No token found");
                setLoading(false);
                return;
            }

            try {
                const response = await fetch(
                    "http://localhost:8080/api/manager/customers",
                    {
                        headers: {
                            Authorization: `Bearer ${token}`,
                            "Content-Type": "application/json",
                        },
                    }
                );

                if (!response.ok) {
                    throw new Error("Failed to fetch customers");
                }

                const data = await response.json();
                setCustomers(data.result);
            } catch (error) {
                setError(error.message);
            } finally {
                setLoading(false);
            }
        };

        fetchCustomers();
    }, []);

    const filterCustomers = () => {
        if (!searchQuery) return customers;
        return customers.filter((customer) =>
            customer.fullName.toLowerCase().includes(searchQuery.toLowerCase())
        );
    };

    return (
        <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
            {loading ? (
                <div className="text-sm text-center">Loading...</div>
            ) : error ? (
                <div className="text-sm text-center text-red-500">{error}</div>
            ) : (
                <div className="p-8 text-sm bg-white rounded-lg shadow-lg">
                    <h1 className="text-2xl font-bold text-gray-800">
                        Quản lý tài khoản khách hàng
                    </h1>
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder="Tìm kiếm khách hàng..."
                        className="w-full max-w-md p-2 mt-4 mb-6 text-sm border border-blue-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />

                    <div className="overflow-auto max-h-[63.5vh]">
                        {filterCustomers().length === 0 ? (
                            <div className="text-center text-gray-500">
                                Không tìm thấy khách hàng
                            </div>
                        ) : (
                            <table className="w-full text-sm border-collapse shadow-md table-auto">
                                <thead className="sticky top-0 bg-blue-100">
                                <tr className="text-blue-900 border-b border-blue-200">
                                    <th className="p-3 font-semibold text-left">
                                        Mã khách hàng
                                    </th>
                                    <th className="p-3 font-semibold text-left">
                                        Tên khách hàng
                                    </th>
                                    <th className="p-3 font-semibold text-left">Email</th>
                                    <th className="p-3 font-semibold text-left">
                                        Số điện thoại
                                    </th>
                                    <th className="p-3 font-semibold text-left">Ngày tạo</th>
                                    <th className="p-3 font-semibold text-left">Nền tảng</th>
                                    <th className="p-3 font-semibold text-left">Lần cuối hoạt động</th>
                                    <th className="p-3 font-semibold text-left">Hoạt động</th>

                                </tr>
                                </thead>
                                <tbody>
                                {filterCustomers().map((customer) => (
                                    <tr
                                        key={customer.customerId}
                                        className="transition-colors border-b border-gray-200 cursor-pointer hover:bg-blue-50"
                                        onClick={() =>
                                            navigate(
                                                `/managerpage/customeraccount/${customer.customerId}`
                                            )
                                        }
                                    >
                                        <td className="p-3 font-medium text-blue-600">
                                            {customer.customerId}
                                        </td>

                                        <td className="p-3 text-gray-700">{customer.fullName}</td>

                                        <td className="p-3 text-gray-700">{customer.email}</td>

                                        <td className="p-3 text-gray-700">
                                            {customer.phoneNumber || "N/A"}
                                        </td>

                                        <td className="p-3 text-gray-700">
                                            {new Date(customer.createdAt).toLocaleDateString("vi-VN")}
                                        </td>

                                        <td className="p-3 text-gray-700">{customer.authProvider}</td>

                                        <td className="p-3 text-gray-700">
                                            {new Date(customer.lastLogin).toLocaleString("vi-VN")}
                                        </td>

                                        <td className="p-3 text-center">
                                          <span
                                              className={`inline-block w-4 h-4 rounded-full ${
                                                  customer.active ? "bg-green-500" : "bg-red-500"
                                              }`}
                                          />
                                        </td>

                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default CustomerAccount;
