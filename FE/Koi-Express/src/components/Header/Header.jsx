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
      className="sticky top-0 z-[9999] w-full mb-5 bg-white "
      style={{ opacity: opacity }}
    >
      <div className="flex justify-between items-center max-w-[1300px] mx-auto px-4">
        {/* Logo Section */}
        <div className="flex items-center flex-shrink-0 text-blue-500">
          <Link to="/">
            <span className="text-2xl font-extrabold tracking-tight">
              Koi Express
            </span>
          </Link>
        </div>

        {/* Options Section */}
        <div className="flex space-x-6">
          <OptionMenu name="Dịch vụ" options={serviceOptions} />
          <OptionMenu name="Khách hàng" options={customerOptions} />
          <OptionMenu name="Tài xế" options={driverOptions} />
          <OptionMenu name="Tuyển dụng" options={recruitmentOptions} />
          <OptionMenu name="Tin tức" options={newsOptions} />
        </div>

        {/* Button Section */}
        <div>
          <button className="px-5 py-3 font-bold text-white transition-colors duration-300 bg-blue-500 rounded-full hover:bg-blue-600">
            <Link to="/login">Giao hàng ngay</Link>
          </button>
        </div>
      </div>
    </div>
  );
}
