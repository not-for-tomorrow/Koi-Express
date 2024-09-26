import React, { useEffect, useState } from "react";
import OptionMenu from "../OptionMenu/OptionMenu";
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
    <>
      <div
        class=" sticky top-0 w-full flex items-center bg-white mx-[132px] z-20  "
        style={{ opacity: opacity }}
      >
        <div class="flex items-center flex-shrink-0 text-blue-500 mr-10">
          <span class="font-extrabold  text-2xl tracking-tight ">
            Koi Express
          </span>
        </div>
        <OptionMenu name="Dịch vụ" options={optionsArray} />
        <OptionMenu name="Khách hàng" options={optionsArray} />
        <OptionMenu name="Tài xế" options={optionsArray} />
        <OptionMenu name="Tuyển dụng" options={optionsArray} />
        <OptionMenu name="Tin tức" options={optionsArray} />
        <div>
          <button className="bg-blue-500 text-white font-bold py-3 px-5 rounded-full hover:bg-blue-600 transition-colors duration-300">
            Giao hàng ngay
          </button>
        </div>
      </div>
    </>
  );
}
