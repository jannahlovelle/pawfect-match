import { useEffect, useRef } from 'react';
import { Stomp } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { jwtDecode } from 'jwt-decode';

export const useWebSocket = (onNotificationReceived) => {
  const stompClientRef = useRef(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) {
      console.warn('No token found, skipping WebSocket connection');
      return;
    }

    let decoded;
    try {
      decoded = jwtDecode(token);
      if (decoded.exp * 1000 < Date.now()) {
        console.warn('Token expired, skipping WebSocket connection');
        return;
      }
    } catch (error) {
      console.error('Invalid token:', error);
      return;
    }

    const userId = decoded.sub;
    console.log('Connecting WebSocket for user ID:', userId);

    // Replace localhost with VITE_API_URL from the environment
    const socketUrl = `${import.meta.env.VITE_API_URL || 'http://localhost:8080'}/ws`;

    const socket = new SockJS(socketUrl);
    stompClientRef.current = Stomp.over(socket);

    const connect = () => {
      stompClientRef.current.connect(
        { Authorization: `Bearer ${token}` },
        () => {
          console.log('WebSocket connected successfully');
          stompClientRef.current.subscribe(`/user/queue/notifications`, (message) => {
            try {
              const payload = JSON.parse(message.body);
              console.log('Received notification:', payload);
              onNotificationReceived(payload);
            } catch (error) {
              console.error('Error parsing message:', error);
            }
          });
        },
        (error) => {
          console.error('WebSocket connection error:', error);
          // Retry connection after 5 seconds
          setTimeout(connect, 5000);
        }
      );
    };

    connect();

    return () => {
      if (stompClientRef.current) {
        console.log('Disconnecting WebSocket');
        stompClientRef.current.disconnect();
        stompClientRef.current = null;
      }
    };
  }, [onNotificationReceived]);
};
