import React, { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import jwt_decode from "jwt-decode";
import "./Register.css";
import logresKoiPic from "../../assets/images/banner/LogResKoiPic.webp";
import { FaArrowLeft } from "react-icons/fa";

const Register = () => {
  const [fullName, setFullName] = useState("");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [password, setPassword] = useState("");
  const [otp, setOtp] = useState("");
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [loading, setLoading] = useState(false);
  const [isOtpStage, setIsOtpStage] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    if (isOtpStage) {
      document.getElementById("otpInput").focus();
    }
  }, [isOtpStage]);

  const handlePhoneNumberChange = (e) => {
    const value = e.target.value;
    if (/^\d*$/.test(value)) {
      setPhoneNumber(value);
    }
  };

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
  };

  const handleRegisterSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    if (!fullName || !email || !phoneNumber || !password) {
      setError("Không được để trống thông tin");
      setLoading(false);
      return;
    }

    if (phoneNumber.length !== 10) {
      setError("Số điện thoại bắt buộc phải có 10 số");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post(
          "http://localhost:8080/api/auth/register",
          { fullName, email, phoneNumber, password },
          { headers: { "Content-Type": "application/json" } }
      );

      if (response.status === 200) {
        setIsOtpStage(true);
        setSuccess("Đăng ký thành công");
        setSuccess("OTP đã được gửi tới điện thoại của bạn");
      } else {
        setError("Đăng ký thất bại, vui lòng kiểm tra lại thông tin");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");
    setSuccess("");

    if (otp.length !== 4) {
      setError("Mã OTP có 4 số");
      setLoading(false);
      return;
    }

    try {
      const response = await axios.post(
          `http://localhost:8080/api/auth/verify-otp?phoneNumber=${phoneNumber}&otp=${otp}`,
          {},
          { headers: { "Content-Type": "application/json" } }
      );

      if (response.status === 200) {
        setSuccess("OTP xác thực thành côngĂ");
        handleOtpSuccess();
      } else {
        setError("OTP xác thực thất bại, vui lonòng thử lạiĂ");
      }
    } catch (err) {
      setError("An error occurred. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  const handleOtpSuccess = async () => {
    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", {
        phoneNumber,
        password,
      });

      if (response.status === 200) {
        const token = response.data.result;
        if (token) {
          localStorage.setItem("token", token);

          const decodedToken = jwt_decode(token);
          const role = decodedToken.role;

          if (role === "CUSTOMER") {
            const userInfoResponse = await axios.get("http://localhost:8080/api/customers/basic-info", {
              headers: { Authorization: `Bearer ${token}` },
            });

            if (userInfoResponse.status === 200) {
              localStorage.setItem("userInfo", JSON.stringify(userInfoResponse.data.result));
              navigate("/appkoiexpress");
            } else {
              setError("Failed to retrieve user information.");
            }
          } else {
            setError("Unexpected role detected.");
          }
        } else {
          setError("No token provided in the response.");
        }
      }
    } catch (err) {
      setError("Login error. Please check your details and try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
      <section className="flex items-center justify-center min-h-screen registerpage bg-gray-50">
        <div className="back-to-home" onClick={() => navigate("/")}>
          <FaArrowLeft size={16} />
          <span>Quay lại trang chính</span>
        </div>
        <div className="flex items-center justify-start max-w-3xl p-5 bg-gray-100 shadow-lg registercard rounded-2xl">
          <div className="hidden w-1/2 mr-auto md:block">
            <img className="rounded-2xl" src={logresKoiPic} alt="Register Illustration" />
          </div>
          <div className="px-8 md:w-1/2 md:px-16">
            <h2 className="font-bold text-2xl text-[#002D74]">{isOtpStage ? "Xác nhận OTP" : "Đăng ký"}</h2>

            {!isOtpStage ? (
                <form onSubmit={handleRegisterSubmit} className="flex flex-col gap-4" autoComplete="off">
                  <input className="p-2 border rounded-xl" type="text" placeholder="Họ tên" value={fullName} onChange={(e) => setFullName(e.target.value)} required />
                  <input className="p-2 border rounded-xl" type="email" placeholder="Địa chỉ Email" value={email} onChange={handleEmailChange} required />
                  <input className="p-2 border rounded-xl" type="text" placeholder="Số điện thoại" value={phoneNumber} onChange={handlePhoneNumberChange} required />
                  <input className="w-full p-2 border rounded-xl" type="password" placeholder="Mật khẩu" value={password} onChange={(e) => setPassword(e.target.value)} required />

                  <button type="submit" className="bg-[#002D74] rounded-xl text-white py-2 hover:scale-105 duration-300">
                    {loading ? "Đang đăng ký..." : "Đăng ký"}
                  </button>
                </form>
            ) : (
                <form onSubmit={handleOtpSubmit} className="flex flex-col gap-4 mt-4">
                  <p className="mt-2 text-sm text-gray-600">Please enter the 4-digit OTP sent to {phoneNumber}.</p>
                  <input id="otpInput" className="p-2 text-center border rounded-lg" type="text" maxLength="4" placeholder="Enter OTP" value={otp} onChange={(e) => setOtp(e.target.value)} required />
                  <button type="submit" className="bg-[#002D74] rounded-xl text-white py-2 hover:scale-105 duration-300" disabled={loading}>
                    {loading ? "Đang xác nhận..." : "Xác nhận OTP"}
                  </button>
                </form>
            )}

            {error && <p className="mt-2 text-sm text-red-500">{error}</p>}
            {success && <p className="mt-2 text-sm text-green-500">{success}</p>}

            {!isOtpStage && (
                <div className="mt-5 text-xs flex justify-between items-center text-[#002D74]">
                  <p>Bạn đã có tài khoản?</p>
                  <Link to="/login">
                    <button className="px-5 py-2 duration-300 bg-white border rounded-xl hover:scale-110">Đăng nhập</button>
                  </Link>
                </div>
            )}
          </div>
        </div>
      </section>
  );
};

export default Register;
