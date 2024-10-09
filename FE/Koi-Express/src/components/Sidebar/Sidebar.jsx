import React, { useState, useEffect } from "react";
import { IoMdClose } from "react-icons/io";
import { IoReorderThreeOutline } from "react-icons/io5";
import { navItems } from "./IconsData";
import jwt_decode from "jwt-decode";

const Sidebar = ({ setActivePage }) => {
  const [click, setClick] = useState(false); // Toggle sidebar open/closed
  const [activeItem, setActiveItem] = useState(1); // Track the active item (default: first item)
  const [userInfo, setUserInfo] = useState({
    fullName: "",
    email: "",
    phone: "",
  });

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      try {
        // Giải mã token
        const decoded = jwt_decode(token);

        console.log("Decoded token:", decoded);
        // Lưu thông tin người dùng
        setUserInfo({
          fullName: decoded.fullName || "Unknown User",
          email: decoded.email || "No Email",
          phone: decoded.sub || "No Phone",
        });
      } catch (error) {
        console.error("Invalid token:", error);
      }
    } else {
      console.error("Token not found in localStorage.");
    }
  }, []);

  const handleItemClick = (item) => {
    setActiveItem(item.id);
    setActivePage(item.title); // Set active page when a sidebar item is clicked
  };

  return (
    <div className="flex items-start">
      <div
        className={`h-screen shadow-2xl bg-white text-gray-400 text-[30px] transition-all duration-300 ${
          click ? "w-[60px]" : "w-80"
        } overflow-y-auto flex flex-col`}
      >
        <div className="flex items-center justify-between">
          {click && (
            <div
              className="flex justify-center items-center w-full h-12 text-blue-600 bg-white p-[4px] transition-all duration-300 cursor-pointer mb-2"
              onClick={() => setClick(!click)}
            >
              <IoReorderThreeOutline className="text-[35px]" />
            </div>
          )}
        </div>

        {!click ? (
          <div className="relative p-4 mb-2 text-white bg-gradient-to-r from-blue-400 to-blue-600">
            <button
              onClick={() => setClick(true)}
              className="absolute top-2 right-2 text-gray-200 text-[24px] hover:text-white"
            >
              <IoMdClose />
            </button>
            <p className="absolute text-lg font-bold text-white top-2 left-4">
              Koi Express
            </p>

            <div
              className="p-3 mt-8 bg-blue-300 cursor-pointer"
              onClick={() => setActivePage("Profile")}
            >
              <div className="flex items-center gap-4">
                <img
                  src="https://via.placeholder.com/40"
                  alt="User Avatar"
                  className="w-10 h-10 rounded-full"
                />
                <div>
                  <p className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    {userInfo.fullName || "Unknown User"}
                  </p>
                  <p className="text-sm text-gray-600">
                    {userInfo.email || "No Email"}
                  </p>
                  <p className="text-sm text-gray-600">
                    {userInfo.phone || "No Phone"}
                  </p>
                </div>
              </div>
            </div>
          </div>
        ) : null}

        <ul className="flex flex-col p-0 m-0">
          {navItems.map((item) => (
            <li
              key={item.id}
              className={`flex items-center transition-all duration-300 cursor-pointer ${
                activeItem === item.id
                  ? "bg-gray-200 border-gray-300 text-black"
                  : "text-gray-500"
              } hover:border hover:border-gray-300 hover:bg-gray-200 p-2 ${
                click ? "justify-center w-[58px]" : "justify-start"
              }`}
              onClick={() => handleItemClick(item)}
              style={{ borderRadius: "0px", marginBottom: "0px" }} // Remove rounded corners and remove margin between items
            >
              <span
                title={item.title}
                className={`flex items-center ${
                  click ? "justify-center w-full" : "mr-3"
                }`}
              >
                {item.icons}
              </span>
              {!click && (
                <span className="ml-3 text-[19px] hover:text-black transition-colors duration-300">
                  {item.title}
                </span>
              )}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
};

export default Sidebar;
