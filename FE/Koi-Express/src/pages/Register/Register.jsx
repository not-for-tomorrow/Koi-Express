import React, { useState } from "react";
import { Link } from "react-router-dom";
import './Register.css'

const Register = () => {
  const [username, setUsername] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  // const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handlePhoneNumberChange = (e) => {
    const value = e.target.value;
    // Ensure only numbers are entered
    if (/^\d*$/.test(value)) {
      setPhoneNumber(value);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    // Validate phone number length
    if (phoneNumber.length !== 10) {
      setError("Please enter exactly 10 digits for the phone number.");
      return;
    }

    // Validate password match
    // if (password !== confirmPassword) {
    //   setError("Passwords do not match.");
    //   return;
    // }

    setError(""); // Clear errors

    // API request payload
    const requestData = {
      username,
      phoneNumber,
      password,
    };

    try {
      const response = await fetch("https://your-api-endpoint.com/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData),
      });

      if (response.ok) {
        setSuccess("Registration successful!");
      } else {
        setError("Registration failed. Please try again.");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    }
  };

  return (
    <>
      <div className="flex items-center justify-center w-full min-h-screen registerpage">
        <div className="w-[32%] h-auto py-10 px-12 rounded-xl registercard">
          <div className="w-full h-auto">
            <h1 className="text-[2rem] text-white font-semibold-mb-1 text-center">
              Sign up
            </h1>
          </div>

          <form onSubmit={handleSubmit}>
            {/* Username Field */}
            <div className="w-full h-auto mb-5">
              <label htmlFor="username" className="block mb-1 text-white">
                Username
              </label>
              <input
                type="text"
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md"
                placeholder="Enter your username"
              />
            </div>

            {/* Phone Number Field */}
            <div className="w-full h-auto mb-5">
              <label htmlFor="phone" className="block mb-1 text-white">
                Phone Number
              </label>
              <input
                type="text"
                id="phone"
                value={phoneNumber}
                onChange={handlePhoneNumberChange}
                maxLength="10"
                className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md"
                placeholder="Enter your phone number"
              />
            </div>

            {/* Error and Success Messages */}
            {error && <p className="mb-5 text-sm text-red-500">{error}</p>}
            {success && (
              <p className="mb-5 text-sm text-green-500">{success}</p>
            )}

            {/* Password Field */}
            <div className="w-full h-auto mb-9">
              <label htmlFor="password" className="block mb-1 text-white">
                Password
              </label>
              <input
                type="password"
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md"
                placeholder="Enter your password"
              />
            </div>

            {/* Confirm Password Field */}
            {/* <div className="w-full h-auto mb-9">
              <label
                htmlFor="confirmPassword"
                className="block mb-1 text-white"
              >
                Confirm Password
              </label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md"
                placeholder="Confirm your password"
              />
            </div> */}

            <button
              type="submit"
              className="w-full h-12 text-lg font-medium text-black rounded-md outline-none mb-7 bg-white/70"
            >
              Sign up
            </button>
          </form>

          <div className="flex items-center justify-center w-full h-auto gap-x-1">
            <p className="text-base font-medium text-white">
              Already have an account?
            </p>
            <Link
              to="/login"
              className="text-base font-medium text-white duration-500 ease-out hover:underline"
            >
              Back to login
            </Link>
          </div>
        </div>
      </div>
    </>
  );
};

export default Register;
