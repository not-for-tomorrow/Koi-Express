import React, {useState, useEffect} from "react";
import ProfileSection from "./ProfileSection";

const Profile = () => {
    const [userInfo, setUserInfo] = useState({
        fullName: "",
        email: "",
        phone: "",
    });
    const [loading, setLoading] = useState(true);

    const fetchUserInfo = () => {
        const token = localStorage.getItem("token");

        if (token) {
            fetch("http://localhost:8080/api/customers/basic-info", {
                method: "GET",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            })
                .then((response) => {
                    if (!response.ok) {
                        throw new Error("Failed to fetch user info");
                    }
                    return response.json();
                })
                .then((data) => {
                    const {fullName, email, phoneNumber} = data.result;
                    setUserInfo({
                        fullName: fullName || "Unknown User",
                        email: email || "No Email",
                        phone: phoneNumber || "No Phone",
                    });
                })
                .catch((error) => {
                    console.error("Error fetching user info:", error);
                })
                .finally(() => {
                    setLoading(false);
                });
        } else {
            console.error("Token not found in localStorage.");
            setLoading(false);

        }
    };

    useEffect(() => {
        fetchUserInfo();
    }, []);

    return (
        <div className="flex items-center justify-center w-full min-h-screen p-8 bg-gray-100">
            {loading ? (
                <p>Loading...</p>
            ) : (
                <div className="w-full max-w-2xl">
                    <ProfileSection
                        fullName={userInfo.fullName}
                        phoneNumber={userInfo.phone}
                        email={userInfo.email}
                        onUpdateSuccess={fetchUserInfo}
                    />
                </div>
            )}
        </div>
    );
};

export default Profile;
