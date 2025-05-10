import React from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Card,
  CardContent,
  Typography,
  Grid,
  IconButton,
  Button
} from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import defaultProfile from '../assets/defaultprofileimage.png';

const PetProfilePopup = ({ open, onClose, pet }) => {
  if (!pet) return null;

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        Pet Profile
        <IconButton
          aria-label="close"
          onClick={onClose}
          sx={{
            position: 'absolute',
            right: 8,
            top: 8,
            color: (theme) => theme.palette.grey[500]
          }}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>

      <DialogContent dividers>
        <Card elevation={0}>
          <CardContent>
            <Grid container spacing={2}>
              <Grid item xs={12} textAlign="center">
                <img
                  src={pet.photo && pet.photo.trim() !== '' ? pet.photo : defaultProfile}
                  alt={pet.name}
                  style={{
                    width: '150px',
                    height: '150px',
                    borderRadius: '50%',
                    objectFit: 'cover',
                    border: '2px solid #ccc'
                  }}
                  onError={(e) => (e.target.src = defaultProfile)}
                />
              </Grid>

              <Grid item xs={12}>
                <Typography variant="h5" gutterBottom align="center">
                  {pet.name}
                </Typography>
              </Grid>

              <Grid item xs={6}>
                <Typography variant="body1"><strong>Type:</strong> {pet.species}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body1"><strong>Breed:</strong> {pet.breed}</Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body1">
                  <strong>Age:</strong> {pet.age ? `${pet.age} years` : 'Unknown'}
                </Typography>
              </Grid>
              <Grid item xs={6}>
                <Typography variant="body1"><strong>Gender:</strong> {pet.gender || 'Unknown'}</Typography>
              </Grid>
              {pet.description && (
                <Grid item xs={12}>
                  <Typography variant="body2" color="text.secondary">
                    <strong>Description:</strong> {pet.description}
                  </Typography>
                </Grid>
              )}
            </Grid>
          </CardContent>
        </Card>
      </DialogContent>

      <DialogActions>
        <Button onClick={onClose} variant="contained" color="primary">
          Close
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default PetProfilePopup;
