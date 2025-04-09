import React from 'react';
import './Banner.css';
import logo from '../assets/logo2.png';
import signoutIcon from '../assets/signoutIcon.png';

export default function Banner({ firstName = "IamDogLover" }) {
  return (
    <div className="banner">
      <div className="banner-left">
        <img src={logo} alt="Logo" className="logo" />
        <span className="greeting">Hello, {firstName}!</span>
      </div>
      <button className="signout-button">
        <img src={signoutIcon} alt="Sign Out" className="signout-icon" />
      </button>
    </div>
  );
}
