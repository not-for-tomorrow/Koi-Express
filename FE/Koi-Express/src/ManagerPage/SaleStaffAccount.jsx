import React, { useState, useEffect } from "react";

const SaleStaffAccount = () => {
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [salesStaff, setSalesStaff] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [newStaff, setNewStaff] = useState({
    fullName: "",
    email: "",
    password: "",
    phoneNumber: "",
    address: "",
  });
  const [formErrors, setFormErrors] = useState({});
  const [creationError, setCreationError] = useState(null);

  const getToken = () => {
    return localStorage.getItem("token");
  };

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
      setSalesStaff(data);
    } catch (error) {
      setError(error.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchSalesStaff();
  }, []);

  const filterSalesStaff = () => {
    if (!searchQuery) return salesStaff;
    return salesStaff.filter((staff) =>
      staff.fullName.toLowerCase().includes(searchQuery.toLowerCase())
    );
  };

  const validateForm = () => {
    const errors = {};
    if (!newStaff.fullName) errors.fullName = "Tên đầy đủ là bắt buộc";
    if (!newStaff.email) {
      errors.email = "Email là bắt buộc";
    } else if (!/\S+@\S+\.\S+/.test(newStaff.email)) {
      errors.email = "Email không hợp lệ";
    }
    if (!newStaff.password || newStaff.password.length < 8) {
      errors.password = "Mật khẩu phải có ít nhất 8 ký tự";
    }
    if (!newStaff.phoneNumber) errors.phoneNumber = "Số điện thoại là bắt buộc";
    if (!newStaff.address) errors.address = "Địa chỉ là bắt buộc";

    setFormErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleCreateAccount = async () => {
    if (!validateForm()) return;

    const token = getToken();
    try {
      const response = await fetch("http://localhost:8080/api/manager/create-sales-staff", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({
          ...newStaff,
          role: "SALES_STAFF",
          active: true,
        }),
      });

      if (!response.ok) {
        if (response.status === 409) {
          setCreationError("Email đã tồn tại"); // Error message for duplicate email
        } else {
          setCreationError("Không thể tạo tài khoản nhân viên bán hàng");
        }
        return;
      }

      setShowModal(false);
      setNewStaff({ fullName: "", email: "", password: "", phoneNumber: "", address: "" });
      setFormErrors({});
      setCreationError(null);
      fetchSalesStaff(); // Refresh the list after creating a new account
    } catch (error) {
      setCreationError("Đã xảy ra lỗi trong quá trình tạo tài khoản");
    }
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
              <button
                onClick={() => setShowModal(true)}
                className="px-4 py-2 text-sm font-semibold text-white bg-blue-500 rounded-lg hover:bg-blue-600"
              >
                Tạo Tài Khoản
              </button>
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

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50">
          <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
            <h2 className="mb-4 text-lg font-semibold text-gray-700">Tạo Tài Khoản Nhân Viên Bán Hàng</h2>
            <input
              type="text"
              value={newStaff.fullName}
              onChange={(e) => setNewStaff({ ...newStaff, fullName: e.target.value })}
              placeholder="Tên đầy đủ"
              className="w-full p-2 mb-1 border rounded"
            />
            {formErrors.fullName && <p className="text-sm text-red-500">{formErrors.fullName}</p>}
            <input
              type="email"
              value={newStaff.email}
              onChange={(e) => setNewStaff({ ...newStaff, email: e.target.value })}
              placeholder="Email"
              className="w-full p-2 mb-1 border rounded"
            />
            {formErrors.email && <p className="text-sm text-red-500">{formErrors.email}</p>}
            <input
              type="password"
              value={newStaff.password}
              onChange={(e) => setNewStaff({ ...newStaff, password: e.target.value })}
              placeholder="Mật khẩu"
              className="w-full p-2 mb-1 border rounded"
            />
            {formErrors.password && <p className="text-sm text-red-500">{formErrors.password}</p>}
            <input
              type="text"
              value={newStaff.phoneNumber}
              onChange={(e) => setNewStaff({ ...newStaff, phoneNumber: e.target.value })}
              placeholder="Số điện thoại"
              className="w-full p-2 mb-1 border rounded"
            />
            {formErrors.phoneNumber && <p className="text-sm text-red-500">{formErrors.phoneNumber}</p>}
            <input
              type="text"
              value={newStaff.address}
              onChange={(e) => setNewStaff({ ...newStaff, address: e.target.value })}
              placeholder="Địa chỉ"
              className="w-full p-2 mb-1 border rounded"
            />
            {formErrors.address && <p className="text-sm text-red-500">{formErrors.address}</p>}
            {creationError && <p className="mt-4 text-sm text-red-500">{creationError}</p>}
            <div className="flex justify-end">
              <button
                onClick={() => setShowModal(false)}
                className="px-4 py-2 mr-2 text-sm font-semibold text-gray-600 bg-gray-200 rounded-lg hover:bg-gray-300"
              >
                Hủy
              </button>
              <button
                onClick={handleCreateAccount}
                className="px-4 py-2 text-sm font-semibold text-white bg-blue-500 rounded-lg hover:bg-blue-600"
              >
                Tạo
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default SaleStaffAccount;
