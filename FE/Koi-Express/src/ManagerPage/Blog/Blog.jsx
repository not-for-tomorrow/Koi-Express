import React, { useEffect, useState } from "react";
import { Routes, Route, Link, useMatch } from "react-router-dom";
import BlogDetail from "./BlogDetail";
import Breadcrumb from "./Breadcrumb";
import { fetchBlogDataByStatus } from "../../koi/api/api";

// Utility function to generate slug from title
const generateSlug = (title) => {
  return title
    .toLowerCase()
    .replace(/[^a-z0-9\s-]/g, "") // Remove special characters
    .trim()
    .replace(/\s+/g, "-"); // Replace spaces with hyphens
};

const Blog = () => {
  const [blogs, setBlogs] = useState([]);
  const [status, setStatus] = useState("PUBLISHED");
  const [needsRefresh, setNeedsRefresh] = useState(false);
  const match = useMatch("/managerpage/blog/:slug");
  const blogSlug = match?.params?.slug;

  const blogTitle = blogSlug
    ? blogs.find((blog) => generateSlug(blog.title) === blogSlug)?.title
    : null;

  useEffect(() => {
    const loadBlogData = async () => {
      const data = await fetchBlogDataByStatus(status);
      setBlogs(data);
      setNeedsRefresh(false); // Reset refresh trigger after loading
      window.scrollTo({ top: 0, behavior: "smooth" });
    };
    loadBlogData();
  }, [status, needsRefresh]);

  return (
    <div className="container p-5 mx-auto max-w-7xl">
      <Breadcrumb blogTitle={blogTitle} />
      <div className="space-y-8">
        {!blogSlug && (
          <div className="flex justify-end mb-4">
            <button
              className={`px-4 py-2 mr-2 font-semibold rounded-full ${
                status === "PUBLISHED"
                  ? "bg-blue-500 text-white"
                  : "bg-gray-200"
              }`}
              onClick={() => setStatus("PUBLISHED")}
            >
              Published
            </button>
            <button
              className={`px-4 py-2 font-semibold rounded-full ${
                status === "DRAFT" ? "bg-blue-500 text-white" : "bg-gray-200"
              }`}
              onClick={() => setStatus("DRAFT")}
            >
              Draft
            </button>
          </div>
        )}
        <Routes>
          <Route
            path="/"
            element={
              <div className="overflow-y-auto max-h-[600px] grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                {blogs.map((blog) => (
                  <Link
                    to={`/managerpage/blog/${generateSlug(blog.title)}`}
                    key={blog.blogId}
                    className="overflow-hidden transition-transform duration-200 transform border border-gray-200 rounded-lg shadow-lg hover:scale-105"
                  >
                    <img
                      src={blog.imageUrl || "/default-image.png"}
                      alt={blog.title}
                      className="object-cover w-full h-48"
                    />
                    <div className="p-5">
                      <span className="block mb-2 text-sm text-gray-500">
                        {new Date(blog.createdAt).toLocaleDateString("vi-VN")}
                      </span>
                      <h2 className="mb-3 text-xl font-semibold">
                        {blog.title}
                      </h2>
                    </div>
                  </Link>
                ))}
              </div>
            }
          />
          <Route
            path=":slug"
            element={
              <BlogDetail
                status={status}
                setNeedsRefresh={setNeedsRefresh}
                generateSlug={generateSlug}
                blogs={blogs}
              />
            }
          />
        </Routes>
      </div>
    </div>
  );
};

export default Blog;
