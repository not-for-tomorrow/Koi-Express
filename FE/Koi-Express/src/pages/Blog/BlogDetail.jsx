import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";

const BlogDetail = ({ blogs }) => {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [blog, setBlog] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadBlog = () => {
      setLoading(true);
      const foundBlog = blogs.find((blog) => blog.slug === slug);
      setBlog(foundBlog);
      setLoading(false);
    };
    loadBlog();
  }, [slug, blogs]);

  const renderContent = () => {
    try {
      const contentJSON = JSON.parse(blog.content);
      return contentJSON.blocks.map((block, index) => (
          <p key={index} className="text-sm leading-relaxed mb-2">
            {block.text}
          </p>
      ));
    } catch (error) {
      console.error("Error parsing content:", error);
      return <p className="text-sm leading-relaxed">{blog.content}</p>;
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
        <h1 className="mb-4 text-3xl font-semibold">{blog.title}</h1>
        <span className="text-gray-500">
        {new Date(blog.createdAt).toLocaleDateString("vi-VN")}
      </span>
        <div className="my-6 flex justify-center">
          <img
              src={blog.imageUrl || "/default-image.png"}
              alt={blog.title}
              className="w-auto h-auto max-w-full max-h-[500px] object-contain"
          />
        </div>
        <div>{renderContent()}</div>
      </div>
  );
};

export default BlogDetail;
