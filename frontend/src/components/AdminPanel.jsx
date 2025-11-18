import React, { useEffect, useState } from 'react';
import api from '../api';

export default function AdminPanel() {
    const [users, setUsers] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState(null);

    const [selectedUserId, setSelectedUserId] = useState(null);
    const [newRole, setNewRole] = useState("");

    async function loadUsers() {
        setLoading(true);
        try {
            const resp = await api.get('/admin/users');
            setUsers(resp.data);
            setErr(null);
        } catch (e) {
            setErr("Failed to load users");
        } finally {
            setLoading(false);
        }
    }

    useEffect(() => {
        loadUsers();
    }, []);

    async function assignRole() {
        if (!selectedUserId || !newRole) return;
        try {
            await api.post('/admin/assign-role', {
                userId: selectedUserId,
                roleName: newRole
            });
            alert("Role assigned");
            loadUsers();
        } catch (e) {
            console.error(e);
            alert("Failed to assign role");
        }
    }

    return (
        <div className="max-w-4xl mx-auto mt-10 p-6 bg-white shadow rounded">
            <h1 className="text-2xl font-bold mb-6">Admin Panel</h1>

            {loading && <div>Loading...</div>}
            {err && <div className="text-red-600 mb-4">{err}</div>}

            {/* Users Table */}
            <table className="w-full border-collapse mb-6">
                <thead>
                <tr className="bg-blue-100 border-b">
                    <th className="p-2 border">ID</th>
                    <th className="p-2 border">Username</th>
                    <th className="p-2 border">Roles</th>
                    <th className="p-2 border">Select</th>
                </tr>
                </thead>
                <tbody>
                {users.map(u => (
                    <tr key={u.id} className="border-b">
                        <td className="p-2 border">{u.id}</td>
                        <td className="p-2 border">{u.username}</td>
                        <td className="p-2 border">
                            {(u.roles || []).map(r => r.name).join(', ')}
                        </td>
                        <td className="p-2 border">
                            <input
                                type="radio"
                                name="selectedUser"
                                onChange={() => setSelectedUserId(u.id)}
                            />
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>

            {/* Assign Role */}
            <div className="mb-4">
                <h2 className="text-lg font-semibold mb-2">Assign New Role</h2>

                <select
                    className="border px-3 py-2 rounded mr-2"
                    value={newRole}
                    onChange={e => setNewRole(e.target.value)}
                >
                    <option value="">-- select role --</option>
                    <option value="USER">USER</option>
                    <option value="STORE_OWNER">STORE_OWNER</option>
                    <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                </select>

                <button
                    onClick={assignRole}
                    className="px-4 py-2 bg-blue-600 text-white rounded"
                    disabled={!selectedUserId || !newRole}
                >
                    Assign Role
                </button>
            </div>
        </div>
    );
}
