import React, { useState, useEffect } from "react";
import Sidebar from "../../components/Sidebar/Sidebar";
import Profile from "../Profile/Profile";
import axios from "axios";
import OrderPage from "../OrderPage/OrderPage";
import OrderHistory from "../OrderHistory/OrderHistory";

const AppHomePages = () => {
  const [activePage, setActivePage] = useState("Đơn hàng mới");
  const [userInfo, setUserInfo] = useState(null);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        // Get token from localStorage or sessionStorage
        const token = localStorage.getItem("token") || sessionStorage.getItem("token");
        if (!token) {
          throw new Error("No token found");
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
        // window.location.href = "/login";
      }
    };

    fetchUserInfo();
  }, []);

  const renderContent = () => {
    switch (activePage) {
      case "Profile":
        // return // userInfo && (
        //   <Profile
             // fullName={userInfo.fullName}
            // phoneNumber={userInfo.phoneNumber}
            // email={userInfo.email}
        //   />
        // );
        return <Profile />;
      case "Đơn hàng mới":
        return <OrderPage />;
      case "Lịch sử đơn hàng":
        return <OrderHistory/>;
      default:
        return <Profile />;
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
