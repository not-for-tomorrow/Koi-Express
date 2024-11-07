import React from 'react';
import ChatWindowForSales from './ChatWindowForSales.jsx';

const ChatPage = () => {
    return (
        <div className="flex h-screen bg-gray-900 text-white">

            {/* Chat Window */}
            <div className="w-full">
                <ChatWindowForSales/>
            </div>
        </div>
    );
};

export default ChatPage;
