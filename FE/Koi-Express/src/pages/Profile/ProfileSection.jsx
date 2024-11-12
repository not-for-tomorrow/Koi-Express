import React, {useState, useEffect} from "react";
import PropTypes from "prop-types";
import {useNavigate} from "react-router-dom";

const ProfileSection = ({fullName, phoneNumber, email, profileImageUrl, onUpdateSuccess}) => {
    const navigate = useNavigate();

    const [isEditing, setIsEditing] = useState(false);
    const [updatedFullName, setUpdatedFullName] = useState(fullName);
    const [updatedEmail, setUpdatedEmail] = useState(email);
    const [isLoading, setIsLoading] = useState(false);

    const [nameErrorMessage, setNameErrorMessage] = useState("");
    const [emailErrorMessage, setEmailErrorMessage] = useState("");
    const [updateSuccessMessage, setUpdateSuccessMessage] = useState("");
    const [updateErrorMessage, setUpdateErrorMessage] = useState("");

    useEffect(() => {
        setUpdatedFullName(fullName);
        setUpdatedEmail(email);
    }, [fullName, email]);

    const handleLogout = () => {
        localStorage.removeItem("token");
        localStorage.removeItem("userInfo");
        navigate("/");
    };

    const handleEdit = () => {
        setIsEditing(true);
    };

    const handleCancel = () => {
        setIsEditing(false);
        setUpdatedFullName(fullName);
        setUpdatedEmail(email);
        setNameErrorMessage("");
        setEmailErrorMessage("");
        setUpdateSuccessMessage("");
    };

    const validateName = (name) => {
        setNameErrorMessage(name.length < 3 ? "Tên phải chứa ít nhất 3 ký tự" : "");
    };

    const validateEmail = (email) => {
        const emailRegex = /^[^\s@]+@gmail\.com$/;
        setEmailErrorMessage(!emailRegex.test(email) ? "Địa chỉ email không hợp lệ" : "");
    };

    const handleUpdate = async () => {
        if (nameErrorMessage || emailErrorMessage) return;

        try {
            setIsLoading(true);
            const token = localStorage.getItem("token");
            const response = await fetch(
                "http://localhost:8080/api/customers/update",
                {
                    method: "PUT",
                    headers: {
                        "Content-Type": "application/json",
                        Authorization: `Bearer ${token}`,
                    },
                    body: JSON.stringify({
                        fullName: updatedFullName,
                        email: updatedEmail,
                    }),
                }
            );

            if (response.ok) {
                setIsEditing(false);
                setUpdateSuccessMessage("Cập nhật thông tin thành công!");

                const newUserInfo = {fullName: updatedFullName, email: updatedEmail};
                localStorage.setItem("userInfo", JSON.stringify(newUserInfo));

                window.dispatchEvent(new Event('storage'));

                onUpdateSuccess();
            } else {
                const errorData = await response.json();
                setUpdateErrorMessage(errorData.message || "Có lỗi xảy ra khi cập nhật.");
            }
        } catch (error) {
            setUpdateErrorMessage("Lỗi kết nối tới máy chủ.");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-gray-100">
            <div className="w-full max-w-2xl p-12 bg-white rounded-lg shadow-lg"> {/* Increased max-w to 2xl */}
                <img
                    src={profileImageUrl || "https://via.placeholder.com/150"}
                    alt="Profile"
                    className="object-cover w-32 h-32 mx-auto mb-6 border-4 border-blue-500 rounded-full"
                />
                <div className={`flex flex-col space-y-4 ${isEditing ? "items-start" : "items-center text-center"}`}>
                    {isEditing ? (
                        <>
                            <p className="w-full text-gray-500 border-b border-gray-300 border-dotted">
                                {phoneNumber || "No Phone Number Available"}
                            </p>
                            <input
                                type="text"
                                value={updatedFullName}
                                onChange={(e) => {
                                    setUpdatedFullName(e.target.value);
                                    validateName(e.target.value);
                                }}
                                className="w-full px-4 py-2 mt-2 border-b border-gray-500 focus:outline-none"
                                placeholder="Full Name"
                            />
                            {nameErrorMessage && (
                                <p className="text-sm text-red-500">{nameErrorMessage}</p>
                            )}
                            <input
                                type="email"
                                value={updatedEmail}
                                onChange={(e) => {
                                    setUpdatedEmail(e.target.value);
                                    validateEmail(e.target.value);
                                }}
                                className="w-full px-4 py-2 mt-2 border-b border-gray-500 focus:outline-none"
                                placeholder="Email"
                            />
                            {emailErrorMessage && (
                                <p className="text-sm text-red-500">{emailErrorMessage}</p>
                            )}
                        </>
                    ) : (
                        <>
                            <h2 className="text-3xl font-semibold text-black">
                                {fullName || "No Name Available"}
                            </h2>
                            <p className="text-lg text-black">
                                {email || "No Email Available"}
                            </p>
                            <p className="text-lg text-black">
                                {phoneNumber || "No Phone Number Available"}
                            </p>
                        </>
                    )}
                </div>

                <div className="w-full h-[1.5px] bg-gray-200 rounded-md my-8"></div>
                {/* Increased margin for spacing */}

                {updateSuccessMessage && (
                    <p className="text-sm text-green-500 text-center">{updateSuccessMessage}</p>
                )}

                {updateErrorMessage && (
                    <p className="text-sm text-red-500 text-center">{updateErrorMessage}</p>
                )}

                <div className="flex flex-col w-full space-y-4">
                    {isEditing ? (
                        <div className="flex justify-between w-full space-x-4">
                            <button onClick={handleCancel}
                                    className="w-full px-6 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none">
                                Hủy
                            </button>
                            <button onClick={handleUpdate}
                                    className="w-full px-6 py-2 text-sm text-white transition-all duration-300 ease-in-out bg-blue-500 rounded hover:bg-blue-600 focus:outline-none"
                                    disabled={nameErrorMessage || emailErrorMessage || isLoading}>
                                {isLoading ? "Đang cập nhật..." : "Cập nhật"}
                            </button>
                        </div>
                    ) : (
                        <button onClick={handleEdit}
                                className="w-full px-6 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none">
                            Thay đổi
                        </button>
                    )}

                    <button
                        className="w-full px-6 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-red-500 hover:text-white focus:outline-none"
                        onClick={handleLogout}>
                        Đăng xuất
                    </button>
                </div>
            </div>
        </div>
    );
};

ProfileSection.propTypes = {
    fullName: PropTypes.string,
    phoneNumber: PropTypes.string,
    email: PropTypes.string,
    profileImageUrl: PropTypes.string,
    onUpdateSuccess: PropTypes.func.isRequired,
};

export default ProfileSection;
