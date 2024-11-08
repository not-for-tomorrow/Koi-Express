import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { fetchBlogDataByStatus } from "../../koi/api/api";

const BlogDetail = ({ status, setNeedsRefresh, generateSlug, blogs }) => {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [blog, setBlog] = useState(null);
  const [loading, setLoading] = useState(true);
  const [approvalMessage, setApprovalMessage] = useState(null);

  useEffect(() => {
    const loadBlog = async () => {
      setLoading(true);
      let foundBlog = blogs.find((blog) => generateSlug(blog.title) === slug);

      if (!foundBlog) {
        const data = await fetchBlogDataByStatus(status);
        foundBlog = data.find((blog) => generateSlug(blog.title) === slug);
      }

      setBlog(foundBlog);
      setLoading(false);
    };
    loadBlog();
  }, [slug, status, blogs, generateSlug]);

  const handleApprove = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      setApprovalMessage({
        type: "error",
        text: "Authorization token not found.",
      });
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/blogs/approve/${blog.blogId}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to approve the blog.");
      }

      setApprovalMessage({
        type: "success",
        text: "Blog approved successfully!",
      });

      setNeedsRefresh(true);
      navigate("/managerpage/blog");
    } catch (error) {
      setApprovalMessage({ type: "error", text: error.message });
    }
  };

  if (loading) {
    return <div className="text-center text-gray-500">Loading...</div>;
  }

  if (!blog) {
    return <div className="text-center text-gray-500">Blog not found</div>;
  }

  return (
    <div className="max-w-2xl p-6 mx-auto rounded-lg h-[calc(100vh-4rem)] overflow-y-auto">
      <h1 className="mb-4 text-3xl font-semibold">{blog?.title}</h1>
      <span className="text-gray-500">
        {new Date(blog?.createdAt).toLocaleDateString("vi-VN")}
      </span>
      <div className="my-6 flex justify-center">
        <img
          src={blog?.imageUrl || "/default-image.png"}
          alt={blog?.title}
          className="w-auto h-auto max-w-full max-h-[500px] object-contain"
        />
      </div>
      <p className="text-sm leading-relaxed">{blog?.content}</p>

      <div className="mt-6 flex justify-center">
        <button
          onClick={handleApprove}
          className="px-6 py-2 text-white bg-blue-600 rounded hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-green-500"
        >
          Chấp nhận
        </button>
      </div>

      {approvalMessage && (
        <p
          className={`mt-4 text-center ${
            approvalMessage.type === "error" ? "text-red-500" : "text-green-500"
          }`}
        >
          {approvalMessage.text}
        </p>
      )}
    </div>
  );
};

export default BlogDetail;
