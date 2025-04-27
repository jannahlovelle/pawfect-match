import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
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



export default function TheRoutes() {    
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/login" />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/home" element={<Dashboard />} />
            <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="/admin-dashboard" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>} />
            <Route path="/profile" element={<ProtectedRoute><UserProfile /></ProtectedRoute>} />
            <Route path="/edit-profile" element={<EditUserProfile />} />
            <Route path="/add-pet" element={<AddPet />} />
            <Route path="/messages" element={<Messages />} />
        </Routes>

    );
}