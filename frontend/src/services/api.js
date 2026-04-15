import axios from 'axios';

const API = axios.create({ baseURL: 'http://localhost:8080/api' });

// ========== DEVICES ==========
export const getDevices = () => API.get('/devices');
export const getDevice = (id) => API.get(`/devices/${id}`);
export const createDevice = (data) => API.post('/devices', data);
export const updateDevice = (id, data) => API.put(`/devices/${id}`, data);
export const deleteDevice = (id) => API.delete(`/devices/${id}`);
export const updateDeviceStatus = (id, status) => API.patch(`/devices/${id}/status?status=${status}`);
export const getDeviceStats = () => API.get('/devices/stats');
export const searchDevices = (name, page = 0, size = 10) =>
  API.get(`/devices/search?name=${name}&page=${page}&size=${size}`);

// ========== TELEMETRY ==========
export const getLatestTelemetry = (deviceId) => API.get(`/telemetry/${deviceId}`);
export const getTelemetryHistory = (deviceId, from, to) =>
  API.get(`/telemetry/${deviceId}/history`, { params: { from, to } });

// ========== ALERTS ==========
export const getAlerts = (params) => API.get('/alerts', { params });
export const acknowledgeAlert = (id, acknowledgedBy) =>
  API.patch(`/alerts/${id}/acknowledge?acknowledgedBy=${acknowledgedBy}`);
export const resolveAlert = (id) => API.patch(`/alerts/${id}/resolve`);
export const getAlertStats = () => API.get('/alerts/stats');

// ========== ALERT RULES ==========
export const getAlertRules = () => API.get('/alerts/rules');
export const createAlertRule = (data) => API.post('/alerts/rules', data);
export const updateAlertRule = (id, data) => API.put(`/alerts/rules/${id}`, data);
export const deleteAlertRule = (id) => API.delete(`/alerts/rules/${id}`);

// ========== LOGS ==========
export const searchLogs = (params) => API.get('/logs/search', { params });
export const getLogsByDevice = (deviceId) => API.get(`/logs/device/${deviceId}`);
export const getLogStats = () => API.get('/logs/stats');

// ========== DASHBOARD ==========
export const getDashboardSummary = () => API.get('/dashboard/summary');

// ========== NOTIFICATIONS ==========
export const getNotifications = () => API.get('/notifications');
