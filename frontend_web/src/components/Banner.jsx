import React from 'react';
import './Banner.css';
import logo from '../assets/Logo1.png';
import { LogOut } from 'lucide-react';

export default function Banner({ firstName = "IamDogLover", onLogout }) {
  return (
    <div className="banner">
      <div className="banner-left">
        <img src={logo} alt="Logo" className="logo" />
        <span className="greeting">Hello, {firstName}!</span>
      </div>
      <button className="signout-button" onClick={onLogout}>
        <LogOut className="signout-icon" size={24} />
      </button>
    </div>
  );
}
