import React, { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { KoiPriceListBlogData } from "./KoiPriceListBlog";

const BlogDetail = ({ blogs }) => {
  const { slug } = useParams();
  const navigate = useNavigate();
  const [blog, setBlog] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadBlog = () => {
      setLoading(true);
      const foundBlog = [...blogs, KoiPriceListBlogData].find((blog) => blog.slug === slug);
      
      if (foundBlog) {
        setBlog(foundBlog);
      } else {
        navigate("/blog");
      }
      
      setLoading(false);
      window.scrollTo({ top: 0, behavior: "smooth" });
    };

    loadBlog();
  }, [slug, blogs, navigate]);

  if (loading) {
    return (
      <div className="flex justify-center items-center h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-blue-500"></div>
      </div>
    );
  }

  if (!blog) {
    return null;
  }

  // Hàm parse nội dung an toàn
  const parseContent = (content) => {
    try {
      // Nếu content là string, parse JSON, ngược lại giữ nguyên
      const parsedContent = typeof content === 'string' 
        ? JSON.parse(content) 
        : content;

      // Xử lý trường hợp content là mảng hoặc có blocks
      const contentBlocks = parsedContent.blocks || parsedContent;

      // Map và chuyển đổi sang chuỗi text
      return contentBlocks.map(block => {
        // Nếu block là object, lấy text, ngược lại giữ nguyên
        return typeof block === 'object' 
          ? (block.text || JSON.stringify(block)) 
          : block;
      });
    } catch (error) {
      console.error("Lỗi parse nội dung blog:", error);
      return ['Nội dung blog không khả dụng'];
    }
  };

  const blogContent = parseContent(blog.content);

  return (
    <div className="container mx-auto px-4 py-8 max-w-3xl">

      {blog.imageUrl && (
        <div className="mb-6 rounded-lg overflow-hidden shadow-md">
          <img 
            src={blog.imageUrl} 
            alt={blog.title} 
            className="w-full h-[400px] object-cover"
          />
        </div>
      )}

      <h1 className="text-3xl font-bold text-gray-900 mb-4">
        {blog.title}
      </h1>

      <div className="flex items-center text-gray-600 mb-6">
        <svg 
          xmlns="http://www.w3.org/2000/svg" 
          className="h-5 w-5 mr-2" 
          viewBox="0 0 20 20" 
          fill="currentColor"
        >
          <path 
            fillRule="evenodd" 
            d="M6 2a1 1 0 00-1 1v1H4a2 2 0 00-2 2v10a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2h-1V3a1 1 0 10-2 0v1H7V3a1 1 0 00-1-1zm0 5a1 1 0 000 2h8a1 1 0 100-2H6z" 
            clipRule="evenodd" 
          />
        </svg>
        {new Date(blog.createdAt).toLocaleDateString("vi-VN", {
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        })}
      </div>

      <div className="prose prose-lg max-w-none">
        {blogContent.map((text, index) => (
          <p key={index} className="mb-4 leading-relaxed text-gray-700">
            {text}
          </p>
        ))}
      </div>
    </div>
  );
};

export default BlogDetail;