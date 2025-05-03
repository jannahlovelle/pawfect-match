import React, { useEffect, useState } from 'react';
import "../styles/UserProfile.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase";
import { signOut } from "firebase/auth";
import defaultProfile from '../assets/defaultprofileimage.png';

export default function UserProfile() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState({
    userId: '',
    fullName: '',
    email: '',
    phone: '',
    address: '',
    profileImage: ''
  });
  const [userPets, setUserPets] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [petsLoading, setPetsLoading] = useState(false);

  useEffect(() => {
    const fetchUserProfile = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const profileResponse = await fetch(`${import.meta.env.VITE_API_URL}/users/me`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!profileResponse.ok) {
          if (profileResponse.status === 401) {
            localStorage.removeItem("token");
            navigate("/login");
            throw new Error("Session expired. Please log in again.");
          }
          const errorData = await profileResponse.json();
          throw new Error(errorData.message || "Failed to fetch user profile");
        }

        const userProfile = await profileResponse.json();
        setUserDetails({
          userId: userProfile.user?.userID || '',
          fullName: `${userProfile.user?.firstName || ''} ${userProfile.user?.lastName || ''}`.trim(),
          email: userProfile.user?.email || '',
          phone: userProfile.user?.phone || '',
          address: userProfile.user?.address || '',
          profileImage: userProfile.user?.profilePicture || defaultProfile
        });

      } catch (err) {
        console.error("Error fetching profile:", err);
        setError(err.message);
      } finally {
        setLoading(false);
      }
    };

    fetchUserProfile();
  }, [navigate]);

  useEffect(() => {
    const fetchUserPets = async () => {
      const token = localStorage.getItem("token");
      if (!token) return;

      setPetsLoading(true);
      try {
        const petsResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/my-pets`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!petsResponse.ok) {
          if (petsResponse.status === 401) {
            localStorage.removeItem("token");
            navigate("/login");
            throw new Error("Session expired. Please log in again.");
          }
          const errorData = await petsResponse.json();
          throw new Error(errorData.message || "Failed to fetch user pets");
        }

        const petsData = await petsResponse.json();
        
        const petsWithPhotos = await Promise.all(
          petsData.map(async (pet) => {
            try {
              const photosResponse = await fetch(
                `${import.meta.env.VITE_API_URL}/pets/${pet.petId}/photos`,
                {
                  headers: {
                    'Authorization': `Bearer ${token}`,
                    'Content-Type': 'application/json'
                  }
                }
              );

              if (!photosResponse.ok) {
                return { ...pet, photo: defaultProfile };
              }

              const photosData = await photosResponse.json();
              return {
                ...pet,
                photo: photosData.length > 0 ? photosData[0].url : defaultProfile
              };
            } catch (err) {
              return { ...pet, photo: defaultProfile };
            }
          })
        );

        setUserPets(petsWithPhotos);
      } catch (err) {
        console.error("Error fetching pets:", err);
        setError(err.message);
      } finally {
        setPetsLoading(false);
      }
    };

    fetchUserPets();
  }, [navigate]);

  const handleLogout = async () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
      await signOut(auth);
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
      <Banner firstName={userDetails.fullName.split(' ')[0] || 'User'} onLogout={handleLogout} />

      <div className="main-content">

        {/* Left Sidebar */}
        <div className="sidebar left">
          <Link to="/dashboard"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
        </div>

        {/* Center Content */}
        <div className="center-content">
          {loading ? (
            <div className="loading-spinner">Loading profile...</div>
          ) : error ? (
            <p className="error-message">{error}</p>
          ) : (
            <div className="profile-container">
              {/* Fixed User Profile Section */}
              <div className="profile-header">
                <div className="profile-image-container">
                  <img
                    src={userDetails.profileImage}
                    alt="Profile"
                    className="profile-image"
                    onError={(e) => {
                      e.target.src = defaultProfile;
                    }}
                  />
                  <Link to="/edit-profile" className="edit-profile-button">
                    Edit Profile
                  </Link>
                </div>
                <div className="profile-info">
                  <h2 className="profile-name">{userDetails.fullName}</h2>
                  <p className="profile-email">{userDetails.email}</p>
                  <p className="profile-phone">
                    <span className="info-label">Phone:</span> {userDetails.phone || 'Not provided'}
                  </p>
                  <p className="profile-address">
                    <span className="info-label">Address:</span> {userDetails.address || 'Not provided'}
                  </p>
                </div>
              </div>

              {/* Scrollable Pets Section */}
              <div className="pets-section-container">
                <div className="section-header">
                  <h3>My Pets</h3>
                  <Link to="/add-pet" className="add-pet-button">
                    <Plus size={18} /> Add Pet
                  </Link>
                </div>

                <div className="pets-scrollable-content">
                  {petsLoading ? (
                    <div className="loading-spinner">Loading pets...</div>
                  ) : userPets.length > 0 ? (
                    <div className="pets-grid">
                      {userPets.map((pet) => (
                        <div key={pet.petId} className="pet-card">
                          <div className="pet-image-container">
                            <img
                              src={pet.photo}
                              alt={pet.name}
                              className="pet-image"
                              onError={(e) => {
                                e.target.src = defaultProfile;
                              }}
                            />
                          </div>
                          <div className="pet-info">
                            <h4 className="pet-name">{pet.name}</h4>
                            <p className="pet-details">
                              <span className="pet-breed">{pet.breed}</span>
                              {pet.species && <span className="pet-species"> • {pet.species}</span>}
                            </p>
                            {pet.age && <p className="pet-age">{pet.age} years old</p>}
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="no-pets-message">
                      <p>You haven't added any pets yet.</p>
                      <Link to="/add-pet" className="add-pet-link">
                        Add your first pet
                      </Link>
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}
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