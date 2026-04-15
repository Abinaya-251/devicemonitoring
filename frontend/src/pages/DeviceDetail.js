import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import {
  Box, Typography, Card, CardContent, Grid, Chip, Button, Tabs, Tab
} from '@mui/material';
import { ArrowBack } from '@mui/icons-material';
import { LineChart, Line, XAxis, YAxis, Tooltip, ResponsiveContainer, CartesianGrid } from 'recharts';
import { getDevice, getLatestTelemetry, getTelemetryHistory, getLogsByDevice } from '../services/api';

const statusColors = { ONLINE: 'success', OFFLINE: 'error', WARNING: 'warning', MAINTENANCE: 'info' };

export default function DeviceDetail() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [device, setDevice] = useState(null);
  const [telemetry, setTelemetry] = useState(null);
  const [history, setHistory] = useState([]);
  const [logs, setLogs] = useState([]);
  const [tab, setTab] = useState(0);

  useEffect(() => {
    getDevice(id).then((res) => setDevice(res.data)).catch(console.error);
    getLatestTelemetry(id).then((res) => setTelemetry(res.data)).catch(() => {});
    getTelemetryHistory(id).then((res) => setHistory(res.data || [])).catch(() => {});
    getLogsByDevice(id).then((res) => setLogs(res.data || [])).catch(() => {});
  }, [id]);

  if (!device) return <Typography>Loading...</Typography>;

  const chartData = history.map((h) => ({
    time: new Date(h.timestamp).toLocaleTimeString(),
    cpu: h.cpuUsage,
    memory: h.memoryUsage,
    bandwidth: h.bandwidthIn,
  }));

  return (
    <Box>
      <Button startIcon={<ArrowBack />} onClick={() => navigate('/devices')} sx={{ mb: 2 }}>
        Back to Devices
      </Button>

      <Box sx={{ display: 'flex', alignItems: 'center', gap: 2, mb: 3 }}>
        <Typography variant="h4" sx={{ fontWeight: 700 }}>{device.deviceName}</Typography>
        <Chip label={device.status} color={statusColors[device.status] || 'default'} />
        <Chip label={device.deviceType} variant="outlined" />
      </Box>

      {/* Device Info */}
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={6}>
          <Card sx={{ bgcolor: '#132f4c' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Device Information</Typography>
              <InfoRow label="IP Address" value={device.ipAddress} />
              <InfoRow label="MAC Address" value={device.macAddress || 'N/A'} />
              <InfoRow label="Firmware" value={device.firmwareVersion || 'N/A'} />
              <InfoRow label="Location" value={device.location || 'N/A'} />
              <InfoRow label="Registered" value={device.registeredAt ? new Date(device.registeredAt).toLocaleString() : 'N/A'} />
              <InfoRow label="Last Heartbeat" value={device.lastHeartbeat ? new Date(device.lastHeartbeat).toLocaleString() : 'N/A'} />
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} md={6}>
          <Card sx={{ bgcolor: '#132f4c' }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Latest Telemetry</Typography>
              {telemetry ? (
                <>
                  <InfoRow label="CPU Usage" value={`${telemetry.cpuUsage}%`} />
                  <InfoRow label="Memory Usage" value={`${telemetry.memoryUsage}%`} />
                  <InfoRow label="Bandwidth In" value={`${telemetry.bandwidthIn} Mbps`} />
                  <InfoRow label="Bandwidth Out" value={`${telemetry.bandwidthOut} Mbps`} />
                  <InfoRow label="Active Clients" value={telemetry.activeClients} />
                  <InfoRow label="Temperature" value={`${telemetry.temperature}°C`} />
                  <InfoRow label="Packet Loss" value={`${telemetry.packetLoss}%`} />
                  <InfoRow label="Latency" value={`${telemetry.latency} ms`} />
                </>
              ) : (
                <Typography color="textSecondary">No telemetry data available</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Tabs: Charts / Logs */}
      <Card sx={{ bgcolor: '#132f4c' }}>
        <Tabs value={tab} onChange={(e, v) => setTab(v)} sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tab label="Telemetry Charts" />
          <Tab label="Device Logs" />
        </Tabs>
        <CardContent>
          {tab === 0 && (
            chartData.length > 0 ? (
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData}>
                  <CartesianGrid strokeDasharray="3 3" stroke="#1e3a5f" />
                  <XAxis dataKey="time" tick={{ fill: '#90caf9', fontSize: 11 }} />
                  <YAxis tick={{ fill: '#90caf9' }} />
                  <Tooltip />
                  <Line type="monotone" dataKey="cpu" stroke="#ff5252" name="CPU %" dot={false} />
                  <Line type="monotone" dataKey="memory" stroke="#00e676" name="Memory %" dot={false} />
                  <Line type="monotone" dataKey="bandwidth" stroke="#00b0ff" name="Bandwidth In" dot={false} />
                </LineChart>
              </ResponsiveContainer>
            ) : (
              <Typography color="textSecondary" sx={{ py: 4, textAlign: 'center' }}>
                No telemetry history available. Data will appear once the simulator runs.
              </Typography>
            )
          )}
          {tab === 1 && (
            logs.length > 0 ? (
              <Box sx={{ maxHeight: 400, overflow: 'auto' }}>
                {logs.map((log, i) => (
                  <Box key={i} sx={{ py: 1, borderBottom: '1px solid #1e3a5f', fontFamily: 'monospace', fontSize: 13 }}>
                    <Chip label={log.level} size="small" sx={{ mr: 1, fontSize: 11 }}
                      color={log.level === 'ERROR' ? 'error' : log.level === 'WARN' ? 'warning' : 'default'} />
                    <Typography variant="caption" sx={{ mr: 2, color: '#90caf9' }}>
                      {new Date(log.timestamp).toLocaleString()}
                    </Typography>
                    {log.message}
                  </Box>
                ))}
              </Box>
            ) : (
              <Typography color="textSecondary" sx={{ py: 4, textAlign: 'center' }}>No logs available</Typography>
            )
          )}
        </CardContent>
      </Card>
    </Box>
  );
}

function InfoRow({ label, value }) {
  return (
    <Box sx={{ display: 'flex', justifyContent: 'space-between', py: 0.5 }}>
      <Typography color="textSecondary">{label}</Typography>
      <Typography sx={{ fontFamily: 'monospace' }}>{value}</Typography>
    </Box>
  );
}
