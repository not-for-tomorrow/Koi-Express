import React, { useState } from "react";
import { Link } from "react-router-dom";

import axios from "axios";
import './Register.css'

const Register = () => {
  const [fullName, setFullName] = useState("");
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


    setError(""); // Clear errors

    // API request payload
    const requestData = {
      fullName,

      phoneNumber,
      password,
    };

    try {

      // Axios POST request to the register endpoint
      const response = await axios.post("http://localhost:8080/api/auth/register", requestData, {
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (response.status === 200) {
        setSuccess("Registration successful!");
        // Optionally clear form or redirect

        setFullName("");
        setPhoneNumber("");
        setPassword("");
      } else {
        setError("Registration failed. Please try again.");
      }
    } catch (err) {
      if (err.response) {
        switch (err.response.status) {
          case 400:
            setError("Invalid data. Please check your input.");
            break;
          case 500:
            setError("Server error. Please try again later.");
            break;
          default:
            setError("An error occurred. Please try again.");
        }
      } else {
        setError("An error occurred. Please try again.");
      }
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
                <label htmlFor="fullName" className="block mb-1 text-white">
                  FullName
                </label>
                <input
                  type="text"
                  id="fullName"
                  value={fullName}
                  onChange={(e) => setFullName(e.target.value)}
                  className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md"
                  placeholder="Enter your FullName"
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


              <button
                type="submit"
                disabled={!fullName || !phoneNumber || !password}
                className={`w-full h-12 text-lg font-medium text-black rounded-md outline-none mb-7 
             bg-white/70 ${!fullName || !phoneNumber || !password ? 'opacity-50 cursor-not-allowed' : ''}`}
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
