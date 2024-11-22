// src/components/blog/KoiPriceListBlog.jsx
import React from "react";

export const KoiPriceListBlogData = {
  blogId: 'koi-price-list',
  title: 'Bảng Giá Cá Koi Chi Tiết',
  slug: 'bang-gia-ca-koi-chi-tiet',
  imageUrl: '/images/koi-price-list.jpg',
  createdAt: new Date().toISOString(),
  content: JSON.stringify({
    blocks: [
      { text: "Bảng Giá Cá Koi Chi Tiết Tại Koi Express" },
      { text: "1. Koi Kohaku: Giá từ 500.000 - 5.000.000 VNĐ" },
      { text: "2. Koi Showa: Giá từ 800.000 - 8.000.000 VNĐ" },
      { text: "3. Koi Sanke: Giá từ 600.000 - 6.000.000 VNĐ" }
    ]
  })
};

const KoiPriceListBlog = () => {
  return (
    <div className="container mx-auto px-4 py-8 max-w-2xl">
      
      <div className="mb-6 flex justify-center">
        <img 
          src={KoiPriceListBlogData.imageUrl} 
          alt={KoiPriceListBlogData.title} 
          className="w-full max-h-[400px] object-cover rounded-lg"
        />
      </div>

      <h1 className="text-3xl font-bold mb-4">{KoiPriceListBlogData.title}</h1>
      <span className="text-gray-500 block mb-4">
        {new Date(KoiPriceListBlogData.createdAt).toLocaleDateString("vi-VN")}
      </span>

      <div className="blog-content">
        {JSON.parse(KoiPriceListBlogData.content).blocks.map((block, index) => (
          <p key={index} className="text-base text-gray-700 mb-4">
            {block.text}
          </p>
        ))}
      </div>

      <div className="mt-8 p-4 bg-blue-50 rounded-lg">
        <h3 className="text-xl font-semibold mb-2">Liên Hệ Tư Vấn</h3>
        <p className="text-base text-gray-700">
          Hotline: 0123 456 789 | Email: contact@koiexpress.com
        </p>
      </div>
    </div>
  );
};

export default KoiPriceListBlog;