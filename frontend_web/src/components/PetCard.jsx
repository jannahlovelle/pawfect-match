import React, { useEffect, useState } from 'react';
import {
  Card, CardHeader, CardMedia, CardContent, CardActions, Collapse,
  Avatar, IconButton, Typography
} from '@mui/material';
import { red } from '@mui/material/colors';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ShareIcon from '@mui/icons-material/Share';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandMore from './ExpandMore';
import defaultProfile from '../assets/defaultprofileimage.png';

export default function PetCard({ pet }) {
  const [expanded, setExpanded] = useState(false);
  const [owner, setOwner] = useState({ name: 'Loading...', address: 'Loading...' });

  const handleExpandClick = () => setExpanded(!expanded);

  const calculateAge = (dateOfBirth) => {
    if (!dateOfBirth) return 'Unknown';
    const dob = new Date(dateOfBirth);
    const today = new Date();
    let age = today.getFullYear() - dob.getFullYear();
    const m = today.getMonth() - dob.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < dob.getDate())) {
      age--;
    }
    return age;
  };

  // 🔍 Fetch owner info using userId
  useEffect(() => {
    if (!pet.userId) return;

    const fetchOwner = async () => {
      try {
        const res = await fetch(`/api/users/${pet.userId}`);
        if (!res.ok) throw new Error('User not found');
        const data = await res.json();
        setOwner({ name: data.fullName || 'Unknown User', address: data.address || 'Unknown Address' });
      } catch (err) {
        setOwner({ name: 'Unknown User', address: 'Unknown Address' });
      }
    };

    fetchOwner();
  }, [pet.userId]);

  return (
    <Card sx={{ maxWidth: 345, marginBottom: 3 }}>
      <CardHeader
        avatar={<Avatar sx={{ bgcolor: red[500] }}>{owner.name[0] || 'P'}</Avatar>}
        title={owner.name}
        subheader={owner.address}
      />
      <CardMedia
        component="img"
        height="194"
        image={pet.photo || defaultProfile}
        alt={pet.name || 'Pet'}
      />
      <CardContent>
        <Typography variant="body2" color="text.secondary">
          Breed: {pet.breed || 'Unknown'}<br />
          Color: {pet.color || 'Unknown'}<br />
          Weight: {pet.weight ? `${pet.weight} ${pet.weightUnit || 'kg'}` : 'Unknown'}<br />
          Age: {calculateAge(pet.dateOfBirth)} | Gender: {pet.gender || 'Unknown'}
        </Typography>
      </CardContent>
      <CardActions disableSpacing>
        <IconButton aria-label="add to favorites"><FavoriteIcon /></IconButton>
        <IconButton aria-label="share"><ShareIcon /></IconButton>
        <ExpandMore
          expand={expanded}
          onClick={handleExpandClick}
          aria-expanded={expanded}
          aria-label="show more"
        >
          <ExpandMoreIcon />
        </ExpandMore>
      </CardActions>
      <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent>
          <Typography paragraph>Description:</Typography>
          <Typography paragraph>{pet.description || 'No description available'}</Typography>
        </CardContent>
      </Collapse>
    </Card>
  );
}
