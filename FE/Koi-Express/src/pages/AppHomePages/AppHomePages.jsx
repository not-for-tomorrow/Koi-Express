import React from "react";
import { Routes, Route } from "react-router-dom";
import Sidebar from "../../components/Sidebar/Sidebar";
import OrderPage from "../OrderPage/OrderPage";
import OrderHistory from "../OrderHistory/OrderHistory";
import OrderDetail from "../OrderPage/OrderDetail";
import Profile from "../Profile/Profile";
import PaymentSuccessful from "../OrderPage/PaymentSuccessful";
import PaymentFailed from "../OrderPage/PaymentFailed.jsx";

const AppHomePages = () => {
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
                    {/* Main Order Page */}
                    <Route path="/" element={<OrderPage />} />

                    {/* Order History */}
                    <Route path="/history" element={<OrderHistory />} />

                    {/* Order Detail Page */}
                    <Route path="/history/detail/:orderId" element={<OrderDetail />} />

                    {/* Profile Page */}
                    <Route path="/profile" element={<Profile />} />

                    {/* Payment Success Page */}
                    <Route path="/payment-successful" element={<PaymentSuccessful />} />

                    <Route path="/payment-failed" element={<PaymentFailed />} />
                </Routes>
            </div>
        </div>
    );
};

export default AppHomePages;
