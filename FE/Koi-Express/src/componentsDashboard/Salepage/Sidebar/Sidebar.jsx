import React, { useState } from "react";
import { IoMdClose } from "react-icons/io";
import { IoReorderThreeOutline } from "react-icons/io5";
import { navItems } from "./IconsData";
import { useNavigate } from "react-router-dom";

const Sidebar = () => {
  const [click, setClick] = useState(false); // Toggle sidebar open/closed
  const [activeItem, setActiveItem] = useState(1); // Track the active item (default: first item)

  const navigate = useNavigate();

  const handleItemClick = (item) => {
    setActiveItem(item.id); // Chỉ thay đổi activeItem cho các mục trong navItems
    // Điều hướng dựa trên item.title
    switch (item.title) {
      case "Tổng đơn hàng":
        navigate("/salepage");
        break;
      case "Tài khoản khách hàng":
        navigate("/salepage/customeraccount");
        break;
      case "Duyệt đơn hàng":
        navigate("/salepage/acceptorder");
        break;
      default:
        break;
    }
  };

  return (
    <div className="flex items-start">
      <div
  className={`h-screen shadow-2xl bg-white text-black font-medium transition-all duration-300 ${
    click ? "w-[60px]" : "w-[250px]"
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
  <div className="relative flex items-center justify-center mb-4 bg-white" style={{ height: "100px" }}>
  <button
    onClick={() => setClick(true)}
    className="absolute top-2 right-2 text-gray-400 text-[20px] hover:text-black"
  >
    <IoMdClose />
  </button>
  {/* Centered Sidebar Title */}
  <div className="flex items-center justify-center w-full">
    <p className="text-[24px] font-bold text-black m-0 p-0" style={{ lineHeight: "1" }}>
      Koi Express
    </p>
  </div>
</div>

  ) : null}

  {/* Sections */}
  <ul className="flex flex-col p-0 m-0">
    {navItems.map((item) => (
      <li
        key={item.id}
        className={`flex items-center transition-all duration-300 cursor-pointer text-sm p-3 ${
          activeItem === item.id
            ? "bg-gray-100 text-black"
            : "text-black"
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
