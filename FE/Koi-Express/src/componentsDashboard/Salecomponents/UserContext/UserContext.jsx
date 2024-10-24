import React, { createContext, useState, useEffect } from "react";

// Create the UserContext
export const UserContext = createContext();

// Create a UserProvider component
export const UserProvider = ({ children }) => {
  const [user, setUser] = useState({
    name: "Hizrian",
    role: "Administrator",
    avatarUrl: "https://via.placeholder.com/150",
  });

  // Simulate fetching user data from API using token (for future use)
  useEffect(() => {
    // Example of future API logic using token
    // const token = localStorage.getItem("token");
    // if (token) {
    //   fetchUserData(token).then((data) => setUser(data));
    // }
  }, []);

  return (
    <UserContext.Provider value={user}>
      {children}
    </UserContext.Provider>
  );
};
