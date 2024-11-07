import React, {useState, useEffect, useRef, useMemo} from 'react';
import {FaSearch, FaPlus} from 'react-icons/fa';
import stompClient from '/src/websocket/websocketService.js';
import jwtDecode from 'jwt-decode';

const contacts = [
    {id: 1, name: 'Nguyen Huy', phone: '+84 981667547', lastMessageTime: '12:30 PM'},
    {id: 2, name: 'Ngoc Hoan', phone: '+84 912345678', lastMessageTime: 'Yesterday'},
];

const ChatWindowForSales = () => {
    const [messages, setMessages] = useState([]);
    const [newMessage, setNewMessage] = useState('');
    const [selectedContact, setSelectedContact] = useState(null);
    const [searchTerm, setSearchTerm] = useState('');
    const [isSearching, setIsSearching] = useState(false);
    const messageEndRef = useRef(null);

    const getStaffIdFromToken = () => {
        const token = localStorage.getItem('token');
        if (token) {
            try {
                const decoded = jwtDecode(token);
                return decoded.accountId;
            } catch (err) {
                console.error('Failed to decode token', err);
                return null;
            }
        }
        return null;
    };

    useEffect(() => {

        const accountId = getStaffIdFromToken();
        if (!accountId) {
            console.error('Unable to find staffId from token');
            return;
        }

        stompClient.connect({}, () => {
            console.log("Connected to WebSocket");

            stompClient.subscribe('/topic/public', (message) => {
                const receivedMessage = JSON.parse(message.body);
                setMessages((prevMessages) => [...prevMessages, receivedMessage]);
            });

            stompClient.subscribe(`/user/${accountId}/queue/private`, (message) => {
                const receivedMessage = JSON.parse(message.body);
                setMessages((prevMessages) => [...prevMessages, receivedMessage]);
            });
        });

        return () => {
            if (stompClient) {
                stompClient.disconnect();
            }
        };
    }, []);

    const handleSendMessage = () => {
        if (newMessage.trim() === '' || !selectedContact) return;

        const accountId = getStaffIdFromToken();
        if (!accountId) {
            console.error('Unable to send message without staffId');
            return;
        }

        const messageData = {
            senderName: 'You',
            text: newMessage,
            timestamp: new Date(),
            receiverId: selectedContact.id,
            customer: {customerId: selectedContact.id},
            staff: {staffId: accountId},
        };

        stompClient.send('/app/chat.sendPrivateMessage', {}, JSON.stringify(messageData));

        setMessages([...messages, {...messageData, id: messages.length + 1}]);
        setNewMessage('');
    };

    const shouldShowTimestamp = (currentMessage, previousMessage) => {
        if (!previousMessage) return true;
        const timeDifference = (currentMessage.timestamp - previousMessage.timestamp) / 60000;
        return timeDifference >= 30;
    };

    const filteredMessages = useMemo(() => {
        return messages.filter(
            (message) =>
                selectedContact &&
                message.receiverId === selectedContact.id &&
                message.text.toLowerCase().includes(searchTerm.toLowerCase())
        );
    }, [messages, selectedContact, searchTerm]);

    useEffect(() => {
        if (messageEndRef.current) {
            messageEndRef.current.scrollIntoView({behavior: 'smooth'});
        }
    }, [messages]);

    return (
        <div className="flex h-screen">
            <div className="w-1/3 bg-white p-4 text-black border-r border-gray-300">
                <input
                    type="text"
                    placeholder="Search (Ctrl+K)"
                    className="w-full px-4 py-2 mb-4 rounded-lg text-black border"
                />
                <ul className="space-y-3">
                    {contacts.map((contact) => (
                        <li
                            key={contact.id}
                            onClick={() => setSelectedContact(contact)}
                            className={`flex items-center p-2 rounded-lg cursor-pointer ${
                                selectedContact && selectedContact.id === contact.id ? 'bg-gray-200' : 'hover:bg-gray-100'
                            }`}
                        >
                            <img
                                src="https://via.placeholder.com/40"
                                alt="Avatar"
                                className="w-10 h-10 rounded-full mr-3"
                            />
                            <div className="flex-1">
                                <div className="flex justify-between">
                                    <p className="font-bold">{contact.name}</p>
                                    <span className="text-sm text-gray-500">{contact.lastMessageTime}</span>
                                </div>
                                <p className="text-sm text-gray-500">{contact.phone}</p>
                            </div>
                        </li>
                    ))}
                </ul>
            </div>

            <div className="flex flex-col w-2/3 bg-white text-black">
                {selectedContact ? (
                    <>
                        {/* Thanh tiêu đề với các icon */}
                        <div className="flex items-center justify-between p-4 border-b border-gray-300 text-purple-600">
                            <div className="flex items-center space-x-4">
                                <img
                                    src="https://via.placeholder.com/40"
                                    alt="Avatar"
                                    className="w-10 h-10 rounded-full"
                                />
                                <div>
                                    <p className="font-bold text-lg text-white">{selectedContact.name}</p>
                                </div>
                            </div>
                            <div className="flex items-center space-x-3">
                                <FaSearch onClick={() => setIsSearching(!isSearching)} className="cursor-pointer"/>
                            </div>
                        </div>

                        {isSearching && (
                            <div className="p-4 border-b border-gray-300">
                                <input
                                    type="text"
                                    placeholder="Search in Conversation (Ctrl+F)"
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="w-full px-4 py-2 rounded-lg border"
                                />
                            </div>
                        )}

                        <div className="flex-1 p-4 overflow-y-auto space-y-4">
                            {filteredMessages.map((message, index) => {
                                const previousMessage = filteredMessages[index - 1];
                                const showTimestamp = shouldShowTimestamp(message, previousMessage);
                                const isSender = message.senderName === 'You';
                                const messageLength = message.text.length;

                                let backgroundColorClass;
                                if (messageLength < 30) {
                                    backgroundColorClass = isSender ? 'bg-purple-700 text-white' : 'bg-gray-700 text-white';
                                } else if (messageLength >= 30 && messageLength < 100) {
                                    backgroundColorClass = isSender ? 'bg-blue-600 text-white' : 'bg-gray-600 text-white';
                                } else {
                                    backgroundColorClass = isSender ? 'bg-gradient-to-r from-purple-700 to-blue-600 text-white' : 'bg-gradient-to-r from-gray-700 to-gray-600 text-white'; // Kết hợp màu thứ nhất và thứ hai
                                }

                                return (
                                    <div key={message.id}>
                                        {showTimestamp && (
                                            <div className="text-center text-gray-500 text-sm mb-2">
                                                {message.timestamp.toLocaleDateString('en-US', {
                                                    weekday: 'short',
                                                    hour: '2-digit',
                                                    minute: '2-digit'
                                                })}
                                            </div>
                                        )}
                                        <div className={`flex ${isSender ? 'justify-end' : 'justify-start'}`}>
                                            <div className={`max-w-xs p-3 rounded-lg ${backgroundColorClass}`}>
                                                <p>{message.text}</p>
                                            </div>
                                        </div>
                                    </div>
                                );
                            })}
                            <div ref={messageEndRef}/>
                        </div>

                        <div className="p-4 border-t border-gray-300 flex items-center space-x-2">

                            <button className="text-blue-500">
                                <FaPlus size={20}/>
                            </button>

                            <textarea
                                placeholder="Type a message..."
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === 'Enter' && !e.shiftKey) {
                                        e.preventDefault();
                                        handleSendMessage();
                                    }
                                }}
                                className="flex-1 px-4 py-2 border rounded-lg border"
                                rows={1}
                                style={{maxHeight: '400px'}}
                            />
                        </div>

                    </>
                ) : (
                    <div className="flex items-center justify-center flex-1 text-gray-500">
                        Select a contact to start chatting
                    </div>
                )}
            </div>
        </div>
    );
};

export default ChatWindowForSales;
