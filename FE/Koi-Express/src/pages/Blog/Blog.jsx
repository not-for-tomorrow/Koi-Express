import React, {useEffect, useState} from "react";
import {Routes, Route, Link, useMatch} from "react-router-dom";
import BlogDetail from "./BlogDetail";
import Breadcrumb from "./Breadcrumb";
import KoiPriceListBlog, {KoiPriceListBlogData} from "./KoiPriceListBlog";
import {loadBlogData} from "../../koi/api/api";

const Blog = () => {
    const [blogs, setBlogs] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const match = useMatch("/blog/:slug");
    const blogSlug = match?.params?.slug;

    const blogTitle = blogSlug
        ? [...blogs, KoiPriceListBlogData].find((blog) => blog.slug === blogSlug)?.title
        : null;

    useEffect(() => {
        const loadBlogDataFromAPI = async () => {
            setLoading(true);
            try {
                const apiBlogs = await loadBlogData();
                const sortedBlogs = [KoiPriceListBlogData, ...apiBlogs].sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
                setBlogs(sortedBlogs);
                setLoading(false);
                window.scrollTo({top: 0, behavior: "smooth"});
            } catch (err) {
                setError("Lỗi khi tải dữ liệu. Vui lòng thử lại.");
                setLoading(false);
            }
        };

        loadBlogDataFromAPI();
    }, []);

    return (
        <div className="container p-5 mx-auto max-w-7xl">
            <Breadcrumb blogTitle={blogTitle}/>
            <div className="space-y-8">
                {loading ? (
                    <div className="text-center text-gray-500">Loading...</div>
                ) : error ? (
                    <div className="text-center text-red-500">{error}</div>
                ) : (
                    <Routes>
                        <Route
                            path="/"
                            element={
                                <>
                                    <h1 className="mb-4 text-4xl font-bold">Có gì trên Koi Express</h1>
                                    <p className="mb-6 text-gray-600">
                                        Nơi cập nhật tất cả tin tức, hoạt động mới nhất từ Koi Express và các đối tác.
                                    </p>
                                    <div className="grid grid-cols-1 gap-6 md:grid-cols-2 lg:grid-cols-3">
                                        {blogs.map((blog) => (
                                            <Link
                                                to={`/blog/${blog.slug}`}
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
                                                    <h2 className="mb-3 text-xl font-semibold">{blog.title}</h2>
                                                </div>
                                            </Link>
                                        ))}
                                    </div>
                                </>
                            }
                        />
                        <Route path=":slug" element={<BlogDetail blogs={blogs}/>}/>
                        <Route path="bang-gia-ca-koi-chi-tiet" element={<KoiPriceListBlog/>}/>
                    </Routes>
                )}
            </div>
        </div>
    );
};

export default Blog;
