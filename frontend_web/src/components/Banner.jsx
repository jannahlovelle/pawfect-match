import React from 'react';
import './Banner.css';
import logo from '../assets/Logo1.png';
import { Link } from 'react-router-dom'; 

export default function Banner({ firstName = "User", onLogout }) {
  return (
    <div className="banner">
      <div className="banner-left">
        <Link to="/dashboard"> 
          <img src={logo} alt="Logo" className="logo" />
        </Link>
        <span className="greeting">Hello, {firstName}!</span>
      </div>
    </div>
  );
}
