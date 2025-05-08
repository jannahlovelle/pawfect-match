import React, { useEffect, useState } from 'react';
import '../styles/home.css';
import Banner from '../components/Banner';
import { Home, Search, Bell, Mail, Settings, Plus, LogOut } from 'lucide-react';
import { Link, useNavigate } from 'react-router-dom';
import { auth } from '../firebase';
import { onAuthStateChanged } from 'firebase/auth';
import { CircularProgress, Box, Grid, Typography } from '@mui/material';
import PetCard from '../components/PetCard';
import defaultProfile from '../assets/defaultprofileimage.png';

export default function PetList() {
  const navigate = useNavigate();
  const firstName = localStorage.getItem('firstName');

  const [userDetails, setUserDetails] = useState({
    fullName: '',
    email: '',
    phone: '',
    address: '',
    profileImage: '',
  });

  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (user) => {
      const profileImage = localStorage.getItem('profileImage');

      if (user) {
        const fullName = user.displayName || localStorage.getItem('fullName');
        const email = user.email || localStorage.getItem('email');

        setUserDetails({
          fullName: fullName || '',
          email: email || '',
          phone: localStorage.getItem('phone') || '',
          address: localStorage.getItem('address') || '',
          profileImage: profileImage || '',
        });
      } else {
        setUserDetails({
          fullName: localStorage.getItem('fullName') || '',
          email: localStorage.getItem('email') || '',
          phone: localStorage.getItem('phone') || '',
          address: localStorage.getItem('address') || '',
          profileImage: profileImage || '',
        });
      }
    });

    return () => unsubscribe();
  }, []);

  const fetchPets = async () => {
    setLoading(true);
    setError(null);

    try {
      const token = localStorage.getItem('token');
      if (!token) throw new Error('No authentication token found');

      const apiUrl = import.meta.env.VITE_API_URL;
      if (!apiUrl) throw new Error('VITE_API_URL is not defined in .env');

      const response = await fetch(`${apiUrl}/pets/my-pets`, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'application/json',
        },
      });

      const responseText = await response.text();

      if (!response.ok) {
        if (response.status === 401) {
          localStorage.removeItem('token');
          navigate('/login');
          throw new Error('Session expired. Please log in again.');
        }

        try {
          const errorData = JSON.parse(responseText);
          throw new Error(errorData.message || 'Failed to fetch pets');
        } catch {
          throw new Error(`Non-JSON response: ${responseText.substring(0, 100)}...`);
        }
      }

      let petsData = JSON.parse(responseText);

      const petsWithPhotos = await Promise.all(
        petsData.map(async (pet) => {
          try {
            const photoResponse = await fetch(`${apiUrl}/pets/${pet.petId}/photos`, {
              headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json',
              },
            });

            const photoText = await photoResponse.text();
            if (photoResponse.ok) {
              const photos = JSON.parse(photoText);
              return { ...pet, photo: photos.length > 0 ? photos[0].url : defaultProfile };
            }
            return { ...pet, photo: defaultProfile };
          } catch {
            return { ...pet, photo: defaultProfile };
          }
        })
      );

      setPets(petsWithPhotos);
    } catch (err) {
      console.error('Error fetching pets:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchPets();
  }, []);

  const handleLogout = async () => {
    const confirmLogout = window.confirm('Are you sure you want to logout?');
    if (!confirmLogout) return;

    try {
      await auth.signOut();
      localStorage.clear();
      alert('You have logged out successfully!');
      navigate('/login');
    } catch (error) {
      alert('Logout failed. Please try again.');
    }
  };

  return (
    <div className="home-wrapper">
      <Banner firstName={firstName} onLogout={handleLogout} />

        
      <div className="main-content">
        <div className="sidebar">
          <div className="sidebar-content">
            <div className="sidebar-section">
              <h4>Menu</h4>
              <Link to="/dashboard"><Home size={20} /> Home</Link>
              <Link to="/search"><Search size={20} /> Search</Link>
              <Link to="/notifications"><Bell size={20} /> Notifications</Link>
              <Link to="/messages"><Mail size={20} /> Messages</Link>
            </div>
            
            <div className="sidebar-section">
              <h4>Pets</h4>
              <Link to="/profile"><User size={20} /> Profile</Link>
              <Link to="/pet-list"><List size={20} /> My Pet List</Link>
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
              <a onClick={handleLogout} style={{cursor: 'pointer'}}><LogOut size={20} /> Logout</a>
            </div>
          </div>
        </div>


        {/* Center Content - FROM YOUR ORIGINAL PETLIST.JSX */}
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

      </div>
    </div>
  );
}
