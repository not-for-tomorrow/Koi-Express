import React, { useState, useEffect } from "react";

const SaleStaffAccount = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [salesStaff, setSalesStaff] = useState([]);

  const getToken = () => {
    return localStorage.getItem("token"); // Adjust token retrieval if necessary
  };

  useEffect(() => {
    const fetchSalesStaff = async () => {
      setLoading(true);
      setError(null);

      const token = getToken();
      if (!token) {
        setError("No token found");
        setLoading(false);
        return;
      }

      try {
        const response = await fetch("http://localhost:8080/api/manager/sales-staff", {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });

        if (!response.ok) {
          throw new Error("Failed to fetch sales staff data");
        }

        const data = await response.json();
        setSalesStaff(data); // Adjust if 'data' wraps the staff array differently
      } catch (error) {
        setError(error.message);
      } finally {
        setLoading(false);
      }
    };

    fetchSalesStaff();
  }, []);

  const filterSalesStaff = () => {
    if (!searchQuery) return salesStaff;
    return salesStaff.filter((staff) =>
      staff.fullName.toLowerCase().includes(searchQuery.toLowerCase())
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
                Quản lý nhân viên bán hàng
              </h1>
            </div>

            <div className="flex items-center mb-6 space-x-6">
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Tìm kiếm nhân viên bán hàng..."
                className="w-full max-w-md p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
              />
            </div>
          </div>

          <div className="overflow-auto max-h-[63.5vh] text-sm">
            {filterSalesStaff().length === 0 ? (
              <div className="text-center text-gray-500">
                Không tìm thấy nhân viên
              </div>
            ) : (
              <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                <thead className="sticky top-0 z-10 bg-blue-100">
                  <tr className="text-blue-900 border-b border-blue-200">
                    <th className="p-2 font-semibold w-1/8">Mã tài khoản</th>
                    <th className="w-1/4 p-2 font-semibold">Tên đầy đủ</th>
                    <th className="w-1/4 p-2 font-semibold">Email</th>
                    <th className="p-2 font-semibold w-1/8">Số điện thoại</th>
                    <th className="p-2 font-semibold w-1/8">Ngày tạo</th>
                  </tr>
                </thead>
                <tbody>
                  {filterSalesStaff().map((staff, index) => (
                    <tr
                      key={index}
                      className="transition duration-300 border-b border-gray-200 hover:bg-blue-50"
                    >
                      <td className="p-2 font-semibold text-blue-600">
                        {staff.accountId}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {staff.fullName}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {staff.email}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {staff.phoneNumber || "N/A"}
                      </td>
                      <td className="p-2 text-sm text-gray-700">
                        {new Date(staff.createdAt).toLocaleString("vi-VN")}
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

export default SaleStaffAccount;
