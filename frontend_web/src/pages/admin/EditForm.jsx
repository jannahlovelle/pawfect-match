import { useState } from 'react';
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  TextField,
  Stack,
  Fade,
} from '@mui/material';
import { formatDateForInput } from './dateUtils';

const EditForm = ({ open, title, fields, initialData, onClose, onSubmit }) => {
  const [formData, setFormData] = useState(initialData);
  const [errors, setErrors] = useState({});

  const handleChange = (field, value) => {
    setFormData({ ...formData, [field]: value });
    if (field === 'password' && value && value.length < 6) {
      setErrors({ ...errors, password: 'Password must be at least 6 characters long' });
    } else {
      setErrors({ ...errors, [field]: '' });
    }
  };

  const handleSubmit = async () => {
    try {
      await onSubmit(formData);
      onClose();
      setErrors({});
    } catch (err) {
      alert(`Failed to update ${title.toLowerCase()}: ${err.response?.data?.message || err.message}`);
    }
  };

  return (
    <Dialog
      open={open}
      onClose={onClose}
      maxWidth="sm"
      fullWidth
      TransitionComponent={Fade}
      TransitionProps={{ timeout: 300 }}
    >
      <DialogTitle>Edit {title}</DialogTitle>
      <DialogContent>
        <Stack spacing={2} sx={{ mt: 1 }}>
          {fields.map(({ label, name, type = 'text', required }) => (
            <TextField
              key={name}
              label={label}
              type={type}
              fullWidth
              value={type === 'date' ? formatDateForInput(formData[name]) : formData[name] || ''}
              onChange={(e) => handleChange(name, e.target.value)}
              required={required}
              error={!!errors[name]}
              helperText={errors[name]}
              variant="outlined"
              size="small"
            />
          ))}
        </Stack>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={onClose}>
          Cancel
        </Button>
        <Button variant="contained" onClick={handleSubmit}>
          Save
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default EditForm;