import React, { useEffect, useState, useRef, useCallback } from 'react';
import "../styles/home.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, User, List, Plus, LogOut } from 'lucide-react';
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
  const [size] = useState(6);
  const [hasMore, setHasMore] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const observer = useRef(null);
  const loadMoreRef = useRef(null);

  useEffect(() => {
    // Check if user is authenticated via token in localStorage
    const token = localStorage.getItem("token");
    if (!token) {
      console.log("No token found, redirecting to login");
      navigate("/login");
      return;
    }

    // Update userDetails from localStorage
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
      if (!token) {
        throw new Error("No authentication token found");
      }

      console.log(`Fetching pets for page ${pageToFetch}`);
      const response = await fetch(`https://pawfect-match-zp0o.onrender.com/pets/feed?page=${pageToFetch}&size=${size}`, {
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
      console.log(`Received ${data.length} pets for page ${pageToFetch}`);

      // Filter out duplicates by petId
      setPets((prevPets) => {
        const existingIds = new Set(prevPets.map((pet) => pet.petId));
        const newPets = data.filter((pet) => !existingIds.has(pet.petId));
        return [...prevPets, ...newPets];
      });

      setHasMore(data.length === size);
      setPage(pageToFetch + 1); // Increment page only after successful fetch
    } catch (err) {
      console.error("Error fetching pets:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  }, [size, loading, hasMore, navigate]);

  useEffect(() => {
    // Initial fetch for page 0
    fetchPets(0);
  }, [fetchPets]);

  const handleObserver = useCallback(
    (entries) => {
      const target = entries[0];
      if (target.isIntersecting && !loading && hasMore) {
        fetchPets(page);
      }
    },
    [fetchPets, page, loading, hasMore]
  );

  useEffect(() => {
    // Set up IntersectionObserver
    observer.current = new IntersectionObserver(handleObserver, {
      threshold: 0.1 // Trigger when 10% of the element is visible
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

  const handleLogout = async () => {
    const confirmLogout = window.confirm("Are you sure you want to logout?");
    if (!confirmLogout) return;

    try {
      // Sign out from Firebase (for Google Sign-In users)
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
      <Banner firstName={userDetails.fullName.split(' ')[0]} onLogout={handleLogout} />

      <div className="main-content">
        {/* Left Sidebar */}
        <div className="sidebar">
          <Link to="/dashboard"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
        </div>

        {/* Middle Section - Pet Feed */}
        <div className="center-content">
          <div className="feed-header">
            <h2>Pet Feed</h2>
          </div>

          {error && <p className="error">{error}</p>}
          {!loading && pets.length === 0 && !error && <p>No pets available.</p>}

          <div className="pet-feed">
            {pets.map((pet, index) => (
              <React.Fragment key={pet.petId}>
                <div className="pet-card">
                  <img
                    src={pet.photoUrl || defaultProfile}
                    alt={pet.name}
                    className="pet-image"
                    loading="lazy"
                  />
                  <div className="pet-info">
                    <h3>{pet.name}</h3>
                    <p>{pet.species} - {pet.breed}</p>
                    <p>{pet.description}</p>
                  </div>
                </div>
                {index < pets.length - 1 && <hr className="pet-divider" />}
              </React.Fragment>
            ))}
          </div>
          {loading && <p className="loading">Loading...</p>}
          {!hasMore && pets.length > 0 && (
            <p className="end-message">No more pets to show</p>
          )}

          <div ref={loadMoreRef} style={{ height: '20px' }} />
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