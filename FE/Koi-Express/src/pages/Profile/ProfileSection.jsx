import React from "react";
import PropTypes from "prop-types";
import { useNavigate } from 'react-router-dom';

const ProfileSection = ({ fullName, phoneNumber, email, profileImageUrl }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    // Clear the authentication token
    localStorage.removeItem("authToken");
    console.log("Logout button clicked");
    
    // Redirect to the login page
    navigate('/login');
  };

  return (
    <div className="bg-white rounded-lg shadow-md p-8 mt-4 md:mt-0 w-full max-w-auto mx-auto flex flex-col items-center justify-between">
      <img
        src={profileImageUrl || "https://via.placeholder.com/150"}
        alt="Profile"
        className="object-cover w-24 h-24 mt-4 border-4 border-blue-500 rounded-full"
      />
      <div className="flex flex-col items-center mt-2 space-y-4 text-center">
        <h2 className="text-2xl font-semibold text-gray-800">
          {fullName || "No Name Available"}
        </h2>
        <p className="text-sm text-gray-500">
          {phoneNumber || "No Phone Number Available"}
        </p>
        <p className="text-sm text-gray-500">
          {email || "No Email Available"}
        </p>
      </div>

      <div className="w-full h-[1.5px] bg-gray-200 rounded-md"></div>

      <div className="flex flex-col w-full mt-4 space-y-2">
        <button
          aria-label="Change Profile"
          className="w-full px-4 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
        >
          Thay đổi
        </button>

        <button
          aria-label="Log Out"
          className="w-full text-sm font-semibold text-red-500 hover:text-red-600"
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
