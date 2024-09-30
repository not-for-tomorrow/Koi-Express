import React, { useState } from "react";
import Sidebar from "../../components/Sidebar/Sidebar";
import Profile from "../Profile/Profile";

const AppHomePages = () => {
  const [activePage, setActivePage] = useState("Đơn hàng mới");

  const renderContent = () => {
    switch (activePage) {
      case "Profile":
        return <Profile />;
      case "Đơn hàng mới":
        return <Orders />;
      case "Giao hàng liên tỉnh":
        return <Delivery />;
      case "Lịch sử đơn hàng":
        return <History />;
      default:
        return <Orders />;
    }
  };

  return (
    <div className="flex h-screen">
      <div className="h-full overflow-hidden">
        <Sidebar setActivePage={setActivePage} />
      </div>
      <div className="flex-1 h-full p-8 overflow-y-auto bg-gray-100">
        {renderContent()}
      </div>
    </div>
  );
};

export default AppHomePages;
