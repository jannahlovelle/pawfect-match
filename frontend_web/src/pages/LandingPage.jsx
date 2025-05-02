import React from 'react';
import { Link } from 'react-router-dom';
import '../styles/LandingPage.css';
import logo from '../assets/Logo1.png';

const LandingPage = () => {
  return (
    <div className="landing-page">
      <div className="background-image" />

      <div className="foreground-content">
        <header className="navbar">
          <div className="navbar-left">
            <img src={logo} alt="Logo" className="logo" />
            <span className="site-name">Pawfect Match</span>
          </div>
          <nav className="navbar-right">
            <a href="#features">Features</a>
            <a href="#how-it-works">How It Works</a>
            <a href="#testimonials">Testimonials</a>
            <a href="#contact">Contact</a>
            <Link to="/login" className="login-btn">Log In</Link>
            <Link to="/signup" className="signup-btn">Sign Up</Link>
          </nav>
        </header>

        <div className="landing-content-container">
          <div className="hero-section">
            <button className="find-btn">Find Your Perfect Pet Match</button>
            <h1>Connect Pets with<br />Their Perfect Companions</h1>
            <p>
              Whether you're looking for a playdate, a companion, or a forever home for your furry
              friend, Pawfect Match brings pet lovers together.
            </p>
            <div className="cta-buttons">
              <button className="get-started">Get Started</button>
              <button className="learn-more">Learn More</button>
            </div>
          </div>

          {/* <div class="image-section">
            <div class="image-wrapper">
              <img src={bgpet} className="pet-image" alt="Pet" />
            </div>
          </div> */}

        </div>
      </div>
    </div>
  );
};

export default LandingPage;
