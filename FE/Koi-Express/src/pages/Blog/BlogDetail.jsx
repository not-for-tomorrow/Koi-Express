// BlogDetail.jsx
import React from "react";
import { useParams } from "react-router-dom";
import { blogData } from "./BlogData";

const BlogDetail = () => {
  const { id } = useParams();
  const blog = blogData.find((blog) => blog.id === parseInt(id));

  if (!blog) return <div className="text-center text-gray-500">Blog not found</div>;

  return (
    <div className="max-w-3xl p-6 mx-auto">
      <h1 className="mb-4 text-3xl font-semibold">{blog.title}</h1>
      <span className="text-gray-500">{blog.date}</span>
      <img src={blog.image} alt={blog.title} className="w-full h-auto my-6" />
      <p className="text-lg leading-relaxed">{blog.content}</p>
    </div>
  );
};

export default BlogDetail;
