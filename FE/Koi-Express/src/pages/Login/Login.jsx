import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { FcGoogle } from "react-icons/fc";
import { FaFacebook } from "react-icons/fa";
import jwt_decode from "jwt-decode";
import "./Login.css";

const Login = () => {
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
    // Check if token and userInfo exist, and navigate to /appkoiexpress if they do
    const token = localStorage.getItem("token");
    const userInfo = localStorage.getItem("userInfo");
    if (token && userInfo) {
      navigate("/appkoiexpress");
    }
  }, [navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        phoneNumber,
        password,
      });

      if (response.status === 200) {
        const token = response.data.result;

        if (token) {
          localStorage.setItem("token", token);
          setSuccess("Login successful");

          // Decode the token to get the role
          const decodedToken = jwt_decode(token);
          const role = decodedToken.role;

          // Store userInfo only if the role is CUSTOMER
          if (role === "CUSTOMER") {
            const userInfoResponse = await axios.get("http://localhost:8080/api/customers/basic-info", {
              headers: { Authorization: `Bearer ${token}` },
            });

            if (userInfoResponse.status === 200) {
              const userInfo = userInfoResponse.data.result;
              localStorage.setItem("userInfo", JSON.stringify(userInfo));
            } else {
              setError("Failed to retrieve user information.");
              return;
            }
          }

          // Redirect based on the user's role
          switch (role) {
            case "CUSTOMER":
              navigate("/appkoiexpress");
              break;
            case "SALES_STAFF":
              navigate("/salepage");
              break;
            case "DELIVERING_STAFF":
              navigate("/deliveringstaffpage");
              break;
            case "MANAGER":
              navigate("/managerpage");
              break;
            default:
              setError("Role not recognized.");
          }
        } else {
          setError("No token provided in the response.");
        }
      }
    } catch (err) {
      if (err.response) {
        switch (err.response.status) {
          case 401:
            setError("Invalid credentials. Please check your phone number and password.");
            break;
          case 500:
            setError("Server error. Please try again later.");
            break;
          default:
            setError("Login failed: " + (err.response.data.message || "Unknown error"));
        }
      } else {
        setError("An unexpected error occurred. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const handleFacebookLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/facebook";
  };

  const handleRegister = () => {
    navigate("/register");
  };

  return (
    <section className="flex items-center justify-center min-h-screen loginpage bg-gray-50 ">
      {/* login container */}
      <div className="flex items-center max-w-3xl p-5 bg-gray-100 shadow-lg logincard rounded-2xl">
        {/* form */}
        <div className="px-8 md:w-1/2 md:px-16">
          <h2 className="font-bold text-2xl text-[#002D74]">Login</h2>

          <form onSubmit={handleSubmit} className="flex flex-col gap-4">
            <input
              className="p-2 mt-8 border rounded-xl"
              type="text"
              placeholder="Phone Number"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
              autoComplete="new-phone"
            />
            <div className="relative">
              <input
                className="w-full p-2 border rounded-xl"
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="new password"
              />
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="gray"
                className="absolute -translate-y-1/2 bi bi-eye top-1/2 right-3"
                viewBox="0 0 16 16"
              >
                <path d="M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM1.173 8a13.133 13.133 0 0 1 1.66-2.043C4.12 4.668 5.88 3.5 8 3.5c2.12 0 3.879 1.168 5.168 2.457A13.133 13.133 0 0 1 14.828 8c-.058.087-.122.183-.195.288-.335.48-.83 1.12-1.465 1.755C11.879 11.332 10.119 12.5 8 12.5c-2.12 0-3.879-1.168-5.168-2.457A13.134 13.134 0 0 1 1.172 8z" />
                <path d="M8 5.5a2.5 2.5 0 1 0 0 5 2.5 2.5 0 0 0 0-5zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z" />
              </svg>
            </div>
            <div className="flex items-center">
              <input
                type="checkbox"
                checked={rememberMe}
                onChange={() => setRememberMe(!rememberMe)}
                id="rememberMe"
              />
              <label htmlFor="rememberMe" className="ml-2 text-sm text-[#002D74]">
                Remember Me
              </label>
            </div>
            <button type="submit" className="bg-[#002D74] rounded-xl text-white py-2 hover:scale-105 duration-300">
              {loading ? "Loading..." : "Login"}
            </button>
          </form>

          {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
          {success && <p className="mt-2 text-sm text-green-500">{success}</p>}

          <div className="grid items-center grid-cols-3 mt-6 text-gray-400">
            <hr className="border-gray-400" />
            <p className="text-sm text-center">OR</p>
            <hr className="border-gray-400" />
          </div>

          <div className="flex flex-col gap-4 mt-5">
            <button
              type="button"
              onClick={handleGoogleLogin}
              className="bg-white border py-2 w-full rounded-xl flex items-center justify-center text-sm hover:scale-105 duration-300 text-[#002D74]"
            >
              <FcGoogle size={24} className="mr-3" />
              <span className="mr-4">Login with Google</span>
            </button>

            <button
              type="button"
              onClick={handleFacebookLogin}
              className="bg-white border py-2 w-full rounded-xl flex items-center justify-center text-sm hover:scale-105 duration-300 text-[#002D74]"
            >
              <FaFacebook size={24} className="mr-3 text-blue-600" />
              <span>Login with Facebook</span>
            </button>
          </div>

          <div className="mt-5 text-xs border-b border-[#002D74] py-4 text-[#002D74]">
            <a href="#">Forgot your password?</a>
          </div>

          <div className="mt-3 text-xs flex justify-between items-center text-[#002D74]">
            <p>Don't have an account?</p>
            <button
              onClick={handleRegister}
              className="px-5 py-2 duration-300 bg-white border rounded-xl hover:scale-110"
            >
              Register
            </button>
          </div>
        </div>

        {/* image */}
        <div className="hidden w-1/2 md:block">
          <img
            className="rounded-2xl"
            src="https://images.unsplash.com/photo-1616606103915-dea7be788566?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80"
            alt="Login Illustration"
          />
        </div>
      </div>
    </section>
  );
};

export default Login;
