import React, { useState, useEffect } from "react";

const CustomerAccount = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [customers, setCustomers] = useState([]);

  // Mock data for customers
  useEffect(() => {
    const mockCustomers = [
      {
        customerId: 1,
        fullName: "Nguyen Van A",
        email: "nguyenvana@example.com",
        phone: "0123456789",
        createdAt: "2024-01-10T10:30:00Z",
      },
      {
        customerId: 2,
        fullName: "Tran Thi B",
        email: "tranthib@example.com",
        phone: "0987654321",
        createdAt: "2024-02-15T15:00:00Z",
      },
      {
        customerId: 3,
        fullName: "Le Van C",
        email: "levanc@example.com",
        phone: "0334567890",
        createdAt: "2024-03-20T09:45:00Z",
      },
    ];
    setCustomers(mockCustomers);
  }, []);

  const filterCustomers = () => {
    if (!searchQuery) {
      return customers;
    }
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
          <div className="sticky top-0 z-20 bg-white">
            <div className="flex items-center justify-between mb-6">
              <h1 className="text-2xl font-bold text-gray-800">
                Quản lý tài khoản khách hàng
              </h1>
            </div>

            <div className="flex items-center mb-6 space-x-6">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Tìm kiếm khách hàng..."
                className="w-full max-w-md p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="overflow-auto max-h-[63.5vh] text-sm">
            {filterCustomers().length === 0 ? (
              <div className="text-center text-gray-500">
                Không tìm thấy khách hàng
              </div>
            ) : (
              <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                <thead className="sticky top-0 z-10 bg-blue-100">
                  <tr className="text-blue-900 border-b border-blue-200">
                    <th className="p-2 font-semibold w-1/8">Mã khách hàng</th>
                    <th className="w-1/4 p-2 font-semibold">Tên khách hàng</th>
                    <th className="w-1/4 p-2 font-semibold">Email</th>
                    <th className="p-2 font-semibold w-1/8">Số điện thoại</th>
                    <th className="p-2 font-semibold w-1/8">Ngày tạo</th>
                  </tr>
                </thead>
                <tbody>
                  {filterCustomers().map((customer, index) => (
                    <tr
                      key={index}
                      className="transition duration-300 border-b border-gray-200 hover:bg-blue-50"
                    >
                      <td className="p-2 font-semibold text-blue-600">
                        {customer.customerId}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {customer.fullName}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {customer.email}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {customer.phone}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {new Date(customer.createdAt).toLocaleString("vi-VN")}
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
