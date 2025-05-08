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
import PetList from './PetList'; //Import Pet List 
import PetProfilePopup from './components/PetProfilePopup'; //Import Pet Profile Popup
 
 
export default function TheRoutes() {    
    return (
        <Routes>
            <Route path="/" element={<LandingPage />} />
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/forgot-password" element={<ForgotPassword />} />
            <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
            <Route path="/admin-dashboard" element={<ProtectedRoute><AdminDashboard /></ProtectedRoute>} />
            <Route path="/profile" element={<ProtectedRoute><UserProfile /></ProtectedRoute>} />
            <Route path="/edit-profile" element={<ProtectedRoute><EditUserProfile /></ProtectedRoute>} />
            <Route path="/add-pet" element={<ProtectedRoute><AddPet /></ProtectedRoute>} />
            <Route path="/messages" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
            <Route path="/messages/:threadId" element={<ProtectedRoute><Messages /></ProtectedRoute>} />
            <Route path="/pet-list" element={<ProtectedRoute><PetList /></ProtectedRoute>} />
            <Route path="/pets/:PetId" element={<PetProfilePopup />} /> 
            
           
        </Routes>
 
    );
}