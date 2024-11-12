import React, {useState, useEffect} from "react";
import {motion, useAnimation} from "framer-motion";

const DeliveringStaffAccount = () => {
    const [searchQuery, setSearchQuery] = useState("");
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    const [deliveringStaff, setDeliveringStaff] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [newStaff, setNewStaff] = useState({
        fullName: "",
        email: "",
        password: "",
        phoneNumber: "",
        address: "",
        level: "BASIC",
    });
    const [formErrors, setFormErrors] = useState({});
    const [creationError, setCreationError] = useState(null);

    const [successMessage, setSuccessMessage] = useState("");

    const getToken = () => {
        return localStorage.getItem("token");
    };

    const fetchDeliveringStaff = async () => {
        setLoading(true);
        setError(null);

        const token = getToken();
        if (!token) {
            setError("No token found");
            setLoading(false);
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/api/manager/delivering-staff", {
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Failed to fetch delivering staff data");
            }

            const data = await response.json();
            setDeliveringStaff(data.result);
        } catch (error) {
            setError(error.message);
        } finally {
            setLoading(false);
        }
    };

    const deactivateDeliveringStaff = async (staffId) => {
        const token = getToken();
        try {
            const response = await fetch(
                `http://localhost:8080/api/manager/delivering-staff/${staffId}/deactivate`,
                {
                    method: "PUT",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                }
            );

            if (!response.ok) {
                throw new Error("Failed to deactivate delivering staff account");
            }

            setSuccessMessage("Delivering staff account deactivated successfully.");
            setTimeout(() => setSuccessMessage(""), 3000);
            await fetchDeliveringStaff();
        } catch (error) {
            setError(error.message);
        }
    };


    useEffect(() => {
        fetchDeliveringStaff();
    }, []);

    const validateForm = () => {
        const errors = {};
        if (!newStaff.fullName.trim()) errors.fullName = "Tên đầy đủ là bắt buộc";
        if (!newStaff.email.trim()) {
            errors.email = "Email là bắt buộc";
        } else if (!/\S+@\S+\.\S+/.test(newStaff.email)) {
            errors.email = "Email không hợp lệ";
        }
        if (!newStaff.password || newStaff.password.length < 8) {
            errors.password = "Mật khẩu phải có ít nhất 8 ký tự";
        }
        if (!newStaff.phoneNumber.trim()) {
            errors.phoneNumber = "Số điện thoại là bắt buộc";
        }
        if (!newStaff.address.trim()) errors.address = "Địa chỉ là bắt buộc";

        setFormErrors(errors);
        return Object.keys(errors).length === 0;
    };

    const handleCreateAccount = async () => {
        if (!validateForm()) return;

        const token = getToken();
        try {
            const response = await fetch(
                "http://localhost:8080/api/manager/create-delivering-staff",
                {
                    method: "POST",
                    headers: {
                        Authorization: `Bearer ${token}`,
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify({
                        ...newStaff,
                        role: "DELIVERING_STAFF",
                        active: true,
                    }),
                }
            );

            if (!response.ok) {
                if (response.status === 409) {
                    setCreationError("Email đã tồn tại");
                } else {
                    setCreationError("Không thể tạo tài khoản nhân viên giao hàng");
                }
                return;
            }

            setShowModal(false);
            setNewStaff({
                fullName: "",
                email: "",
                password: "",
                phoneNumber: "",
                address: "",
                level: "BASIC",
            });
            setFormErrors({});
            setCreationError(null);
            await fetchDeliveringStaff();

            setSuccessMessage("Tạo tài khoản thành công");
            setTimeout(() => {
                setSuccessMessage("");
            }, 3000);
        } catch (error) {
            setCreationError("Đã xảy ra lỗi trong quá trình tạo tài khoản");
        }
    };

    const filterDeliveringStaff = () => {
        if (!searchQuery) return deliveringStaff;
        return deliveringStaff.filter((staff) =>
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
                                Quản lý nhân viên giao hàng
                            </h1>
                            <button
                                onClick={() => setShowModal(true)}
                                className="px-4 py-2 text-sm font-semibold text-white bg-blue-500 rounded-lg hover:bg-blue-600"
                            >
                                Tạo Tài Khoản
                            </button>

                            {successMessage && (
                                <motion.div
                                    initial={{x: "100%", opacity: 0}}
                                    animate={{x: "0%", opacity: 1}}
                                    exit={{x: "100%", opacity: 0}}
                                    transition={{type: "spring", stiffness: 300, damping: 25}}
                                    className="fixed flex flex-col items-start p-4 space-y-2 bg-white border-l-4 border-green-500 rounded-lg shadow-lg top-6 right-6 w-72"
                                >
                                    <div className="text-sm font-medium text-green-600">
                                        {successMessage}
                                    </div>
                                    {/* Subtle Divider */}
                                    <div className="w-full my-1 border-t border-gray-100"></div>
                                    {/* Countdown Progress Bar */}
                                    <div className="relative w-full h-1 overflow-hidden bg-gray-300 rounded">
                                        <motion.div
                                            initial={{width: "100%"}}
                                            animate={{width: 0}}
                                            transition={{duration: 3, ease: "linear"}}
                                            className="absolute top-0 left-0 h-full bg-green-500"
                                        />
                                    </div>
                                </motion.div>
                            )}

                        </div>

                        <div className="flex items-center mb-6 space-x-6">
                            <input
                                type="text"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                                placeholder="Tìm kiếm nhân viên giao hàng..."
                                className="w-full max-w-md p-2 text-sm transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>
                    </div>

                    <div className="overflow-auto max-h-[63.5vh] text-sm">
                        {filterDeliveringStaff().length === 0 ? (
                            <div className="text-center text-gray-500">
                                Không tìm thấy nhân viên
                            </div>
                        ) : (
                            <table className="w-full text-sm text-left border-collapse shadow-md table-auto">
                                <thead className="sticky top-0 z-10 bg-blue-100">
                                <tr className="text-blue-900 border-b border-blue-200">
                                    <th className="p-2 font-semibold w-1/10">ID</th>
                                    <th className="p-2 font-semibold w-1/10">Tên</th>
                                    <th className="p-2 font-semibold w-1/10">Email</th>
                                    <th className="p-2 font-semibold w-1/10">Số điện thoại</th>
                                    <th className="p-2 font-semibold w-1/10">Đánh giá</th>
                                    <th className="p-2 font-semibold w-1/10">Trạng thái</th>
                                    <th className="p-2 font-semibold w-1/10">Cấp bậc</th>
                                    <th className="p-2 font-semibold w-1/10">Ngày tạo</th>
                                    <th className="p-2 pl-8 font-semibold w-1/10">Hành động</th>
                                </tr>
                                </thead>
                                <tbody>
                                {filterDeliveringStaff().map((staff, index) => (
                                    <tr
                                        key={index}
                                        className={`transition duration-300 border-b border-gray-200 hover:bg-blue-50 ${
                                            !staff.active ? "bg-gray-100 text-gray-400" : ""
                                        }`}
                                    >
                                        <td className="p-2 font-semibold text-blue-600 w-1/10">
                                            {staff.staffId}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {staff.fullName}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {staff.email}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {staff.phoneNumber || "N/A"}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {staff.averageRating}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {staff.status}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {staff.level}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {new Date(staff.createdAt).toLocaleDateString("vi-VN")}
                                        </td>
                                        <td className="p-2 text-sm w-1/10">
                                            {/* Hide button if staff is inactive */}
                                            {staff.active && (
                                                <button
                                                    onClick={() => deactivateDeliveringStaff(staff.staffId)}
                                                    className="px-4 py-2 text-white transition duration-300 ease-in-out transform bg-red-500 rounded hover:bg-red-700 hover:scale-105"
                                                >
                                                    Dừng hoạt động
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                                </tbody>
                            </table>
                        )}
                    </div>
                </div>
            )}

            {showModal && (
                <motion.div
                    initial={{opacity: 0, scale: 0.9}}
                    animate={{opacity: 1, scale: 1}}
                    transition={{duration: 0.1}}
                    className="fixed inset-0 z-50 flex items-center justify-center bg-white bg-opacity-50"
                    role="dialog"
                    aria-modal="true"
                    aria-labelledby="modal-title"
                >
                    <div className="w-full max-w-md p-8 bg-white rounded-lg shadow-lg">
                        <h2 className="mb-4 text-lg font-semibold text-gray-700">
                            Tạo Tài Khoản Nhân Viên Giao Hàng
                        </h2>
                        <form
                            onSubmit={(e) => {
                                e.preventDefault();
                                handleCreateAccount();
                            }}
                            noValidate
                        >
                            {/* Full Name */}
                            <div className="mb-4">
                                <label className="block mb-1 text-sm font-medium text-gray-700">
                                    Tên đầy đủ
                                </label>
                                <input
                                    type="text"
                                    value={newStaff.fullName}
                                    onChange={(e) =>
                                        setNewStaff({...newStaff, fullName: e.target.value})
                                    }
                                    className={`w-full p-2 border rounded ${
                                        formErrors.fullName ? "border-red-500" : "border-gray-300"
                                    }`}
                                    required
                                />
                                {formErrors.fullName && (
                                    <p className="mt-1 text-sm text-red-500">
                                        {formErrors.fullName}
                                    </p>
                                )}
                            </div>

                            {/* Email */}
                            <div className="mb-4">
                                <label className="block mb-1 text-sm font-medium text-gray-700">
                                    Email
                                </label>
                                <input
                                    type="email"
                                    value={newStaff.email}
                                    onChange={(e) =>
                                        setNewStaff({...newStaff, email: e.target.value})
                                    }
                                    className={`w-full p-2 border rounded ${
                                        formErrors.email ? "border-red-500" : "border-gray-300"
                                    }`}
                                    required
                                />
                                {formErrors.email && (
                                    <p className="mt-1 text-sm text-red-500">
                                        {formErrors.email}
                                    </p>
                                )}
                            </div>

                            {/* Password */}
                            <div className="mb-4">
                                <label className="block mb-1 text-sm font-medium text-gray-700">
                                    Mật khẩu
                                </label>
                                <input
                                    type="password"
                                    value={newStaff.password}
                                    onChange={(e) =>
                                        setNewStaff({...newStaff, password: e.target.value})
                                    }
                                    className={`w-full p-2 border rounded ${
                                        formErrors.password ? "border-red-500" : "border-gray-300"
                                    }`}
                                    required
                                />
                                {formErrors.password && (
                                    <p className="mt-1 text-sm text-red-500">
                                        {formErrors.password}
                                    </p>
                                )}
                            </div>

                            {/* Phone Number */}
                            <div className="mb-4">
                                <label className="block mb-1 text-sm font-medium text-gray-700">
                                    Số điện thoại
                                </label>
                                <input
                                    type="tel"
                                    value={newStaff.phoneNumber}
                                    onChange={(e) =>
                                        setNewStaff({...newStaff, phoneNumber: e.target.value})
                                    }
                                    className={`w-full p-2 border rounded ${
                                        formErrors.phoneNumber
                                            ? "border-red-500"
                                            : "border-gray-300"
                                    }`}
                                    required
                                />
                                {formErrors.phoneNumber && (
                                    <p className="mt-1 text-sm text-red-500">
                                        {formErrors.phoneNumber}
                                    </p>
                                )}
                            </div>

                            {/* Address */}
                            <div className="mb-4">
                                <label className="block mb-1 text-sm font-medium text-gray-700">
                                    Địa chỉ
                                </label>
                                <input
                                    type="text"
                                    value={newStaff.address}
                                    onChange={(e) =>
                                        setNewStaff({...newStaff, address: e.target.value})
                                    }
                                    className={`w-full p-2 border rounded ${
                                        formErrors.address ? "border-red-500" : "border-gray-300"
                                    }`}
                                    required
                                />
                                {formErrors.address && (
                                    <p className="mt-1 text-sm text-red-500">
                                        {formErrors.address}
                                    </p>
                                )}
                            </div>

                            {/* Level Dropdown */}
                            <div className="mb-4">
                                <label className="block mb-1 text-sm font-medium text-gray-700">
                                    Cấp bậc
                                </label>
                                <select
                                    value={newStaff.level}
                                    onChange={(e) =>
                                        setNewStaff({...newStaff, level: e.target.value})
                                    }
                                    className="w-full p-3 text-gray-700 transition duration-200 ease-in-out border border-gray-300 rounded-lg shadow-sm bg-gray-50 focus:outline-none focus:ring-2 focus:ring-blue-500 hover:border-blue-400 hover:bg-blue-100"
                                >
                                    <option value="BASIC">BASIC</option>
                                    <option value="INTERMEDIATE">INTERMEDIATE</option>
                                    <option value="ADVANCED">ADVANCED</option>
                                </select>
                            </div>

                            {/* Error message */}
                            {creationError && (
                                <p className="mb-4 text-sm text-red-500">{creationError}</p>
                            )}

                            {/* Buttons */}
                            <div className="flex justify-end">
                                <motion.button
                                    type="button"
                                    onClick={() => setShowModal(false)}
                                    className="px-4 py-2 mr-3 text-sm font-semibold text-gray-600 bg-gray-200 rounded-lg hover:bg-gray-300"
                                >
                                    Hủy
                                </motion.button>
                                <motion.button
                                    type="submit"
                                    whileHover={{scale: 1.05}}
                                    className="px-4 py-2 text-sm font-semibold text-white bg-blue-500 rounded-lg hover:bg-blue-600"
                                >
                                    Tạo
                                </motion.button>
                            </div>
                        </form>
                    </div>
                </motion.div>
            )}
        </div>
    );
};

export default DeliveringStaffAccount;
