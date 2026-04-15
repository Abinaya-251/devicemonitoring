import React, { useEffect, useState } from 'react';
import {
  Box, Typography, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
  Paper, Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, MenuItem,
  IconButton, Chip
} from '@mui/material';
import { Add, Delete, Edit } from '@mui/icons-material';
import { getAlertRules, createAlertRule, deleteAlertRule } from '../services/api';

const METRICS = ['cpuUsage', 'memoryUsage', 'bandwidthIn', 'bandwidthOut', 'temperature', 'packetLoss', 'latency'];
const CONDITIONS = ['GREATER_THAN', 'LESS_THAN', 'EQUALS'];
const SEVERITIES = ['INFO', 'WARNING', 'CRITICAL'];

export default function AlertRules() {
  const [rules, setRules] = useState([]);
  const [open, setOpen] = useState(false);
  const [form, setForm] = useState({
    ruleName: '', metric: 'cpuUsage', condition: 'GREATER_THAN',
    threshold: 90, severity: 'CRITICAL', enabled: true, cooldownMinutes: 5,
  });

  const fetchRules = () => {
    getAlertRules().then((res) => setRules(res.data)).catch(console.error);
  };

  useEffect(() => { fetchRules(); }, []);

  const handleCreate = () => {
    createAlertRule(form).then(() => { setOpen(false); fetchRules(); }).catch(console.error);
  };

  const handleDelete = (id) => {
    if (window.confirm('Delete this rule?')) {
      deleteAlertRule(id).then(fetchRules).catch(console.error);
    }
  };

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>Alert Rules</Typography>
        <Button variant="contained" startIcon={<Add />} onClick={() => setOpen(true)}>
          Add Rule
        </Button>
      </Box>

      <TableContainer component={Paper} sx={{ bgcolor: '#132f4c' }}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>Rule Name</TableCell>
              <TableCell>Metric</TableCell>
              <TableCell>Condition</TableCell>
              <TableCell>Threshold</TableCell>
              <TableCell>Severity</TableCell>
              <TableCell>Enabled</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {rules.map((rule) => (
              <TableRow key={rule.id} hover>
                <TableCell>{rule.ruleName}</TableCell>
                <TableCell><Chip label={rule.metric} size="small" variant="outlined" /></TableCell>
                <TableCell>{rule.condition}</TableCell>
                <TableCell sx={{ fontFamily: 'monospace' }}>{rule.threshold}</TableCell>
                <TableCell>
                  <Chip label={rule.severity} size="small"
                    color={rule.severity === 'CRITICAL' ? 'error' : rule.severity === 'WARNING' ? 'warning' : 'info'} />
                </TableCell>
                <TableCell>
                  <Chip label={rule.enabled ? 'Yes' : 'No'} size="small"
                    color={rule.enabled ? 'success' : 'default'} />
                </TableCell>
                <TableCell>
                  <IconButton size="small" color="error" onClick={() => handleDelete(rule.id)}>
                    <Delete fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
            {rules.length === 0 && (
              <TableRow>
                <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                  No alert rules configured. Click "Add Rule" to create one.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>

      {/* Add Rule Dialog */}
      <Dialog open={open} onClose={() => setOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create Alert Rule</DialogTitle>
        <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
          <TextField label="Rule Name" value={form.ruleName}
            onChange={(e) => setForm({ ...form, ruleName: e.target.value })} fullWidth required />
          <TextField label="Metric" select value={form.metric}
            onChange={(e) => setForm({ ...form, metric: e.target.value })} fullWidth>
            {METRICS.map((m) => <MenuItem key={m} value={m}>{m}</MenuItem>)}
          </TextField>
          <TextField label="Condition" select value={form.condition}
            onChange={(e) => setForm({ ...form, condition: e.target.value })} fullWidth>
            {CONDITIONS.map((c) => <MenuItem key={c} value={c}>{c}</MenuItem>)}
          </TextField>
          <TextField label="Threshold" type="number" value={form.threshold}
            onChange={(e) => setForm({ ...form, threshold: parseFloat(e.target.value) })} fullWidth />
          <TextField label="Severity" select value={form.severity}
            onChange={(e) => setForm({ ...form, severity: e.target.value })} fullWidth>
            {SEVERITIES.map((s) => <MenuItem key={s} value={s}>{s}</MenuItem>)}
          </TextField>
          <TextField label="Cooldown (minutes)" type="number" value={form.cooldownMinutes}
            onChange={(e) => setForm({ ...form, cooldownMinutes: parseInt(e.target.value) })} fullWidth />
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpen(false)}>Cancel</Button>
          <Button variant="contained" onClick={handleCreate}>Create</Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
}
