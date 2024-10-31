import React from 'react';
import ContactList from './ContactList';
import ChatWindowForSales from './ChatWindowForSales.jsx';

const ChatPage = () => {
    return (
        <div className="flex h-screen bg-gray-900 text-white">
            {/* Contact List Sidebar */}
            <div className="w-1/4 border-r border-gray-700">
                <ContactList />
            </div>

            {/* Chat Window */}
            <div className="w-3/4">
                <ChatWindowForSales />
            </div>
        </div>
    );
};

export default ChatPage;
