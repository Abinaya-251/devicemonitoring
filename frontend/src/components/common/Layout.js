import React from 'react';
import { Box, Drawer, List, ListItem, ListItemIcon, ListItemText, AppBar, Toolbar, Typography } from '@mui/material';
import { Dashboard, Devices, Warning, Rule, Search, Notifications } from '@mui/icons-material';
import { useNavigate, useLocation } from 'react-router-dom';

const drawerWidth = 240;

const menuItems = [
  { text: 'Dashboard', icon: <Dashboard />, path: '/' },
  { text: 'Devices', icon: <Devices />, path: '/devices' },
  { text: 'Alerts', icon: <Warning />, path: '/alerts' },
  { text: 'Alert Rules', icon: <Rule />, path: '/alerts/rules' },
  { text: 'Log Explorer', icon: <Search />, path: '/logs' },
];

export default function Layout({ children }) {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Box sx={{ display: 'flex' }}>
      <AppBar position="fixed" sx={{ zIndex: (theme) => theme.zIndex.drawer + 1, bgcolor: '#0a1929' }}>
        <Toolbar>
          <Notifications sx={{ mr: 1, color: '#00e676' }} />
          <Typography variant="h6" noWrap sx={{ fontWeight: 700 }}>
            NetPulse
          </Typography>
          <Typography variant="body2" sx={{ ml: 2, color: '#90caf9' }}>
            Device Monitoring Platform
          </Typography>
        </Toolbar>
      </AppBar>

      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          '& .MuiDrawer-paper': { width: drawerWidth, boxSizing: 'border-box', bgcolor: '#0d2137' },
        }}
      >
        <Toolbar />
        <List>
          {menuItems.map((item) => (
            <ListItem
              button
              key={item.text}
              onClick={() => navigate(item.path)}
              selected={location.pathname === item.path}
              sx={{
                '&.Mui-selected': { bgcolor: 'rgba(0,230,118,0.15)', borderRight: '3px solid #00e676' },
                '&:hover': { bgcolor: 'rgba(0,230,118,0.08)' },
              }}
            >
              <ListItemIcon sx={{ color: location.pathname === item.path ? '#00e676' : '#90caf9' }}>
                {item.icon}
              </ListItemIcon>
              <ListItemText primary={item.text} />
            </ListItem>
          ))}
        </List>
      </Drawer>

      <Box component="main" sx={{ flexGrow: 1, p: 3 }}>
        <Toolbar />
        {children}
      </Box>
    </Box>
  );
}
