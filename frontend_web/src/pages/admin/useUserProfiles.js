import { useState, useEffect } from 'react';
import { getUserProfiles, fetchPets } from './api';
import axios from 'axios';

export const useUserProfiles = () => {
  const [userProfiles, setUserProfiles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchData = async () => {
    try {
      setLoading(true);
      const data = await getUserProfiles();
      console.log('Raw UserProfile data:', data); // Debug raw data

      // Flatten user object and fetch full pet details
      const flattenedData = await Promise.all(
        data.map(async (profile) => {
          const pets = Array.isArray(profile.pets) ? profile.pets : [];
          const normalizedPets = await Promise.all(
            pets.map(async (pet) => {
              if (pet.petId) {
                try {
                  const response = await axios.get(`http://localhost:8080/admin/pets/${pet.petId}`, {
                    headers: {
                      Authorization: `Bearer ${localStorage.getItem('token')}`,
                    },
                  });
                  const fullPet = response.data;
                  return {
                    petId: fullPet.petId || '',
                    name: fullPet.name || 'Unknown',
                    species: fullPet.species || 'Unknown',
                    breed: fullPet.breed || '',
                    gender: fullPet.gender || '',
                    dateOfBirth: fullPet.dateOfBirth || '',
                    weight: fullPet.weight || '',
                    color: fullPet.color || '',
                    description: fullPet.description || '',
                    availabilityStatus: fullPet.availabilityStatus || '',
                    price: fullPet.price || '',
                    pedigreeInfo: fullPet.pedigreeInfo || '',
                    healthStatus: fullPet.healthStatus || '',
                    photos: fullPet.photos || [],
                  };
                } catch (err) {
                  console.warn(`Failed to fetch pet ${pet.petId}:`, err);
                  return {
                    petId: pet.petId || '',
                    name: pet.name || 'Unknown',
                    species: pet.species || 'Unknown',
                    breed: pet.breed || '',
                    gender: pet.gender || '',
                    dateOfBirth: pet.dateOfBirth || '',
                    weight: pet.weight || '',
                    color: pet.color || '',
                    description: pet.description || '',
                    availabilityStatus: pet.availabilityStatus || '',
                    price: pet.price || '',
                    pedigreeInfo: pet.pedigreeInfo || '',
                    healthStatus: pet.healthStatus || '',
                    photos: pet.photos || [],
                  };
                }
              }
              return {
                petId: pet.petId || '',
                name: pet.name || 'Unknown',
                species: pet.species || 'Unknown',
                breed: pet.breed || '',
                gender: pet.gender || '',
                dateOfBirth: pet.dateOfBirth || '',
                weight: pet.weight || '',
                color: pet.color || '',
                description: pet.description || '',
                availabilityStatus: pet.availabilityStatus || '',
                price: pet.price || '',
                pedigreeInfo: pet.pedigreeInfo || '',
                healthStatus: pet.healthStatus || '',
                photos: pet.photos || [],
              };
            })
          );
          console.log(`Normalized pets for user ${profile.user?.userID}:`, normalizedPets); // Debug normalized pets
          return {
            ...profile.user,
            pets: normalizedPets,
          };
        })
      );
      setUserProfiles(flattenedData);
      setError(null);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to fetch user profiles');
      console.error('Error fetching user profiles:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const refreshUserProfiles = () => {
    fetchData();
  };

  return { userProfiles, loading, error, refreshUserProfiles };
};