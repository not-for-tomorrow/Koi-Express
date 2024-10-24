import React, { createContext, useState, useEffect } from "react";
import jwt_decode from "jwt-decode";

// Create the UserContext
export const UserContext = createContext();

// Create a UserProvider component
export const UserProvider = ({ children }) => {
  const [user, setUser] = useState({
    name: "",
    role: "",
    avatarUrl: "https://via.placeholder.com/150",
  });

  // Fetch user data from the token
  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      const decodedToken = jwt_decode(token); // Decode the JWT token
      const { fullName, role } = decodedToken; // Extract fullName and role from the token
      setUser({
        name: fullName || "Guest", // Fallback if fullName is not available
        role: role || "User", // Fallback role if not provided
        avatarUrl: "https://via.placeholder.com/150", // Default avatar URL
      });
    }
  }, []);

  return <UserContext.Provider value={user}>{children}</UserContext.Provider>;
};
