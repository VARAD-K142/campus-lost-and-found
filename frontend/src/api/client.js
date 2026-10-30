import axios from 'axios';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api/v1',
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to attach JWT token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle token expiry
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.status === 401) {
      // Don't auto-redirect on login attempt failure
      if (!error.config.url.includes('/auth/login')) {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        window.dispatchEvent(new Event('auth:unauthorized'));
      }
    }
    return Promise.reject(error);
  }
);

// API Service Wrappers
export const authApi = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  logout: () => api.post('/auth/logout'),
  getMe: () => api.get('/users/me'),
  updateProfile: (data) => api.put('/users/me', data),
};

export const itemsApi = {
  search: (params) => api.get('/items', { params }),
  getById: (id) => api.get(`/items/${id}`),
  create: (data) => api.post('/items', data),
  update: (id, data) => api.put(`/items/${id}`, data),
  delete: (id) => api.delete(`/items/${id}`),
  getMyItems: (params) => api.get('/items/my', { params }),
  adminUpdateStatus: (id, data) => api.patch(`/admin/items/${id}/status`, data),
  adminResolve: (id, data) => api.post(`/admin/items/${id}/resolve`, data),
};

export const claimsApi = {
  submit: (itemId, data) => api.post(`/items/${itemId}/claims`, data),
  getById: (id) => api.get(`/claims/${id}`),
  getByItem: (itemId) => api.get(`/claims/item/${itemId}`),
  getMyClaims: () => api.get('/users/me/claims'),
  getAllForAdmin: (params) => api.get('/admin/claims', { params }),
  review: (id, data) => api.post(`/admin/claims/${id}/review`, data),
};

export const collectionsApi = {
  record: (data) => api.post('/collections', data),
  getByItemId: (itemId) => api.get(`/collections/item/${itemId}`),
  getMyCollections: (params) => api.get('/collections/my', { params }),
  getAllForAdmin: (params) => api.get('/admin/collections', { params }),
};

export const dashboardApi = {
  getUserDashboard: () => api.get('/dashboard/user'),
  getAdminDashboard: () => api.get('/dashboard/admin'),
  getPublicStats: () => api.get('/dashboard/public'),
  getJdbcTest: () => api.get('/dashboard/jdbc-test'),
};

export const auditApi = {
  getAll: (params) => api.get('/admin/audit-logs', { params }),
};

export default api;
