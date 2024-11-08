// Blog.jsx
import React from "react";
import { Routes, Route, Link, useMatch } from "react-router-dom";
import { blogData } from "./BlogData";
import BlogDetail from "./BlogDetail";
import Breadcrumb from "./Breadcrumb";

const Blog = () => {
  const match = useMatch("/blog/:id");
  const blogId = match?.params?.id;

  const blogTitle = blogId
    ? blogData.find((blog) => blog.blogId === parseInt(blogId))?.title
    : null; // updated to blogId

  return (
    <div className="container p-5 mx-auto max-w-7xl">
      <Breadcrumb blogTitle={blogTitle} />
      <div className="space-y-8">
        <Routes>
          <Route
            path="/"
            element={
              <>
                <h1 className="mb-4 text-4xl font-bold">Có gì trên Ahamove</h1>
                <p className="mb-6 text-gray-600">
                  Nơi cập nhật tất cả tin tức, hoạt động mới nhất từ Ahamove và
                  các đối tác.
                </p>
                <div className="flex flex-wrap gap-3 mb-8">
                  <button className="px-4 py-2 font-semibold text-white bg-blue-500 rounded-full">
                    Tất cả tin tức
                  </button>
                  <button className="px-4 py-2 font-semibold bg-gray-200 rounded-full hover:bg-blue-500 hover:text-white">
                    Khách hàng
                  </button>
                  <button className="px-4 py-2 font-semibold bg-gray-200 rounded-full hover:bg-blue-500 hover:text-white">
                    Tài xế
                  </button>
                  <button className="px-4 py-2 font-semibold bg-gray-200 rounded-full hover:bg-blue-500 hover:text-white">
                    Đồng hành cùng Thương hiệu
                  </button>
                </div>
                <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                  {blogData.map((blog) => (
                    <Link
                      to={`/blog/${blog.blogId}`}
                      key={blog.blogId}
                      className="overflow-hidden transition-transform duration-200 transform border border-gray-200 rounded-lg shadow-lg hover:scale-105"
                    >
                      {" "}
                      {/* updated to blogId */}
                      <img
                        src={blog.image}
                        alt={blog.title}
                        className="object-cover w-full h-48"
                      />
                      <div className="p-5">
                        <span className="block mb-2 text-sm text-gray-500">
                          {blog.createdAt}
                        </span>{" "}
                        {/* updated to createdAt */}
                        <h2 className="mb-3 text-xl font-semibold">
                          {blog.title}
                        </h2>
                      </div>
                    </Link>
                  ))}
                </div>
              </>
            }
          />
          <Route path=":id" element={<BlogDetail />} />
        </Routes>
      </div>
    </div>
  );
};

export default Blog;
