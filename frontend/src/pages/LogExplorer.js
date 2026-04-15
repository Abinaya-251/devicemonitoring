import React, { useState } from 'react';
import {
  Box, Typography, TextField, Button, Table, TableBody, TableCell, TableContainer,
  TableHead, TableRow, Paper, Chip, MenuItem, Grid
} from '@mui/material';
import { Search } from '@mui/icons-material';
import { searchLogs } from '../services/api';

const LEVELS = ['', 'DEBUG', 'INFO', 'WARN', 'ERROR', 'FATAL'];

const levelColors = {
  ERROR: 'error', FATAL: 'error', WARN: 'warning', INFO: 'info', DEBUG: 'default',
};

export default function LogExplorer() {
  const [logs, setLogs] = useState([]);
  const [query, setQuery] = useState('');
  const [level, setLevel] = useState('');
  const [deviceId, setDeviceId] = useState('');
  const [searched, setSearched] = useState(false);

  const handleSearch = () => {
    const params = {};
    if (query) params.q = query;
    if (level) params.level = level;
    if (deviceId) params.deviceId = deviceId;

    searchLogs(params)
      .then((res) => { setLogs(res.data); setSearched(true); })
      .catch(console.error);
  };

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>Log Explorer</Typography>

      {/* Search Filters */}
      <Paper sx={{ p: 3, mb: 3, bgcolor: '#132f4c' }}>
        <Grid container spacing={2} alignItems="center">
          <Grid item xs={12} md={4}>
            <TextField label="Search logs..." value={query} onChange={(e) => setQuery(e.target.value)}
              fullWidth placeholder="e.g. link down, port error" />
          </Grid>
          <Grid item xs={12} md={2}>
            <TextField label="Log Level" select value={level} onChange={(e) => setLevel(e.target.value)} fullWidth>
              <MenuItem value="">All Levels</MenuItem>
              {LEVELS.filter(Boolean).map((l) => <MenuItem key={l} value={l}>{l}</MenuItem>)}
            </TextField>
          </Grid>
          <Grid item xs={12} md={3}>
            <TextField label="Device ID" value={deviceId} onChange={(e) => setDeviceId(e.target.value)}
              fullWidth placeholder="Filter by device" />
          </Grid>
          <Grid item xs={12} md={2}>
            <Button variant="contained" startIcon={<Search />} onClick={handleSearch} fullWidth sx={{ height: 56 }}>
              Search
            </Button>
          </Grid>
        </Grid>
      </Paper>

      {/* Results */}
      <TableContainer component={Paper} sx={{ bgcolor: '#132f4c' }}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Timestamp</TableCell>
              <TableCell>Level</TableCell>
              <TableCell>Device</TableCell>
              <TableCell>Source</TableCell>
              <TableCell>Message</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {logs.map((log, i) => (
              <TableRow key={i} hover>
                <TableCell sx={{ whiteSpace: 'nowrap', fontSize: 12 }}>
                  {log.timestamp ? new Date(log.timestamp).toLocaleString() : 'N/A'}
                </TableCell>
                <TableCell>
                  <Chip label={log.level} size="small" color={levelColors[log.level] || 'default'} />
                </TableCell>
                <TableCell sx={{ fontFamily: 'monospace', fontSize: 12 }}>
                  {log.deviceName || log.deviceId}
                </TableCell>
                <TableCell>{log.source}</TableCell>
                <TableCell sx={{ fontFamily: 'monospace', fontSize: 12, maxWidth: 400 }}>
                  {log.message}
                </TableCell>
              </TableRow>
            ))}
            {searched && logs.length === 0 && (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 4 }}>No logs found matching your search</TableCell>
              </TableRow>
            )}
            {!searched && (
              <TableRow>
                <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
                  Use the search filters above and click "Search" to explore device logs
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </TableContainer>
    </Box>
  );
}
