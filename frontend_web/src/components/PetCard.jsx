import React from 'react';
import { Card, CardHeader, CardMedia, CardContent, CardActions, Collapse, Avatar, IconButton, Typography } from '@mui/material';
import { red } from '@mui/material/colors';
import FavoriteIcon from '@mui/icons-material/Favorite';
import ShareIcon from '@mui/icons-material/Share';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import ExpandMore from './ExpandMore';
import defaultProfile from '../assets/defaultprofileimage.png';

export default function PetCard({ pet }) {
  const [expanded, setExpanded] = React.useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  // Calculate age from dateOfBirth
  const calculateAge = (dateOfBirth) => {
    if (!dateOfBirth) return 'Unknown';
    const dob = new Date(dateOfBirth);
    const today = new Date();
    const years = today.getFullYear() - dob.getFullYear();
    const months = today.getMonth() - dob.getMonth();
    if (months < 0 || (months === 0 && today.getDate() < dob.getDate())) {
      return years - 1;
    }
    return years;
  };

  return (
    <Card sx={{ maxWidth: 345, marginBottom: 3 }}>
      <CardHeader
        avatar={
          <Avatar sx={{ bgcolor: red[500] }} aria-label="pet-avatar">
            {pet.name?.[0] || 'P'}
          </Avatar>
        }
        title={pet.name || 'Unnamed Pet'}
        subheader={`Age: ${calculateAge(pet.dateOfBirth)} | Gender: ${pet.gender || 'Unknown'}`}
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
          Weight: {pet.weight ? `${pet.weight} ${pet.weightUnit || 'kg'}` : 'Unknown'}
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