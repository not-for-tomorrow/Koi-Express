import React, { useState } from "react";
import axios from "axios";
import "./OtpModal.css"; // Ensure the CSS file exists in the correct location

const OtpModal = ({ phoneNumber, onClose, onVerifySuccess }) => {
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    if (otp.length !== 4) {
      setError("OTP must be 4 digits");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/api/auth/verify-otp", {
        phoneNumber,
        otp,
      });

      if (response.status === 200) {
        setSuccess("OTP verification successful!");
        onVerifySuccess(); // Call the success callback
      } else {
        setError("OTP verification failed. Please try again.");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="otp-modal-overlay">
      <div className="otp-modal-container">
        <h2 className="font-bold text-2xl text-[#002D74]">Verify OTP</h2>
        <p className="mt-2 text-sm text-gray-600">Please enter the 6-digit OTP sent to {phoneNumber}.</p>

        <form onSubmit={handleSubmit} className="flex flex-col gap-4 mt-4">
          <input
            className="p-2 text-center border rounded-lg"
            type="text"
            maxLength="6"
            placeholder="Enter OTP"
            value={otp}
            onChange={(e) => setOtp(e.target.value)}
            required
          />
          <button
            type="submit"
            className="bg-[#002D74] rounded-xl text-white py-2 hover:scale-105 duration-300"
            disabled={loading}
          >
            {loading ? "Verifying..." : "Verify OTP"}
          </button>
        </form>

        {error && <p className="error-message">{error}</p>}
        {success && <p className="success-message">{success}</p>}

        <button onClick={onClose} className="cancel-button">
          Cancel
        </button>
      </div>
    </div>
  );
};

export default OtpModal;
