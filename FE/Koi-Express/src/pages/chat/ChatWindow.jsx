import React, { useState, useEffect } from "react";
import { Client } from "@stomp/stompjs";
import { FiArrowLeft, FiX, FiSend } from "react-icons/fi";

const formatTime = () => {
    const now = new Date();
    return `${now.getHours().toString().padStart(2, "0")}:${now.getMinutes().toString().padStart(2, "0")}`;
};

const useSequentialTypingEffect = (messages, typingSpeed = 50, onComplete) => {
    const [displayedMessages, setDisplayedMessages] = useState([]);
    const [currentMessageIndex, setCurrentMessageIndex] = useState(0);
    const [currentCharIndex, setCurrentCharIndex] = useState(0);

    useEffect(() => {
        if (currentMessageIndex < messages.length) {
            const intervalId = setInterval(() => {
                setDisplayedMessages((prev) => {
                    const updatedMessages = [...prev];
                    if (!updatedMessages[currentMessageIndex]) {
                        updatedMessages[currentMessageIndex] = "";
                    }
                    updatedMessages[currentMessageIndex] += messages[currentMessageIndex][currentCharIndex];
                    return updatedMessages;
                });

                setCurrentCharIndex((prevIndex) => prevIndex + 1);

                if (currentCharIndex + 1 === messages[currentMessageIndex].length) {
                    clearInterval(intervalId);
                    setCurrentCharIndex(0);
                    setCurrentMessageIndex((prevIndex) => prevIndex + 1);
                }
            }, typingSpeed);

            return () => clearInterval(intervalId);
        } else if (onComplete) {
            onComplete();
        }
    }, [currentMessageIndex, currentCharIndex, messages, typingSpeed, onComplete]);

    return displayedMessages;
};

const ChatWindow = ({ onClose }) => {
    const [isOffline, setIsOffline] = useState(false);
    const [email, setEmail] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [showEmailForm, setShowEmailForm] = useState(false);
    const [showInputField, setShowInputField] = useState(false);
    const [message, setMessage] = useState("");
    const [messages, setMessages] = useState([]);
    const [stompClient, setStompClient] = useState(null);

    const offlineMessages = [
        "Our reps are currently offline.",
        "Please leave your email, and we will get back to you as soon as possible."
    ];

    const onlineMessages = [
        "Welcome to Koi Express. Thank you for your interest in our services.",
        "How can we help you today?"
    ];

    // Initialize WebSocket Connection
    useEffect(() => {
        const client = new Client({
            brokerURL: "ws://localhost:8080/ws",
            reconnectDelay: 5000,
            onConnect: () => {
                client.subscribe("/topic/public", (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [
                        ...prevMessages,
                        { id: receivedMessage.id || Date.now(), sender: receivedMessage.sender, text: receivedMessage.content, timestamp: formatTime() }
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

    useEffect(() => {
        const currentHour = new Date().getHours();
        setIsOffline(currentHour >= 22 || currentHour < 7);
    }, []);

    const typedMessages = useSequentialTypingEffect(
        isOffline ? offlineMessages : onlineMessages,
        50,
        () => {
            if (isOffline) {
                setShowEmailForm(true);
            } else {
                setShowInputField(true);
            }
        }
    );

    const handleEmailSubmit = (e) => {
        e.preventDefault();
        setIsSubmitting(true);

        setTimeout(() => {
            setIsSubmitting(false);
            setIsSubmitted(true);
            setEmail("");
        }, 1500);
    };

    const handleSendMessage = () => {
        if (message.trim() && stompClient) {
            const chatMessage = {
                sender: "Customer",
                content: message,
                type: "CHAT"
            };

            stompClient.publish({
                destination: "/app/chat.sendMessage",
                body: JSON.stringify(chatMessage),
            });

            setMessages((prevMessages) => [
                ...prevMessages,
                { id: Date.now(), sender: "You", text: message, timestamp: formatTime() },
            ]);
            setMessage("");
        }
    };

    const handleRestartConversation = () => {
        setIsSubmitted(false);
        setIsOffline(false);
        setShowEmailForm(false);
        setMessages([]);
    };

    return (
        <div className="fixed bottom-5 right-5 w-[24rem] h-[40rem] bg-white shadow-2xl rounded-2xl flex flex-col z-[9999] animate-slide-fade-in">
            <div className="flex items-center justify-between p-4 bg-gradient-to-r from-blue-700 to-blue-800 text-white rounded-t-2xl shadow-lg animate-fade-down">
                <div className="flex items-center space-x-3">
                    <button className="text-2xl hover:bg-blue-600 rounded-full p-1 transition" aria-label="Back">
                        <FiArrowLeft />
                    </button>
                    <img src="/src/assets/images/Icons/KoiExpress1.webp" alt="Chat Icon" className="w-8 h-8 rounded-full bg-white p-1 shadow-md" />
                    <div>
                        <h2 className="text-lg font-semibold">Koi Express</h2>
                        <p className="text-xs text-blue-200">You are chatting with Koi Express</p>
                    </div>
                </div>
                <button onClick={onClose} className="text-2xl hover:bg-blue-600 rounded-full p-1 transition" aria-label="Close Chat">
                    <FiX />
                </button>
            </div>

            <div className="flex-1 p-4 space-y-3 overflow-y-auto bg-gray-50 rounded-b-2xl animate-fade-in">
                <p className="text-xs text-gray-500 text-center mb-2">Today, {formatTime()}</p>

                {typedMessages.map((msg, index) => (
                    <div key={index} className="flex items-start space-x-2 animate-slide-right">
                        <img src="/src/assets/images/Icons/KoiExpress1.webp" alt="Agent Icon" className="w-6 h-6 rounded-full bg-white shadow-md" />
                        <div className="bg-blue-50 text-blue-900 p-3 rounded-2xl shadow-inner max-w-[80%] border border-blue-100">
                            <p>{msg}</p>
                            <p className="text-xs text-blue-500 mt-1">{formatTime()}</p>
                        </div>
                    </div>
                ))}

                {messages.map((msg, index) => (
                    <div key={index} className={`flex ${msg.sender === "You" ? "justify-end" : "justify-start"}`}>
                        <div className="bg-gray-200 p-3 rounded-lg max-w-xs shadow-md">
                            <p>{msg.text}</p>
                            <p className="text-xs text-gray-400 text-right">{msg.timestamp}</p>
                        </div>
                    </div>
                ))}

                {isOffline && showEmailForm && !isSubmitted && (
                    <div className="bg-gray-200 p-4 rounded-2xl shadow-lg mt-3 animate-slide-up">
                        <p className="text-gray-700">Please leave your email, and we will contact you soon.</p>
                        <form onSubmit={handleEmailSubmit} className="mt-4">
                            <label className="block text-sm font-medium text-gray-600 mb-1">Email</label>
                            <div className="flex items-center border border-gray-300 rounded-full p-1 shadow-sm bg-white">
                                <input
                                    type="email"
                                    value={email}
                                    onChange={(e) => setEmail(e.target.value)}
                                    placeholder="Enter your Email"
                                    className="flex-1 p-2 text-gray-800 placeholder-gray-500 rounded-full focus:outline-none"
                                    required
                                />
                                <button
                                    type="submit"
                                    className="bg-gradient-to-r from-blue-500 to-blue-600 text-white p-2 rounded-full flex items-center justify-center ml-2 shadow-md"
                                    disabled={isSubmitting}
                                >
                                    {isSubmitting ? (
                                        <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin"></span>
                                    ) : (
                                        <FiSend className="text-lg" />
                                    )}
                                </button>
                            </div>
                        </form>
                    </div>
                )}

                {isSubmitted && (
                    <div className="bg-green-100 p-3 rounded-2xl text-green-700 shadow-lg mt-3 animate-slide-up">
                        <p>Thank you! We have received your email. Our team will reach out soon.</p>
                    </div>
                )}
            </div>

            {showInputField && (
                <div className="bg-white p-3 border-t border-gray-300 shadow-lg flex items-center justify-center">
                    <div className="flex items-center border border-gray-300 rounded-full p-1 shadow-sm w-full">
                        <input
                            type="text"
                            placeholder="Type a message..."
                            value={message}
                            onChange={(e) => setMessage(e.target.value)}
                            className="flex-1 p-2 rounded-full border-none focus:outline-none"
                        />
                        <button
                            onClick={handleSendMessage}
                            className="text-blue-600 p-2 rounded-full flex items-center justify-center ml-2"
                        >
                            <FiSend className="text-lg" />
                        </button>
                    </div>
                </div>
            )}

            <div
                onClick={handleRestartConversation}
                className="p-3 bg-blue-900 text-white font-semibold rounded-b-2xl flex items-center justify-center text-sm cursor-pointer hover:bg-blue-800 transition duration-200 animate-fade-up"
            >
                <svg className="w-5 h-5 mr-2 text-white" fill="currentColor" viewBox="0 0 24 24"><path d="M12 4V1L8 5l4 4V6c3.87 0 7 3.13 7 7 0 1.65-.56 3.17-1.51 4.39l1.43 1.43C20.1 17.47 21 15.31 21 13c0-5.52-4.48-10-10-10zm-1 14c-3.87 0-7-3.13-7-7 0-1.65.56 3.17 1.51 4.39L3.51 4.61A9.976 9.976 0 002 11c0 5.52 4.48 10 10 10v3l4-4-4-4v3z"/></svg>
                Restart conversation
            </div>
        </div>
    );
};

export default ChatWindow;
