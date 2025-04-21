import React from 'react';
import "../styles/UserProfile.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase";   
import profilePic from '../assets/profilepicture.png';

export default function Dashboard() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");

  const handleLogout = async () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
      await auth.signOut();
      localStorage.clear();
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
          <Link to="/home"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
        </div>

        {/* Center - Profile */}
        <div className="center-content">
          <div className="profile-container">
            <div className="profile-content">
              {/* Left - Image + Edit */}
              <div className="profile-left">
                <img src={profilePic} alt="Profile" className="profile-pic" />
                <Link to="/edit-profile" className="edit-profile-link">Edit Profile</Link>
              </div>

              {/* Right - Info */}
              <div className="profile-right profile-info">
                <h2>Alyssa Blanche Alivio</h2>
                <p>alyssablanchealivio@gmail.com</p>
                <p>+63 932 212 9876</p>
                <p>Salvador Ext., Labangon, Cebu City</p>
              </div>
            </div>

            <hr className="divider" />

            <div className="pet-gallery">
            <div className="add-pet-box">
              <Link to="/add-pet">
                <button className="add-pet-btn">+ Add Pet</button>
              </Link>
            </div>
              {/* Future pet images */}
              {/* <img src="..." alt="Pet" className="pet-image" /> */}
            </div>
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
