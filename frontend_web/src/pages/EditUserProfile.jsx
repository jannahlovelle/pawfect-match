import React, { useEffect, useState } from 'react';
import "../styles/EditUserProfile.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import defaultProfilePic from '../assets/defaultprofileimage.png';
import { onAuthStateChanged } from "firebase/auth"; // 🔥 added
import { auth } from "../firebase"; // 🔥 added

export default function EditUserProfile() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");
  const [selectedImage, setSelectedImage] = useState(null);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: ''
  });

  // Load image and form data
  useEffect(() => {
    const savedImage = localStorage.getItem("profileImage");
    if (savedImage) {
      setSelectedImage(savedImage);
    }

    const savedFormData = {
      firstName: localStorage.getItem("firstName") || '',
      lastName: localStorage.getItem("lastName") || '',
      email: localStorage.getItem("email") || '',
      phone: localStorage.getItem("phone") || '',
      address: localStorage.getItem("address") || ''
    };

    onAuthStateChanged(auth, (user) => {
      if (user) {
        setFormData({
          firstName: savedFormData.firstName || (user.displayName?.split(' ')[0] || ''),
          lastName: savedFormData.lastName || (user.displayName?.split(' ')[1] || ''),
          email: savedFormData.email || (user.email || ''),
          phone: savedFormData.phone,
          address: savedFormData.address
        });
      } else {
        setFormData(savedFormData);
      }
    });
  }, []);

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setSelectedImage(reader.result);
        localStorage.setItem("profileImage", reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prevData => ({
      ...prevData,
      [name]: value
    }));
  };

  const handleCancel = () => {
    navigate("/profile");
  };

  const handleSave = (e) => {
    e.preventDefault();
    // Save to localStorage
    Object.keys(formData).forEach(key => {
      localStorage.setItem(key, formData[key]);
    });

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
            <img 
              src={selectedImage || defaultProfilePic} 
              alt="Profile" 
              className="profile-pic" 
            />
            <label className="change-photo-btn">
              Change photo
              <input 
                type="file" 
                accept="image/*" 
                onChange={handleImageChange} 
                style={{ display: 'none' }} 
              />
            </label>

            <form onSubmit={handleSave} className="form-group">
              <input
                type="text"
                name="firstName"
                placeholder="Enter first name"
                value={formData.firstName}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="lastName"
                placeholder="Enter last name"
                value={formData.lastName}
                onChange={handleChange}
                required
              />
              <input
                type="email"
                name="email"
                placeholder="Enter email address"
                value={formData.email}
                onChange={handleChange}
                required
              />
              <input
                type="tel"
                name="phone"
                placeholder="Enter phone number"
                value={formData.phone}
                onChange={handleChange}
                required
              />
              <input
                type="text"
                name="address"
                placeholder="Enter address"
                value={formData.address}
                onChange={handleChange}
                required
              />

              <div className="form-buttons">
                <button type="button" className="cancel-btn" onClick={handleCancel}>Cancel</button>
                <button type="submit" className="save-btn">Save changes</button>
              </div>
            </form>
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
