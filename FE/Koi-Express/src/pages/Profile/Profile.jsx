import React, { useState, useEffect } from "react";
import ProfileSection from "./ProfileSection";
import AccountInfo from "./AccountInfo";
import PropTypes from "prop-types";
import jwt_decode from "jwt-decode";

const Profile = () => {
  const [userInfo, setUserInfo] = useState({
    fullName: "",
    email: "",
    phone: "",
  });

  useEffect(() => {
    const token = localStorage.getItem("token");
  
    if (token) {
      try {
        const decoded = jwt_decode(token);
        console.log("Decoded token:", decoded);
        
        // Set user information
        setUserInfo({
          fullName: decoded.fullName || "Unknown User",
          email: decoded.email || "No Email",
          phone: decoded.sub || "No Phone",
        });
      } catch (error) {
        console.error("Invalid token:", error);
      }
    } else {
      console.error("Token not found in localStorage.");
    }
  }, []);

  return (
    <div className="flex flex-col min-h-screen gap-4 p-8 bg-gray-100 md:flex-row md:items-start">
      <div className="flex-shrink-0 w-full md:w-1/4">
        <ProfileSection 
          fullName={userInfo.fullName}
          phoneNumber={userInfo.phone}
          email={userInfo.email}
        />
      </div>

      <div className="w-full md:w-3/4 md:mt-[-32px]">
        <AccountInfo 
          fullName={userInfo.fullName}
          phoneNumber={userInfo.phone}
          email={userInfo.email}
        />
      </div>
    </div>
  );
};

Profile.propTypes = {
  fullName: PropTypes.string,
  phoneNumber: PropTypes.string,
  email: PropTypes.string,
};

export default Profile;
