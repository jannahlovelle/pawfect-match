import React from 'react';
import './Banner.css';
import logo from '../assets/Logo1.png';
import { LogOut } from 'lucide-react';
import { Link } from 'react-router-dom'; // <-- Import Link

export default function Banner({ firstName = "User", onLogout }) {
  return (
    <div className="banner">
      <div className="banner-left">
        <Link to="/dashboard"> {/* <-- Wrap logo in Link */}
          <img src={logo} alt="Logo" className="logo" />
        </Link>
        <span className="greeting">Hello, {firstName}!</span>
      </div>
      <button className="signout-button" onClick={onLogout}>
        <LogOut className="signout-icon" size={24} />
      </button>
    </div>
  );
}
