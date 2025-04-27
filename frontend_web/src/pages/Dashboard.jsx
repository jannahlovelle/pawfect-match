import React from 'react';
import "../styles/home.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus, LogOut } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase"; 

export default function Dashboard() {
  const navigate = useNavigate();

  const firstName = localStorage.getItem("firstName"); 

  const handleLogout = async () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;
  
    try {
      await auth.signOut();
      localStorage.removeItem("token");
      localStorage.removeItem("firstName");
      localStorage.removeItem("role");
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
        {/* Left Sidebar */}
      <div className="sidebar">
          <Link to="/home"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
  
      </div>


        {/* Middle Section */}
        <div className="center-content">
          <div className="home-placeholder">This is the home page</div>
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
