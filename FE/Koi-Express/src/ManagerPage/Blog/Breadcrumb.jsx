// Breadcrumb.jsx
import React from "react";
import { Link } from "react-router-dom";

const Breadcrumb = ({ blogTitle }) => {
    return (
        <nav className="mb-4 text-sm text-gray-500">
            <Link to="/managerpage" className="hover:underline">
                Trang chủ
            </Link>
            <span className="mx-2"> &gt; </span>
            <Link to="/managerpage/blog" className="hover:underline">
                Tin tức
            </Link>
            {blogTitle && (
                <>
                    <span className="mx-2"> &gt; </span>
                    <span className="font-semibold">{blogTitle}</span>
                </>
            )}
        </nav>
    );
};

export default Breadcrumb;