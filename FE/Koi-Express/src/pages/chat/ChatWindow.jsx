import React, {useState, useEffect, useRef} from "react";
import stompClient from '/src/websocket/websocketService.js';
import {FiArrowLeft, FiX, FiSend} from "react-icons/fi";
import jwtDecode from 'jwt-decode';

const formatTime = (timestamp) => {
    const date = new Date(timestamp);
    return `${date.getHours().toString().padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
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

const ChatWindow = ({onClose}) => {
    const [isOffline, setIsOffline] = useState(false);
    const [email, setEmail] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [isSubmitted, setIsSubmitted] = useState(false);
    const [showEmailForm, setShowEmailForm] = useState(false);
    const [showInputField, setShowInputField] = useState(false);
    const [newMessage, setNewMessage] = useState('');
    const [messages, setMessages] = useState([]);
    const messageEndRef = useRef(null);
    const [customerId, setCustomerId] = useState(null);
    const [isWebSocketConnected, setIsWebSocketConnected] = useState(false);
    const isConnecting = useRef(false);

    const offlineMessages = [
        "Our reps are currently offline.",
        "Please leave your email, and we will get back to you as soon as possible."
    ];

    const onlineMessages = [
        "Welcome to Koi Express. Thank you for your interest in our services.",
        "How can we help you today?"
    ];

    useEffect(() => {
        const fetchCustomerId = () => {
            try {
                const token = localStorage.getItem('token');
                if (token) {
                    const decodedToken = jwtDecode(token);
                    const customerIdFromToken = decodedToken.customerId;
                    if (customerIdFromToken) {
                        console.log("Customer ID fetched from token:", customerIdFromToken);
                        setCustomerId(customerIdFromToken);
                    } else {
                        console.error("Customer ID not found in token");
                    }
                } else {
                    console.error("Token not found");
                }
            } catch (error) {
                console.error("Failed to decode token:", error);
            }
        };

        fetchCustomerId();
    }, []);

    useEffect(() => {
        if (!customerId || isConnecting.current) {
            console.error("Customer ID is required to connect to WebSocket or already connecting.");
            return;
        }

        const connectWebSocket = () => {
            console.log("Attempting to connect to WebSocket with customerId:", customerId);
            isConnecting.current = true;

            stompClient.connect({}, () => {
                console.log("Connected to WebSocket successfully!");
                setIsWebSocketConnected(true);
                isConnecting.current = false;

                // Đăng ký kênh công khai cho các tin nhắn chung
                stompClient.subscribe('/topic/public', (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [...prevMessages, receivedMessage]);
                });

                // Đăng ký kênh cá nhân với customerId
                stompClient.subscribe(`/user/${customerId}/queue/private`, (message) => {
                    const receivedMessage = JSON.parse(message.body);
                    setMessages((prevMessages) => [...prevMessages, receivedMessage]);
                });
            }, (error) => {
                console.error("Failed to connect to WebSocket:", error);
                isConnecting.current = false;
            });
        };

        connectWebSocket();

        return () => {
            if (stompClient && isWebSocketConnected) {
                stompClient.disconnect(() => {
                    console.log("Disconnected from WebSocket");
                    setIsWebSocketConnected(false);
                });
            }
        };
    }, [customerId]);

    useEffect(() => {
        const currentHour = new Date().getHours();
        setIsOffline(currentHour >= 22 || currentHour < 7);
    }, []);

    useEffect(() => {
        messageEndRef.current?.scrollIntoView({behavior: "smooth"});
    }, [messages]);

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
        if (newMessage.trim() === '') {
            console.warn("Message is empty. Cannot send.");
            return;
        }

        if (!isWebSocketConnected) {
            console.error("WebSocket is not connected.");
            return;
        }

        const messageData = {
            senderName: 'You',
            text: newMessage,
            timestamp: new Date().toISOString(),
        };

        try {
            stompClient.send('/app/chat.sendPrivateMessage', {}, JSON.stringify(messageData));
            setMessages([...messages, {...messageData, id: messages.length + 1}]);
            setNewMessage('');
        } catch (error) {
            console.error('Failed to send message:', error);
        }
    };

    const handleRestartConversation = () => {
        setIsSubmitted(false);
        setIsOffline(false);
        setShowEmailForm(false);
        setMessages([]);
    };

    return (
        <div
            className="fixed bottom-5 right-5 w-[24rem] h-[40rem] bg-white shadow-2xl rounded-2xl flex flex-col z-[9999] animate-slide-fade-in">
            <div
                className="flex items-center justify-between p-4 bg-gradient-to-r from-blue-700 to-blue-800 text-white rounded-t-2xl shadow-lg animate-fade-down">
                <div className="flex items-center space-x-3">
                    <button className="text-2xl hover:bg-blue-600 rounded-full p-1 transition" aria-label="Back">
                        <FiArrowLeft/>
                    </button>
                    <img src="/src/assets/images/Icons/KoiExpress1.webp" alt="Chat Icon"
                         className="w-8 h-8 rounded-full bg-white p-1 shadow-md"/>
                    <div>
                        <h2 className="text-lg font-semibold">Koi Express</h2>
                        <p className="text-xs text-blue-200">You are chatting with Koi Express</p>
                    </div>
                </div>
                <button onClick={onClose} className="text-2xl hover:bg-blue-600 rounded-full p-1 transition"
                        aria-label="Close Chat">
                    <FiX/>
                </button>
            </div>

            <div className="flex-1 p-4 overflow-y-auto space-y-4">
                {typedMessages.map((msg, index) => (
                    <div key={index} className="flex items-start space-x-2 animate-slide-right">
                        <img src="/src/assets/images/Icons/KoiExpress1.webp" alt="Agent Icon"
                             className="w-6 h-6 rounded-full bg-white shadow-md"/>
                        <div
                            className="bg-blue-50 text-blue-900 p-3 rounded-2xl shadow-inner max-w-[80%] border border-blue-100">
                            <p>{msg}</p>
                            <p className="text-xs text-blue-500 mt-1">{formatTime(new Date())}</p>
                        </div>
                    </div>
                ))}

                {messages.map((message, index) => {
                    const previousMessage = messages[index - 1];
                    const showTimestamp = previousMessage ? (new Date(message.timestamp) - new Date(previousMessage.timestamp)) > 60000 : true;
                    const isSender = message.senderName === 'You';
                    const messageLength = message.text.length;

                    let backgroundColorClass;
                    if (messageLength < 30) {
                        backgroundColorClass = isSender ? 'bg-blue-500 text-white' : 'bg-gray-300 text-black';
                    } else if (messageLength >= 30 && messageLength < 100) {
                        backgroundColorClass = isSender ? 'bg-blue-600 text-white' : 'bg-gray-400 text-black';
                    } else {
                        backgroundColorClass = isSender ? 'bg-gradient-to-r from-blue-500 to-blue-700 text-white' : 'bg-gradient-to-r from-gray-400 to-gray-500 text-black';
                    }

                    return (
                        <div key={message.id}>
                            {showTimestamp && (
                                <div className="text-center text-gray-500 text-sm mb-2">
                                    {new Date(message.timestamp).toLocaleTimeString([], {
                                        hour: '2-digit',
                                        minute: '2-digit'
                                    })}
                                </div>
                            )}
                            <div className={`flex ${isSender ? 'justify-end' : 'justify-start'} items-start space-x-2`}>
                                {!isSender && (
                                    <img src="/src/assets/images/Icons/KoiExpress1.webp" alt="Agent Icon"
                                         className="w-8 h-8 rounded-full bg-white shadow-md"/>
                                )}
                                <div className={`max-w-xs p-3 rounded-lg ${backgroundColorClass} shadow-md`}>
                                    <p>{message.text}</p>
                                </div>
                                {isSender && (
                                    <img src="/src/assets/images/Icons/UserIcon.webp" alt="User Icon"
                                         className="w-8 h-8 rounded-full bg-white shadow-md"/>
                                )}
                            </div>
                        </div>
                    );
                })}
                <div ref={messageEndRef}/>
            </div>

            {showInputField && (
                <div className="bg-white p-3 border-t border-gray-300 shadow-lg flex items-center justify-center">
                    <div className="flex items-center border border-gray-300 rounded-full p-1 shadow-sm w-full">
                        <input
                            type="text"
                            placeholder="Type a message..."
                            value={newMessage}
                            onChange={(e) => setNewMessage(e.target.value)}
                            className="flex-1 p-2 rounded-full border-none focus:outline-none"
                        />
                        <button
                            onClick={handleSendMessage}
                            className="text-blue-600 p-2 rounded-full flex items-center justify-center ml-2"
                        >
                            <FiSend className="text-lg"/>
                        </button>
                    </div>
                </div>
            )}

            <div
                onClick={handleRestartConversation}
                className="p-3 bg-blue-900 text-white font-semibold rounded-b-2xl flex items-center justify-center text-sm cursor-pointer hover:bg-blue-800 transition duration-200 animate-fade-up"
            >
                <svg className="w-5 h-5 mr-2 text-white" fill="currentColor" viewBox="0 0 24 24">
                    <path
                        d="M12 4V1L8 5l4 4V6c3.87 0 7 3.13 7 7 0 1.65-.56 3.17-1.51 4.39l1.43 1.43C20.1 17.47 21 15.31 21 13c0-5.52-4.48-10-10-10zm-1 14c-3.87 0-7-3.13-7-7 0-1.65.56 3.17 1.51 4.39L3.51 4.61A9.976 9.976 0 002 11c0 5.52 4.48 10 10 10v3l4-4-4-4v3z"
                    />
                </svg>
                Restart conversation
            </div>
        </div>
    );
};

export default ChatWindow;
