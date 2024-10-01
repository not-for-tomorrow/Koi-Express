import React from "react";

const ProfileSection = ({fullName, phoneNumber, email}) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-8 mt-4 md:mt-0 w-full max-w-auto h-[437px] mx-auto flex flex-col items-center justify-between">
      <img
        src="https://via.placeholder.com/150"
        alt="Profile"
        className="object-cover w-24 h-24 mt-4 border-4 border-blue-500 rounded-full"
      />
      <div className="flex flex-col items-center mt-2 space-y-4 text-center">
        <h2 className="text-2xl font-semibold text-gray-800">
        {fullName || "No Name Available"}
        </h2>
        <p className="text-sm text-gray-500">{phoneNumber || "No Phone Number Available"}</p>
        <p className="text-sm text-gray-500">{email || "No Email Available"}</p>
      </div>

      <div className="w-full h-[1.5px] bg-gray-200 rounded-md"></div>

      <div className="flex flex-col w-full mt-4 space-y-2">
        <button className="w-full px-4 py-2 text-sm text-gray-700 transition-all duration-300 ease-in-out bg-gray-200 rounded hover:bg-gray-300 focus:outline-none focus:ring-2 focus:ring-gray-400">
          Thay đổi
        </button>

        <button className="w-full text-sm font-semibold text-red-500 hover:text-red-600">
          Đăng xuất
        </button>
      </div>
    </div>
  );
};

export default ProfileSection;
