import React, { useEffect, useState, useRef, useCallback } from 'react';
import "../styles/home.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, Plus, LogOut, Moon, Sun, Trash2, Calendar, MessageSquare, X } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase";
import { signOut } from "firebase/auth";
import defaultProfile from '../assets/defaultprofileimage.png';

export default function Dashboard() {
  const navigate = useNavigate();
  const [userDetails, setUserDetails] = useState({
    fullName: localStorage.getItem("firstName") || '',
    email: localStorage.getItem("email") || '',
    phone: localStorage.getItem("phone") || '',
    address: localStorage.getItem("address") || '',
    profileImage: localStorage.getItem("profileImage") || ''
  });

  const [pets, setPets] = useState([]);
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showSettings, setShowSettings] = useState(false);
  const [darkMode, setDarkMode] = useState(localStorage.getItem('darkMode') === 'true');
  const [selectedPet, setSelectedPet] = useState(null);
  const [showSearch, setShowSearch] = useState(false);
  const [searchQuery, setSearchQuery] = useState('');

  const observer = useRef(null);
  const loadMoreRef = useRef(null);
  const settingsRef = useRef(null);
  const modalRef = useRef(null);

  useEffect(() => {
    if (darkMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  }, [darkMode]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (settingsRef.current && !settingsRef.current.contains(event.target)) {
        setShowSettings(false);
      }
      if (modalRef.current && !modalRef.current.contains(event.target)) {
        setSelectedPet(null);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    setUserDetails({
      fullName: localStorage.getItem("firstName") || '',
      email: localStorage.getItem("email") || '',
      phone: localStorage.getItem("phone") || '',
      address: localStorage.getItem("address") || '',
      profileImage: localStorage.getItem("profileImage") || ''
    });
  }, [navigate]);

  const fetchPets = useCallback(async (pageToFetch) => {
    if (loading || !hasMore) return;

    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("No authentication token found");

      const response = await fetch(`${import.meta.env.VITE_API_URL}/pets/feed?page=${pageToFetch}&size=${size}`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const errorData = await response.json();
        if (response.status === 401) {
          localStorage.removeItem("token");
          navigate("/login");
          throw new Error("Session expired. Please log in again.");
        }
        throw new Error(errorData.message || "Failed to fetch pet feed");
      }

      const data = await response.json();
      setPets((prevPets) => {
        const existingIds = new Set(prevPets.map((pet) => pet.petId));
        const newPets = data.filter((pet) => !existingIds.has(pet.petId));
        return [...prevPets, ...newPets];
      });

      setHasMore(data.length === size);
      setPage(pageToFetch + 1);
    } catch (err) {
      console.error("Error fetching pets:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [size, loading, hasMore, navigate]);

  useEffect(() => {
    fetchPets(0);
  }, [fetchPets]);

  const handleObserver = useCallback((entries) => {
    const target = entries[0];
    if (target.isIntersecting && !loading && hasMore) {
      fetchPets(page);
    }
  }, [fetchPets, page, loading, hasMore]);

  useEffect(() => {
    observer.current = new IntersectionObserver(handleObserver, {
      threshold: 0.1
    });

    if (loadMoreRef.current) {
      observer.current.observe(loadMoreRef.current);
    }

    return () => {
      if (observer.current && loadMoreRef.current) {
        observer.current.unobserve(loadMoreRef.current);
      }
    };
  }, [handleObserver]);

  const toggleDarkMode = () => {
    const newMode = !darkMode;
    setDarkMode(newMode);
    localStorage.setItem('darkMode', newMode.toString());
    if (newMode) {
      document.documentElement.classList.add('dark');
    } else {
      document.documentElement.classList.remove('dark');
    }
  };

  const handleDeleteAccount = async () => {
    const confirmDelete = window.confirm("Are you sure you want to delete your account? This action cannot be undone.");
    if (!confirmDelete) return;

    try {
      const token = localStorage.getItem("token");
      if (!token) throw new Error("User not authenticated");

      const response = await fetch(`${import.meta.env.VITE_API_URL}/users/delete/me`, {
        method: "DELETE",
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to delete account.");
      }

      const result = await response.json();
      alert(result.message || "Account deleted successfully");

      await signOut(auth);
      localStorage.clear();
      navigate("/login");
    } catch (err) {
      console.error("Account deletion failed:", err);
      alert(err.message || "Account deletion failed.");
    }
  };

  const handleLogout = async () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
      await signOut(auth);
      localStorage.clear();
      navigate("/login");
    } catch (error) {
      console.error("Logout failed: ", error);
    }
  };

  const handlePetClick = (pet) => {
    setSelectedPet(pet);
  };

  const filteredPets = pets.filter(pet =>
    pet.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    pet.breed.toLowerCase().includes(searchQuery.toLowerCase()) ||
    pet.species.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <div className={`home-wrapper ${darkMode ? 'dark' : ''}`}>
      <Banner firstName={userDetails.fullName.split(' ')[0]} />

      <div className="main-content">
        <div className="sidebar">
          <div className="sidebar-content">
            <div className="sidebar-section">
              <h4>Menu</h4>
              <Link to="/dashboard"><Home size={20} /> Home</Link>
              <a onClick={() => setShowSearch(!showSearch)} style={{ cursor: 'pointer' }}>
                <Search size={20} /> Search
              </a>
              <Link to="/notifications"><Bell size={20} /> Notifications</Link>
              <Link to="/messages"><Mail size={20} /> Messages</Link>
              <Link to="/bookings"><Calendar size={20} /> Bookings</Link>
            </div>

            <div className="sidebar-section">
              <h4>Pets</h4>
              <Link to="/profile"><User size={20} /> Profile</Link>
              <Link to="/add-pet"><Plus size={20} /> Add Pet</Link>
            </div>

            <div className="sidebar-section">
              <h4>Account</h4>
              <div className="settings-container" ref={settingsRef}>
                <button className="settings-button" onClick={() => setShowSettings(!showSettings)}>
                  <Settings size={20} /> Settings
                </button>
                {showSettings && (
                  <div className="settings-popup">
                    <button className="settings-item" onClick={toggleDarkMode}>
                      {darkMode ? <Sun size={16} /> : <Moon size={16} />}
                      {darkMode ? 'Light Mode' : 'Dark Mode'}
                    </button>
                    <button className="settings-item delete-account" onClick={handleDeleteAccount}>
                      <Trash2 size={16} /> Delete Account
                    </button>
                  </div>
                )}
              </div>
              <a onClick={handleLogout} style={{ cursor: 'pointer' }}><LogOut size={20} /> Logout</a>
            </div>
          </div>
        </div>

        <div className="center-content">
          <div className="feed-header">
            <h2>Pet Feed</h2>
          </div>

          {showSearch && (
            <div className="search-bar" style={{ marginBottom: '20px' }}>
              <input
                type="text"
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
                placeholder="Search pets by name, breed, or species..."
                style={{
                  width: '100%',
                  padding: '10px',
                  borderRadius: '6px',
                  border: '1px solid #cbd5e1',
                  fontSize: '14px'
                }}
              />
            </div>
          )}

          {error && <p className="error">{error}</p>}
          {!loading && pets.length === 0 && !error && <p>No pets available.</p>}

          <div className="pet-feed-grid">
            {(showSearch ? filteredPets : pets).map((pet) => (
              <div
                className="pet-card"
                key={pet.petId}
                onClick={() => handlePetClick(pet)}
                style={{ cursor: 'pointer' }}
              >
                <div className="pet-image-container">
                  <img
                    src={pet.photoUrl || defaultProfile}
                    alt={pet.name}
                    className="pet-image"
                    loading="lazy"
                  />
                </div>
                <div className="pet-info">
                  <h3>{pet.name}</h3>
                  <p>{pet.species} - {pet.breed}</p>
                  <p className="pet-description">{pet.description}</p>
                </div>
              </div>
            ))}
          </div>

          {loading && <p className="loading">Loading...</p>}
          {!hasMore && pets.length > 0 && (
            <p className="end-message">No more pets to show</p>
          )}

          <div ref={loadMoreRef} style={{ height: '20px' }} />
        </div>

        {selectedPet && (
          <div className="modal-overlay">
            <div className="modal-content" ref={modalRef}>
              <div className="modal-header">
                <h3>{selectedPet.name}</h3>
                <button onClick={() => setSelectedPet(null)} className="close-button">
                  <X size={20} />
                </button>
              </div>
              <div className="modal-body">
                <img
                  src={selectedPet.photoUrl || defaultProfile}
                  alt={selectedPet.name}
                  className="modal-pet-image"
                />
                <p><strong>Breed:</strong> {selectedPet.breed}</p>
                <p><strong>Description:</strong> {selectedPet.description}</p>
                <p><strong>Price:</strong> ${selectedPet.price}</p>
                <p><strong>Availability:</strong> {selectedPet.availabilityStatus}</p>
              </div>
              <div className="modal-footer">
                <button
                  onClick={() => navigate('/booking', { state: { petId: selectedPet.petId, petName: selectedPet.name } })}
                  className="modal-button"
                >
                  <Calendar size={16} /> Book
                </button>
                <button
                  onClick={() => navigate(`/messages/${selectedPet.userId}`)}
                  className="modal-button"
                >
                  <MessageSquare size={16} /> Chat
                </button>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
