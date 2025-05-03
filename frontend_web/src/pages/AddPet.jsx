import React, { useState } from 'react';
import "../styles/AddPet.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus, Camera } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';

export default function AddPet() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");
  const token = localStorage.getItem("token");

  const [petData, setPetData] = useState({
    name: '',
    species: 'Dog',
    breed: '',
    gender: '',
    dateOfBirth: '',
    age: '',
    weight: '',
    weightUnit: 'kg',
    color: '',
    description: '',
    availabilityStatus: 'available',
    price: '',
    pedigreeInfo: '',
    healthStatus: ''
  });
  
  const [photoFile, setPhotoFile] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    
    if (name === 'dateOfBirth') {
      const today = new Date();
      const birthDate = new Date(value);
      let age = today.getFullYear() - birthDate.getFullYear();
      const monthDiff = today.getMonth() - birthDate.getMonth();
      
      if (monthDiff < 0 || (monthDiff === 0 && today.getDate() < birthDate.getDate())) {
        age--;
      }

      setPetData(prev => ({
        ...prev,
        dateOfBirth: value,
        age: age >= 0 ? age.toString() : ''
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
      const validTypes = ['image/jpeg', 'image/png'];
      if (!validTypes.includes(file.type)) {
        setError("Please upload a JPEG or PNG image.");
        return;
      }
      if (file.size > 5 * 1024 * 1024) {
        setError("Image size exceeds 5MB limit.");
        return;
      }
      setPhotoFile(file);
      setError(null);
    }
  };

  const handleCancel = () => {
    navigate("/profile");
  };

  const handleSave = async (e) => {
    e.preventDefault();
  
    if (!petData.name || !petData.breed || !petData.gender) {
      setError("Please fill in at least Name, Breed, and Gender!");
      return;
    }
  
    if (!token) {
      navigate("/login");
      return;
    }
  
    setIsLoading(true);
    setError(null);
  
    try {
      const weightValue = petData.weight ? parseFloat(petData.weight) : null;
      const priceValue = petData.price ? parseFloat(petData.price) : null;
      const formattedDate = petData.dateOfBirth ? new Date(petData.dateOfBirth).toISOString() : null;

      const createResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/create`, {
        method: "POST",
        headers: {
          "Authorization": `Bearer ${token}`,
          "Content-Type": "application/json"
        },
        body: JSON.stringify({
          name: petData.name,
          species: petData.species,
          breed: petData.breed,
          gender: petData.gender,
          dateOfBirth: formattedDate,
          weight: weightValue,
          color: petData.color,
          description: petData.description,
          availabilityStatus: petData.availabilityStatus,
          price: priceValue,
          pedigreeInfo: petData.pedigreeInfo,
          healthStatus: petData.healthStatus
        })
      });
  
      if (!createResponse.ok) {
        const errorData = await createResponse.json();
        throw new Error(errorData.message || "Failed to create pet");
      }
  
      const createdPet = await createResponse.json();
  
      if (photoFile) {
        const formData = new FormData();
        formData.append("file", photoFile);
  
        const photoResponse = await fetch(
          `${import.meta.env.VITE_API_URL}/pets/${createdPet.petId}/photos`,
          {
            method: "POST",
            headers: {
              "Authorization": `Bearer ${token}`
            },
            body: formData
          }
        );
  
        if (!photoResponse.ok) {
          throw new Error("Failed to upload pet photo");
        }
      }
  
      navigate("/profile");
    } catch (err) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
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
            <h2 className="form-title">Add New Pet</h2>
            {error && <div className="error-message">{error}</div>}

            {/* Fixed Photo Upload Section */}
            <div className="fixed-upload-section">
              <input
                type="file"
                id="pet-photo-upload"
                accept="image/*"
                onChange={handlePhotoChange}
                style={{ display: 'none' }}
                disabled={isLoading}
              />
              <label htmlFor="pet-photo-upload" className="upload-label">
                {photoFile ? (
                  <div className="photo-preview-container">
                    <img 
                      src={URL.createObjectURL(photoFile)} 
                      alt="Pet preview" 
                      className="pet-photo-preview"
                    />
                    <div className="upload-overlay">
                      <span>Change Photo</span>
                    </div>
                  </div>
                ) : (
                  <div className="upload-placeholder">
                    <Camera size={48} className="camera-icon" />
                    <p>Click to upload pet photo</p>
                  </div>
                )}
              </label>
              <div className="upload-instructions">
                {photoFile ? (
                  <button 
                    className="remove-photo-button"
                    onClick={() => setPhotoFile(null)}
                    disabled={isLoading}
                  >
                    Remove Photo
                  </button>
                ) : (
                  <p className="file-requirements">JPEG or PNG, max 5MB</p>
                )}
              </div>
            </div>

            {/* Scrollable Form Section */}
            <div className="scrollable-form-section">
              <form className="pet-info-form" onSubmit={handleSave}>
                <div className="form-group">
                  <label>Name*</label>
                  <input 
                    type="text" 
                    name="name" 
                    value={petData.name} 
                    onChange={handleInputChange} 
                    required
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group">
                  <label>Species</label>
                  <select 
                    name="species" 
                    value={petData.species} 
                    onChange={handleInputChange}
                    disabled={isLoading}
                  >
                    <option value="Dog">Dog</option>
                    <option value="Cat">Cat</option>
                    <option value="Bird">Bird</option>
                    <option value="Other">Other</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Breed*</label>
                  <input 
                    type="text" 
                    name="breed" 
                    value={petData.breed} 
                    onChange={handleInputChange} 
                    required
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group gender-group">
                  <label>Gender*</label>
                  <div className="radio-options">
                    <label className="radio-label">
                      <input 
                        type="radio" 
                        name="gender" 
                        value="Male" 
                        checked={petData.gender === "Male"} 
                        onChange={handleInputChange} 
                        disabled={isLoading}
                      />
                      <span>Male</span>
                    </label>
                    <label className="radio-label">
                      <input 
                        type="radio" 
                        name="gender" 
                        value="Female" 
                        checked={petData.gender === "Female"} 
                        onChange={handleInputChange} 
                        disabled={isLoading}
                      />
                      <span>Female</span>
                    </label>
                  </div>
                </div>

                <div className="form-group">
                  <label>Date of Birth</label>
                  <input 
                    type="date" 
                    name="dateOfBirth" 
                    value={petData.dateOfBirth} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group">
                  <label>Age</label>
                  <input 
                    type="number" 
                    name="age" 
                    value={petData.age} 
                    onChange={handleInputChange} 
                    disabled
                  />
                </div>

                <div className="form-group">
                  <label>Weight (kg)</label>
                  <input 
                    type="number" 
                    name="weight" 
                    value={petData.weight} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                    step="0.1"
                  />
                </div>

                <div className="form-group">
                  <label>Color</label>
                  <input 
                    type="text" 
                    name="color" 
                    value={petData.color} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                  />
                </div>

                <div className="form-group full-width">
                  <label>Description</label>
                  <textarea 
                    name="description" 
                    value={petData.description} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                    rows="3"
                  />
                </div>

                <div className="form-group">
                  <label>Availability Status</label>
                  <select 
                    name="availabilityStatus" 
                    value={petData.availabilityStatus} 
                    onChange={handleInputChange}
                    disabled={isLoading}
                  >
                    <option value="available">Available</option>
                    <option value="unavailable">Unavailable</option>
                    <option value="pending">Pending</option>
                  </select>
                </div>

                <div className="form-group">
                  <label>Price (₱)</label>
                  <input 
                    type="number" 
                    name="price" 
                    value={petData.price} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                    step="0.01"
                    min="0"
                  />
                </div>

                <div className="form-group">
                  <label>Pedigree Information</label>
                  <input 
                    type="text" 
                    name="pedigreeInfo" 
                    value={petData.pedigreeInfo} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                    placeholder="e.g. AKC registered"
                  />
                </div>

                <div className="form-group">
                  <label>Health Status</label>
                  <input 
                    type="text" 
                    name="healthStatus" 
                    value={petData.healthStatus} 
                    onChange={handleInputChange} 
                    disabled={isLoading}
                    placeholder="e.g. Vaccinated and healthy"
                  />
                </div>

                <div className="form-buttons">
                  <button 
                    type="button"
                    className="cancel-btn" 
                    onClick={handleCancel}
                    disabled={isLoading}
                  >
                    Cancel
                  </button>
                  <button 
                    type="submit" 
                    className="save-btn" 
                    disabled={isLoading}
                  >
                    {isLoading ? 'Saving...' : 'Save Pet'}
                  </button>
                </div>
              </form>
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