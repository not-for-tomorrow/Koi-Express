import React, { useContext, useState } from "react";
import { IoMdClose } from "react-icons/io";
import { IoReorderThreeOutline } from "react-icons/io5";
import { navItems } from "./IconsData";
import { useNavigate } from "react-router-dom";
import { UserContext } from "../UserContext/UserContext"; // Import UserContext

const Sidebar = () => {
  const [click, setClick] = useState(false);
  const [activeItem, setActiveItem] = useState(1);
  const navigate = useNavigate();

  // Access user info from context
  const user = useContext(UserContext);

  const handleItemClick = (item) => {
    setActiveItem(item.id); // Chỉ thay đổi activeItem cho các mục trong navItems
    // Điều hướng dựa trên item.title
    switch (item.title) {
      case "Tổng đơn hàng":
        navigate("/deliveringstaffpage");
        break;
        case "Logout":
          navigate("/login");
          break;
      default:
        break;
    }
  };

  return (
    <div className="flex items-start">
      <div
        className={`h-screen shadow-2xl bg-white text-black font-medium transition-all duration-300 ${
          click ? "w-[60px]" : "w-[280px]"
        } overflow-y-auto flex flex-col`}
      >
        {/* Sidebar header */}
        <div className="flex items-center justify-between">
          {click && (
            <div
              className="flex items-center justify-center w-full h-12 p-2 mb-2 text-blue-600 transition-all duration-300 bg-white cursor-pointer"
              onClick={() => setClick(!click)}
            >
              <IoReorderThreeOutline className="text-[28px]" />
            </div>
          )}
        </div>

        {!click ? (
          <div
            className="relative flex items-center justify-start p-4 bg-white"
            style={{ height: "100px" }}
          >
            <button
              onClick={() => setClick(true)}
              className="absolute top-2 right-2 text-gray-400 text-[20px] hover:text-black"
            >
              <IoMdClose />
            </button>

            {/* User Info */}
            <div className="flex items-center">
              <img
                className="w-12 h-12 rounded-full"
                src={user.avatarUrl || "https://via.placeholder.com/150"}
                alt="User Avatar"
              />
              <div className="ml-4">
                <p className="font-bold text-black">{user.name}</p>
                <p className="text-sm text-gray-500">{user.role}</p>
              </div>
            </div>
          </div>
        ) : null}

        {/* Navigation Items */}
        <ul className="flex flex-col p-0 m-0">
          {navItems.map((item) => (
            <li
              key={item.id}
              className={`flex items-center transition-all duration-300 cursor-pointer text-sm p-3 ${
                activeItem === item.id ? "bg-gray-100 text-black" : "text-black"
              } hover:bg-gray-100 ${
                click ? "justify-center w-[60px]" : "justify-start"
              }`}
              onClick={() => handleItemClick(item)}
            >
              <span
                className={`flex items-center ${
                  click ? "justify-center w-full" : "mr-3"
                }`}
              >
                {item.icons}
              </span>
              {!click && (
                <span className="ml-3 text-[16px] hover:text-black transition-colors duration-300">
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
