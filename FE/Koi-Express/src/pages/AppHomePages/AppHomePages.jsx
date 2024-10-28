import React, { useEffect } from "react";
import { Routes, Route, useNavigate } from "react-router-dom";
import Sidebar from "../../components/Sidebar/Sidebar";
import OrderPage from "../OrderPage/OrderPage";
import OrderHistory from "../OrderHistory/OrderHistory";
import OrderDetail from "../OrderPage/OrderDetail";
import Profile from "../Profile/Profile";

const AppHomePages = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    const userInfo = localStorage.getItem("userInfo");

    // Redirect to /login if token or user info is missing
    if (!token || !userInfo) {
      navigate("/login");
    }
  }, [navigate]);

  console.log("AppHomePages loaded");

  return (
    <div className="flex h-screen">
      {/* Sidebar */}
      <div className="transition-all duration-300 bg-white shadow-lg">
        <Sidebar />
      </div>

      {/* Content area */}
      <div className="flex-grow h-full overflow-auto bg-gray-100">
        <Routes>
          <Route path="/" element={<OrderPage />} />
          <Route path="/history" element={<OrderHistory />} />
          <Route path="/history/detail/:orderId" element={<OrderDetail />} />
          <Route path="/profile" element={<Profile />} />
        </Routes>
      </div>
    </div>
  );
};

export default AppHomePages;
