import { Card, CardHeader, CardContent, Button, Box, Typography } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import styles from '../../styles/AdminDashboard.module.css';

const UserProfileList = ({ userProfiles, refreshUserProfiles, setEditUser, setEditPet }) => {
  const columns = [
    { field: 'firstName', headerName: 'First Name', flex: 1 },
    { field: 'lastName', headerName: 'Last Name', flex: 1 },
    { field: 'email', headerName: 'Email', flex: 1.5 },
    { field: 'phone', headerName: 'Phone', flex: 1 },
    { field: 'address', headerName: 'Address', flex: 1.5 },
    {
      field: 'pets',
      headerName: 'Pets',
      flex: 2,
      sortable: false,
      renderCell: (params) => {
        console.log(`Pets for user ${params.row.userID}:`, params.row.pets); // Debug pets
        return (
          <Box className={styles.petList}>
            {params.row.pets.length > 0 ? (
              params.row.pets.map((pet) => (
                <Box key={pet.petId || `pet-${Math.random()}`} className={styles.petItem}>
                  <Typography variant="body2">
                    {pet.name === 'Unknown' && pet.species === 'Unknown'
                      ? 'Incomplete pet data'
                      : `${pet.name} (${pet.species})`}
                  </Typography>
                  <Button
                    size="small"
                    variant="text"
                    onClick={() => setEditPet(pet)}
                    disabled={!pet.petId}
                  >
                    Edit Pet
                  </Button>
                </Box>
              ))
            ) : (
              <Typography variant="body2">No pets</Typography>
            )}
          </Box>
        );
      },
    },
    {
      field: 'actions',
      headerName: 'Actions',
      flex: 1,
      sortable: false,
      renderCell: (params) => (
        <Button
          size="small"
          variant="outlined"
          onClick={() => setEditUser(params.row)}
        >
          Edit User
        </Button>
      ),
    },
  ];

  return (
    <Card className={styles.card}>
      <CardHeader
        title="Users with Pets"
        titleTypographyProps={{ variant: 'h5' }}
      />
      <CardContent>
        <Box className={styles.tableContainer}>
          <DataGrid
            rows={userProfiles}
            columns={columns}
            getRowId={(row) => row.userID}
            autoHeight
            pageSizeOptions={[5, 10, 25]}
            disableSelectionOnClick
            className={styles.dataGrid}
          />
        </Box>
      </CardContent>
    </Card>
  );
};

export default UserProfileList;