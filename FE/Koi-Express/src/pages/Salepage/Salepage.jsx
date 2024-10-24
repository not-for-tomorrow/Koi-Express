import React from "react";
import { Route, Routes } from "react-router-dom";
import Sidebar from "../../componentsDashboard/Salecomponents/Sidebar/Sidebar";
import Header from "../../componentsDashboard/Salecomponents/Header/Header"; // Import Header component
import Order from "./Order";
import CustomerAccount from "./CustomerAccount";
import AcceptOrder from "./AcceptOrder";
import { UserProvider } from "../../componentsDashboard/Salecomponents/UserContext/UserContext"; // Import UserProvider

const Salepage = () => {
  return (
    <UserProvider>
      <div className="flex flex-col h-screen overflow-hidden">
        <Header />

        <div className="flex flex-grow overflow-hidden">
          <div className="h-full bg-white shadow-lg">
            <Sidebar />
          </div>

          {/* Content area: Keep within full screen, prevent scrolling */}
          <div className="flex-grow h-full bg-gray-100">
            <Routes>
              <Route path="/" element={<Order />} />
              <Route path="/customeraccount" element={<CustomerAccount />} />
              <Route path="/acceptorder" element={<AcceptOrder />} />
            </Routes>
          </div>
        </div>
      </div>
    </UserProvider>
  );
};

export default Salepage;
