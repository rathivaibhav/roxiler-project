// frontend/src/App.js
import React, { useState, useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import NavBar from './components/NavBar';
import Login from './components/Login';
import Register from './components/Register';
import AdminDashboard from './components/AdminDashboard';
import OwnerDashboard from './components/OwnerDashboard';
// --- FIX: Import StoreList, not UserDashboard ---
import StoreList from './components/StoreList';

// Helper to get user from localStorage on initial load
const getStoredUser = () => {
    try {
        const raw = localStorage.getItem('user');
        return raw ? JSON.parse(raw) : null;
    } catch {
        return null;
    }
};

export default function App() {
    const [user, setUser] = useState(getStoredUser());

    useEffect(() => {
        function onStorage() {
            setUser(getStoredUser());
        }
        window.addEventListener('storage', onStorage);
        return () => window.removeEventListener('storage', onStorage);
    }, []);

    // Helper variables for routing
    const roles = user?.roles || [];
    const isAdmin = roles.includes('SYSTEM_ADMIN');
    const isOwner = roles.includes('STORE_OWNER');

    return (
        // NO <BrowserRouter> here
        <>
            <NavBar user={user} setUser={setUser} />

            <div className="container mx-auto p-4">
                <Routes>
                    {/* Public Routes */}
                    <Route path="/login" element={<Login setUser={setUser} />} />
                    <Route path="/register" element={<Register />} />

                    {/* Admin Route */}
                    <Route
                        path="/admin"
                        element={isAdmin ? <AdminDashboard /> : <Navigate to="/login" />}
                    />
                    {/* Owner Route */}
                    <Route
                        path="/owner"
                        element={isOwner ? <OwnerDashboard /> : <Navigate to="/login" />}
                    />

                    {/* --- FIX: User's main pages go to StoreList --- */}
                    <Route
                        path="/me"
                        element={user ? <StoreList /> : <Navigate to="/login" />}
                    />
                    <Route
                        path="/stores"
                        element={user ? <StoreList /> : <Navigate to="/login" />}
                    />

                    {/* Default Route */}
                    <Route path="*" element={<Navigate to={user ? "/me" : "/login"} />} />
                </Routes>
            </div>
        </>
    );
}