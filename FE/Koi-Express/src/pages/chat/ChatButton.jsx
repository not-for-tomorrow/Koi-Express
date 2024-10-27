import React from "react";
import PropTypes from "prop-types";
import "/src/css/ChatButton.css";

const koiFishImage = new URL("../../assets/images/Icons/KoiExpress1.webp", import.meta.url).href;

const ChatButton = ({ onClick }) => (
    <div className="fixed bottom-5 right-5">
        <button onClick={onClick} className="chat-button rounded-full shadow-lg bg-white p-2">
            <img src={koiFishImage} alt="Koi Fish" className="animated-icon" style={{ height: "50px", width: "50px" }} />
        </button>
    </div>
);

ChatButton.propTypes = {
    onClick: PropTypes.func.isRequired, // Validate onClick as a required function
};

export default ChatButton;
