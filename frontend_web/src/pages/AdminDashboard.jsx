import { useState, useEffect } from 'react';
import {
  Box,
  Button,
  Drawer,
  List,
  ListItem,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Typography,
  Alert,
  CircularProgress,
  Fade,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { People, Pets, Group } from '@mui/icons-material';
import UserList from './admin/UserList';
import UserEditForm from './admin/UserEditForm';
import PetList from './admin/PetList';
import PetEditForm from './admin/PetEditForm';
import UserProfileList from './admin/UserProfileList';
import { useUsers } from './admin/useUsers';
import { usePets } from './admin/usePets';
import { useUserProfiles } from './admin/useUserProfiles';
import styles from '../styles/AdminDashboard.module.css';

const AdminDashboard = () => {
  const [activeTab, setActiveTab] = useState(() => localStorage.getItem('adminActiveTab') || 'users');
  const [editUser, setEditUser] = useState(null);
  const [editPet, setEditPet] = useState(null);
  const navigate = useNavigate();

  const { users, loading: usersLoading, error: usersError, refreshUsers } = useUsers();
  const { pets, loading: petsLoading, error: petsError, refreshPets } = usePets();
  const { userProfiles, loading: profilesLoading, error: profilesError, refreshUserProfiles } = useUserProfiles();

  useEffect(() => {
    localStorage.setItem('adminActiveTab', activeTab);
  }, [activeTab]);

  const handleLogout = () => {
    localStorage.clear();
    navigate('/login');
  };

  const loading = activeTab === 'users' ? usersLoading : activeTab === 'pets' ? petsLoading : profilesLoading;
  const error = activeTab === 'users' ? usersError : activeTab === 'pets' ? petsError : profilesError;

  return (
    <Box className={styles.dashboardContainer}>
      {/* Sidebar */}
      <Drawer
        variant="permanent"
        className={styles.sidebar}
        classes={{ paper: styles.sidebarPaper }}
      >
        <Typography variant="h6" className={styles.sidebarTitle}>
          Admin Dashboard
        </Typography>
        <List>
          <ListItem disablePadding>
            <ListItemButton
              selected={activeTab === 'users'}
              onClick={() => setActiveTab('users')}
              className={activeTab === 'users' ? styles.activeTab : ''}
            >
              <ListItemIcon>
                <People />
              </ListItemIcon>
              <ListItemText primary="Users" />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
            <ListItemButton
              selected={activeTab === 'pets'}
              onClick={() => setActiveTab('pets')}
              className={activeTab === 'pets' ? styles.activeTab : ''}
            >
              <ListItemIcon>
                <Pets />
              </ListItemIcon>
              <ListItemText primary="Pets" />
            </ListItemButton>
          </ListItem>
          <ListItem disablePadding>
            <ListItemButton
              selected={activeTab === 'profiles'}
              onClick={() => setActiveTab('profiles')}
              className={activeTab === 'profiles' ? styles.activeTab : ''}
            >
              <ListItemIcon>
                <Group />
              </ListItemIcon>
              <ListItemText primary="Users with Pets" />
            </ListItemButton>
          </ListItem>
        </List>
        <Button
          variant="contained"
          color="secondary"
          onClick={handleLogout}
          className={styles.logoutButton}
        >
          Logout
        </Button>
      </Drawer>

      {/* Main Content */}
      <Box className={styles.mainContent}>
        {error && (
          <Fade in={true}>
            <Alert severity="error" className={styles.errorAlert}>
              {error}
            </Alert>
          </Fade>
        )}
        {loading ? (
          <Box className={styles.loadingContainer}>
            <CircularProgress />
          </Box>
        ) : (
          <Fade in={true}>
            <Box>
              {activeTab === 'users' && (
                <>
                  <UserList
                    users={users}
                    refreshUsers={refreshUsers}
                    editUser={editUser}
                    setEditUser={setEditUser}
                  />
                  {editUser && (
                    <UserEditForm
                      user={editUser}
                      onClose={() => setEditUser(null)}
                      refreshUsers={refreshUsers}
                    />
                  )}
                </>
              )}
              {activeTab === 'pets' && (
                <>
                  <PetList
                    pets={pets}
                    refreshPets={refreshPets}
                    editPet={editPet}
                    setEditPet={setEditPet}
                  />
                  {editPet && (
                    <PetEditForm
                      pet={editPet}
                      onClose={() => setEditPet(null)}
                      refreshPets={refreshPets}
                    />
                  )}
                </>
              )}
              {activeTab === 'profiles' && (
                <UserProfileList
                  userProfiles={userProfiles}
                  refreshUserProfiles={refreshUserProfiles}
                  setEditUser={setEditUser}
                  setEditPet={setEditPet}
                />
              )}
            </Box>
          </Fade>
        )}
      </Box>
    </Box>
  );
};

export default AdminDashboard;