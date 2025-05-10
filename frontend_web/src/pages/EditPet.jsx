import React, { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import "../styles/EditPet.css";

export default function EditPet() {
  const { petId } = useParams();
  const navigate = useNavigate();
  const fileInputRef = useRef(null);

  const [pet, setPet] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [previewImage, setPreviewImage] = useState(null);
  const [mainPhotoId, setMainPhotoId] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    species: '',
    breed: '',
    gender: '',
    dateOfBirth: '',
    weight: '',
    color: '',
    description: '',
    availabilityStatus: 'available',
    price: '',
    pedigreeInfo: '',
    healthStatus: ''
  });

  const speciesOptions = ['Dog', 'Cat', 'Bird', 'Other'];
  const genderOptions = ['Male', 'Female'];
  const statusOptions = ['available', 'pending', 'adopted'];

  useEffect(() => {
    const fetchPetDetails = async () => {
      const token = localStorage.getItem("token");
      if (!token) {
        navigate("/login");
        return;
      }

      try {
        const petResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/${petId}`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (!petResponse.ok) {
          const errorData = await petResponse.json();
          throw new Error(errorData.message || "Failed to fetch pet details");
        }

        const petData = await petResponse.json();
        setPet(petData);

        const formattedDate = petData.dateOfBirth
          ? new Date(petData.dateOfBirth).toISOString().split('T')[0]
          : '';

        setFormData({
          name: petData.name || '',
          species: petData.species || '',
          breed: petData.breed || '',
          gender: petData.gender || '',
          dateOfBirth: formattedDate,
          weight: petData.weight || '',
          color: petData.color || '',
          description: petData.description || '',
          availabilityStatus: petData.availabilityStatus || 'available',
          price: petData.price || '',
          pedigreeInfo: petData.pedigreeInfo || '',
          healthStatus: petData.healthStatus || ''
        });

        const photosResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/${petId}/photos`, {
          headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });

        if (photosResponse.ok) {
          const photosData = await photosResponse.json();
          if (photosData.length > 0) {
            setPreviewImage(photosData[0].url);
            setMainPhotoId(photosData[0].photoId);
          }
        }
      } catch (err) {
        console.error("Error fetching pet:", err);
        setError(err.message || "Failed to load pet details");
      } finally {
        setLoading(false);
      }
    };

    fetchPetDetails();
  }, [petId, navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleNumberChange = (e) => {
    const { name, value } = e.target;
    if (value === '' || /^[0-9]*\.?[0-9]*$/.test(value)) {
      setFormData(prev => ({
        ...prev,
        [name]: value
      }));
    }
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (!['image/jpeg', 'image/png'].includes(file.type)) {
        setError("Only JPEG and PNG files are allowed");
        return;
      }

      if (file.size > 5 * 1024 * 1024) {
        setError("File size exceeds the limit of 5MB");
        return;
      }

      const reader = new FileReader();
      reader.onloadend = () => {
        setPreviewImage(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    const token = localStorage.getItem("token");
    if (!token) {
      navigate("/login");
      return;
    }

    // Validate required fields
    if (!formData.name.trim()) {
      setError("Pet name is required");
      return;
    }
    if (!formData.species) {
      setError("Species is required");
      return;
    }
    if (!formData.gender) {
      setError("Gender is required");
      return;
    }

    try {
      // Format dateOfBirth as ISO string
      const formattedDate = formData.dateOfBirth
        ? new Date(formData.dateOfBirth).toISOString()
        : null;

      // Debug: Log the request body
      const petUpdateBody = {
        name: formData.name,
        species: formData.species,
        breed: formData.breed,
        gender: formData.gender,
        dateOfBirth: formattedDate,
        weight: formData.weight === '' ? null : parseFloat(formData.weight),
        color: formData.color,
        description: formData.description,
        availabilityStatus: formData.availabilityStatus,
        price: formData.price === '' ? null : parseFloat(formData.price),
        pedigreeInfo: formData.pedigreeInfo,
        healthStatus: formData.healthStatus
      };
      console.log("Pet update request body:", petUpdateBody);

      // Update pet info
      const petResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/update/${petId}`, {
        method: "PUT",
        headers: {
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(petUpdateBody)
      });

      // Debug: Log response status and body
      console.log("Pet update response status:", petResponse.status);
      const petResponseBody = await petResponse.json();
      console.log("Pet update response body:", petResponseBody);

      if (!petResponse.ok) {
        throw new Error(petResponseBody.message || `Failed to update pet (Status: ${petResponse.status})`);
      }

      // Update photo if a new one was selected
      if (fileInputRef.current.files[0]) {
        const photoFormData = new FormData();
        photoFormData.append('file', fileInputRef.current.files[0]);

        let photoResponse;
        if (mainPhotoId) {
          // Update existing photo
          console.log("Updating photo with ID:", mainPhotoId);
          photoResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/photos/${mainPhotoId}`, {
            method: "PUT",
            headers: {
              'Authorization': `Bearer ${token}`
            },
            body: photoFormData
          });
        } else {
          // Upload new photo
          console.log("Uploading new photo for petId:", petId);
          photoResponse = await fetch(`${import.meta.env.VITE_API_URL}/pets/${petId}/photos`, {
            method: "POST",
            headers: {
              'Authorization': `Bearer ${token}`
            },
            body: photoFormData
          });
        }

        // Debug: Log photo response
        console.log("Photo response status:", photoResponse.status);
        const photoResponseBody = await photoResponse.json();
        console.log("Photo response body:", photoResponseBody);

        if (!photoResponse.ok) {
          throw new Error(photoResponseBody.message || `Failed to ${mainPhotoId ? 'update' : 'upload'} photo (Status: ${photoResponse.status})`);
        }

        // Update mainPhotoId and previewImage for new photo
        if (!mainPhotoId) {
          setMainPhotoId(photoResponseBody.photoId);
          setPreviewImage(photoResponseBody.url);
        }
      }

      alert("Pet updated successfully");
      navigate("/profile");
    } catch (err) {
      console.error("Error updating pet:", err);
      setError(err.message || "An unexpected error occurred");
    }
  };

  const handleDeletePhoto = async () => {
    if (!mainPhotoId) return;

    try {
      const token = localStorage.getItem("token");
      const response = await fetch(`${import.meta.env.VITE_API_URL}/pets/photos/${mainPhotoId}`, {
        method: "DELETE",
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (response.ok) {
        setPreviewImage(null);
        setMainPhotoId(null);
        fileInputRef.current.value = null;
        alert("Photo deleted successfully");
      } else {
        const errorData = await response.json();
        throw new Error(errorData.message || "Failed to delete photo");
      }
    } catch (err)      {
      setError(err.message || "Failed to delete photo");
    }
  };

  if (loading) return <div className="loading">Loading pet details...</div>;
  if (error) return <div className="error">{error}</div>;

  return (
    <div className="edit-pet-container">
      <h2>Edit Pet: {pet?.name}</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-grid">
          {/* Photo Upload Section */}
          <div className="form-section photo-section">
            <div className="form-group photo-upload">
              <label>Pet Photo</label>
              <div className="image-preview-container">
                {previewImage ? (
                  <img src={previewImage} alt="Pet preview" className="image-preview" />
                ) : (
                  <div className="image-placeholder">No photo available</div>
                )}
              </div>
              <input
                type="file"
                ref={fileInputRef}
                onChange={handleImageChange}
                accept="image/jpeg,image/png"
                className="file-input"
              />
              <div className="photo-actions">
                <button
                  type="button"
                  className="upload-btn"
                  onClick={() => fileInputRef.current.click()}
                >
                  {previewImage ? 'Change' : 'Upload'}
                </button>
                {previewImage && (
                  <button
                    type="button"
                    className="delete-btn"
                    onClick={handleDeletePhoto}
                  >
                    Delete
                  </button>
                )}
              </div>
            </div>
          </div>

          {/* Core Fields Section */}
          <div className="form-section core-fields">
            <div className="form-group">
              <label>Name *</label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleChange}
                required
              />
            </div>
            <div className="form-group">
              <label>Species *</label>
              <select
                name="species"
                value={formData.species}
                onChange={handleChange}
                required
              >
                <option value="">Select</option>
                {speciesOptions.map(option => (
                  <option key={option} value={option}>{option}</option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Breed</label>
              <input
                type="text"
                name="breed"
                value={formData.breed}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label>Gender *</label>
              <select
                name="gender"
                value={formData.gender}
                onChange={handleChange}
                required
              >
                <option value="">Select</option>
                {genderOptions.map(option => (
                  <option key={option} value={option}>{option}</option>
                ))}
              </select>
            </div>
          </div>

          {/* Secondary Fields Section */}
          <div className="form-section secondary-fields">
            <div className="form-group">
              <label>Date of Birth</label>
              <input
                type="date"
                name="dateOfBirth"
                value={formData.dateOfBirth}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label>Weight (kg)</label>
              <input
                type="text"
                name="weight"
                value={formData.weight}
                onChange={handleNumberChange}
                placeholder="e.g., 5.5"
              />
            </div>
            <div className="form-group">
              <label>Color</label>
              <input
                type="text"
                name="color"
                value={formData.color}
                onChange={handleChange}
              />
            </div>
            <div className="form-group">
              <label>Price ($)</label>
              <input
                type="text"
                name="price"
                value={formData.price}
                onChange={handleNumberChange}
                placeholder="e.g., 500.00"
              />
            </div>
          </div>

          {/* Textarea Fields Section */}
          <div className="form-section textarea-fields">
            <div className="form-group">
              <label>Description</label>
              <textarea
                name="description"
                value={formData.description}
                onChange={handleChange}
                rows="3"
              />
            </div>
            <div className="form-group">
              <label>Availability</label>
              <select
                name="availabilityStatus"
                value={formData.availabilityStatus}
                onChange={handleChange}
              >
                {statusOptions.map(option => (
                  <option key={option} value={option}>
                    {option.charAt(0).toUpperCase() + option.slice(1)}
                  </option>
                ))}
              </select>
            </div>
            <div className="form-group">
              <label>Pedigree Info</label>
              <textarea
                name="pedigreeInfo"
                value={formData.pedigreeInfo}
                onChange={handleChange}
                rows="3"
              />
            </div>
            <div className="form-group">
              <label>Health Status</label>
              <textarea
                name="healthStatus"
                value={formData.healthStatus}
                onChange={handleChange}
                rows="3"
              />
            </div>
          </div>
        </div>

        <div className="form-actions">
          <button type="submit" className="save-btn">Save</button>
          <button
            type="button"
            className="cancel-btn"
            onClick={() => navigate("/profile")}
          >
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
