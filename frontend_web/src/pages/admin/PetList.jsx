import { useState } from 'react';
import {
  Card,
  CardHeader,
  CardContent,
  Button,
  Stack,
  Box,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  IconButton,
  TextField,
  Typography,
} from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { Delete, PhotoCamera } from '@mui/icons-material';
import { deletePet, updatePetPhoto, deletePetPhoto } from './api';
import styles from '../../styles/AdminDashboard.module.css';

const PetList = ({ pets, refreshPets, editPet, setEditPet }) => {
  const [photoDialogOpen, setPhotoDialogOpen] = useState(false);
  const [selectedPet, setSelectedPet] = useState(null);
  const [photoFile, setPhotoFile] = useState(null);

  const handleDelete = async (id) => {
    if (!id) {
      alert('Cannot delete pet: Invalid pet ID');
      return;
    }
    if (!window.confirm('Delete this pet?')) return;
    try {
      await deletePet(id);
      refreshPets();
    } catch (err) {
      alert('Failed to delete pet: ' + (err.response?.data?.message || err.message));
    }
  };

  const handlePhotoDialogOpen = (pet) => {
    console.log(`Opening photo dialog for pet ${pet.petId}:`, pet.photos); // Debug photos
    setSelectedPet(pet);
    setPhotoDialogOpen(true);
  };

  const handlePhotoDialogClose = () => {
    setPhotoDialogOpen(false);
    setSelectedPet(null);
    setPhotoFile(null);
  };

  const handlePhotoUpload = async () => {
    if (!photoFile) {
      alert('Please select a photo');
      return;
    }
    if (!selectedPet.petId) {
      alert('Invalid pet ID');
      return;
    }
    try {
      // Use the first photoId if available, or a placeholder (backend should handle)
      const photoId = selectedPet.photos?.[0]?.photoId || `temp-${selectedPet.petId}`;
      console.log(`Uploading photo for pet ${selectedPet.petId}, photoId: ${photoId}`, photoFile); // Debug upload
      const response = await updatePetPhoto(photoId, photoFile);
      console.log('Photo upload response:', response); // Debug response
      refreshPets();
      handlePhotoDialogClose();
    } catch (err) {
      console.error('Photo upload error:', err); // Debug error
      alert('Failed to update photo: ' + (err.response?.data?.message || err.message));
    }
  };

  const handlePhotoDelete = async (photoId) => {
    if (!photoId) {
      alert('Invalid photo ID');
      return;
    }
    if (!window.confirm('Delete this photo?')) return;
    try {
      await deletePetPhoto(photoId);
      refreshPets();
    } catch (err) {
      alert('Failed to delete photo: ' + (err.response?.data?.message || err.message));
    }
  };

  const columns = [
    { field: 'name', headerName: 'Name', flex: 1 },
    { field: 'species', headerName: 'Species', flex: 1 },
    { field: 'breed', headerName: 'Breed', flex: 1 },
    { field: 'gender', headerName: 'Gender', flex: 0.5 },
    { field: 'dateOfBirth', headerName: 'Date of Birth', flex: 1 },
    { field: 'weight', headerName: 'Weight', flex: 0.5 },
    { field: 'color', headerName: 'Color', flex: 1 },
    { field: 'description', headerName: 'Description', flex: 1.5 },
    { field: 'availabilityStatus', headerName: 'Availability', flex: 1 },
    { field: 'price', headerName: 'Price', flex: 0.5 },
    { field: 'pedigreeInfo', headerName: 'Pedigree Info', flex: 1 },
    { field: 'healthStatus', headerName: 'Health Status', flex: 1 },
    {
      field: 'photos',
      headerName: 'Photos',
      flex: 1,
      sortable: false,
      renderCell: (params) => {
        console.log(`Photos for pet ${params.row.petId}:`, params.row.photos); // Debug photos
        return (
          <Stack direction="row" spacing={1} alignItems="center">
            {params.row.photos?.[0]?.url ? (
              <>
                <img
                  src={params.row.photos[0].url}
                  alt={params.row.name || 'Pet photo'}
                  className={styles.petPhoto}
                  onError={(e) => console.error(`Failed to load image: ${params.row.photos[0].url}`, e)} // Debug image error
                />
                <IconButton
                  size="small"
                  onClick={() => handlePhotoDelete(params.row.photos[0].photoId)}
                >
                  <Delete />
                </IconButton>
              </>
            ) : (
              <Typography variant="body2">No photo</Typography>
            )}
            <IconButton
              size="small"
              onClick={() => handlePhotoDialogOpen(params.row)}
            >
              <PhotoCamera />
            </IconButton>
          </Stack>
        );
      },
    },
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 1,
      sortable: false,
      renderCell: (params) => (
        <Stack direction="row" spacing={1}>
          <Button
            size="small"
            variant="outlined"
            onClick={() => setEditPet(params.row)}
          >
            Edit
          </Button>
          <Button
            size="small"
            variant="contained"
            color="error"
            onClick={() => handleDelete(params.row.petId)}
          >
            Delete
          </Button>
        </Stack>
      ),
    },
  ];

  return (
    <>
      <Card className={styles.card}>
        <CardHeader
          title="Pet Management"
          titleTypographyProps={{ variant: 'h5' }}
        />
        <CardContent>
          <Box className={styles.tableContainer}>
            <DataGrid
              rows={pets}
              columns={columns}
              getRowId={(row) => row.petId}
              autoHeight
              pageSizeOptions={[5, 10, 25]}
              disableSelectionOnClick
              className={styles.dataGrid}
            />
          </Box>
        </CardContent>
      </Card>
      <Dialog
        open={photoDialogOpen}
        onClose={handlePhotoDialogClose}
        maxWidth="sm"
        fullWidth
      >
        <DialogTitle>Update Pet Photo</DialogTitle>
        <DialogContent>
          <TextField
            type="file"
            inputProps={{ accept: 'image/jpeg,image/png' }}
            onChange={(e) => setPhotoFile(e.target.files[0])}
            fullWidth
            variant="outlined"
            size="small"
            helperText="Upload JPEG or PNG (max 5MB)"
          />
        </DialogContent>
        <DialogActions>
          <Button variant="outlined" onClick={handlePhotoDialogClose}>
            Cancel
          </Button>
          <Button variant="contained" onClick={handlePhotoUpload}>
            Upload
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default PetList;