import React, { useState } from 'react';
import "../styles/AddPet.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';


export default function AddPet() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");

  const [petData, setPetData] = useState({
    name: '',
    breed: '',
    gender: '',
    birthday: '',
    age: '',
    weight: '',
    weightUnit: '',
    color: '',
    description: '',
    photo: '' // will hold base64 image
  });

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'birthday') {
      const today = new Date();
      const birthDate = new Date(value);
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }

      setPetData(prev => ({
        ...prev,
        birthday: value,
        age: age >= 0 ? age : '' // clear if invalid
      }));
    } else {
      setPetData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handlePhotoChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onloadend = () => {
        setPetData(prev => ({
          ...prev,
          photo: reader.result
        }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleCancel = () => {
    navigate("/dashboard");
  };

  const handleSave = (e) => {
    e.preventDefault();
  
    if (!petData.name || !petData.breed || !petData.gender) {
      alert("Please fill in at least Name, Breed, and Gender!");
      return;
    }
  
    // Get existing pets from localStorage
    const existingPets = JSON.parse(localStorage.getItem("userPets")) || [];
  
    // Add the new pet
    const updatedPets = [...existingPets, petData];
  
    // Save back to localStorage
    localStorage.setItem("userPets", JSON.stringify(updatedPets));
  
    // Show success message
    alert("Pet saved successfully!");
  
    // Navigate to profile page
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

        {/* Middle Content */}
        <div className="center-content">
          <div className="add-pet-container">

            <div className="photo-upload">
              <div className="photo-box">
                {petData.photo ? (
                  <img src={petData.photo} alt="Pet" className="uploaded-photo" />
                ) : (
                  <span>+</span>
                )}
                <input
                  type="file"
                  accept="image/*"
                  id="photo-upload-input"
                  style={{ display: "none" }}
                  onChange={handlePhotoChange}
                />
                <button
                  className="add-photo-btn"
                  onClick={() => document.getElementById('photo-upload-input').click()}
                >
                  Add photo
                </button>
              </div>
            </div>

            <div className="form-group">
              <input type="text" placeholder="Name" name="name" value={petData.name} onChange={handleInputChange} />
            </div>

            <div className="form-group">
              <input type="text" placeholder="Breed" name="breed" value={petData.breed} onChange={handleInputChange} />
            </div>

            <div className="form-group gender-group">
              <label>Gender</label>
              <label>
                <input type="radio" name="gender" value="Male" checked={petData.gender === "Male"} onChange={handleInputChange} />
                Male
              </label>
              <label>
                <input type="radio" name="gender" value="Female" checked={petData.gender === "Female"} onChange={handleInputChange} />
                Female
              </label>
            </div>

            <div className="form-group">
              <input type="date" name="birthday" value={petData.birthday} onChange={handleInputChange} />
            </div>

            <div className="form-group">
              <input type="number" placeholder="Age" name="age" value={petData.age} onChange={handleInputChange} />
            </div>

            <div className="form-group weight-group">
              <input type="number" placeholder="Weight" name="weight" value={petData.weight} onChange={handleInputChange} />
              <input type="text" placeholder="Unit (e.g. kg)" name="weightUnit" value={petData.weightUnit} onChange={handleInputChange} />
            </div>

            <div className="form-group">
              <input type="text" placeholder="Color" name="color" value={petData.color} onChange={handleInputChange} />
            </div>

            <div className="form-group">
              <textarea placeholder="Description" name="description" value={petData.description} onChange={handleInputChange} />
            </div>

            <div className="form-buttons">
              <button className="cancel-btn" onClick={handleCancel}>Cancel</button>
              <button type="button" className="save-btn" onClick={handleSave}>Save changes</button>
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
