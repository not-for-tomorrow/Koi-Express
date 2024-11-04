import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { blogData, setBlogData } from '../data/blogData';

const BlogDataComponent = () => {
  const [localBlogData, setLocalBlogData] = useState(blogData);

  useEffect(() => {
    const fetchBlogData = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/blogs/all-blogs/DRAFT');
        const data = response.data;

        const updatedData = await Promise.all(
          data.map(async (blog) => {
            const content = await decodeFileContent(blog.filePath);
            const image = await decodeImageUrl(blog.imageUrl);

            return {
              id: blog.blogId,
              title: blog.title,
              date: new Date(blog.updatedAt).toLocaleDateString('vi-VN'),
              content: content || "Nội dung không khả dụng",
              image: image || "/images/default.png",
            };
          })
        );

        setBlogData(updatedData); // Cập nhật blogData gốc
        setLocalBlogData(updatedData); // Cập nhật vào state để render UI
      } catch (error) {
        console.error("Error fetching blog data:", error);
      }
    };

    fetchBlogData();
  }, []);

  const decodeFileContent = async (filePath) => {
    if (!filePath) return null;
    try {
      const response = await axios.get(filePath, { responseType: 'blob' });
      const text = await response.data.text();
      return text;
    } catch (error) {
      console.error("Error decoding file content:", error);
      return null;
    }
  };

  const decodeImageUrl = async (imageUrl) => {
    if (!imageUrl) return null;
    return imageUrl;
  };

  return (
    <div>
      {localBlogData.map((blog) => (
        <div key={blog.id} className="blog-item">
          <h2>{blog.title}</h2>
          <p>{blog.date}</p>
          <p>{blog.content}</p>
          <img src={blog.image} alt={blog.title} />
        </div>
      ))}
    </div>
  );
};

export default BlogDataComponent;
