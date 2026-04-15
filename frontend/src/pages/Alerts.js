import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Chip, Button, ButtonGroup
} from '@mui/material';
import { CheckCircle, DoneAll } from '@mui/icons-material';
import { getAlerts, acknowledgeAlert, resolveAlert } from '../services/api';

const severityColors = { CRITICAL: 'error', WARNING: 'warning', INFO: 'info' };
const statusColors = { OPEN: 'error', ACKNOWLEDGED: 'warning', RESOLVED: 'success' };

export default function Alerts() {
  const [alerts, setAlerts] = useState([]);
  const [filter, setFilter] = useState('ALL');

  const fetchAlerts = () => {
    const params = filter !== 'ALL' ? { status: filter } : {};
    getAlerts(params).then((res) => setAlerts(res.data)).catch(console.error);
  };

  useEffect(() => { fetchAlerts(); }, [filter]);

  const handleAcknowledge = (id) => {
    acknowledgeAlert(id, 'admin').then(fetchAlerts).catch(console.error);
  };

  const handleResolve = (id) => {
    resolveAlert(id).then(fetchAlerts).catch(console.error);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>Alerts</Typography>

      <ButtonGroup sx={{ mb: 3 }}>
        {['ALL', 'OPEN', 'ACKNOWLEDGED', 'RESOLVED'].map((s) => (
          <Button key={s} variant={filter === s ? 'contained' : 'outlined'} onClick={() => setFilter(s)}>
            {s}
          </Button>
        ))}
      </ButtonGroup>

      <TableContainer component={Paper} sx={{ bgcolor: '#132f4c' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Severity</TableCell>
              <TableCell>Device</TableCell>
              <TableCell>Message</TableCell>
              <TableCell>Status</TableCell>
              <TableCell>Triggered</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {alerts.map((alert) => (
              <TableRow key={alert.id} hover>
                <TableCell>
                  <Chip label={alert.severity} size="small" color={severityColors[alert.severity] || 'default'} />
                </TableCell>
                <TableCell>{alert.deviceName || alert.deviceId}</TableCell>
                <TableCell sx={{ maxWidth: 300 }}>{alert.message}</TableCell>
                <TableCell>
                  <Chip label={alert.status} size="small" color={statusColors[alert.status] || 'default'} />
                </TableCell>
                <TableCell>{alert.triggeredAt ? new Date(alert.triggeredAt).toLocaleString() : 'N/A'}</TableCell>
                <TableCell>
                  {alert.status === 'OPEN' && (
                    <Button size="small" startIcon={<CheckCircle />} onClick={() => handleAcknowledge(alert.id)}>
                      Ack
                    </Button>
                  )}
                  {(alert.status === 'OPEN' || alert.status === 'ACKNOWLEDGED') && (
                    <Button size="small" color="success" startIcon={<DoneAll />} onClick={() => handleResolve(alert.id)}>
                      Resolve
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
            {alerts.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} align="center" sx={{ py: 4 }}>No alerts found</TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
