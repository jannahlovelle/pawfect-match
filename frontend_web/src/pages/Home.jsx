import React from 'react';
import "../styles/home.css";
import Banner from './components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Home() {
  return (
    <div className="home-wrapper">
      <Banner firstName="IamDogLover" />

      <div className="main-content">
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
