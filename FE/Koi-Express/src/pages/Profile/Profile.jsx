import React, { useState, useEffect } from "react";
import ProfileSection from "./ProfileSection";
import AccountInfo from "./AccountInfo";
import PropTypes from "prop-types";

const Profile = () => {
  const [userInfo, setUserInfo] = useState({
    fullName: "",
    email: "",
    phone: "",
  });

  const fetchUserInfo = () => {
    const token = localStorage.getItem("token");

    if (token) {
      fetch("http://localhost:8080/api/customers/basic-info", {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      })
        .then((response) => {
          if (!response.ok) {
            throw new Error("Failed to fetch user info");
          }
          return response.json();
        })
        .then((data) => {
          const { fullName, email, phoneNumber } = data.result;
          setUserInfo({
            fullName: fullName || "Unknown User",
            email: email || "No Email",
            phone: phoneNumber || "No Phone",
          });
        })
        .catch((error) => {
          console.error("Error fetching user info:", error);
        });
    } else {
      console.error("Token not found in localStorage.");
    }
  };

  useEffect(() => {
    fetchUserInfo();
  }, []);

  return (
    <div className="flex flex-col min-h-screen gap-4 p-8 bg-gray-100 md:flex-row md:items-start">
      <div className="flex-shrink-0 w-full md:w-1/4">
        <ProfileSection
          fullName={userInfo.fullName}
          phoneNumber={userInfo.phone}
          email={userInfo.email}
          onUpdateSuccess={fetchUserInfo} // Pass the fetchUserInfo function as a prop
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
