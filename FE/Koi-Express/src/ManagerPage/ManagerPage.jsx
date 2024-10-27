import React from "react";
import { Route, Routes } from "react-router-dom";
import Sidebar from "../componentsDashboard/Managercomponents/Sidebar/Sidebar";
import Order from "./Order";
import { UserProvider } from "../componentsDashboard/UserContext";
import SaleStaffAccount from "./SaleStaffAccount";
import DeliveringStaffAccount from "./DeliveringStaffAccount";
import CustomerAccount from "./CustomerAccount";

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
              <Route path="/" element={<Order />} />
              <Route path="/customeraccount" element={<CustomerAccount />} />
              <Route path="/salestaffaccount" element={<SaleStaffAccount />} />
              <Route path="/deliveringstaffaccount" element={<DeliveringStaffAccount />} />
            </Routes>
          </div>
        </div>
      </div>
    </UserProvider>
  );
};

export default ManagerPage;
