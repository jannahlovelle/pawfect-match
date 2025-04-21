import React from 'react';
import "../styles/EditUserProfile.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import profilePic from '../assets/profilepicture.png';

export default function EditUserProfile() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");

  const handleCancel = () => {
    navigate("/profile");
  };

  const handleSave = () => {
    // Save logic here
    alert("Profile changes saved!");
    navigate("/profile");
  };

  return (
    <div className="home-wrapper"> 
      <Banner firstName={firstName} />

      <div className="main-content">
        {/* Left Sidebar */}
        <div className="sidebar">
          <Link to="/home"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
        </div>

        {/* Center - Editable Profile */}
        <div className="center-content">
          <div className="edit-profile-container">
            <img src={profilePic} alt="Profile" className="profile-pic" />
            <button className="change-photo-btn">Change photo</button>

            <div className="form-group">
              <input type="text" placeholder="Alyssa Blanche Alivio" />
            </div>
            <div className="form-group">
              <input type="email" placeholder="alyssablanchealivio@gmail.com" />
            </div>
            <div className="form-group">
              <input type="text" placeholder="+63 932 212 9876" />
            </div>
            <div className="form-group">
              <input type="text" placeholder="Salvador Ext., Labangon, Cebu City" />
            </div>

            <div className="form-buttons">
              <button className="cancel-btn" onClick={handleCancel}>Cancel</button>
              <button className="save-btn" onClick={handleSave}>Save changes</button>
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
