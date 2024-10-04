import React, { useState } from "react";
import { IoMdClose } from "react-icons/io";
import { IoReorderThreeOutline } from "react-icons/io5";
import { navItems } from "./IconsData";

const Sidebar = ({ setActivePage }) => {
  const [click, setClick] = useState(false); // Toggle sidebar open/closed
  const [activeItem, setActiveItem] = useState(1); // Track the active item (default: first item)

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
              className="flex justify-center items-center w-full h-12 text-blue-600 rounded-lg bg-white p-[4px] transition-all duration-300 cursor-pointer mb-5"
              onClick={() => setClick(!click)}
            >
              <IoReorderThreeOutline className="text-[35px]" />
            </div>
          )}
        </div>

        {!click ? (
          <div className="relative p-4 mb-5 text-white rounded-lg bg-gradient-to-r from-blue-400 to-blue-600">
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
              className="p-3 mt-8 mb-6 bg-blue-300 rounded-lg cursor-pointer"
              onClick={() => {setActivePage("Profile")}}
            >
              <div className="flex items-center gap-4">
                <img
                  src="https://via.placeholder.com/40"
                  alt="User Avatar"
                  className="w-10 h-10 rounded-full"
                />
                <div>
                  <p className="text-lg font-semibold text-gray-800 whitespace-nowrap">
                    Nguyễn Nhất Huy
                  </p>
                  <p className="text-sm text-gray-600">Đồ điện gia dụng_L012</p>
                  <p className="text-sm text-gray-600">+84 909984643</p>
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
              } hover:border hover:border-gray-300 hover:bg-gray-200 p-[1px] rounded-lg ${
                click
                  ? "justify-center w-[58px] mr-[1px] mb-10" // Add spacing between icons when collapsed
                  : "justify-start mb-7" // Add different spacing when expanded
              }`}
              onClick={() => handleItemClick(item)}
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
