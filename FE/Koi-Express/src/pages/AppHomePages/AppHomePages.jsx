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
      {/* Sidebar without onClick, as it's handled inside Sidebar itself */}
      <div className="transition-all duration-300 bg-white shadow-lg">
        <Sidebar setActivePage={setActivePage} />
      </div>

      {/* Content area that expands based on sidebar's width */}
      <div className="flex-grow h-full overflow-auto bg-gray-100">
        {renderContent()}
      </div>
    </div>
  );
};

export default AppHomePages;
