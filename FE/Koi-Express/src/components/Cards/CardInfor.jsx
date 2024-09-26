import React from "react";

export default function CardInfor({ src, alt, name, desc }) {
  return (
    <>
      {src.map((src, index) => (
        <div key={index} className="w-64 flex flex-col items-center">
          <img className="h-[88px] w-[88px] " src={src} alt={alt[index]} />
          <div className="mt-5 text-xl font-semibold ">{name[index]}</div>
          <div className="mt-2 text-center">{desc[index]}</div>
        </div>
      ))}
    </>
  );
}
