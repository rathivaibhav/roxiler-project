import React, { useRef, useState, useEffect } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';

export default function NavBar({ user, setUser }) {
    const navigate = useNavigate();
    const location = useLocation();
    const profileRef = useRef(null);

    const [openMobile, setOpenMobile] = useState(false);
    const [openProfile, setOpenProfile] = useState(false);

    useEffect(() => {
        function onDoc(e) {
            if (profileRef.current && !profileRef.current.contains(e.target)) setOpenProfile(false);
        }
        document.addEventListener('click', onDoc);
        return () => document.removeEventListener('click', onDoc);
    }, []);

    useEffect(() => setOpenMobile(false), [location.pathname]);

    function logout() {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setUser(null);
        navigate('/login');
    }

    const roles = (() => {
        if (!user) return [];
        const r = user.roles ?? user.authorities ?? [];
        if (Array.isArray(r)) {
            return r.map(it => (typeof it === 'string' ? it : (it.name || it.role || it.authority || ''))).map(s => s?.toString()?.toUpperCase()).filter(Boolean);
        }
        if (typeof r === 'string') return [r.toUpperCase()];
        return [];
    })();

    const isAdmin = roles.includes('SYSTEM_ADMIN');
    const isOwner = roles.includes('STORE_OWNER');
    const token = localStorage.getItem('token');

    const activeClass = (path) => (location.pathname === path ? 'text-blue-900 font-semibold' : 'text-blue-900 hover:text-blue-800');

    return (
        <header className="w-full bg-blue-50 border-b border-blue-200">
            <div className="container mx-auto px-4 py-3 flex items-center justify-between">
                {/* ... (Logo/Brand is unchanged) ... */}

                <nav className="hidden md:flex items-center gap-4">
                    <Link to="/stores" className={`px-2 py-1 rounded ${activeClass('/stores')}`}>Stores</Link>

                    {/* Dashboard shortcuts */}
                    {isAdmin && <Link to="/admin" className={`px-2 py-1 rounded ${activeClass('/admin')}`}>Admin</Link>}
                    {isOwner && <Link to="/owner" className={`px-2 py-1 rounded ${activeClass('/owner')}`}>Owner Dashboard</Link>}

                    {/* Show "My Dashboard" for ANY logged-in user */}
                    {user && (
                        <Link
                            to="/me"
                            className={`px-2 py-1 rounded ${location.pathname === '/me' ? 'text-blue-900 font-semibold' : 'text-blue-900 hover:text-blue-800'}`}
                        >
                            My Dashboard
                        </Link>
                    )}
                </nav>

                <div className="flex items-center gap-3">
                    {!user ? ( // --- FIX: Check 'user' prop ---
                        <>
                            <Link to="/login" className="px-3 py-1 rounded bg-white text-blue-700 border border-blue-200 shadow-sm hover:bg-blue-50">Login</Link>
                            <Link to="/register" className="px-3 py-1 rounded bg-blue-500 text-white shadow-sm hover:bg-blue-600">Register</Link>
                        </>
                    ) : (
                        <div className="relative" ref={profileRef}>
                            <button onClick={() => setOpenProfile(v => !v)} className="flex items-center gap-2 bg-white px-2 py-1 rounded shadow-sm">
                                <div className="w-8 h-8 rounded-full bg-blue-300 text-blue-900 flex items-center justify-center font-medium">
                                    {/* --- FIX: Use user.name --- */}
                                    {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
                                </div>
                                {/* --- FIX: Use user.name --- */}
                                <div className="text-sm text-blue-900 hidden sm:block">{user?.name || 'User'}</div>
                            </button>

                            {openProfile && (
                                <div className="absolute right-0 mt-2 w-44 bg-white border border-blue-100 rounded shadow-lg z-20">
                                    <Link to="/me" className="block px-3 py-2 text-sm text-blue-900 hover:bg-blue-50">My Dashboard</Link>
                                    {isAdmin && <Link to="/admin" className="block px-3 py-2 text-sm text-blue-900 hover:bg-blue-50">Admin Panel</Link>}
                                    {isOwner && <Link to="/owner" className="block px-3 py-2 text-sm text-blue-900 hover:bg-blue-50">Owner Dashboard</Link>}
                                    <button onClick={logout} className="w-full text-left px-3 py-2 text-sm text-red-600 hover:bg-blue-50">Logout</button>
                                </div>
                            )}
                        </div>
                    )}
                    {/* ... (Mobile menu button is unchanged) ... */}
                </div>
            </div>

            {/* --- FIX: Mobile menu logic --- */}
            {openMobile && (
                <div className="md:hidden border-t border-blue-200 bg-blue-50">
                    <div className="px-4 py-3 flex flex-col gap-2">
                        <Link to="/stores" className="px-2 py-2 rounded text-blue-900 hover:bg-blue-100">Stores</Link>
                        {isAdmin && <Link to="/admin" className="px-2 py-2 rounded text-blue-900 hover:bg-blue-100">Admin</Link>}
                        {isOwner && <Link to="/owner" className="px-2 py-2 rounded text-blue-900 hover:bg-blue-100">Owner Dashboard</Link>}
                        {user && <Link to="/me" className="px-2 py-2 rounded text-blue-900 hover:bg-blue-100">My Dashboard</Link>}
                        {!user && (
                            <div className="flex gap-2 mt-2">
                                <Link to="/login" className="flex-1 px-3 py-2 text-center rounded bg-white text-blue-700 border border-blue-200">Login</Link>
                                <Link to="/register" className="flex-1 px-3 py-2 text-center rounded bg-blue-500 text-white">Register</Link>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </header>
    );
}