import { useCallback } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import api from '../api/axiosConfig';
import { setCredentials, logout as logoutAction } from '../store/slices/authSlice';

// Wraps Redux auth state and the auth API calls behind one hook.
const useAuth = () => {
  const dispatch = useDispatch();
  const { user, isAuthenticated } = useSelector((state) => state.auth);

  const login = useCallback(
    async (credentials) => {
      const response = await api.post('/auth/login', credentials);
      const data = response.data.data;
      dispatch(
        setCredentials({
          user: { userId: data.userId, email: data.email },
          accessToken: data.accessToken,
          refreshToken: data.refreshToken,
        })
      );
      return data;
    },
    [dispatch]
  );

  const register = useCallback(
    async (registrationData) => {
      const response = await api.post('/auth/register', registrationData);
      const data = response.data.data;
      dispatch(
        setCredentials({
          user: { userId: data.userId, email: data.email },
          accessToken: data.accessToken,
          refreshToken: data.refreshToken,
        })
      );
      return data;
    },
    [dispatch]
  );

  const logout = useCallback(() => {
    dispatch(logoutAction());
  }, [dispatch]);

  return { user, isAuthenticated, login, register, logout };
};

export default useAuth;
