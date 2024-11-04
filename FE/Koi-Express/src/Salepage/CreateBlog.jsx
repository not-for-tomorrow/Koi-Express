import React, { useState } from 'react';
import axios from 'axios';

const CreateBlog = () => {
  const [title, setTitle] = useState('');
  const [imageFile, setImageFile] = useState(null);
  const [documentFile, setDocumentFile] = useState(null);

  const handleFileChange = (e, setFile) => {
    setFile(e.target.files[0]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const token = 'YOUR_TOKEN_HERE'; // Thay thế bằng token của bạn
    const formData = new FormData();

    formData.append('title', title);
    formData.append('status', 'DRAFT');
    if (imageFile) formData.append('imageFile', imageFile);
    if (documentFile) formData.append('documentFile', documentFile);

    try {
      const response = await axios.post('http://localhost:8080/api/blogs/create-blog', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
          Authorization: `Bearer ${token}`,
        },
      });
      console.log('Blog created successfully:', response.data);
    } catch (error) {
      console.error('Error creating blog:', error);
    }
  };

  return (
    <div className="max-w-lg p-6 mx-auto bg-white rounded-lg shadow-md">
      <h1 className="mb-6 text-3xl font-bold text-center text-gray-800">Create Blog</h1>
      <form onSubmit={handleSubmit} className="space-y-6">
        <div>
          <label className="block text-sm font-semibold text-gray-700">Title</label>
          <input
            type="text"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
            className="block w-full px-4 py-2 mt-2 border border-gray-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            placeholder="Enter blog title"
          />
        </div>

        <div>
          <label className="block text-sm font-semibold text-gray-700">Image File</label>
          <input
            type="file"
            onChange={(e) => handleFileChange(e, setImageFile)}
            className="block w-full mt-2 text-sm text-gray-500"
          />
        </div>

        <div>
          <label className="block text-sm font-semibold text-gray-700">Document File</label>
          <input
            type="file"
            onChange={(e) => handleFileChange(e, setDocumentFile)}
            className="block w-full mt-2 text-sm text-gray-500"
          />
        </div>

        <button
          type="submit"
          className="w-full px-6 py-3 text-white bg-blue-600 rounded-lg shadow-lg hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-300"
        >
          Create Blog
        </button>
      </form>
    </div>
  );
};

export default CreateBlog;
