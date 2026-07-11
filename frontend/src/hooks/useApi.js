import { useCallback } from 'react';
import api from '../api/axiosConfig';

// Extracts the backend's ApiResponse message when a call fails so
// components can show a friendly error instead of an Axios stack trace.
const toFriendlyError = (error) => {
  const message =
    error.response?.data?.message || error.message || 'Something went wrong. Please try again.';
  return new Error(message);
};

// Thin wrapper over the configured Axios instance (JWT interceptor already
// attached). Each method returns the backend's ApiResponse body:
// { success, message, data }.
const useApi = () => {
  const get = useCallback(async (url, config) => {
    try {
      const response = await api.get(url, config);
      return response.data;
    } catch (error) {
      throw toFriendlyError(error);
    }
  }, []);

  const post = useCallback(async (url, body, config) => {
    try {
      const response = await api.post(url, body, config);
      return response.data;
    } catch (error) {
      throw toFriendlyError(error);
    }
  }, []);

  const put = useCallback(async (url, body, config) => {
    try {
      const response = await api.put(url, body, config);
      return response.data;
    } catch (error) {
      throw toFriendlyError(error);
    }
  }, []);

  const del = useCallback(async (url, config) => {
    try {
      const response = await api.delete(url, config);
      return response.data;
    } catch (error) {
      throw toFriendlyError(error);
    }
  }, []);

  return { get, post, put, del };
};

export default useApi;
