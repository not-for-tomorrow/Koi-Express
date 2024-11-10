import React, {useEffect, useState} from 'react';
import axios from 'axios';
import '/src/css/Blog.css';

const Blogs = () => {
    const [blogs, setBlogs] = useState([]);
    const [status, setStatus] = useState('PUBLISHED');
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);
    const [hasMore, setHasMore] = useState(true);

    const fetchBlogs = async (reset = false) => {
        if (!hasMore && !reset) return;

        try {
            setLoading(true);
            const token = localStorage.getItem('token');
            if (!token) {
                setError('Authentication token is missing');
                setLoading(false);
                return;
            }

            const response = await axios.get(`http://localhost:8080/api/blogs/all-blogs/${status}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                },
            });

            const newBlogs = Array.isArray(response.data) ? response.data : [];
            if (newBlogs.length === 0) {
                setHasMore(false);
            } else {
                setHasMore(true);
            }

            setBlogs((prevBlogs) => {
                const existingIds = new Set(prevBlogs.map((blog) => blog.id));
                const uniqueNewBlogs = newBlogs.filter((blog) => !existingIds.has(blog.id));
                return reset ? uniqueNewBlogs : [...prevBlogs, ...uniqueNewBlogs];
            });
        } catch (err) {
            console.error('Error fetching blogs:', err);
            setError('Error fetching blog data');
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        setBlogs([]);
        setHasMore(true);
        setError(null);
        fetchBlogs(true);
    }, [status]);

    const setStatusPublished = () => setStatus('PUBLISHED');
    const setStatusDraft = () => setStatus('DRAFT');

    return (
        <div className="container mx-auto py-10">
            <h1 className="text-2xl font-bold mb-6 text-center">Blogs</h1>
            {error && <p className="text-red-500 text-center">{error}</p>}

            <div className="flex items-center justify-center mb-6 space-x-4">
                <button
                    onClick={setStatusPublished}
                    className={`px-6 py-3 rounded-full transition-all duration-500 ease-in-out ${
                        status === 'PUBLISHED'
                            ? 'bg-gradient-to-r from-blue-500 to-blue-700 text-white'
                            : 'bg-gray-200 text-gray-700'
                    }`}
                >
                    Published Blogs
                </button>
                <button
                    onClick={setStatusDraft}
                    className={`px-6 py-3 rounded-full transition-all duration-500 ease-in-out ${
                        status === 'DRAFT'
                            ? 'bg-gradient-to-r from-green-500 to-green-700 text-white'
                            : 'bg-gray-200 text-gray-700'
                    }`}
                >
                    Draft Blogs
                </button>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                {blogs.map((blog, index) => {
                    const uniqueKey = blog.id ? `${blog.id}-${index}` : index;
                    const decodedImageUrl = blog.imageUrl ? decodeURIComponent(blog.imageUrl) : null;

                    return (
                        <div
                            key={uniqueKey}
                            className="blog-item bg-white rounded-lg shadow-lg overflow-hidden transform transition-all duration-500 hover:scale-105"
                        >
                            {decodedImageUrl ? (
                                <img
                                    src={decodedImageUrl}
                                    alt={blog.title}
                                    className="w-full h-56 object-cover zoom-image"
                                />
                            ) : (
                                <div className="w-full h-56 bg-gray-200 flex items-center justify-center">
                                    <p>Image not available</p>
                                </div>
                            )}
                            <div className="p-4">
                                <p className="text-gray-500 text-sm">
                                    {new Date(blog.createdAt).toLocaleDateString()}
                                </p>
                                <h3 className="blog-title text-lg font-semibold mt-2 uppercase">
                                    {blog.title}
                                </h3>
                            </div>
                        </div>
                    );
                })}
            </div>

            {loading && <p className="text-center mt-4">Loading...</p>}
            {!loading && hasMore && (
                <div className="text-center mt-4">
                    <button
                        onClick={() => fetchBlogs()}
                        className="px-4 py-2 bg-gradient-to-r from-indigo-500 to-indigo-700 text-white rounded-full hover:from-indigo-600 hover:to-indigo-800 transition-all duration-300"
                    >
                        Load More
                    </button>
                </div>
            )}
            {!hasMore && <p className="text-center mt-4">No more blogs to load.</p>}
        </div>
    );
};

export default Blogs;