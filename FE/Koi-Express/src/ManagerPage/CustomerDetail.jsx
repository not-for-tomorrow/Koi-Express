import React, { useState, useEffect } from "react";
import { useParams, Link } from "react-router-dom";

const CustomerDetail = () => {
  const { customerId } = useParams();
  const [customer, setCustomer] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const getToken = () => localStorage.getItem("token");

  useEffect(() => {
    const fetchCustomerDetail = async () => {
      setLoading(true);
      setError(null);

      const token = getToken();

      if (!token) {
        setError("No token found");
        setLoading(false);
        return;
      }

      try {
        const response = await fetch(`http://localhost:8080/api/manager/id/${customerId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch customer details");
        }

        const data = await response.json();
        setCustomer(data.result);
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchCustomerDetail();
  }, [customerId]);

  if (loading) return <div className="text-center">Loading...</div>;
  if (error) return <div className="text-center text-red-500">{error}</div>;
  if (!customer) return <div className="text-center">No customer details available</div>;

  return (
    <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
      <div className="p-8 bg-white rounded-lg shadow-lg">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-bold text-gray-800">Customer Details</h1>
          <Link to="/managerpage/customeraccount">
            <button className="px-4 py-2 text-white bg-blue-500 rounded hover:bg-blue-600">
              Back to Customer List
            </button>
          </Link>
        </div>

        <table className="w-full text-left border-collapse shadow-md table-auto">
          <tbody>
            <tr className="bg-blue-100">
              <th className="p-2 font-semibold text-blue-900">Field</th>
              <th className="p-2 font-semibold text-blue-900">Detail</th>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Customer ID</td>
              <td className="p-2">{customer.customerId}</td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Full Name</td>
              <td className="p-2">{customer.fullName}</td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Email</td>
              <td className="p-2">{customer.email}</td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Phone Number</td>
              <td className="p-2">{customer.phoneNumber || "N/A"}</td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Address</td>
              <td className="p-2">{customer.address || "Not available"}</td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Role</td>
              <td className="p-2">{customer.role}</td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Created At</td>
              <td className="p-2">
                {new Date(customer.createdAt).toLocaleString("vi-VN")}
              </td>
            </tr>
            <tr className="border-b border-gray-200">
              <td className="p-2 font-semibold text-gray-700">Activated</td>
              <td className="p-2">{customer.activated ? "Yes" : "No"}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerDetail;
