import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFacebook, faTwitter, faInstagram, faLinkedin } from "@fortawesome/free-brands-svg-icons";

export default function Footer() {
  return (
      <div className="bg-white text-gray-800 px-10 py-12">
        <div className="max-w-7xl mx-auto flex justify-between items-center space-x-6">

          {/* Left section with Koi Express title and all lists in a horizontal layout */}
          <div className="flex-1">
            <h2 className="text-blue-500 font-extrabold text-2xl mb-4">Koi Express</h2>
            <div className="flex space-x-14">
              <ul>
                <li className="text-lg font-semibold mb-2">Dịch vụ</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Dịch vụ giao cá koi</li>
              </ul>

              <ul>
                <li className="text-lg font-semibold mb-2">Công ty</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Về chúng tôi</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Tin tức</li>
                <li className="hover:text-amber-500 cursor-pointer">Tuyển dụng</li>
              </ul>

              <ul>
                <li className="text-lg font-semibold mb-2">Hỗ trợ</li>
                <li className="hover:text-amber-500 cursor-pointer">Chính sách và điều khoản</li>
              </ul>

              <ul>
                <li className="text-lg font-semibold mb-2">Khách hàng</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Khách hàng cá nhân</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Khách hàng doanh nghiệp</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Cộng đồng khách hàng</li>
                <li className="hover:text-amber-500 cursor-pointer mb-2">Trung tâm hỗ trợ</li>
              </ul>
            </div>
          </div>

          {/* Right section with email subscription form and icons */}
          <div className="w-full md:w-1/4">
            <h2 className="text-lg font-semibold mb-2">Đăng ký nhận tin</h2>
            <form className="flex mb-4">
              <input
                  type="email"
                  placeholder="Nhập email của bạn"
                  className="p-2 border border-gray-300 rounded-l w-2/3 focus:outline-none"
              />
              <button className="p-2 bg-blue-500 text-white rounded-r w-1/3">
                Đăng ký
              </button>
            </form>

            {/* Icons section */}
            <div className="flex space-x-4 mt-4">
              <a href="#" className="text-gray-500 hover:text-blue-500">
                <FontAwesomeIcon icon={faFacebook} className="text-blue-600" size="lg"/>
              </a>
              <a href="#" className="text-gray-500 hover:text-blue-500">
                <FontAwesomeIcon icon={faTwitter} className="text-blue-400" size="lg"/>
              </a>
              <a href="#" className="text-gray-500 hover:text-blue-500">
                <FontAwesomeIcon icon={faInstagram} className="text-pink-500" size="lg"/>
              </a>
              <a href="#" className="text-gray-500 hover:text-blue-500">
                <FontAwesomeIcon icon={faLinkedin} className="text-blue-800" size="lg"/>
              </a>
            </div>
          </div>
        </div>
      </div>
  );
}
