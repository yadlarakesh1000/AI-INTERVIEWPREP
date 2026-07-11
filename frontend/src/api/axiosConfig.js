import axios from 'axios';
import { store } from '../store/store';
import { setCredentials, logout } from '../store/slices/authSlice';

const BASE_URL = import.meta.env.VITE_API_BASE_URL;

const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor: attach the in-memory access token from Redux.
api.interceptors.request.use(
  (config) => {
    const token = store.getState().auth.accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Auth endpoints where a 401 means bad credentials, not an expired token —
// never attempt a refresh for these.
const AUTH_PATHS = ['/auth/login', '/auth/register', '/auth/refresh'];

// Share one in-flight refresh across concurrent 401s so we only hit
// /auth/refresh once when several requests fail together.
let refreshPromise = null;

const refreshAccessToken = async () => {
  const refreshToken = store.getState().auth.refreshToken;
  if (!refreshToken) {
    throw new Error('No refresh token available');
  }
  // Plain axios (not the api instance) so this call skips the interceptors.
  const response = await axios.post(`${BASE_URL}/auth/refresh`, { refreshToken });
  const data = response.data.data;
  store.dispatch(
    setCredentials({
      accessToken: data.accessToken,
      refreshToken: data.refreshToken,
    })
  );
  return data.accessToken;
};

// Response interceptor: on 401, refresh the token once and retry the
// original request; if the refresh fails, log out and go to /login.
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    const status = error.response?.status;
    const isAuthPath = AUTH_PATHS.some((path) => originalRequest?.url?.includes(path));

    if (status === 401 && !originalRequest._retry && !isAuthPath) {
      originalRequest._retry = true;
      try {
        if (!refreshPromise) {
          refreshPromise = refreshAccessToken().finally(() => {
            refreshPromise = null;
          });
        }
        const newAccessToken = await refreshPromise;
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return api(originalRequest);
      } catch (refreshError) {
        store.dispatch(logout());
        window.location.href = '/login';
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default api;
