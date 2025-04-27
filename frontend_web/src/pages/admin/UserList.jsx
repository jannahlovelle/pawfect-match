import { Card, CardHeader, CardContent, Button, Stack, Box } from '@mui/material';
import { DataGrid } from '@mui/x-data-grid';
import { deleteUser } from './api';
import styles from '../../styles/AdminDashboard.module.css';

const UserList = ({ users, refreshUsers, editUser, setEditUser }) => {
  const handleDelete = async (id) => {
    if (!id) {
      alert('Cannot delete user: Invalid user ID');
      return;
    }
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    try {
      await deleteUser(id);
      refreshUsers();
    } catch (err) {
      alert('Failed to delete user: ' + (err.response?.data?.message || err.message));
    }
  };

  const columns = [
    { field: 'firstName', headerName: 'First Name', flex: 1 },
    { field: 'lastName', headerName: 'Last Name', flex: 1 },
    { field: 'email', headerName: 'Email', flex: 1.5 },
    { field: 'phone', headerName: 'Phone', flex: 1 },
    { field: 'address', headerName: 'Address', flex: 1.5 },
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
            onClick={() => setEditUser(params.row)}
          >
            Edit
          </Button>
          <Button
            size="small"
            variant="contained"
            color="error"
            onClick={() => handleDelete(params.row.userID)}
          >
            Delete
          </Button>
        </Stack>
      ),
    },
  ];

  return (
    <Card className={styles.card}>
      <CardHeader
        title="User Management"
        titleTypographyProps={{ variant: 'h5' }}
      />
      <CardContent>
        <Box className={styles.tableContainer}>
          <DataGrid
            rows={users}
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

export default UserList;