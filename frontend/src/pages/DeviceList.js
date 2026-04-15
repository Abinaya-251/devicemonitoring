import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Button, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Chip, IconButton, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem
} from '@mui/material';
import { Add, Delete, Edit, Visibility } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import { getDevices, createDevice, deleteDevice } from '../services/api';

const DEVICE_TYPES = ['ROUTER', 'SWITCH', 'ACCESS_POINT', 'FIREWALL', 'GATEWAY'];
const STATUSES = ['ONLINE', 'OFFLINE', 'WARNING', 'MAINTENANCE'];

const statusColors = {
  ONLINE: 'success', OFFLINE: 'error', WARNING: 'warning', MAINTENANCE: 'info',
};

export default function DeviceList() {
  const [devices, setDevices] = useState([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    deviceName: '', deviceType: 'ROUTER', ipAddress: '', macAddress: '',
    firmwareVersion: '', location: '', status: 'ONLINE',
  });
  const navigate = useNavigate();

  const fetchDevices = () => {
    getDevices().then((res) => setDevices(res.data)).catch(console.error);
  };

  useEffect(() => { fetchDevices(); }, []);

  const handleCreate = () => {
    createDevice(form).then(() => { setOpen(false); fetchDevices(); resetForm(); }).catch(console.error);
  };

  const handleDelete = (id) => {
    if (window.confirm('Delete this device?')) {
      deleteDevice(id).then(fetchDevices).catch(console.error);
    }
  };

  const resetForm = () => setForm({
    deviceName: '', deviceType: 'ROUTER', ipAddress: '', macAddress: '',
    firmwareVersion: '', location: '', status: 'ONLINE',
  });

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>Devices</Typography>
        <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>
          Add Device
        </Button>
      </Box>

      <TableContainer component={Paper} sx={{ bgcolor: '#132f4c' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>IP Address</TableCell>
              <TableCell>Location</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {devices.map((device) => (
              <TableRow key={device.id} hover>
                <TableCell>{device.deviceName}</TableCell>
                <TableCell><Chip label={device.deviceType} size="small" variant="outlined" /></TableCell>
                <TableCell sx={{ fontFamily: 'monospace' }}>{device.ipAddress}</TableCell>
                <TableCell>{device.location}</TableCell>
                <TableCell>
                  <Chip label={device.status} size="small" color={statusColors[device.status] || 'default'} />
                </TableCell>
                <TableCell>
                  <IconButton size="small" onClick={() => navigate(`/devices/${device.id}`)}>
                    <Visibility fontSize="small" />
                  </IconButton>
                  <IconButton size="small" color="error" onClick={() => handleDelete(device.id)}>
                    <Delete fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {devices.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 4 }}>
                  No devices registered. Click "Add Device" to get started.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add Device Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add New Device</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField label="Device Name" value={form.deviceName}
            onChange={(e) => setForm({ ...form, deviceName: e.target.value })} fullWidth required />
          <TextField label="Device Type" select value={form.deviceType}
            onChange={(e) => setForm({ ...form, deviceType: e.target.value })} fullWidth>
            {DEVICE_TYPES.map((t) => <MenuItem key={t} value={t}>{t}</MenuItem>)}
          </TextField>
          <TextField label="IP Address" value={form.ipAddress}
            onChange={(e) => setForm({ ...form, ipAddress: e.target.value })} fullWidth required />
          <TextField label="MAC Address" value={form.macAddress}
            onChange={(e) => setForm({ ...form, macAddress: e.target.value })} fullWidth />
          <TextField label="Firmware Version" value={form.firmwareVersion}
            onChange={(e) => setForm({ ...form, firmwareVersion: e.target.value })} fullWidth />
          <TextField label="Location" value={form.location}
            onChange={(e) => setForm({ ...form, location: e.target.value })} fullWidth />
          <TextField label="Status" select value={form.status}
            onChange={(e) => setForm({ ...form, status: e.target.value })} fullWidth>
            {STATUSES.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
          </TextField>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreate}>Create</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
