import axios from 'axios';

const API_URL = 'http://localhost:8080/admin';
const token = localStorage.getItem('token');

const axiosInstance = axios.create({
  baseURL: API_URL,
  headers: {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'application/json',
  },
});

const axiosMultipartInstance = axios.create({
  baseURL: API_URL,
  headers: {
    Authorization: `Bearer ${token}`,
    'Content-Type': 'multipart/form-data',
  },
});

export const fetchUsers = async () => {
  const response = await axiosInstance.get('/users');
  return response.data;
};

export const fetchPets = async () => {
  const response = await axiosInstance.get('/pets');
  return response.data;
};

export const getUserProfiles = async () => {
  const response = await axiosInstance.get('/users-with-pets');
  return response.data;
};

export const updateUser = async (userId, userData) => {
  const response = await axiosInstance.put(`/update/${userId}`, userData);
  return response.data;
};

export const deleteUser = async (userId) => {
  const response = await axiosInstance.delete(`/delete/${userId}`);
  return response.data;
};

export const updatePet = async (petId, petData) => {
  const response = await axiosInstance.put(`/pets/${petId}`, petData);
  return response.data;
};

export const deletePet = async (petId) => {
  const response = await axiosInstance.delete(`/pets/${petId}`);
  return response.data;
};

export const updatePetPhoto = async (photoId, file) => {
  console.log(`Sending photo update for photoId: ${photoId}`, file); // Debug request
  const formData = new FormData();
  formData.append('file', file);
  try {
    const response = await axiosMultipartInstance.put(`/pets/photos/${photoId}`, formData);
    console.log('Photo update response:', response.data); // Debug response
    return response.data;
  } catch (err) {
    console.error('Photo update error:', err.response?.data || err.message); // Debug error
    throw err;
  }
};

export const deletePetPhoto = async (photoId) => {
  const response = await axiosInstance.delete(`/pets/photos/${photoId}`);
  return response.data;
};