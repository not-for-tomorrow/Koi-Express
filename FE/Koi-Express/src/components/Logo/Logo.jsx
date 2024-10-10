import React from "react";

export default function Logo() {
  return (
    <div className="flex justify-between items-center w-full px-8 gap-x-4">
      {/* Logo 1 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="/src/assets/images/LogoBrands/logo-askoi-2019.png"
          alt="Logo 1"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="/src/assets/images/LogoBrands/logo-askoi-2019.png"
          alt="Logo 1 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 2 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="/src/assets/images/LogoBrands/logo-ca-canh-thai-hoa.jpg"
          alt="Logo 2"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="/src/assets/images/LogoBrands/logo-ca-canh-thai-hoa.jpg"
          alt="Logo 2 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 3 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="/src/assets/images/LogoBrands/CakoiVN.png"
          alt="Logo 3"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="/src/assets/images/LogoBrands/CakoiVN.png"
          alt="Logo 3 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 4 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="/src/assets/images/LogoBrands/KoiloverVN.png"
          alt="Logo 4"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="/src/assets/images/LogoBrands/KoiloverVN.png"
          alt="Logo 4 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 5 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="/src/assets/images/LogoBrands/AAkoifarm.webp"
          alt="Logo 5"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="/src/assets/images/LogoBrands/AAkoifarm.webp"
          alt="Logo 5 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>
    </div>
  );
}
