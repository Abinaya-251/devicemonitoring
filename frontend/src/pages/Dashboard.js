import React, { useEffect, useState } from 'react';
import { Grid, Card, CardContent, Typography, Box, Chip } from '@mui/material';
import { Devices, Warning, CheckCircle, Error, CloudOff } from '@mui/icons-material';
import { PieChart, Pie, Cell, ResponsiveContainer, BarChart, Bar, XAxis, YAxis, Tooltip } from 'recharts';
import { getDashboardSummary } from '../services/api';

const COLORS = ['#00e676', '#ff5252', '#ffab00', '#90caf9'];

export default function Dashboard() {
  const [summary, setSummary] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchData = () => {
    getDashboardSummary()
      .then((res) => setSummary(res.data))
      .catch((err) => console.error('Dashboard fetch error:', err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 10000);
    return () => clearInterval(interval);
  }, []);

  if (loading) return <Typography>Loading dashboard...</Typography>;
  if (!summary) return <Typography color="error">Failed to load dashboard</Typography>;

  const statusData = [
    { name: 'Online', value: summary.onlineDevices, color: '#00e676' },
    { name: 'Offline', value: summary.offlineDevices, color: '#ff5252' },
    { name: 'Warning', value: summary.warningDevices, color: '#ffab00' },
  ].filter((d) => d.value > 0);

  const typeData = summary.devicesByType
    ? Object.entries(summary.devicesByType).map(([key, value]) => ({ name: key, count: value }))
    : [];

  const alertData = summary.alertsBySeverity
    ? Object.entries(summary.alertsBySeverity).map(([key, value]) => ({ name: key, count: value }))
    : [];

  return (
    <Box>
      <Typography variant="h4" gutterBottom sx={{ fontWeight: 700 }}>
        Dashboard
      </Typography>

      {/* Summary Cards */}
      <Grid container spacing={3} sx={{ mb: 4 }}>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: '#132f4c' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Devices sx={{ color: '#00b0ff', mr: 1 }} />
                <Typography color="textSecondary">Total Devices</Typography>
              </Box>
              <Typography variant="h3" sx={{ fontWeight: 700 }}>{summary.totalDevices}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: '#132f4c' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <CheckCircle sx={{ color: '#00e676', mr: 1 }} />
                <Typography color="textSecondary">Online</Typography>
              </Box>
              <Typography variant="h3" sx={{ fontWeight: 700, color: '#00e676' }}>{summary.onlineDevices}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: '#132f4c' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Warning sx={{ color: '#ffab00', mr: 1 }} />
                <Typography color="textSecondary">Open Alerts</Typography>
              </Box>
              <Typography variant="h3" sx={{ fontWeight: 700, color: '#ffab00' }}>{summary.openAlerts}</Typography>
            </CardContent>
          </Card>
        </Grid>
        <Grid item xs={12} sm={6} md={3}>
          <Card sx={{ bgcolor: '#132f4c' }}>
            <CardContent>
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 1 }}>
                <Error sx={{ color: '#ff5252', mr: 1 }} />
                <Typography color="textSecondary">Critical Alerts</Typography>
              </Box>
              <Typography variant="h3" sx={{ fontWeight: 700, color: '#ff5252' }}>{summary.criticalAlerts}</Typography>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Charts */}
      <Grid container spacing={3}>
        <Grid item xs={12} md={4}>
          <Card sx={{ bgcolor: '#132f4c', height: 350 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Device Status</Typography>
              {statusData.length > 0 ? (
                <ResponsiveContainer width="100%" height={250}>
                  <PieChart>
                    <Pie data={statusData} dataKey="value" nameKey="name" cx="50%" cy="50%"
                         outerRadius={80} label={({ name, value }) => `${name}: ${value}`}>
                      {statusData.map((entry, i) => (
                        <Cell key={i} fill={entry.color} />
                      ))}
                    </Pie>
                    <Tooltip />
                  </PieChart>
                </ResponsiveContainer>
              ) : (
                <Typography color="textSecondary" sx={{ mt: 4, textAlign: 'center' }}>No devices registered</Typography>
              )}
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ bgcolor: '#132f4c', height: 350 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Devices by Type</Typography>
              <ResponsiveContainer width="100%" height={250}>
                <BarChart data={typeData}>
                  <XAxis dataKey="name" tick={{ fill: '#90caf9', fontSize: 11 }} />
                  <YAxis tick={{ fill: '#90caf9' }} />
                  <Tooltip />
                  <Bar dataKey="count" fill="#00b0ff" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card sx={{ bgcolor: '#132f4c', height: 350 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom>Alerts by Severity</Typography>
              <ResponsiveContainer width="100%" height={250}>
                <BarChart data={alertData}>
                  <XAxis dataKey="name" tick={{ fill: '#90caf9', fontSize: 11 }} />
                  <YAxis tick={{ fill: '#90caf9' }} />
                  <Tooltip />
                  <Bar dataKey="count" fill="#ffab00" radius={[4, 4, 0, 0]} />
                </BarChart>
              </ResponsiveContainer>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}
