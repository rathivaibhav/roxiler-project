import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export default function Login({ setUser }) {
    const navigate = useNavigate();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [err, setErr] = useState(null);
    const [loading, setLoading] = useState(false);

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setErr(null);

        try {
            const payload = { email, password };
            const resp = await api.post('/auth/login', payload); // No /api

            const { token, ...user } = resp.data;

            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(user));
            if (setUser) {
                setUser(user);
            }

            navigate('/me');

        } catch (e) {
            console.error('Login failed', e);
            setErr(e?.response?.data || 'Invalid email or password');

            localStorage.removeItem('token');
            localStorage.removeItem('user');
            if (setUser) {
                setUser(null);
            }

        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
            <h2 className="text-2xl font-semibold mb-6">Login</h2>
            {/* Form is unchanged */}
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block text-sm font-medium text-gray-700">Email</label>
                    <input
                        type="email"
                        placeholder="admin@app.com"
                        required
                        value={email}
                        onChange={e => setEmail(e.target.value)}
                        className="w-full p-2 border rounded"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Password</label>
                    <input
                        type="password"
                        placeholder="AdminPass123!"
                        required
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        className="w-full p-2 border rounded"
                    />
                </div>
                {err && <div className="text-red-600 text-sm">{err}</div>}
                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                >
                    {loading ? 'Logging in...' : 'Login'}
                </button>
            </form>
        </div>
    );
}