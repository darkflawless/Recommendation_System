import { createContext, useContext, useState, useCallback } from 'react';
import { authAPI } from '../services/api';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try {
      const stored = localStorage.getItem('user');
      return stored ? JSON.parse(stored) : null;
    } catch {
      return null;
    }
  });

  const saveSession = useCallback((data) => {
    // data = { token, type, userId, email, role }
    localStorage.setItem('token', data.token);
    localStorage.setItem('user', JSON.stringify(data));
    setUser(data);
  }, []);

  const login = useCallback(async (credentials) => {
    const res = await authAPI.login(credentials);
    saveSession(res.data);
    return res.data;
  }, [saveSession]);

  const register = useCallback(async (payload) => {
    const res = await authAPI.register(payload);
    saveSession(res.data);
    return res.data;
  }, [saveSession]);

  const logout = useCallback(() => {
    localStorage.clear();
    setUser(null);
  }, []);

  const isAuthenticated = Boolean(user?.token);

  return (
    <AuthContext.Provider value={{ user, isAuthenticated, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used inside <AuthProvider>');
  return ctx;
};
