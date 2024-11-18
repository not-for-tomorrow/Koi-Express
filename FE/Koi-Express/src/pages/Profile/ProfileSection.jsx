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
            const response = await fetch("http://localhost:8080/api/customers/update", {
                method: "PUT",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: `Bearer ${token}`,
                },
                body: JSON.stringify({
                    fullName: updatedFullName,
                    email: updatedEmail,
                }),
            });

            if (response.ok) {
                setIsEditing(false);
                setUpdateSuccessMessage("Cập nhật thông tin thành công!");

                const newUserInfo = {fullName: updatedFullName, email: updatedEmail};
                localStorage.setItem("userInfo", JSON.stringify(newUserInfo));

                window.dispatchEvent(new Event("storage"));

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
        <div className="flex flex-col items-center min-h-screen ">
            <div className="flex flex-col items-center w-full max-w-md p-6 pb-4 text-center">
                <img
                    src={profileImageUrl || "https://via.placeholder.com/150"}
                    alt="Profile"
                    className="w-32 h-32 mb-4 border-4 border-blue-500 rounded-full"
                />
                <h2 className="text-3xl font-semibold text-gray-800">{fullName || "No Name Available"}</h2>
                <p className="text-lg text-gray-500">{email || "No Email Available"}</p>
                <p className="text-gray-500">{phoneNumber || "No Phone Number Available"}</p>

                {isEditing && (
                    <div className="w-full mt-4 space-y-4">
                        <input
                            type="text"
                            value={updatedFullName}
                            onChange={(e) => {
                                setUpdatedFullName(e.target.value);
                                validateName(e.target.value);
                            }}
                            className="w-full px-4 py-2 border-b border-gray-300 focus:outline-none"
                            placeholder="Tên tài khoản"
                        />
                        {nameErrorMessage && <p className="text-sm text-red-500">{nameErrorMessage}</p>}
                        <input
                            type="email"
                            value={updatedEmail}
                            onChange={(e) => {
                                setUpdatedEmail(e.target.value);
                                validateEmail(e.target.value);
                            }}
                            className="w-full px-4 py-2 border-b border-gray-300 focus:outline-none"
                            placeholder="Email"
                        />
                        {emailErrorMessage && <p className="text-sm text-red-500">{emailErrorMessage}</p>}
                    </div>
                )}

                {isEditing ? (
                    <div className="flex mt-4 space-x-4">
                        <button onClick={handleCancel}
                                className="px-4 py-2 bg-gray-200 rounded-lg hover:bg-gray-300">Hủy
                        </button>
                        <button
                            onClick={handleUpdate}
                            className="px-4 py-2 text-white bg-blue-500 rounded-lg hover:bg-blue-600"
                            disabled={isLoading || nameErrorMessage || emailErrorMessage}
                        >
                            {isLoading ? "Đang cập nhật..." : "Cập nhật"}
                        </button>
                    </div>
                ) : (
                    <button onClick={handleEdit}
                            className="px-4 py-2 mt-4 bg-gray-200 rounded-lg hover:bg-gray-300">Chỉnh sửa</button>
                )}

                <button onClick={handleLogout}
                        className="px-4 py-2 mt-4 text-white bg-red-500 rounded-lg hover:bg-red-600">Đăng xuất
                </button>

                {updateSuccessMessage && <p className="mt-4 text-green-500">{updateSuccessMessage}</p>}
                {updateErrorMessage && <p className="mt-4 text-red-500">{updateErrorMessage}</p>}
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
