import { useState, useEffect } from 'react';
import { fetchPets } from './api';

export const usePets = () => {
  const [pets, setPets] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const refreshPets = async () => {
    setLoading(true);
    try {
      const data = await fetchPets();
      setPets(data);
    } catch (err) {
      setError('Failed to fetch pets');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    refreshPets();
  }, []);

  return { pets, loading, error, refreshPets };
};