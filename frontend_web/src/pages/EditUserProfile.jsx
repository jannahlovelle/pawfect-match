import React, { useState, useEffect } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import "../styles/EditUserProfile.css";
import defaultProfilePic from '../assets/defaultprofileimage.png';

export default function EditProfile() {
  const navigate = useNavigate();
  const { state } = useLocation();
  const { onProfileUpdated } = state || {};
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    address: '',
    password: '',
    confirmPassword: ''
  });
  const [profileImage, setProfileImage] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);
  const [imageLoading, setImageLoading] = useState(false);
  const [userId, setUserId] = useState('');

  // Load initial form data
  useEffect(() => {
    const fetchProfile = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }

      try {
        const response = await fetch("https://pawfect-match-zp0o.onrender.com/users/me", {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!response.ok) {
          if (response.status === 401) {
            localStorage.removeItem("token");
            navigate("/login");
            throw new Error("Session expired. Please log in again.");
          }
          const errorData = await response.json();
          throw new Error(errorData.message || "Failed to fetch user profile");
        }

        const userProfile = await response.json();
        console.log("API Response:", userProfile); // Debug log
        
        setUserId(userProfile.user?.userID || '');
        setFormData({
          firstName: userProfile.user?.firstName || '',
          lastName: userProfile.user?.lastName || '',
          email: userProfile.user?.email || '',
          phone: userProfile.user?.phone || '',
          address: userProfile.user?.address || '',
          password: '',
          confirmPassword: ''
        });
        setProfileImage(userProfile.user?.profilePicture || '');
      } catch (err) {
        console.error("Error fetching profile:", err);
        setError(err.message);
      }
    };

    fetchProfile();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleImageChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;

    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    // Validate file
    const validTypes = ['image/jpeg', 'image/png'];
    if (!validTypes.includes(file.type)) {
      setError("Please upload a JPEG or PNG image.");
      return;
    }
    if (file.size > 5 * 1024 * 1024) {
      setError("Image size exceeds 5MB limit.");
      return;
    }

    setImageLoading(true);
    setError(null);

    try {
      const formData = new FormData();
      formData.append("file", file);

      const response = await fetch(`https://pawfect-match-zp0o.onrender.com/users/${userId}/profile-picture`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`
        },
        body: formData
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to upload profile picture");
      }

      const updatedUser = await response.json();
      setProfileImage(updatedUser.profilePicture || '');
      alert("Profile picture updated successfully!");
    } catch (err) {
      console.error("Error uploading profile picture:", err);
      setError(err.message);
    } finally {
      setImageLoading(false);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError(null);

    // Password validation
    if (formData.password && formData.password !== formData.confirmPassword) {
      setError("Passwords don't match");
      setLoading(false);
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    try {
      // Prepare update data (only include password if provided)
      const updateData = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        phone: formData.phone,
        address: formData.address,
        ...(formData.password && { password: formData.password }) // Only include if not empty
      };

      const response = await fetch(`https://pawfect-match-zp0o.onrender.com/users/update/${userId}`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(updateData)
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to update profile");
      }

      const result = await response.json();
      alert(result.message || "Profile updated successfully");
      if (onProfileUpdated) onProfileUpdated();
      navigate("/profile");
    } catch (err) {
      console.error("Error updating profile:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = () => {
    navigate("/profile");
  };

  return (
    <div className="home-wrapper">
      <div className="main-content">
        <div className="editcenter-content">
          <div className="edit-profile-container">
            <h2>Edit Profile</h2>
            {error && <p className="error">{error}</p>}
            <div className="profile-pic-section">
              <img
                src={profileImage || defaultProfilePic}
                alt="Profile"
                className="profile-pic"
                onError={(e) => {
                  e.target.src = defaultProfilePic;
                }}
              />
              <label className="change-photo-btn">
                {imageLoading ? 'Uploading...' : 'Change Photo'}
                <input
                  type="file"
                  accept="image/jpeg,image/png"
                  onChange={handleImageChange}
                  disabled={imageLoading}
                  style={{ display: 'none' }}
                />
              </label>
            </div>
            <form onSubmit={handleSubmit} className="edit-profile-form">
              <div className="form-group">
                <label htmlFor="firstName">First Name*</label>
                <input
                  type="text"
                  id="firstName"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="lastName">Last Name</label>
                <input
                  type="text"
                  id="lastName"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                />
              </div>
              <div className="form-group">
                <label htmlFor="email">Email*</label>
                <input
                  type="email"
                  id="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  required
                  disabled // Typically email shouldn't be editable
                />
              </div>
              <div className="form-group">
                <label htmlFor="phone">Phone</label>
                <input
                  type="tel"
                  id="phone"
                  name="phone"
                  value={formData.phone}
                  onChange={handleChange}
                />
              </div>
              <div className="form-group">
                <label htmlFor="address">Address</label>
                <input
                  type="text"
                  id="address"
                  name="address"
                  value={formData.address}
                  onChange={handleChange}
                />
              </div>
              <div className="form-group">
                <label htmlFor="password">New Password</label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Leave blank to keep current"
                />
              </div>
              <div className="form-group">
                <label htmlFor="confirmPassword">Confirm Password</label>
                <input
                  type="password"
                  id="confirmPassword"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Only needed if changing password"
                />
              </div>
              <div className="form-actions">
                <button type="submit" className="save-btn" disabled={loading || imageLoading}>
                  {loading ? 'Saving...' : 'Save Changes'}
                </button>
                <button type="button" className="cancel-btn" onClick={handleCancel} disabled={loading || imageLoading}>
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
}