import React, { useEffect, useState } from 'react';
import "../styles/UserProfile.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase";
import { onAuthStateChanged } from "firebase/auth";
import defaultProfile from '../assets/defaultprofileimage.png';

export default function UserProfile() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");

  const [userDetails, setUserDetails] = useState({
    fullName: '',
    email: '',
    phone: '',
    address: '',
    profileImage: ''
  });

  const [userPets, setUserPets] = useState([]); // changed from petData (single) to userPets (array)

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

    // Load userPets from localStorage
    const savedPets = JSON.parse(localStorage.getItem("userPets")) || [];
    setUserPets(savedPets);

    return () => unsubscribe();
  }, []);

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
                <img
                  src={userDetails.profileImage || defaultProfile}
                  alt="Profile"
                  className="profile-pic"
                />
                <Link to="/edit-profile" className="edit-profile-link">Edit Profile</Link>
              </div>

              {/* Right - Info */}
              <div className="profile-right profile-info">
                <h2>{userDetails.fullName}</h2>
                <p>{userDetails.email}</p>
                <p>{userDetails.phone}</p>
                <p>{userDetails.address}</p>
              </div>
            </div>

            <hr className="divider" />

            {/* Pet Gallery */}
            <div className="pet-gallery">
              <div className="add-pet-box">
                <Link to="/add-pet">
                  <button className="add-pet-btn">+ Add Pet</button>
                </Link>
              </div>

              {/* Show saved pets here */}
              {userPets.length > 0 ? (
                userPets.map((pet, index) => (
                  <div key={index} className="pet-card">
                    <img
                      src={pet.photo}
                      alt={pet.name}
                      className="pet-image"
                    />
                    <h3>{pet.name}</h3>
                    <p>{pet.breed}</p>
                  </div>
                ))
              ) : (
                <p>No pets added yet.</p>
              )}
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