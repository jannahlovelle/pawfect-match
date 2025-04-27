import React from 'react';
import '../styles/Messages.css'; // make sure to create and style this CSS
import { ArrowLeft, UserPlus, Plus, Send, LogOut } from 'lucide-react';
import logo from '../assets/logo2.png';

export default function Messages() {
  return (
    <div className="messages-wrapper">
      {/* Top Bar */}
      <div className="messages-header">
        <img src={logo} alt="Logo" className="header-logo" />
        <h2 className="header-title">Message a Fur Parent!</h2>
        <LogOut className="logout-icon" />
      </div>

      {/* Main Content */}
      <div className="messages-main">
        {/* Left Sidebar */}
        <div className="sidebar-left">
          <div className="messages-title">
            <ArrowLeft size={20} />
            <h3>Messages</h3>
            <UserPlus size={20} />
          </div>
        </div>

        {/* Chat Area */}
        <div className="chat-container">
          {/* Top User Info */}
          <div className="chat-header">
            {/* <img src={kathyPic} alt="KathyB" className="chat-avatar" /> */}
            <div className="chat-user-info">
              <strong>KathyB</strong>
              <p>Cogon Pardo, Cebu City</p>
            </div>
          </div>

          {/* Messages Area */}
          <div className="chat-messages">
            {/* You can map messages here */}
          </div>

          {/* Message Input */}
          <div className="message-input-bar">
            <Plus className="plus-icon" />
            <input type="text" placeholder="Write a message" className="message-input" />
            <Send className="send-icon" />
          </div>
        </div>
      </div>
    </div>
  );
}
