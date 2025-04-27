import EditForm from './EditForm';
import { updateUser } from './api';

const UserEditForm = ({ user, onClose, refreshUsers }) => {
  const fields = [
    { label: 'First Name', name: 'firstName', required: true },
    { label: 'Last Name', name: 'lastName', required: true },
    { label: 'Email', name: 'email', required: true },
    { label: 'Phone', name: 'phone' },
    { label: 'Address', name: 'address' },
    { label: 'Password (optional)', name: 'password', type: 'password' },
  ];

  const handleSubmit = async (formData) => {
    const payload = { ...formData };
    if (!payload.password) delete payload.password;
    await updateUser(user.userID, payload);
    refreshUsers();
  };

  return (
    <EditForm
      open={!!user}
      title="User"
      fields={fields}
      initialData={{
        userID: user.userID || '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        email: user.email || '',
        phone: user.phone || '',
        address: user.address || '',
        password: '',
      }}
      onClose={onClose}
      onSubmit={handleSubmit}
    />
  );
};

export default UserEditForm;