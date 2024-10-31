import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { FcGoogle } from "react-icons/fc";
import { FaArrowLeft, FaFacebook } from "react-icons/fa";
import jwt_decode from "jwt-decode";
import "./Login.css";
import logresKoiPic from "../../assets/images/banner/LogResKoiPic.webp";

const Login = () => {
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);

  const navigate = useNavigate();

  useEffect(() => {
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

          const decodedToken = jwt_decode(token);
          const role = decodedToken.role;

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
    <section className="flex items-center justify-center min-h-screen loginpage bg-gray-50">
       <div className="back-to-home" onClick={() => navigate("/")}>
        <FaArrowLeft size={16} />
        <span>Back to Home</span>
      </div>
      <div className="flex items-center max-w-3xl p-5 bg-gray-100 shadow-lg logincard rounded-2xl">
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
                type={showPassword ? "text" : "password"}
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                autoComplete="new-password"
              />
              <svg
                onClick={() => setShowPassword(!showPassword)}
                xmlns="http://www.w3.org/2000/svg"
                width="16"
                height="16"
                fill="gray"
                className="absolute -translate-y-1/2 cursor-pointer bi bi-eye top-1/2 right-3"
                viewBox="0 0 16 16"
              >
                <path
                  d={showPassword
                    ? "M13.359 5.66a5.726 5.726 0 0 0-1.362-.86.552.552 0 0 1-.231-.856.558.558 0 0 1 .856-.231c1.284.646 2.393 1.652 3.246 2.94C14.555 8.67 11.68 11.5 8 11.5s-6.555-2.83-7.874-4.487c.807-1.198 1.83-2.228 2.992-3.021A.558.558 0 0 1 3.666 4.5a.556.556 0 0 1 .859.231 5.764 5.764 0 0 0-.879.853C2.297 7.47 4.654 9.5 8 9.5s5.703-2.03 6.34-2.768z"
                    : "M16 8s-3-5.5-8-5.5S0 8 0 8s3 5.5 8 5.5S16 8 16 8zM4.5 8a3.5 3.5 0 1 1 7 0 3.5 3.5 0 0 1-7 0z"
                  }
                />
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
            src={logresKoiPic}
            alt="Login Illustration"
          />
        </div>
      </div>
    </section>
  );
};

export default Login;
