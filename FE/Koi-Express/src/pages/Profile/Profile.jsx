import React from "react";
import ProfileSection from "./ProfileSection";
import AccountInfo from "./AccountInfo";
import PropTypes from "prop-types";

const Profile = ({ fullName, phoneNumber, email }) => {
  return (
    <div className="flex flex-col min-h-screen gap-4 p-8 bg-gray-100 md:flex-row md:items-start">
      <div className="flex-shrink-0 w-full md:w-1/4">
        <ProfileSection 
          fullName={fullName}
          phoneNumber={phoneNumber}
          email={email}
        />
      </div>

      <div className="w-full md:w-3/4 md:mt-[-32px]">
        <AccountInfo 
          fullName={fullName}
          phoneNumber={phoneNumber}
          email={email}
        />
      </div>
    </div>
  );
};

Profile.propTypes = {
  fullName: PropTypes.string,
  phoneNumber: PropTypes.string,
  email: PropTypes.string,
};

export default Profile;
