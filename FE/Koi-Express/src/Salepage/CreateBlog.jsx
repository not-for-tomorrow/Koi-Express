import React, { useState } from "react";
import { EditorState, convertToRaw } from "draft-js";
import Editor from "@draft-js-plugins/editor";
import createToolbarPlugin, {
  Separator,
} from "@draft-js-plugins/static-toolbar";
import {
  ItalicButton,
  BoldButton,
  UnderlineButton,
  CodeButton,
  UnorderedListButton,
  OrderedListButton,
  BlockquoteButton,
} from "@draft-js-plugins/buttons";
import "@draft-js-plugins/static-toolbar/lib/plugin.css";
import "draft-js/dist/Draft.css";
import { createBlogAPI } from "../koi/api/api.js";

const toolbarPlugin = createToolbarPlugin();
const { Toolbar } = toolbarPlugin;
const plugins = [toolbarPlugin];

const CreateBlog = () => {
  const [title, setTitle] = useState("");
  const [editorState, setEditorState] = useState(EditorState.createEmpty());
  const [imageFile, setImageFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [message, setMessage] = useState(null);

  const handleFileChange = (e) => {
    const file = e.target.files[0];
    console.log("Selected File:", file);
    setImageFile(file);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const content = JSON.stringify(
      convertToRaw(editorState.getCurrentContent())
    );

    if (!title.trim() || !content.trim()) {
      setMessage({ type: "error", text: "Title and content are required." });
      return;
    }

    setIsLoading(true);
    setMessage(null);

    try {
      console.log("Title:", title);
      console.log("Content:", content);
      console.log("Image File (before sending):", imageFile);

      await createBlogAPI(title, content, imageFile);
      setMessage({ type: "success", text: "Blog đã được tạo thành công" });
      setTitle("");
      setEditorState(EditorState.createEmpty());
      setImageFile(null);
      setTimeout(() => setMessage(null), 5000);
    } catch (error) {
      const errorText = error.response?.data?.message || "Lỗi tạo blog.";
      setMessage({ type: "error", text: errorText });
      console.error("Error creating blog:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEditorChange = (newEditorState) => {
    setEditorState(newEditorState);
  };

  return (
    <div className="w-full h-screen flex items-center justify-center bg-gray-100">
      <div className="w-full max-w-5xl p-6 bg-white rounded-lg shadow-md">
        <h1 className="text-3xl font-bold text-center text-gray-800 mb-6">
          Tạo blog
        </h1>

        {message && (
          <p
            className={`text-center mb-4 ${
              message.type === "error" ? "text-red-500" : "text-green-500"
            }`}
          >
            {message.text}
          </p>
        )}

        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="mt-4">
            <label className="block text-lg font-semibold text-gray-700">
              Tiêu đề
            </label>
            <input
                type="text"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                className="w-full px-4 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                placeholder="Nhập tiêu đề blog"
            />
          </div>

          <div className="mt-4">
            <label className="block text-lg font-semibold text-gray-700 mb-2">
              Nội dung
            </label>
            <div
                className="editor-container border border-gray-300 rounded-lg shadow-sm"
                style={{height: "400px", display: "flex", flexDirection: "column"}}
            >
              {/* Toolbar - Fixed at the top of the editor */}
              <div className="toolbar-container p-2 border-b border-gray-200 bg-white">
                <Toolbar>
                  {(externalProps) => (
                      <>
                        <BoldButton {...externalProps} />
                        <ItalicButton {...externalProps} />
                        <UnderlineButton {...externalProps} />
                        <CodeButton {...externalProps} />
                        <Separator {...externalProps} />
                        <UnorderedListButton {...externalProps} />
                        <OrderedListButton {...externalProps} />
                        <BlockquoteButton {...externalProps} />
                      </>
                  )}
                </Toolbar>
              </div>
              {/* Editor Content - Scrollable when content is too long */}
              <div
                  className="editor-content p-4"
                  style={{flex: 1, overflowY: "auto"}}
              >
                <Editor
                    editorState={editorState}
                    onChange={handleEditorChange}
                    plugins={plugins}
                    placeholder="Viết nội dung blog ở đây..."
                />
              </div>
            </div>
          </div>


          <div className="mt-4">
            <label className="block text-lg font-semibold text-gray-700">
              Hình ảnh
            </label>
            <input
                type="file"
                onChange={handleFileChange}
                className="w-full mt-1 text-sm text-gray-500"
            />
          </div>

          <button
              type="submit"
              className="w-full py-3 text-white bg-blue-600 rounded hover:bg-blue-700 focus:outline-none focus:ring-4 focus:ring-blue-300"
              disabled={isLoading}
          >
            {isLoading ? "Đang tạo..." : "Tạo Blog"}
          </button>
        </form>
      </div>
    </div>
  );
};

export default CreateBlog;
