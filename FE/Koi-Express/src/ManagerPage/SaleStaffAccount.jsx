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
    if (!newStaff.fullName.trim()) errors.fullName = "Tên đầy đủ là bắt buộc";
    if (!newStaff.email.trim()) {
      errors.email = "Email là bắt buộc";
    } else if (!/\S+@\S+\.\S+/.test(newStaff.email)) {
      errors.email = "Email không hợp lệ";
    }
    if (!newStaff.password) {
      errors.password = "Mật khẩu là bắt buộc";
    } else if (newStaff.password.length < 8) {
      errors.password = "Mật khẩu phải có ít nhất 8 ký tự";
    }
    if (!newStaff.phoneNumber.trim()) {
      errors.phoneNumber = "Số điện thoại là bắt buộc";
    } else if (!/^\d{10,15}$/.test(newStaff.phoneNumber)) {
      errors.phoneNumber = "Số điện thoại không hợp lệ";
    }
    if (!newStaff.address.trim()) errors.address = "Địa chỉ là bắt buộc";

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
      setNewStaff({ fullName: "", email: "", password: "", phoneNumber: "", address: ""});
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
                      aria-label="Search Sales Staff"
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
                              key={staff.accountId}
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
            <div
                className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-50"
                role="dialog"
                aria-modal="true"
                aria-labelledby="modal-title"
            >
              <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                <h2 id="modal-title" className="mb-4 text-lg font-semibold text-gray-700">
                  Tạo Tài Khoản Nhân Viên Bán Hàng
                </h2>
                <form
                    onSubmit={(e) => {
                      e.preventDefault();
                      handleCreateAccount();
                    }}
                    noValidate
                >
                  <div className="mb-4">
                    <label htmlFor="fullName" className="block mb-1 text-sm font-medium text-gray-700">
                      Tên đầy đủ
                    </label>
                    <input
                        id="fullName"
                        type="text"
                        value={newStaff.fullName}
                        onChange={(e) => setNewStaff({...newStaff, fullName: e.target.value})}
                        placeholder="Nhập tên đầy đủ"
                        className={`w-full p-2 border rounded ${
                            formErrors.fullName ? "border-red-500" : "border-gray-300"
                        }`}
                        required
                    />
                    {formErrors.fullName && (
                        <p className="mt-1 text-sm text-red-500">{formErrors.fullName}</p>
                    )}
                  </div>

                  <div className="mb-4">
                    <label htmlFor="email" className="block mb-1 text-sm font-medium text-gray-700">
                      Email
                    </label>
                    <input
                        id="email"
                        type="email"
                        value={newStaff.email}
                        onChange={(e) => setNewStaff({...newStaff, email: e.target.value})}
                        placeholder="Nhập email"
                        className={`w-full p-2 border rounded ${
                            formErrors.email ? "border-red-500" : "border-gray-300"
                        }`}
                        required
                    />
                    {formErrors.email && (
                        <p className="mt-1 text-sm text-red-500">{formErrors.email}</p>
                    )}
                  </div>

                  <div className="mb-4">
                    <label htmlFor="password" className="block mb-1 text-sm font-medium text-gray-700">
                      Mật khẩu
                    </label>
                    <input
                        id="password"
                        type="password"
                        value={newStaff.password}
                        onChange={(e) => setNewStaff({...newStaff, password: e.target.value})}
                        placeholder="Nhập mật khẩu"
                        className={`w-full p-2 border rounded ${
                            formErrors.password ? "border-red-500" : "border-gray-300"
                        }`}
                        required
                    />
                    {formErrors.password && (
                        <p className="mt-1 text-sm text-red-500">{formErrors.password}</p>
                    )}
                  </div>

                  <div className="mb-4">
                    <label htmlFor="phoneNumber" className="block mb-1 text-sm font-medium text-gray-700">
                      Số điện thoại
                    </label>
                    <input
                        id="phoneNumber"
                        type="tel"
                        value={newStaff.phoneNumber}
                        onChange={(e) => setNewStaff({...newStaff, phoneNumber: e.target.value})}
                        placeholder="Nhập số điện thoại"
                        className={`w-full p-2 border rounded ${
                            formErrors.phoneNumber ? "border-red-500" : "border-gray-300"
                        }`}
                        required
                    />
                    {formErrors.phoneNumber && (
                        <p className="mt-1 text-sm text-red-500">{formErrors.phoneNumber}</p>
                    )}
                  </div>

                  <div className="mb-4">
                    <label htmlFor="address" className="block mb-1 text-sm font-medium text-gray-700">
                      Địa chỉ
                    </label>
                    <input
                        id="address"
                        type="text"
                        value={newStaff.address}
                        onChange={(e) => setNewStaff({...newStaff, address: e.target.value})}
                        placeholder="Nhập địa chỉ"
                        className={`w-full p-2 border rounded ${
                            formErrors.address ? "border-red-500" : "border-gray-300"
                        }`}
                        required
                    />
                    {formErrors.address && (
                        <p className="mt-1 text-sm text-red-500">{formErrors.address}</p>
                    )}
                  </div>

                  {creationError && (
                      <p className="mb-4 text-sm text-red-500">{creationError}</p>
                  )}

                  <div className="flex justify-end">
                    <button
                        type="button"
                        onClick={() => setShowModal(false)}
                        className="px-4 py-2 mr-2 text-sm font-semibold text-gray-600 bg-gray-200 rounded-lg hover:bg-gray-300"
                    >
                      Hủy
                    </button>
                    <button
                        type="submit"
                        className="px-4 py-2 text-sm font-semibold text-white bg-blue-500 rounded-lg hover:bg-blue-600"
                    >
                      Tạo
                    </button>
                  </div>
                </form>
              </div>
            </div>
        )}
      </div>
  );
};

export default SaleStaffAccount;
