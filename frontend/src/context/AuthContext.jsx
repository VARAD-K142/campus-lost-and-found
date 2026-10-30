import React, { createContext, useContext, useState, useEffect } from 'react';
import { authApi } from '../api/client';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    const savedUser = localStorage.getItem('user');
    return savedUser ? JSON.parse(savedUser) : null;
  });
  const [token, setToken] = useState(() => localStorage.getItem('token') || null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const handleUnauthorized = () => {
      logout();
    };
    window.addEventListener('auth:unauthorized', handleUnauthorized);
    return () => window.removeEventListener('auth:unauthorized', handleUnauthorized);
  }, []);

  useEffect(() => {
    // Verify token on initial load
    if (token) {
      authApi.getMe()
        .then((res) => {
          if (res.data.success) {
            setUser(res.data.data);
            localStorage.setItem('user', JSON.stringify(res.data.data));
          }
        })
        .catch(() => {
          logout();
        })
        .finally(() => setLoading(false));
    } else {
      setLoading(false);
    }
  }, [token]);

  const login = async (email, password) => {
    const response = await authApi.login({ email, password });
    if (response.data.success) {
      const authData = response.data.data;
      localStorage.setItem('token', authData.token);
      setToken(authData.token);

      const userData = {
        id: authData.id,
        fullName: authData.fullName,
        email: authData.email,
        role: authData.role,
        studentId: authData.studentId,
        department: authData.department,
      };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return authData;
    }
    throw new Error(response.data.message || 'Login failed');
  };

  const register = async (formData) => {
    const response = await authApi.register(formData);
    if (response.data.success) {
      const authData = response.data.data;
      localStorage.setItem('token', authData.token);
      setToken(authData.token);

      const userData = {
        id: authData.id,
        fullName: authData.fullName,
        email: authData.email,
        role: authData.role,
        studentId: authData.studentId,
        department: authData.department,
      };
      localStorage.setItem('user', JSON.stringify(userData));
      setUser(userData);
      return authData;
    }
    throw new Error(response.data.message || 'Registration failed');
  };

  const logout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    setToken(null);
    setUser(null);
  };

  const updateUser = (updatedFields) => {
    setUser((prev) => {
      const updated = { ...prev, ...updatedFields };
      localStorage.setItem('user', JSON.stringify(updated));
      return updated;
    });
  };

  const value = {
    user,
    token,
    loading,
    isAuthenticated: !!user,
    isAdmin: user?.role === 'ADMIN',
    login,
    register,
    logout,
    updateUser,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
