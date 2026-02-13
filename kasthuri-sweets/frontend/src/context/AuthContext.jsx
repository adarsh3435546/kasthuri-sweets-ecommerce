import { createContext, useState, useContext, useEffect } from 'react';
import api from '../api/axios';

const AuthContext = createContext();

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (token) {
            // Ideally verify token with backend, but specific instruction is just to check local storage
            // For now, assume if token exists, user is logged in (until 401 interceptor catches it)
            setUser({ token });
        }
        setLoading(false);
    }, [token]);

    const login = async (email, password) => {
        try {
            const response = await api.post('/api/auth/login', { email, password });

            // Backend returns { "token": "..." }
            const data = response.data;
            const newToken = data.token;

            if (newToken) {
                localStorage.setItem('token', newToken);
                setToken(newToken);
                setUser({ token: newToken, email }); // Store email too if useful
                return true;
            } else {
                console.error("Login successful but no token received");
                return false;
            }
        } catch (error) {
            console.error("Login Error:", error);
            // Throw a more specific error message if possible
            if (error.response && error.response.status === 401) {
                throw new Error("Invalid email or password");
            } else if (error.code === 'ERR_NETWORK') {
                throw new Error("Cannot connect to server. Is the backend running?");
            }
            throw error;
        }
    };

    const register = async (name, email, password) => {
        // Adjust payload based on standard Spring Boot User DTO.
        // "POST /api/auth/register"
        try {
            await api.post('/api/auth/register', { name, email, password });
            return true;
        } catch (error) {
            console.error("Registration failed", error);
            throw error;
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        setToken(null);
        setUser(null);
        window.location.href = '/login';
    };

    return (
        <AuthContext.Provider value={{ user, login, register, logout, loading }}>
            {!loading && children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => useContext(AuthContext);
