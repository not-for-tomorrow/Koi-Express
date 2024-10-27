import React from "react";
import { Route, Routes } from "react-router-dom";
import Sidebar from "../componentsDashboard/DeliveringStaffcomponents/Sidebar/Sidebar";
import Order from "./Order";
import { UserProvider } from "../componentsDashboard/UserContext";

const DeliveringStaffpage = () => {
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
            </Routes>
          </div>
        </div>
      </div>
    </UserProvider>
  );
};

export default DeliveringStaffpage;
