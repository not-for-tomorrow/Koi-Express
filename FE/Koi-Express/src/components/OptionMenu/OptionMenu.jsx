import React from "react";

export default function OptionMenu({ name, options }) {
  return (
    <div>
      <div className="relative group">
        <div className="flex px-10 py-8 cursor-pointer hover:text-[#fe5f00]">
          <span className="font-semibold mr-1">{name}</span>
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth="1.5"
            stroke="currentColor"
            className="size-4 mt-[2px]"
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="m19.5 8.25-7.5 7.5-7.5-7.5"
            />
          </svg>
        </div>
        <div className="absolute hidden left-10 group-hover:block bg-white z-[1]">
          <ul className=" w-48 bg-gray-100">
            {options.map((option, index) => (
              <li
                key={index}
                className="px-4 py-4 hover:bg-gray-200 hover:text-[#fe5f00] cursor-pointer"
              >
                <div className="flex justify-between">
                  {option}
                  <svg
                    xmlns="http://www.w3.org/2000/svg"
                    fill="none"
                    viewBox="0 0 24 24"
                    strokeWidth={1.5}
                    stroke="currentColor"
                    className="size-4 mt-[2px]"
                  >
                    <path
                      strokeLinecap="round"
                      strokeLinejoin="round"
                      d="M13.5 4.5 21 12m0 0-7.5 7.5M21 12H3"
                    />
                  </svg>
                </div>
              </li>
            ))}
          </ul>
        </div>
      </div>
    </div>
  );
}
