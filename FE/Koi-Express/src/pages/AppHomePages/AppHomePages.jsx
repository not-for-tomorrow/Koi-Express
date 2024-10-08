import React, { useState } from "react";
import Sidebar from "../../components/Sidebar/Sidebar";
import OrderPage from "../OrderPage/OrderPage";
import OrderHistory from "../OrderHistory/OrderHistory";
import Profile from "../Profile/Profile";

const AppHomePages = () => {
  const [activePage, setActivePage] = useState("Đơn hàng mới");

  const renderContent = () => {
    switch (activePage) {
      case "Profile":
        return <Profile />;
      case "Đơn hàng mới":
        return <OrderPage />;
      case "Lịch sử đơn hàng":
        return <OrderHistory />;
      default:
        return <OrderPage />;
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
