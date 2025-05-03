import React, { useEffect, useState, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { Client } from '@stomp/stompjs';
import '../styles/Messages.css';
import { ArrowLeft, UserPlus, Plus, Send, LogOut, User } from 'lucide-react';
import logo from '../assets/logo1.png';

export default function Messages() {
  const { threadId } = useParams();
  const navigate = useNavigate();
  const [threads, setThreads] = useState([]);
  const [messages, setMessages] = useState([]);
  const [newMessage, setNewMessage] = useState('');
  const [client, setClient] = useState(null);
  const [userInfo, setUserInfo] = useState({ name: 'Loading...', location: '' });
  const [connectionError, setConnectionError] = useState(null);
  const token = localStorage.getItem('token');
  const messagesEndRef = useRef(null);

  // Handle logout
  const handleLogout = () => {
    localStorage.clear();
    alert('You have logged out successfully!');
    navigate('/login');
  };

  // Fetch user's threads
  useEffect(() => {
    if (!token) {
      navigate('/login');
      return;
    }

    async function fetchThreads() {
      try {
        const response = await fetch('http://localhost:8080/api/chat/threads', {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.ok) {
          const data = await response.json();
          setThreads(data);
        } else {
          console.error('Failed to fetch threads:', response.status);
          setConnectionError('Failed to load conversations. Please try again.');
        }
      } catch (error) {
        console.error('Error fetching threads:', error);
        setConnectionError('Network error. Please check your connection.');
      }
    }

    fetchThreads();
  }, [token, navigate]);

  // Fetch messages and thread info
  useEffect(() => {
    if (!threadId || !token) return;

    async function fetchMessages() {
      try {
        const response = await fetch(`http://localhost:8080/api/chat/threads/${threadId}/messages`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.ok) {
          const data = await response.json();
          setMessages(data);
        } else {
          console.error('Failed to fetch messages:', response.status);
          setConnectionError('Failed to load messages. Please try again.');
        }
      } catch (error) {
        console.error('Error fetching messages:', error);
        setConnectionError('Network error. Please check your connection.');
      }
    }

    async function fetchThreadInfo() {
      try {
        const response = await fetch(`http://localhost:8080/api/chat/threads/${threadId}`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (response.ok) {
          const thread = await response.json();
          const participants = thread.participantIds;
          const currentUser = extractUserIdFromToken(token);
          const otherUser = participants.find(id => id !== currentUser);
          const userResponse = await fetch(`http://localhost:8080/api/users/${otherUser}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          if (userResponse.ok) {
            const userData = await userResponse.json();
            setUserInfo({
              name: userData.name || otherUser,
              location: userData.location || 'Unknown Location',
            });
          }
        } else {
          console.error('Failed to fetch thread info:', response.status);
          setUserInfo({ name: 'Unknown User', location: 'Unknown Location' });
        }
      } catch (error) {
        console.error('Error fetching thread/user info:', error);
        setUserInfo({ name: 'Unknown User', location: 'Unknown Location' });
        setConnectionError('Failed to load user info. Please try again.');
      }
    }

    async function markAsRead() {
      try {
        await fetch(`http://localhost:8080/api/chat/threads/${threadId}/read`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${token}` },
        });
      } catch (error) {
        console.error('Error marking messages as read:', error);
      }
    }

    fetchThreadInfo();
    fetchMessages();
    markAsRead();
  }, [threadId, token]);

  // WebSocket setup
  useEffect(() => {
    if (!threadId || !token) return;

    const stompClient = new Client({
      brokerURL: 'ws://localhost:8080/chat',
      connectHeaders: { Authorization: `Bearer ${token}` },
      debug: (str) => console.log(str),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      onConnect: () => {
        console.log('Connected to WebSocket');
        setConnectionError(null);
        stompClient.subscribe(`/topic/thread/${threadId}`, (message) => {
          const newMsg = JSON.parse(message.body);
          setMessages((prev) => [...prev, newMsg]);
        });
      },
      onStompError: (frame) => {
        console.error('STOMP Error:', frame);
        setConnectionError('WebSocket error. Reconnecting...');
      },
      onWebSocketError: (error) => {
        console.error('WebSocket Error:', error);
        setConnectionError('Failed to connect to chat server. Reconnecting...');
      },
    });

    stompClient.activate();
    setClient(stompClient);

    return () => {
      stompClient.deactivate();
      console.log('Disconnected from WebSocket');
    };
  }, [threadId, token]);

  // Auto-scroll to latest message
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // Send message
  async function handleSendMessage() {
    if (!newMessage.trim()) return;

    if (client && client.active) {
      try {
        client.publish({
          destination: `/app/thread/${threadId}`,
          body: JSON.stringify({ content: newMessage }),
        });
        setNewMessage('');
      } catch (error) {
        console.error('Error sending message via WebSocket:', error);
        setConnectionError('Failed to send message. Please try again.');
      }
    } else {
      console.error('STOMP client not connected, using REST fallback');
      try {
        const response = await fetch(`http://localhost:8080/test-send/${threadId}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ content: newMessage }),
        });
        if (response.ok) {
          const sentMessage = await response.json();
          setMessages((prev) => [...prev, sentMessage]);
          setNewMessage('');
        } else {
          console.error('Failed to send message via REST:', response.status);
          setConnectionError('Failed to send message. Please try again.');
        }
      } catch (error) {
        console.error('Error sending message via REST:', error);
        setConnectionError('Network error. Please check your connection.');
      }
    }
  }

  // Extract userId from token
  function extractUserIdFromToken(token) {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload).sub;
    } catch (error) {
      console.error('Error decoding token:', error);
      return null;
    }
  }

  // Navigate to a thread
  function handleThreadClick(selectedThreadId) {
    navigate(`/messages/${selectedThreadId}`);
  }

  // Render for /messages (no threadId)
  if (!threadId) {
    return (
      <div className="messages-wrapper">
        <div className="messages-header">
          <img src={logo} alt="Logo" className="header-logo" />
          <h2 className="header-title">Message a Fur Parent!</h2>
          <LogOut className="logout-icon" onClick={handleLogout} />
        </div>
        <div className="messages-main">
          <div className="sidebar-left">
            <div className="messages-title">
            <div onClick={() => navigate('/dashboard')} className="back-to-dashboard" style={{ cursor: 'pointer' }}>
              <ArrowLeft size={20} />
            </div>
              <h3>Messages</h3>
              <UserPlus size={20} />
            </div>
            <div className="thread-list">
              {connectionError ? (
                <p className="error-message">{connectionError}</p>
              ) : threads.length > 0 ? (
                threads.map((thread) => {
                  const currentUser = extractUserIdFromToken(token);
                  const otherUser = thread.participantIds.find((id) => id !== currentUser);
                  return (
                    <div
                      key={thread.threadId}
                      className={`thread-item ${thread.threadId === threadId ? 'active' : ''}`}
                      onClick={() => handleThreadClick(thread.threadId)}
                    >
                      <User className="thread-icon" size={24} />
                      <div className="thread-info">
                        <span className="thread-name">{otherUser}</span>
                        {thread.unreadCounts?.[currentUser.replace('@', '_')] > 0 && (
                          <span className="unread-count">
                            {thread.unreadCounts[currentUser.replace('@', '_')]}
                          </span>
                        )}
                      </div>
                    </div>
                  );
                })
              ) : (
                <p>No conversations yet</p>
              )}
            </div>
          </div>
          <div className="chat-container">
            <div className="no-thread-selected">
              <p>Select a conversation to start chatting</p>
            </div>
          </div>
        </div>
      </div>
    );
  }

  // Render for /messages/:threadId
  return (
    <div className="messages-wrapper">
      <div className="messages-header">
        <img src={logo} alt="Logo" className="header-logo" />
        <h2 className="header-title">Message a Fur Parent!</h2>
        <LogOut className="logout-icon" onClick={handleLogout} />
      </div>
      <div className="messages-main">
        <div className="sidebar-left">
          <div className="messages-title">
            <ArrowLeft size={20} />
            <h3>Messages</h3>
            <UserPlus size={20} />
          </div>
          <div className="thread-list">
            {connectionError ? (
              <p className="error-message">{connectionError}</p>
            ) : threads.length > 0 ? (
              threads.map((thread) => {
                const currentUser = extractUserIdFromToken(token);
                const otherUser = thread.participantIds.find((id) => id !== currentUser);
                return (
                  <div
                    key={thread.threadId}
                    className={`thread-item ${thread.threadId === threadId ? 'active' : ''}`}
                    onClick={() => handleThreadClick(thread.threadId)}
                  >
                    <User className="thread-icon" size={24} />
                    <div className="thread-info">
                      <span className="thread-name">{otherUser}</span>
                      {thread.unreadCounts?.[currentUser.replace('@', '_')] > 0 && (
                        <span className="unread-count">
                          {thread.unreadCounts[currentUser.replace('@', '_')]}
                        </span>
                      )}
                    </div>
                  </div>
                );
              })
            ) : (
              <p>No conversations yet</p>
            )}
          </div>
        </div>
        <div className="chat-container">
          <div className="chat-header">
            <div className="chat-user-info">
              <strong>{userInfo.name}</strong>
              <p>{userInfo.location}</p>
            </div>
          </div>
          <div className="chat-messages">
            {connectionError ? (
              <p className="error-message">{connectionError}</p>
            ) : (
              messages.map((msg) => (
                <div
                  key={msg.messageId}
                  className={`message ${
                    msg.senderEmail === extractUserIdFromToken(token) ? 'sent' : 'received'
                  }`}
                >
                  <p>{msg.content}</p>
                  <span className="timestamp">{new Date(msg.sentAt).toLocaleTimeString()}</span>
                </div>
              ))
            )}
            <div ref={messagesEndRef} />
          </div>
          <div className="message-input-bar">
            <Plus className="plus-icon" />
            <input
              type="text"
              placeholder="Write a message"
              className="message-input"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              onKeyPress={(e) => e.key === 'Enter' && handleSendMessage()}
            />
            <Send className="send-icon" onClick={handleSendMessage} />
          </div>
        </div>
      </div>
    </div>
  );
}