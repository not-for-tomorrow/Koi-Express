import React, { useState, useEffect } from "react";
import PropTypes from "prop-types";
import { useNavigate } from 'react-router-dom';

const ProfileSection = ({ fullName, phoneNumber, email, profileImageUrl }) => {
  const navigate = useNavigate();

  const [isEditing, setIsEditing] = useState(false);
  const [updatedFullName, setUpdatedFullName] = useState(fullName);
  const [updatedEmail, setUpdatedEmail] = useState(email);

  const [nameErrorMessage, setNameErrorMessage] = useState(""); // Lỗi tên
  const [emailErrorMessage, setEmailErrorMessage] = useState(""); // Lỗi email
  const [updateSuccessMessage, setUpdateSuccessMessage] = useState(""); // Success message
  const [updateErrorMessage, setUpdateErrorMessage] = useState(""); // API error message

  useEffect(() => {
    setUpdatedFullName(fullName);
    setUpdatedEmail(email);
  }, [fullName, email]);

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    console.log("Logout button clicked");
    navigate('/login');
  };

  const handleEdit = () => {
    setIsEditing(true);
  };

  const handleCancel = () => {
    setIsEditing(false);
    setUpdatedFullName(fullName);
    setUpdatedEmail(email);
    setNameErrorMessage(""); // Reset lỗi khi hủy
    setEmailErrorMessage("");
    setUpdateSuccessMessage(""); // Clear any success message
  };

  const validateName = (name) => {
    if (name.length < 3) {
      setNameErrorMessage("Tên phải chứa ít nhất 3 ký tự");
    } else {
      setNameErrorMessage("");
    }
  };

  const validateEmail = (email) => {
    const emailRegex = /^[^\s@]+@gmail\.com$/;
    if (!emailRegex.test(email)) {
      setEmailErrorMessage("Địa chỉ email không hợp lệ");
    } else {
      setEmailErrorMessage("");
    }
  };

  const handleUpdate = async () => {
    if (!nameErrorMessage && !emailErrorMessage) {
      try {
        const token = localStorage.getItem("token"); // Get JWT from localStorage
        const response = await fetch('http://localhost:8080/api/customers/update', {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
          },
          body: JSON.stringify({
            fullName: updatedFullName,
            email: updatedEmail
          })
        });
  
        if (response.ok) {
          const data = await response.json();
          setIsEditing(false);
          setUpdateSuccessMessage("Cập nhật thông tin thành công!");
          // Optionally, update profile details based on response
        } else {
          const errorData = await response.json();
          setUpdateErrorMessage(errorData.message || "Có lỗi xảy ra khi cập nhật.");
        }
      } catch (error) {
        setUpdateErrorMessage("Lỗi kết nối tới máy chủ.");
      }
    }
  };
  

  return (
    <div className="flex flex-col items-center w-full p-8 mx-auto mt-4 bg-white rounded-lg shadow-md md:mt-0 max-w-auto">
      <img
        src={profileImageUrl || "https://via.placeholder.com/150"}
        alt="Profile"
        className="object-cover w-24 h-24 mt-4 border-4 border-blue-500 rounded-full"
      />
      <div className={`flex flex-col mt-2 space-y-2 w-full ${isEditing ? 'items-start text-left' : 'items-center text-center'}`}>
        {isEditing ? (
          <>
            <p className="w-full text-gray-400 border-b border-gray-400 border-dotted">
              {phoneNumber || "No Phone Number Available"}
            </p>
            <p className="w-full text-black border-b border-gray-500 border-dotted">
              <input
                type="text"
                value={updatedFullName}
                onChange={(e) => {
                  setUpdatedFullName(e.target.value);
                  validateName(e.target.value);
                }}
                className="w-full text-left focus:outline-none"
                style={{ border: 'none', outline: 'none' }}
              />
            </p>
            {nameErrorMessage && (
              <p className="text-sm text-red-500">{nameErrorMessage}</p>
            )}
            <p className="w-full text-black border-b border-gray-500 border-dotted">
              <input
                type="email"
                value={updatedEmail}
                onChange={(e) => {
                  setUpdatedEmail(e.target.value);
                  validateEmail(e.target.value);
                }}
                className="w-full text-left focus:outline-none"
                style={{ border: 'none', outline: 'none' }}
              />
            </p>
            {emailErrorMessage && (
              <p className="text-sm text-red-500">{emailErrorMessage}</p>
            )}
          </>
        ) : (
          <>
            <h2 className="text-2xl font-semibold text-black">
              {fullName || "No Name Available"}
            </h2>
            <p className="text-sm text-black">
              {email || "No Email Available"}
            </p>
            <p className="text-sm text-black">
              {phoneNumber || "No Phone Number Available"}
            </p>
          </>
        )}
      </div>

      <div className="w-full h-[1.5px] bg-gray-200 rounded-md my-4"></div>

      {updateSuccessMessage && (
        <p className="text-sm text-green-500">{updateSuccessMessage}</p>
      )}

      {updateErrorMessage && (
        <p className="text-sm text-red-500">{updateErrorMessage}</p>
      )}

      <div className="flex flex-col w-full space-y-4">
        {isEditing ? (
          <div className="flex justify-between w-full space-x-2">
            <button
              aria-label="Cancel"
              onClick={handleCancel}
              className="w-full px-4 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
            >
              Hủy
            </button>
            <button
              aria-label="Update"
              onClick={handleUpdate}
              className="w-full px-4 py-2 text-sm text-white transition-all duration-300 ease-in-out bg-blue-500 rounded hover:bg-blue-600 focus:outline-none focus:ring-2 focus:ring-blue-400"
              disabled={nameErrorMessage || emailErrorMessage}
            >
              Cập nhật
            </button>
          </div>
        ) : (
          <button
            aria-label="Change Profile"
            onClick={handleEdit}
            className="w-full px-4 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
          >
            Thay đổi
          </button>
        )}

        <button
          aria-label="Log Out"
          className="w-full px-4 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-red-500 hover:text-white focus:outline-none focus:ring-2 focus:ring-red-400"
          onClick={handleLogout}
        >
          Đăng xuất
        </button>
      </div>
    </div>
  );
};

ProfileSection.propTypes = {
  fullName: PropTypes.string,
  phoneNumber: PropTypes.string,
  email: PropTypes.string,
  profileImageUrl: PropTypes.string,
};

export default ProfileSection;
