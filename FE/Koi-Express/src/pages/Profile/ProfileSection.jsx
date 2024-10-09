import React from "react";
import PropTypes from "prop-types";
import { useNavigate } from 'react-router-dom';

const ProfileSection = ({ fullName, phoneNumber, email, profileImageUrl }) => {
  const navigate = useNavigate();

  const handleLogout = () => {
    localStorage.removeItem("authToken");
    console.log("Logout button clicked");
    navigate('/login');
  };

  return (
    <div className="flex flex-col items-center w-full p-8 mx-auto mt-4 bg-white rounded-lg shadow-md md:mt-0 max-w-auto">
      <img
        src={profileImageUrl || "https://via.placeholder.com/150"}
        alt="Profile"
        className="object-cover w-24 h-24 mt-4 border-4 border-blue-500 rounded-full"
      />
      <div className="flex flex-col items-center mt-2 space-y-4 text-center">
        <h2 className="text-2xl font-semibold text-gray-800">
          {fullName || "No Name Available"}
        </h2>
        <p className="text-sm text-black">
          {phoneNumber || "No Phone Number Available"}
        </p>
        <p className="text-sm text-black">
          {email || "No Email Available"}
        </p>
      </div>

      <div className="w-full h-[1.5px] bg-gray-200 rounded-md my-4"></div>

      <div className="flex flex-col w-full space-y-4">
        <button
          aria-label="Change Profile"
          className="w-full px-4 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400"
        >
          Thay đổi
        </button>
        
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