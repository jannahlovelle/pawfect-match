import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import LandingPage from './LandingPage';
import Login from './Login';
import Signup from './SignUp';
import ForgotPassword from './ForgotPassword';
import AdminDashboard from './AdminDashboard';
import Dashboard from './Dashboard'; // Import your Dashboard component
import ProtectedRoute from './ProtectedRoute'; // Import your ProtectedRoute component
import UserProfile from './UserProfile'; // Import User Profile
import EditUserProfile from './EditUserProfile'; // Import EditUserProfile
import AddPet from './AddPet'; // Import AddPet
import Messages from './Messages'; // Import Messages
import BookingCalendar from './BookingCalendar'; // Import BookingCalendar
import Notifications from './Notifications'; // Import Notifications
import EditPet from './EditPet'; // Import EditPet
 
 
 
export default function TheRoutes() {    
    return (
        <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/home" element={<Dashboard />} />
            <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="/admin-dashboard" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>} />
            <Route path="/profile" element={<ProtectedRoute><UserProfile /></ProtectedRoute>} />
            <Route path="/edit-profile" element={<ProtectedRoute><EditUserProfile /></ProtectedRoute>} />
            <Route path="/add-pet" element={<ProtectedRoute><AddPet /></ProtectedRoute>} />
            <Route path="/messages" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
            <Route path="/messages/:threadId" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
            <Route path="/bookings" element={<ProtectedRoute><BookingCalendar /></ProtectedRoute>} />
            <Route path="/booking" element={<ProtectedRoute><BookingCalendar /></ProtectedRoute>} />
            <Route path="/notifications" element={<ProtectedRoute><Notifications /></ProtectedRoute>} />
            <Route path="/edit-pet/:petId" element={<ProtectedRoute><EditPet /></ProtectedRoute>} />
        </Routes>
 
    );
}