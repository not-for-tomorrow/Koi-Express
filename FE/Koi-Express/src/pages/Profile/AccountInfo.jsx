import React, { useState } from "react";
import { iconsData } from "./IconsData.jsx";
import { FaPlus, FaTimes } from "react-icons/fa";

const AccountInfo = () => {
  const [tags, setTags] = useState([
    "Khách hàng tiềm năng",
    "Hàng cồng kềnh",
    "Trả hàng",
  ]);

  const handleAddTag = () => {
    const newTag = prompt("Nhập tên tag mới:");
    if (newTag) {
      setTags([...tags, newTag]);
    }
  };

  const handleDeleteTag = (index) => {
    const newTags = tags.filter((_, i) => i !== index);
    setTags(newTags);
  };

  return (
    <div className="p-8 mt-4 space-y-6 md:mt-0">
      {iconsData.map((card, index) => (
        <div
          key={index}
          className="flex items-center p-6 transition-shadow duration-300 ease-in-out bg-white rounded-lg shadow-md hover:shadow-lg"
        >
          {card.icon && <div className="mr-4">{card.icon}</div>}
          <div>
            <h3 className="text-sm font-semibold text-gray-800">
              {card.title}
            </h3>
            <p className="mt-2 text-sm text-gray-600">{card.description}</p>
          </div>
        </div>
      ))}

      <div className="flex items-center justify-between p-6 transition-shadow duration-300 ease-in-out bg-white rounded-lg shadow-md hover:shadow-lg">
        <div>
          <h1 className="text-[20px] font-semibold text-gray-800">Ngôn ngữ</h1>
        </div>
        <div className="flex items-center space-x-4">
          <button className="flex items-center px-4 py-2 text-sm transition-colors duration-300 border border-gray-300 rounded-lg hover:bg-gray-100">
            <img
              src="https://img.icons8.com/color/48/000000/great-britain.png"
              alt="English"
              className="w-5 h-5 mr-2"
            />
            English
          </button>
          <button className="flex items-center px-4 py-2 text-sm transition-colors duration-300 border border-gray-300 rounded-lg hover:bg-gray-100">
            <img
              src="https://img.icons8.com/color/48/000000/vietnam.png"
              alt="Tiếng Việt"
              className="w-5 h-5 mr-2"
            />
            Tiếng Việt
          </button>
        </div>
      </div>

      <div className="p-6 transition-shadow duration-300 ease-in-out bg-white rounded-lg shadow-md hover:shadow-lg">
        <div className="flex items-center justify-between mb-4">
          <h1 className="text-[20px] font-semibold text-gray-800">
            Tag đơn hàng
          </h1>
          <button
            onClick={handleAddTag}
            className="flex items-center px-4 py-2 text-sm text-gray-800 transition-colors duration-300 bg-gray-100 border border-gray-300 rounded-lg hover:bg-gray-200"
          >
            <FaPlus className="mr-2" />
            Tạo mới
          </button>
        </div>

        <div className="w-full h-[1.5px] bg-gray-200 rounded-md mb-4"></div>

        {tags.map((tag, index) => (
          <div
            key={index}
            className="flex items-center justify-between p-2 mb-4 transition-all duration-300 ease-in-out rounded-lg hover:bg-gray-100 group"
          >
            <span className="text-[14px] text-gray-700">{tag}</span>
            <button
              onClick={() => handleDeleteTag(index)}
              className="ml-2 text-gray-500 transition-opacity duration-300 opacity-0 hover:text-red-600 group-hover:opacity-100"
            >
              <FaTimes />
            </button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AccountInfo;
