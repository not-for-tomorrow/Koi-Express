import React, { useState, useEffect } from "react";
import Sidebar from "../../components/Sidebar/Sidebar";
import Profile from "../Profile/Profile";
import axios from "axios";
import OrderPage from "../OrderPage/OrderPage";
import OrderHistory from "../OrderHistory/OrderHistory";
import { useNavigate } from 'react-router-dom';

const AppHomePages = () => {
  const [activePage, setActivePage] = useState("Đơn hàng mới");
  const [userInfo, setUserInfo] = useState({
    fullName: "",
    phoneNumber: "",
    email: ""
  });

  const navigate = useNavigate();

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        // Get token from localStorage or sessionStorage
        const token = localStorage.getItem("token") || sessionStorage.getItem("token");
        if (!token) {
          console.warn("No token found, waiting for authentication...");
          return;
        }

        // Make an API request to get user information
        const response = await axios.get("http://localhost:8080/api/customers/me", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });

        if (response.status === 200 && response.data.code === 200) {
          setUserInfo(response.data.result);
        }
      } catch (error) {
        console.error("Failed to fetch user information:", error);
        // Redirect user to login page if fetching user info fails
        navigate("/login");
      }
    };

    const timer = setTimeout(fetchUserInfo, 1000);

    return () => clearTimeout(timer);
  }, [navigate]);

  const renderContent = () => {
    switch (activePage) {
      case "Profile":
        return userInfo && (
          <Profile
            fullName={userInfo.fullName}
            phoneNumber={userInfo.phoneNumber}
            email={userInfo.email}
          />
        );
      case "Đơn hàng mới":
        return <OrderPage />;
      case "Lịch sử đơn hàng":
        return <OrderHistory />;
      default:
        return <Profile 
          fullName={userInfo.fullName}
          phoneNumber={userInfo.phoneNumber}
          email={userInfo.email}
        />;
    }
  };

  return (
    <div className="flex h-screen">
      <div className="h-full overflow-hidden">
        <Sidebar setActivePage={setActivePage} userInfo={userInfo} />
      </div>
      <div className="flex-1 h-full p-8 overflow-y-auto bg-gray-100">
        {renderContent()}
      </div>
    </div>
  );
};

export default AppHomePages;
