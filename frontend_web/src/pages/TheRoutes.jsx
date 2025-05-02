import React from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Login from './Login';
import Signup from './SignUp'; 
import ForgotPassword from './ForgotPassword';
import AdminDashboard from './AdminDashboard';
import Dashboard from './Dashboard';
import ProtectedRoute from './ProtectedRoute';
import UserProfile from './UserProfile';
import EditUserProfile from './EditUserProfile';
import AddPet from './AddPet';
import Messages from './Messages';

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
      <Route path="/edit-profile" element={<ProtectedRoute><EditUserProfile /></ProtectedRoute>} />
      <Route path="/add-pet" element={<ProtectedRoute><AddPet /></ProtectedRoute>} />
      <Route path="/messages" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
      <Route path="/messages/:threadId" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
    </Routes>
  );
}