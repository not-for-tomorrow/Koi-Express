import React, { useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import OtpModal from "../OTP/OtpModal";
import "./Register.css";

const Register = () => {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [showOtpModal, setShowOtpModal] = useState(false);

  const handlePhoneNumberChange = (e) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) {
      setPhoneNumber(value);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    if (phoneNumber.length !== 10) {
      setError("Please enter exactly 10 digits for the phone number.");
      setLoading(false);
      return;
    }

    const requestData = {
      fullName,
      email,
      phoneNumber,
      password,
    };

    try {
      const response = await axios.post(
        "http://localhost:8080/api/auth/register",
        requestData,
        {
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (response.status === 200) {
        // Show OTP modal
        setShowOtpModal(true);
      } else {
        setError("Registration failed. Please try again.");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleOtpVerifySuccess = () => {
    setShowOtpModal(false);
    // Proceed with successful registration logic, e.g., redirect to homepage or login
    alert("Registration successful! OTP verified.");
  };

  return (
    <section className="registerpage bg-gray-50 min-h-screen flex items-center justify-center">
      <div className="registercard bg-gray-100 flex rounded-2xl shadow-lg max-w-3xl p-5 items-center justify-start">
        <div className="md:block hidden w-1/2 mr-auto">
          <img
            className="rounded-2xl"
            src="https://images.unsplash.com/photo-1616606103915-dea7be788566?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80"
            alt="Register Illustration"
          />
        </div>

        <div className="md:w-1/2 px-8 md:px-16">
          <h2 className="font-bold text-2xl text-[#002D74]">Register</h2>

          <form
            onSubmit={handleSubmit}
            className="flex flex-col gap-4"
            autoComplete="off"
          >
            {/* Hidden fields to prevent autofill */}
            <input
              type="text"
              name="hidden-fullName"
              style={{ display: "none" }}
              autoComplete="fullName"
            />
            <input
              type="password"
              name="hidden-password"
              style={{ display: "none" }}
              autoComplete="new-password"
            />

            <input
              className="p-2 mt-8 rounded-xl border"
              type="text"
              id="full_name_no_autofill"
              name="register_full_name_custom"
              placeholder="Full Name"
              value={fullName}
              onChange={(e) => setFullName(e.target.value)}
              required
              autoComplete="off"
              data-lpignore="true"
              data-form-type="other"
              aria-autocomplete="none"
            />
            <input
              className="p-2 mt-4 rounded-xl border"
              type="email"
              name="register_email_custom"
              placeholder="Email Address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
              autoComplete="off"
            />
            <input
              className="p-2 mt-4 rounded-xl border"
              type="text"
              name="register_phone_number_custom"
              placeholder="Phone Number"
              value={phoneNumber}
              onChange={handlePhoneNumberChange}
              required
              autoComplete="off"
            />
            <input
              className="p-2 rounded-xl border w-full"
              type="password"
              id="password_no_autofill"
              name="register_password_custom"
              placeholder="Password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
              autoComplete="off"
            />

            <button
              type="submit"
              className="bg-[#002D74] rounded-xl text-white py-2 hover:scale-105 duration-300"
            >
              {loading ? "Registering..." : "Register"}
            </button>
          </form>

          {error && <p className="text-red-500 text-sm mt-2">{error}</p>}

          <div className="mt-5 text-xs flex justify-between items-center text-[#002D74]">
            <p>Already have an account?</p>
            <Link to="/login">
              <button className="py-2 px-5 bg-white border rounded-xl hover:scale-110 duration-300">
                Login
              </button>
            </Link>
          </div>
        </div>
      </div>

      {/* OTP Modal */}
      {showOtpModal && (
        <OtpModal
          phoneNumber={phoneNumber}
          onClose={() => setShowOtpModal(false)}
          onVerifySuccess={handleOtpVerifySuccess}
        />
      )}
    </section>
  );
};

export default Register;
