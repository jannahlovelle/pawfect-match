import React, { useEffect, useState } from 'react';
import "../styles/home.css";
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, Plus } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from "../firebase"; 
import { onAuthStateChanged } from "firebase/auth";
import { CircularProgress, Box, Grid, Typography } from '@mui/material';
import PetCard from '../components/PetCard';
import defaultProfile from '../assets/defaultprofileimage.png';

export default function PetList() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem("firstName");

  const [userDetails, setUserDetails] = useState({
    fullName: '',
    email: '',
    phone: '',
    address: '',
    profileImage: ''
  });

  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

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

    return () => unsubscribe();
  }, []);

  const fetchPets = async () => {
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem("token");
      if (!token) {
        throw new Error("No authentication token found");
      }

      const apiUrl = import.meta.env.VITE_API_URL;
      if (!apiUrl) {
        throw new Error("VITE_API_URL is not defined in .env");
      }

      console.log(`Fetching pets from ${apiUrl}/pets/my-pets with token: ${token.substring(0, 10)}...`);
      const response = await fetch(`${apiUrl}/pets/my-pets`, {
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        }
      });

      // Log raw response for debugging
      const responseText = await response.text();
      console.log("Raw response:", responseText);

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem("token");
          navigate("/login");
          throw new Error("Session expired. Please log in again.");
        }
        try {
          const errorData = JSON.parse(responseText);
          throw new Error(errorData.message || "Failed to fetch pets");
        } catch (parseError) {
          throw new Error(`Non-JSON response: ${responseText.substring(0, 100)}...`);
        }
      }

      let petsData;
      try {
        petsData = JSON.parse(responseText);
      } catch (parseError) {
        throw new Error("Response is not valid JSON");
      }

      console.log(`Received ${petsData.length} pets`);

      // Fetch photos for each pet
      const petsWithPhotos = await Promise.all(
        petsData.map(async (pet) => {
          try {
            const photoResponse = await fetch(`${apiUrl}/pets/${pet.petId}/photos`, {
              headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
              }
            });

            const photoText = await photoResponse.text();
            if (photoResponse.ok) {
              const photos = JSON.parse(photoText);
              return { ...pet, photo: photos.length > 0 ? photos[0].url : defaultProfile };
            }
            console.warn(`No photos for pet ${pet.petId}: ${photoText}`);
            return { ...pet, photo: defaultProfile };
          } catch (photoError) {
            console.error(`Error fetching photos for pet ${pet.petId}:`, photoError);
            return { ...pet, photo: defaultProfile };
          }
        })
      );

      setPets(petsWithPhotos);
    } catch (err) {
      console.error("Error fetching pets:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPets();
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
          <Link to="/dashboard"><Home size={20} /> Home</Link>
          <Link to="/search"><Search size={20} /> Search</Link>
          <Link to="/notifications"><Bell size={20} /> Notifications</Link>
          <Link to="/messages"><Mail size={20} /> Messages</Link>
          <Link to="/settings"><Settings size={20} /> Settings</Link>
        </div>

        {/* Center Content */}
        <div className="center-content" style={{ padding: '1rem', overflowY: 'auto' }}>
          <div className="feed-header">
            <h2>My Pet List</h2>
          </div>

          {error && <Typography color="error">{error}</Typography>}
          {!loading && pets.length === 0 && !error && (
            <Typography>No pets available. Please add a pet first.</Typography>
          )}

          <Grid container spacing={2}>
            {loading ? (
              <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
                <CircularProgress />
              </Box>
            ) : (
              pets.map((pet) => (
                <Grid item xs={12} sm={6} md={4} key={pet.petId}>
                  <PetCard pet={pet} />
                </Grid>
              ))
            )}
          </Grid>
        </div>

        {/* Right Sidebar */}
        <div className="sidebar right">
          <Link to="/profile"><Home size={20} /> Profile</Link>
          <Link to="/pet-list"><Search size={20} /> My Pet List</Link>
          <Link to="/add-pet"><Plus size={20} /> Add Pet</Link>
        </div>
      </div>
    </div>
  );
}