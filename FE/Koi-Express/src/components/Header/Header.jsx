import React, { useEffect, useState } from "react";
import OptionMenu from "../OptionMenu/OptionMenu";
import { Link } from "react-router-dom";

const optionsArray = ["Option 1", "Option 2", "Option 3"];

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
          <span className="text-2xl font-extrabold tracking-tight">
            Koi Express
          </span>
        </div>

        {/* Options Section */}
        <div className="flex space-x-6">
          <OptionMenu name="Dịch vụ" options={optionsArray} />
          <OptionMenu name="Khách hàng" options={optionsArray} />
          <OptionMenu name="Tài xế" options={optionsArray} />
          <OptionMenu name="Tuyển dụng" options={optionsArray} />
          <OptionMenu name="Tin tức" options={optionsArray} />
        </div>

        {/* Button Section */}
        <div>
          <button className="px-5 py-3 font-bold text-white transition-colors duration-300 bg-blue-500 rounded-full hover:bg-blue-600">
            <Link to="/appkoiexpress">Giao hàng ngay</Link>
          </button>
        </div>
      </div>
    </div>
  );
}
