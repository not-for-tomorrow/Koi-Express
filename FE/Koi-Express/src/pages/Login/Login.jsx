import React, { useState } from "react";
import { Link } from "react-router-dom";
import "./Login.css";

const Login = () => {
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handlePhoneNumberChange = (e) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) {
      setPhoneNumber(value);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (phoneNumber.length !== 10) {
      setError("Please enter exactly 10 digits for the phone number.");
      return;
    }

    setError("");

    const requestData = {
      phoneNumber,
      password,
    };

    try {
      const response = await fetch("https://your-api-endpoint.com/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(requestData),
      });

      if (response.ok) {
        const data = await response.json();
        setSuccess("Login successful!");
        console.log("Token:", data.token);
      } else {
        setError("Login failed. Please check your phone number and password.");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    }
  };

  // Function to handle Google OAuth login
  const handleGoogleLogin = () => {
    // Redirect to Google OAuth API
    window.location.href = "http://localhost:8080/login/oauth2/code/google";
  };

  return (
    <div className="flex items-center justify-center w-full min-h-screen loginpage">
      <div className="w-[32%] h-auto py-10 px-12 rounded-xl logincard">
        <div className="w-full h-auto">
          <h1 className="text-[2rem] text-white font-semibold-mb-1 text-center">
            Sign in
          </h1>
        </div>

        <form onSubmit={handleSubmit}>
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
            {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
          </div>

          <div className="w-full h-auto mb-5">
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

          <div className="flex items-center justify-between w-full h-auto mb-5">
            <div className="flex items-center gap-x-2">
              <input
                type="checkbox"
                id="remember"
                className="w-4 h-4 text-white border rounded-md border-gray-200/20 accent-gray-200/20"
              />
              <label htmlFor="remember" className="text-[0.875rem] text-white">
                Remember me
              </label>
            </div>
            <div className="w-auto h-auto text-white">
              <Link className="text-sm font-medium text-white duration-500 ease-out hover:underline">
                Forgot password?
              </Link>
            </div>
          </div>

          <button
            type="submit"
            className="w-full h-12 text-lg font-medium text-black rounded-md outline-none bg-white/70"
          >
            Sign in
          </button>
        </form>

        <div className="flex items-center w-full h-auto my-5 gap-x-1">
          <div className="w-1/2 h-[1.5px] rounded-md h1 bg-gray-200/40"></div>
          <p className="px-2 text-sm font-normal text-gray-300">OR</p>
          <div className="w-1/2 h-[1.5px] rounded-md h1 bg-gray-200/40"></div>
        </div>

        <div className="flex items-center w-full h-auto mb-5 gap-7">
          {/* Google OAuth Button */}
          <div className="w-1/2 h-auto">
            <button
              onClick={handleGoogleLogin} // Google OAuth login handler
              className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md flex items-center gap-x-2 hover:bg-gray-100/40 ease-out duration-700"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                className="bi bi-google"
                viewBox="0 0 16 16"
              >
                <path d="M15.545 6.558a9.4 9.4 0 0 1 .139 1.626c0 2.434-.87 4.492-2.384 5.885h.002C11.978 15.292 10.158 16 8 16A8 8 0 1 1 8 0a7.7 7.7 0 0 1 5.352 2.082l-2.284 2.284A4.35 4.35 0 0 0 8 3.166c-2.087 0-3.86 1.408-4.492 3.304a4.8 4.8 0 0 0 0 3.063h.003c.635 1.893 2.405 3.301 4.492 3.301 1.078 0 2.004-.276 2.722-.764h-.003a3.7 3.7 0 0 0 1.599-2.431H8v-3.08z" />
              </svg>
              <i className="bi bi-google">Google</i>
            </button>
          </div>

          {/* Facebook Button (Placeholder for future implementation) */}
          <div className="w-1/2 h-auto">
            <button className="w-full h-12 p-4 outline-none bg-transparent border-[2px] border-gray-200/40 text-white rounded-md flex items-center gap-x-2 hover:bg-gray-100/40 ease-out duration-700">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="currentColor"
                className="bi bi-facebook"
                viewBox="0 0 16 16"
              >
                <path d="M16 8.049c0-4.446-3.582-8.05-8-8.05C3.58 0-.002 3.603-.002 8.05c0 4.017 2.926 7.347 6.75 7.951v-5.625h-2.03V8.05H6.75V6.275c0-2.017 1.195-3.131 3.022-3.131.876 0 1.791.157 1.791.157v1.98h-1.009c-.993 0-1.303.621-1.303 1.258v1.51h2.218l-.354 2.326H9.25V16c3.824-.604 6.75-3.934 6.75-7.951" />
              </svg>
              <i className="bi bi-facebook">Facebook</i>
            </button>
          </div>
        </div>

        <div className="flex items-center justify-center w-full h-auto gap-x-1">
          <p className="text-base font-medium text-white">
            Dont have an account?
          </p>
          <Link
            to="/register"
            className="text-base font-medium text-white duration-500 ease-out hover:underline"
          >
            Create New Account
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Login;
