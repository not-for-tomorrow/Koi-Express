import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import qs from "qs";
import "./Register.css";
import logresKoiPic from "../../assets/images/banner/LogResKoiPic.webp";

const Register = () => {
  // State quản lý dữ liệu đầu vào của người dùng
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [otp, setOtp] = useState("");

  // State quản lý trạng thái hiển thị và thông báo lỗi/thành công
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [otpLoading, setOtpLoading] = useState(false);
  const [showOtpInput, setShowOtpInput] = useState(false);

  // Khai báo navigate để điều hướng trang
  const navigate = useNavigate();
  const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || "http://localhost:8080";

  // Hàm định dạng số điện thoại, tự động chuyển đầu số 0 thành +84
  const formatPhoneNumber = (number) => {
    return number.startsWith("0") ? `+84${number.slice(1)}` : number;
  };

  // Xử lý thay đổi số điện thoại, chỉ cho phép nhập số
  const handlePhoneNumberChange = (e) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) setPhoneNumber(value);
  };

  // Xử lý thay đổi OTP, chỉ cho phép nhập số
  const handleOtpChange = (e) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) setOtp(value);
  };

  // Hàm xử lý khi nhấn nút "Đăng ký"
  const handleSubmit = async (e) => {
    e.preventDefault(); // Ngăn chặn hành động mặc định của form
    setLoading(true); // Bắt đầu quá trình đăng ký
    setError(""); // Reset lỗi trước đó nếu có

    // Kiểm tra độ dài số điện thoại có đủ 10 chữ số không
    if (phoneNumber.length !== 10) {
      setError("Please enter exactly 10 digits for the phone number.");
      setLoading(false);
      return;
    }

    // Định dạng số điện thoại
    const formattedPhone = formatPhoneNumber(phoneNumber);
    const requestData = {
      fullName,
      email,
      phoneNumber: formattedPhone,
      password,
    };

    // Gửi yêu cầu đăng ký đến server
    try {
      const response = await axios.post(`${API_BASE_URL}/api/auth/register`, requestData, {
        headers: { "Content-Type": "application/json" },
      });

      // Nếu thành công, hiển thị phần nhập OTP
      if (response.status === 200) {
        setShowOtpInput(true);
      } else {
        setError("Registration failed. Please try again.");
      }
    } catch (err) {
      setError(err.response?.data?.message || "An error occurred. Please try again.");
    } finally {
      setLoading(false); // Kết thúc quá trình đăng ký
    }
  };

  // Hàm xác thực OTP
  const handleOtpVerify = async () => {
    setOtpLoading(true);
    setError("");
    setSuccessMessage("");

    try {
      const response = await axios.post(
          `${API_BASE_URL}/api/auth/verify-otp`,
          qs.stringify({ phoneNumber: formatPhoneNumber(phoneNumber), otp }),
          {
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
          }
      );

      if (response.status === 200) {
        setSuccessMessage("OTP verified! Registration successful.");
        setTimeout(() => {
          navigate("/login");
        }, 2000); // Điều hướng đến trang đăng nhập sau 2 giây
      } else {
        setError("Invalid OTP. Please try again.");
      }
    } catch (err) {
      setError("An error occurred during OTP verification. Please try again.");
    } finally {
      setOtpLoading(false); // Kết thúc quá trình xác thực OTP
    }
  };

  return (
      <section className="flex items-center justify-center min-h-screen registerpage bg-gray-100">
        <div className="flex items-center max-w-3xl p-5 bg-white shadow-lg registercard rounded-2xl">
          <div className="hidden w-1/2 mr-auto md:block">
            <img className="rounded-2xl" src={logresKoiPic} alt="Register Illustration" />
          </div>

          <div className="px-8 md:w-1/2 md:px-16">
            <h2 className="font-bold text-3xl text-[#002D74] mb-4">Create an Account</h2>

            {!showOtpInput ? (
                <form onSubmit={handleSubmit} className="flex flex-col gap-4" autoComplete="off">
                  <input
                      className="p-3 border border-gray-300 rounded-lg focus:border-[#002D74] focus:outline-none transition duration-300"
                      type="text"
                      placeholder="Full Name"
                      value={fullName}
                      onChange={(e) => setFullName(e.target.value)}
                      required
                  />
                  <input
                      className="p-3 border border-gray-300 rounded-lg focus:border-[#002D74] focus:outline-none transition duration-300"
                      type="email"
                      placeholder="Email Address"
                      value={email}
                      onChange={(e) => setEmail(e.target.value)}
                      required
                  />
                  <input
                      className="p-3 border border-gray-300 rounded-lg focus:border-[#002D74] focus:outline-none transition duration-300"
                      type="text"
                      placeholder="Phone Number"
                      value={phoneNumber}
                      onChange={handlePhoneNumberChange}
                      required
                  />
                  <input
                      className="p-3 border border-gray-300 rounded-lg focus:border-[#002D74] focus:outline-none transition duration-300"
                      type="password"
                      placeholder="Password"
                      value={password}
                      onChange={(e) => setPassword(e.target.value)}
                      required
                  />
                  <button
                      type="submit"
                      className="bg-[#002D74] text-white py-3 rounded-lg hover:bg-[#014a8d] transition duration-300"
                      disabled={loading}
                  >
                    {loading ? "Registering..." : "Register"}
                  </button>
                </form>
            ) : (
                <div className="flex flex-col gap-4 mt-4">
                  <p className="text-gray-600">Please enter the OTP sent to {formatPhoneNumber(phoneNumber)}.</p>
                  <input
                      className="p-3 border border-gray-300 rounded-lg focus:border-[#002D74] focus:outline-none transition duration-300"
                      type="text"
                      placeholder="Enter OTP"
                      value={otp}
                      onChange={handleOtpChange}
                      required
                  />
                  <button
                      onClick={handleOtpVerify}
                      className="bg-[#002D74] text-white py-3 rounded-lg hover:bg-[#014a8d] transition duration-300"
                      disabled={otpLoading}
                  >
                    {otpLoading ? "Verifying..." : "Verify OTP"}
                  </button>
                </div>
            )}

            {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
            {successMessage && <p className="mt-2 text-sm text-green-500">{successMessage}</p>}

            <div className="mt-6 text-xs flex justify-between items-center text-[#002D74]">
              <p>Already have an account?</p>
              <Link to="/login">
                <button className="px-6 py-2 bg-white border rounded-lg hover:scale-105 transition duration-300">
                  Login
                </button>
              </Link>
            </div>
          </div>
        </div>
      </section>
  );
};

export default Register;
