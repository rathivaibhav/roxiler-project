import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api';

export default function Register() {
    const navigate = useNavigate();

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [address, setAddress] = useState('');

    const [err, setErr] = useState(null);
    const [loading, setLoading] = useState(false);

    const passwordRegex = /^(?=.*[A-Z])(?=.*[!@#$&*]).{8,16}$/;
    const nameMinLength = 20;

    async function handleSubmit(e) {
        e.preventDefault();
        setLoading(true);
        setErr(null);

        if (name.length < nameMinLength) {
            setErr(`Name must be at least ${nameMinLength} characters long.`);
            setLoading(false);
            return;
        }
        if (!passwordRegex.test(password)) {
            setErr('Password must be 8-16 chars, with one uppercase letter and one special character (!@#$&*).');
            setLoading(false);
            return;
        }

        try {
            const payload = { name, email, password, address };

            // --- FIX: Removed /api from the URL ---
            await api.post('/auth/register', payload);

            navigate('/login');

        } catch (e) {
            console.error('Registration failed', e);
            setErr(e?.response?.data || 'Registration failed. Email may be taken.');
        } finally {
            setLoading(false);
        }
    }

    return (
        <div className="max-w-md mx-auto mt-10 p-6 bg-white rounded shadow">
            <h2 className="text-2xl font-semibold mb-6">Create Account</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
                {/* ... (form fields are the same) ... */}
                <div>
                    <label className="block text-sm font-medium text-gray-700">Name (min 20 chars)</label>
                    <input
                        type="text"
                        placeholder="Your full name (e.g., Johnathan Doe Smith)"
                        required
                        value={name}
                        onChange={e => setName(e.target.value)}
                        className="w-full p-2 border rounded"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Email</label>
                    <input
                        type="email"
                        placeholder="you@example.com"
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
                        placeholder="8-16 chars, 1 uppercase, 1 special"
                        required
                        value={password}
                        onChange={e => setPassword(e.target.value)}
                        className="w-full p-2 border rounded"
                    />
                </div>
                <div>
                    <label className="block text-sm font-medium text-gray-700">Address</label>
                    <textarea
                        placeholder="123 Main St"
                        value={address}
                        onChange={e => setAddress(e.target.value)}
                        className="w-full p-2 border rounded"
                        rows={3}
                    />
                </div>
                {err && <div className="text-red-600 text-sm">{err}</div>}
                <button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                >
                    {loading ? 'Creating...' : 'Create Account'}
                </button>
            </form>
        </div>
    );
}