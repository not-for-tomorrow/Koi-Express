import React, { useState, useEffect } from 'react';
import { Client } from '@stomp/stompjs';

const ChatWindowForSales = () => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [stompClient, setStompClient] = useState(null);

    useEffect(() => {
        const client = new Client({
            brokerURL: 'ws://localhost:8080/ws',
            reconnectDelay: 5000,
            onConnect: () => {
                client.subscribe('/topic/public', (message) => {
                    const chatMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [
                        ...prevMessages,
                        { id: chatMessage.id || Date.now(), sender: chatMessage.sender, text: chatMessage.content, timestamp: 'Now' }
                    ]);
                });
            },
        });

        client.activate();
        setStompClient(client);

        return () => {
            client.deactivate();
        };
    }, []);

    const handleSendMessage = () => {
        if (newMessage.trim() === '' || !stompClient) return;

        const chatMessage = {
            sender: 'Sales Rep',
            content: newMessage,
            type: 'CHAT'
        };

        stompClient.publish({
            destination: '/app/chat.sendMessage',
            body: JSON.stringify(chatMessage),
        });

        setMessages([
            ...messages,
            { id: messages.length + 1, sender: 'You', text: newMessage, timestamp: 'Now' },
        ]);
        setNewMessage('');
    };

    return (
        <div className="flex flex-col h-full">
            <div className="flex items-center justify-between p-4 bg-gray-800 border-b border-gray-700">
                <div>
                    <p className="font-bold text-lg">Nguyen Huy</p>
                    <p className="text-sm text-gray-400">+84 981667547</p>
                </div>
            </div>

            <div className="flex-1 p-4 overflow-y-auto space-y-4">
                {messages.map((message) => (
                    <div key={message.id} className={`flex ${message.sender === 'You' ? 'justify-end' : 'justify-start'}`}>
                        <div className="max-w-xs p-3 rounded-lg bg-gray-700 text-white">
                            <p>{message.text}</p>
                            <p className="text-xs text-gray-400 text-right">{message.timestamp}</p>
                        </div>
                    </div>
                ))}
            </div>

            <div className="p-4 border-t border-gray-700 bg-gray-800">
                <input
                    type="text"
                    placeholder="Type a message..."
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
                    className="w-full px-4 py-2 text-black rounded-lg"
                />
            </div>
        </div>
    );
};

export default ChatWindowForSales;
