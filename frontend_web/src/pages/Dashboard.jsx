import React, { useEffect, useState } from 'react';
import "../styles/home.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus, LogOut } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase"; 
import { onAuthStateChanged } from "firebase/auth";
import defaultProfile from '../assets/defaultprofileimage.png';

export default function Dashboard() {
  const navigate = useNavigate();

  const firstName = localStorage.getItem("firstName");

  const [userDetails, setUserDetails] = useState({
    fullName: '',
    email: '',
    phone: '',
    address: '',
    profileImage: ''
  });

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      const profileImage = localStorage.getItem("profileImage");

      if (user) {
        const fullName = user.displayName || localStorage.getItem("fullName");
        const email = user.email || localStorage.getItem("email");

        setUserDetails({
          fullName: fullName || '',
          email: email || '',
          phone: localStorage.getItem("phone") || '',
          address: localStorage.getItem("address") || '',
          profileImage: profileImage || ''
        });
      } else {
        setUserDetails({
          fullName: localStorage.getItem("fullName") || '',
          email: localStorage.getItem("email") || '',
          phone: localStorage.getItem("phone") || '',
          address: localStorage.getItem("address") || '',
          profileImage: profileImage || ''
        });
      }
    });

    return () => unsubscribe();
  }, []);

  const handleLogout = async () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
      await auth.signOut();
      localStorage.clear(); // clear all stored user data
      alert("You have logged out successfully!");
      navigate("/login");
    } catch (error) {
      console.error("Logout failed: ", error);
      alert("Logout failed. Please try again.");
    }
  };

  return (
    <div className="home-wrapper">
      <Banner firstName={firstName} onLogout={handleLogout} />

      <div className="main-content">
        {/* Left Sidebar */}
        <div className="sidebar">
          <Link to="/dashboard"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
        </div>

        {/* Middle Section */}
        <div className="center-content">
          <div className="home-placeholder">
            <img
              src={userDetails.profileImage || defaultProfile}
              alt="Profile"
              className="profile-pic"
              style={{ width: "120px", height: "120px", borderRadius: "50%", marginBottom: "10px" }}
            />
            <h2>Welcome, {userDetails.fullName || "User"}!</h2>
            <p>{userDetails.email}</p>
            <p>{userDetails.phone}</p>
            <p>{userDetails.address}</p>
          </div>
        </div>

        {/* Right Sidebar */}
        <div className="sidebar right">
          <Link to="/profile"><User size={20} /> Profile</Link>
          <Link to="/pet-list"><List size={20} /> My Pet List</Link>
          <Link to="/add-pet"><Plus size={20} /> Add Pet</Link>
        </div>
      </div>
    </div>
  );
}
