import { useState, useEffect } from 'react';
import axios from 'axios';
import { useWebSocket } from './useWebSocket';

const NotificationPopup = () => {
  const [notifications, setNotifications] = useState([]);
  const token = localStorage.getItem('token');

  // Handle new notifications
  const handleNotification = (notification) => {
    const id = Date.now(); // Unique ID for timer
    setNotifications((prev) => [...prev, { ...notification, id }]);
  };

  // Initialize WebSocket
  useWebSocket(handleNotification);

  // Auto-hide notifications after 5 seconds
  useEffect(() => {
    if (notifications.length === 0) return;

    const timers = notifications.map((notif) =>
      setTimeout(() => {
        setNotifications((prev) => prev.filter((n) => n.id !== notif.id));
      }, 5000)
    );

    return () => timers.forEach((timer) => clearTimeout(timer));
  }, [notifications]);

  // Handle booking actions
  const handleBookingAction = async (bookingId, action) => {
    try {
      await axios.post(
        `${import.meta.env.VITE_API_URL}/api/bookings/${bookingId}/${action}`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      setNotifications((prev) => prev.filter((n) => n.link !== bookingId));
    } catch (error) {
      alert(`Failed to ${action} booking: ${error.response?.data?.message || error.message}`);
    }
  };

  return (
    <div className="notification-popup">
      {notifications.map((notification) => (
        <div key={notification.id} className="notification">
          <p>{notification.message}</p>
          {notification.type === 'BOOKING_REQUEST' && (
            <div>
              <button
                onClick={() => handleBookingAction(notification.link, 'approve')}
              >
                Approve
              </button>
              <button
                onClick={() => handleBookingAction(notification.link, 'reject')}
              >
                Reject
              </button>
            </div>
          )}
        </div>
      ))}
    </div>
  );
};

export default NotificationPopup;
