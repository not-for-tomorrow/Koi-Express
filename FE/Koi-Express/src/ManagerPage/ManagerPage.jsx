import React from "react";
import { Route, Routes } from "react-router-dom";
import Sidebar from "../componentsDashboard/Managercomponents/Sidebar/Sidebar";
import { UserProvider } from "../componentsDashboard/UserContext";
import SaleStaffAccount from "./SaleStaffAccount";
import DeliveringStaffAccount from "./DeliveringStaffAccount";
import CustomerAccount from "./CustomerAccount/CustomerAccount.jsx";
import CustomerDetail from "./CustomerAccount/CustomerDetail.jsx";
import OrderDetail from "./OrderDetail/OrderDetail.jsx";
import Dashboard from "./Dashboard.jsx";
import Blog from "./Blog/Blog.jsx";

const ManagerPage = () => {
  return (
    <UserProvider>
      <div className="flex flex-col h-screen overflow-hidden">
        <div className="flex flex-grow overflow-hidden">
          <div className="h-full bg-white shadow-lg">
            <Sidebar />
          </div>

          {/* Content area: Keep within full screen, prevent scrolling */}
          <div className="flex-grow h-full bg-gray-100">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/customeraccount" element={<CustomerAccount />} />
              <Route path="/salestaffaccount" element={<SaleStaffAccount />} />
              <Route
                path="/deliveringstaffaccount"
                element={<DeliveringStaffAccount />}
              />
              <Route
                path="/customeraccount/:customerId"
                element={<CustomerDetail />}
              />
              <Route
                path="/recentorder/detail/:orderId"
                element={<OrderDetail />}
              />
              <Route path="/blog/*" element={<Blog />} />
            </Routes>
          </div>
        </div>
      </div>
    </UserProvider>
  );
};

export default ManagerPage;