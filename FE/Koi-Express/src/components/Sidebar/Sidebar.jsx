import React, { useState, useEffect } from "react";
import { IoMdClose } from "react-icons/io";
import { IoReorderThreeOutline } from "react-icons/io5";
import { navItems } from "./IconsData";
import { useNavigate } from "react-router-dom";

const Sidebar = () => {
  const [click, setClick] = useState(false); // Toggle sidebar open/closed
  const [activeItem, setActiveItem] = useState(1); // Track the active item (default: first item)
  const [userInfo, setUserInfo] = useState({
    fullName: "",
    email: "",
    phone: "",
  });

  const navigate = useNavigate();

  // Lấy thông tin fullName và email từ localStorage
  const fetchUserInfoFromLocalStorage = () => {
    const storedUserInfo = localStorage.getItem("userInfo");
    if (storedUserInfo) {
      const parsedUserInfo = JSON.parse(storedUserInfo);
      setUserInfo((prevInfo) => ({
        ...prevInfo,
        fullName: parsedUserInfo.fullName || "Unknown User",
        email: parsedUserInfo.email || "No Email",
      }));
    }
  };

  // Gọi API để lấy phone từ basic-info
  const fetchPhoneFromAPI = async () => {
    const token = localStorage.getItem("token");
    if (token) {
      try {
        const response = await fetch("http://localhost:8080/api/customers/basic-info", {
          method: "GET",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        });
        if (response.ok) {
          const data = await response.json();
          setUserInfo((prevInfo) => ({
            ...prevInfo,
            phone: data.result.phoneNumber || "No Phone",
          }));
        } else {
          throw new Error("Failed to fetch phone number");
        }
      } catch (error) {
        console.error("Error fetching phone number:", error);
      }
    }
  };

  useEffect(() => {
    // Lấy thông tin fullName và email từ localStorage
    fetchUserInfoFromLocalStorage();

    // Lấy thông tin phone từ API
    fetchPhoneFromAPI();

    // Lắng nghe sự kiện thay đổi localStorage để cập nhật thông tin
    const handleStorageChange = () => {
      fetchUserInfoFromLocalStorage();
    };

    window.addEventListener("storage", handleStorageChange);

    return () => {
      window.removeEventListener("storage", handleStorageChange);
    };
  }, []);

  const handleItemClick = (item) => {
    setActiveItem(item.id); // Chỉ thay đổi activeItem cho các mục trong navItems
    // Điều hướng dựa trên item.title
    switch (item.title) {
      case "Đơn hàng mới":
        navigate("/appkoiexpress");
        break;
      case "Lịch sử đơn hàng":
        navigate("/appkoiexpress/history");
        break;
      default:
        break;
    }
  };

  const handleProfileClick = () => {
    // Điều hướng đến trang Profile mà không thay đổi trạng thái activeItem
    setActiveItem(null);
    navigate("/appkoiexpress/profile");
  };

  return (
    <div className="flex items-start">
      <div
        className={`h-screen shadow-2xl bg-white text-gray-400 text-[24px] transition-all duration-300 ${
          click ? "w-[60px]" : "w-80"
        } overflow-y-auto flex flex-col`}
      >
        {/* Sidebar header */}
        <div className="flex items-center justify-between">
          {click && (
            <div
              className="flex justify-center items-center w-full h-12 text-blue-600 bg-white p-[4px] transition-all duration-300 cursor-pointer mb-2"
              onClick={() => setClick(!click)}
            >
              <IoReorderThreeOutline className="text-[28px]" />
            </div>
          )}
        </div>
        {!click ? (
          <div className="relative p-4 mb-2 text-white bg-gradient-to-r from-blue-400 to-blue-600">
            <button
              onClick={() => setClick(true)}
              className="absolute top-2 right-2 text-gray-200 text-[20px] hover:text-white"
            >
              <IoMdClose />
            </button>
            <p className="absolute text-lg font-bold text-white top-2 left-4">
              Koi Express
            </p>

            <div
              className="p-3 mt-8 bg-blue-300 cursor-pointer"
              onClick={handleProfileClick} // Điều hướng tới Profile khi click vào user info
            >
              <div className="flex items-center gap-4">
                <img
                  src="https://via.placeholder.com/40"
                  alt="User Avatar"
                  className="w-8 h-8 rounded-full"
                />
                <div>
                  <p className="text-sm font-semibold text-black whitespace-nowrap">
                    {userInfo.fullName || "Unknown User"}
                  </p>
                  <p className="text-xs text-black">
                    {userInfo.email || "No Email"}
                  </p>
                  <p className="text-xs text-black">
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
              className={`flex items-center transition-all duration-300 cursor-pointer text-sm ${
                activeItem === item.id
                  ? "bg-gray-200 border-gray-300 text-black"
                  : "text-gray-500"
              } hover:border hover:border-gray-300 hover:bg-gray-200 p-2 ${
                click ? "justify-center w-[58px]" : "justify-start"
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
