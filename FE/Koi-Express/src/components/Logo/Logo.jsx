import React from "react";

export default function Logo() {
  return (
    <div className="flex justify-between">
      <div className="flex justify-center items-center relative  w-48 h-48">
        <img
          src="https://www.ahamove.com/static/images/home/partner-pharmacitylogo.webp"
          alt="Image 1"
          className="absolute  w-48 h-48 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0"
        />
        <img
          src="https://www.ahamove.com/static/images/home/partner-pharmacitylogo-hover.webp"
          alt="Image 2"
          className="absolute w-48 h-48 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>
      {/*  */}
      <div className="flex justify-center items-center relative w-48 h-48">
        <img
          src="https://www.ahamove.com/static/images/home/partner-concunglogo.webp"
          alt="Image 1"
          className="absolute  w-48 h-48 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0"
        />
        <img
          src="https://www.ahamove.com/static/images/home/partner-concunglogo-hover.webp"
          alt="Image 2"
          className="absolute w-48 h-48 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>
      <div className="flex justify-center items-center relative w-48 h-48">
        <img
          src="https://www.ahamove.com/static/images/home/partner-lazadalogo.webp"
          alt="Image 1"
          className="absolute  w-48 h-48 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0"
        />
        <img
          src="https://www.ahamove.com/static/images/home/partner-lazadalogo-hover.webp"
          alt="Image 2"
          className="absolute w-48 h-48 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      <div className="flex justify-center items-center relative w-48 h-48">
        <img
          src="https://www.ahamove.com/static/images/home/partner-thecoffeehouselogo.webp"
          alt="Image 1"
          className="absolute  w-48 h-48 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0"
        />
        <img
          src="https://www.ahamove.com/static/images/home/partner-thecoffeehouselogo-hover.webp"
          alt="Image 2"
          className="absolute w-72 h-48 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>
      <div className="flex justify-center items-center relative w-48 h-48">
        <img
          src="https://www.ahamove.com/static/images/home/partner-fahasalogo.webp"
          alt="Image 1"
          className="absolute  w-48 h-48 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0"
        />
        <img
          src="https://www.ahamove.com/static/images/home/partner-fahasalogo-hover.webp"
          alt="Image 2"
          className="absolute w-72 h-48 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>

      <div className="flex justify-center items-center relative  w-48 h-48">
        <img
          src="https://www.ahamove.com/static/images/home/partner-junologo.webp"
          alt="Image 1"
          className="absolute   w-48 h-48 object-contain transition-opacity duration-300 opacity-100 hover:opacity-0"
        />
        <img
          src="https://www.ahamove.com/static/images/home/partner-junologo-hover.webp"
          alt="Image 2"
          className="absolute  w-44 h-48 object-contain transition-opacity duration-300 opacity-0 hover:opacity-100"
        />
      </div>
    </div>
  );
}
