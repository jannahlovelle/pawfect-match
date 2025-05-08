import React, { useState, useEffect } from 'react';
import { Calendar, momentLocalizer } from 'react-big-calendar';
import moment from 'moment';
import axios from 'axios';
import { X, History } from 'lucide-react';
import "../styles/BookingCalendar.css";
import 'react-big-calendar/lib/css/react-big-calendar.css';

const localizer = momentLocalizer(moment);

const BookingCalendar = () => {
  const [events, setEvents] = useState([]);
  const [pendingBookings, setPendingBookings] = useState([]);
  const [bookingHistory, setBookingHistory] = useState([]);
  const [newBooking, setNewBooking] = useState({
    petId: '',
    date: '',
    title: '',
    status: 'PENDING',
  });
  const [isHistoryModalOpen, setIsHistoryModalOpen] = useState(false);

  // Fetch bookings and history on component mount
  useEffect(() => {
    const fetchBookings = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/api/bookings', {
          headers: { Authorization: `Bearer ${token}` },
        });
        const confirmed = response.data
          .filter(booking => booking.status === 'CONFIRMED')
          .map(booking => ({
            id: booking.bookingId,
            title: booking.title,
            start: new Date(booking.date),
            end: new Date(booking.date),
            petId: booking.petId,
          }));
        const pending = response.data.filter(booking => booking.status === 'PENDING');
        setEvents(confirmed);
        setPendingBookings(pending);
      } catch (error) {
        console.error('Error fetching bookings:', error);
      }
    };

    const fetchBookingHistory = async () => {
      try {
        const token = localStorage.getItem('token');
        const response = await axios.get('http://localhost:8080/api/bookings/history', {
          headers: { Authorization: `Bearer ${token}` },
        });
        setBookingHistory(response.data);
      } catch (error) {
        console.error('Error fetching booking history:', error);
      }
    };

    fetchBookings();
    fetchBookingHistory();
  }, []);

  // Handle form input changes
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewBooking({ ...newBooking, [name]: value });
  };

  // Handle form submission to create a booking
  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const token = localStorage.getItem('token');
      const bookingData = {
        ...newBooking,
        date: new Date(newBooking.date).toISOString(),
      };
      const response = await axios.post('http://localhost:8080/api/bookings', bookingData, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setPendingBookings([...pendingBookings, response.data]);
      setBookingHistory([...bookingHistory, response.data]);
      setNewBooking({ petId: '', date: '', title: '', status: 'PENDING' });
    } catch (error) {
      console.error('Error creating booking:', error);
    }
  };

  // Toggle history modal
  const toggleHistoryModal = () => {
    setIsHistoryModalOpen(!isHistoryModalOpen);
  };

  return (
    <div className="booking-calendar-container">
      <h2 className="section-title">Book an Appointment</h2>
      <div className="booking-card">
        <form onSubmit={handleSubmit} className="booking-form">
          <div className="form-group">
            <label htmlFor="petId">Pet ID</label>
            <input
              type="text"
              id="petId"
              name="petId"
              value={newBooking.petId}
              onChange={handleInputChange}
              required
              placeholder="Enter Pet ID"
            />
          </div>
          <div className="form-group">
            <label htmlFor="date">Date and Time</label>
            <input
              type="datetime-local"
              id="date"
              name="date"
              value={newBooking.date}
              onChange={handleInputChange}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="title">Title</label>
            <input
              type="text"
              id="title"
              name="title"
              value={newBooking.title}
              onChange={handleInputChange}
              required
              placeholder="e.g., Breeding Appointment"
            />
          </div>
          <button type="submit" className="submit-button">Request Booking</button>
        </form>
      </div>

      <h2 className="section-title">Pending Bookings</h2>
      <div className="booking-card">
        {pendingBookings.length === 0 ? (
          <p className="no-data">No pending bookings.</p>
        ) : (
          <ul className="booking-list">
            {pendingBookings.map(booking => (
              <li key={booking.bookingId} className="booking-item">
                <span>{booking.title}</span>
                <span>{new Date(booking.date).toLocaleString()}</span>
                <span>Pet ID: {booking.petId}</span>
                <span className={`status ${booking.status.toLowerCase()}`}>
                  {booking.status}
                </span>
              </li>
            ))}
          </ul>
        )}
      </div>

      <h2 className="section-title">Confirmed Bookings</h2>
      <div className="booking-card">
        <Calendar
          localizer={localizer}
          events={events}
          startAccessor="start"
          endAccessor="end"
          className="booking-calendar"
        />
      </div>

      <button onClick={toggleHistoryModal} className="history-button">
        <History size={20} /> View History
      </button>

      {isHistoryModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3>Booking History</h3>
              <button onClick={toggleHistoryModal} className="close-button">
                <X size={20} />
              </button>
            </div>
            {bookingHistory.length === 0 ? (
              <p className="no-data">No booking history.</p>
            ) : (
              <table className="history-table">
                <thead>
                  <tr>
                    <th>Title</th>
                    <th>Date</th>
                    <th>Pet ID</th>
                    <th>Status</th>
                    <th>Role</th>
                  </tr>
                </thead>
                <tbody>
                  {bookingHistory.map(booking => (
                    <tr key={booking.bookingId}>
                      <td>{booking.title}</td>
                      <td>{new Date(booking.date).toLocaleString()}</td>
                      <td>{booking.petId}</td>
                      <td className={`status ${booking.status.toLowerCase()}`}>
                        {booking.status}
                      </td>
                      <td>
                        {booking.userId === localStorage.getItem('userId') ? 'Requester' : 'Owner'}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default BookingCalendar;