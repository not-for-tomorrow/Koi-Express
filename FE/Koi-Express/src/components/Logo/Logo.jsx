import React from "react";

export default function Logo() {
  return (
    <div className="flex justify-between items-center w-full px-8 gap-x-4">
      {/* Logo 1 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="https://askoi.vn/wp-content/uploads/2019/08/logo-askoi-2019.png"
          alt="Logo 1"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="https://askoi.vn/wp-content/uploads/2019/08/logo-askoi-2019.png"
          alt="Logo 1 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 2 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="https://cacanhthaihoa.com/wp-content/uploads/2020/07/logo-ca-canh-thai-hoa.jpg"
          alt="Logo 2"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="https://cacanhthaihoa.com/wp-content/uploads/2020/07/logo-ca-canh-thai-hoa.jpg"
          alt="Logo 2 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 3 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="https://www.cakoi.vn/wp-content/uploads/2018/01/logo.png"
          alt="Logo 3"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="https://www.cakoi.vn/wp-content/uploads/2018/01/logo.png"
          alt="Logo 3 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 4 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="https://koilover.vn//uploads/home/logo.png"
          alt="Logo 4"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="https://koilover.vn//uploads/home/logo.png"
          alt="Logo 4 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      {/* Logo 5 */}
      <div className="flex justify-center items-center relative w-28 h-28">
        <img
          src="https://static.wixstatic.com/media/24f4dd_f9c576388dc24dc397e7561de265d3fd~mv2.png/v1/fill/w_76,h_81,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/LOGO.png"
          alt="Logo 5"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0 filter grayscale"
        />
        <img
          src="https://static.wixstatic.com/media/24f4dd_f9c576388dc24dc397e7561de265d3fd~mv2.png/v1/fill/w_76,h_81,al_c,q_85,usm_0.66_1.00_0.01,enc_auto/LOGO.png"
          alt="Logo 5 Alt"
          className="absolute w-28 h-28 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>
    </div>
  );
}
