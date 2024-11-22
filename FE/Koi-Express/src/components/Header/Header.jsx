import React from "react";
import {Link} from "react-router-dom";

export default function Header() {
    return (
        <div className="sticky top-0 z-[9999] w-full bg-white">
            <div className="flex justify-between items-center max-w-[1300px] mx-auto px-6 py-4">
                {/* Logo Section */}
                <Link
                    to="/"
                    className="flex items-center space-x-1 text-2xl font-extrabold tracking-tight"
                >
                    <span style={{color: "#0F2C57"}}>K</span>
                    <span style={{color: "#0F2C57"}}>O</span>
                    <span style={{color: "#0F2C57"}}>I</span>
                    <span style={{color: "#3A84DF"}}>E</span>
                    <span style={{color: "#3A84DF"}}>X</span>
                    <span style={{color: "#3A84DF"}}>P</span>
                    <span style={{color: "#3A84DF"}}>R</span>
                    <span style={{color: "#3A84DF"}}>E</span>
                    <span style={{color: "#3A84DF"}}>S</span>
                    <span style={{color: "#3A84DF"}}>S</span>
                </Link>

                {/* Button Section - Direct link to Tin tức */}
                <div className="flex items-center space-x-6">
                    <Link
                        to="/blog"
                        className="font-bold text-gray-700 transition-colors duration-300 hover:text-blue-600 mr-10"
                    >
                        Tin tức
                    </Link>

                    <Link to="/login">
                        <button
                            className="px-5 py-4 font-bold text-white transition-colors duration-300 bg-blue-500 rounded-full hover:bg-blue-600">
                            Giao hàng ngay
                        </button>
                    </Link>
                </div>
            </div>
        </div>
    );
}
