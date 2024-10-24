import React, { useContext } from "react";
import { UserContext } from "../UserContext/UserContext"; // Import UserContext

const Header = () => {
  const user = useContext(UserContext);

  return (
    <header className="flex items-center justify-between p-4 bg-white shadow">
      <div className="text-xl font-bold ml-7 ">Koi Express</div>

      <div className="flex items-center mr-4">
        <img
          className="w-8 h-8 mr-2 rounded-full"
          src={user.avatarUrl || "https://via.placeholder.com/150"}
          alt="User Avatar"
        />
        <div>
          <p className="font-bold">{user.name}</p>
          <p className="text-sm text-gray-500">{user.role}</p>
        </div>
      </div>
    </header>
  );
};

export default Header;
