import React, { useState } from "react";
import axios from "axios";

const OtpVerification = ({ phoneNumber }) => {
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const response = await axios.post("http://localhost:8080/api/auth/verify-otp", { phoneNumber, otp });

      if (response.status === 200) {
        setSuccess("OTP verified successfully!");
        // Redirect or perform other actions
      } else {
        setError("Invalid OTP. Please try again.");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label>Enter OTP</label>
        <input
          type="text"
          value={otp}
          onChange={(e) => setOtp(e.target.value)}
          maxLength="4"
        />
      </div>
      {error && <p>{error}</p>}
      {success && <p>{success}</p>}
      <button type="submit">Verify OTP</button>
    </form>
  );
};

export default OtpVerification;
