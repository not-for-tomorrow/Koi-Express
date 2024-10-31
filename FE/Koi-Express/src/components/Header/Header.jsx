import React, { useEffect, useState } from "react";
import OptionMenu from "../OptionMenu/OptionMenu";
import { Link } from "react-router-dom";

// Define individual options arrays for each OptionMenu component
const serviceOptions = [
  { label: "Express Delivery", path: "/services/express" },
  { label: "Standard Delivery", path: "/services/standard" },
  { label: "International Shipping", path: "/services/international" },
];
const customerOptions = [
  { label: "Profile", path: "/customers/profile" },
  { label: "Order History", path: "/customers/orders" },
  { label: "Support", path: "/customers/support" },
];
const driverOptions = [
  { label: "Driver Dashboard", path: "/drivers/dashboard" },
  { label: "Earnings", path: "/drivers/earnings" },
  { label: "Support", path: "/drivers/support" },
];
const recruitmentOptions = [
  { label: "Job Openings", path: "/careers/jobs" },
  { label: "Apply Now", path: "/careers/apply" },
  { label: "Internships", path: "/careers/internships" },
];
const newsOptions = [
  { label: "Tin tức", path: "/blog" },
];

export default function Header() {
  const [opacity, setOpacity] = useState(1);

  useEffect(() => {
    const handleScroll = () => {
      const scrollPosition = window.scrollY;
      const newOpacity = Math.max(0.8, 1 - scrollPosition / 100);
      setOpacity(newOpacity);
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, []);

  return (
      <div
          className="sticky top-0 z-[9999] w-full bg-white"
          style={{ opacity: opacity }}
      >
        <div className="flex justify-between items-center max-w-[1300px] mx-auto px-6 py-4">
          {/* Logo Section */}
          <div className="flex items-center">
            <Link to="/" className="text-2xl font-extrabold tracking-tight flex space-x-1">
              {/* Dark Blue for "KOI" */}
              <span style={{ color: "#0F2C57" }}>K</span>
              <span style={{ color: "#0F2C57" }}>O</span>
              <span style={{ color: "#0F2C57" }}>I</span>
              {/* Light Blue for "EXPRESS" */}
              <span style={{ color: "#3A84DF" }}>E</span>
              <span style={{ color: "#3A84DF" }}>X</span>
              <span style={{ color: "#3A84DF" }}>P</span>
              <span style={{ color: "#3A84DF" }}>R</span>
              <span style={{ color: "#3A84DF" }}>E</span>
              <span style={{ color: "#3A84DF" }}>S</span>
              <span style={{ color: "#3A84DF" }}>S</span>
            </Link>
          </div>

          {/* Options Section */}
          <div className="flex items-center space-x-8">
            <OptionMenu name="Dịch vụ" options={serviceOptions} />
            <OptionMenu name="Khách hàng" options={customerOptions} />
            <OptionMenu name="Tài xế" options={driverOptions} />
            <OptionMenu name="Tuyển dụng" options={recruitmentOptions} />
            <OptionMenu name="Tin tức" options={newsOptions} />
          </div>

          {/* Button Section */}
          <div className="flex items-center">
            <Link to="/login">
              <button className="px-5 py-3 font-bold text-white transition-colors duration-300 bg-blue-500 rounded-full hover:bg-blue-600">
                Giao hàng ngay
              </button>
            </Link>
          </div>
        </div>
      </div>
  );
}
