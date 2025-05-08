import React, { useState, useEffect } from 'react';
import { Card, CardContent, Typography, Button, Grid, Avatar, TextField } from '@mui/material';
import { LocalizationProvider, DatePicker, TimePicker } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';

export default function BookingCalendar() {
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [selectedTime, setSelectedTime] = useState(new Date());
  const [user, setUser] = useState({ name: '', location: '', avatar: '' });

  // Fetch user data on mount
  useEffect(() => {
    // Replace with your API endpoint
    fetch('/users')
      .then((res) => res.json())
      .then((data) => {
        setUser(data);
      })
      .catch((err) => console.error('Failed to fetch user', err));
  }, []);

  const handleSubmit = () => {
    alert(`Appointment set for ${selectedDate.toDateString()} at ${selectedTime.toLocaleTimeString()}`);
  };

  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Grid container spacing={2} padding={2}>
        {/* Left side - Profile & Details */}
        <Grid item xs={12} md={4}>
          <Card>
            <CardContent>
              <Avatar
                alt={user.name}
                src={user.avatar || 'https://via.placeholder.com/150'}
                sx={{ width: 80, height: 80, mb: 2 }}
              />
              <Typography variant="h6">{user.name || 'Loading...'}</Typography>
              <Typography color="text.secondary">{user.location}</Typography>
              <Typography mt={2}>Selected Date: {selectedDate.toDateString()}</Typography>
              <Typography>Selected Time: {selectedTime.toLocaleTimeString()}</Typography>
            </CardContent>
          </Card>
        </Grid>

        {/* Right side - Calendar */}
        <Grid item xs={12} md={8}>
          <Card>
            <CardContent>
              <Typography variant="h5" gutterBottom>
                Select Date and Time
              </Typography>
              <DatePicker
                label="Pick a date"
                value={selectedDate}
                onChange={(newValue) => setSelectedDate(newValue)}
                renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
              />
              <TimePicker
                label="Pick a time"
                value={selectedTime}
                onChange={(newValue) => setSelectedTime(newValue)}
                renderInput={(params) => <TextField {...params} fullWidth margin="normal" />}
              />
              <Button
                variant="contained"
                color="primary"
                fullWidth
                onClick={handleSubmit}
                sx={{ mt: 2 }}
              >
                Submit Appointment
              </Button>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </LocalizationProvider>
  );
}
