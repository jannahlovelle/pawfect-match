import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Bell } from 'lucide-react';

const Notifications = () => {
  const [notifications, setNotifications] = useState([]);

  // Fetch notifications on component mount
  useEffect(() => {
    const fetchNotifications = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/api/notifications', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setNotifications(response.data);
      } catch (error) {
        console.error('Error fetching notifications:', error);
      }
    };
    fetchNotifications();
  }, []);

  // Handle approve/reject actions
  const handleAction = async (bookingId, action) => {
    try {
      const token = localStorage.getItem('token');
      await axios.post(`http://localhost:8080/api/bookings/${bookingId}/${action}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setNotifications(notifications.filter(n => n.link !== bookingId));
    } catch (error) {
      console.error(`Error ${action} booking:`, error);
    }
  };

  return (
    <div style={{ padding: '20px' }}>
      <h2><Bell size={24} /> Notifications</h2>
      {notifications.length === 0 ? (
        <p>No notifications.</p>
      ) : (
        <ul style={{ listStyle: 'none', padding: 0 }}>
          {notifications.map(notification => (
            <li
              key={notification.notificationId}
              style={{
                padding: '10px',
                borderBottom: '1px solid #ccc',
                backgroundColor: notification.read ? '#fff' : '#f0f0f0',
              }}
            >
              <p>{notification.message}</p>
              {notification.type === 'BOOKING_REQUEST' && (
                <div>
                  <button
                    onClick={() => handleAction(notification.link, 'approve')}
                    style={{ marginRight: '10px', padding: '5px 10px' }}
                  >
                    Approve
                  </button>
                  <button
                    onClick={() => handleAction(notification.link, 'reject')}
                    style={{ padding: '5px 10px' }}
                  >
                    Reject
                  </button>
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
};

export default Notifications;