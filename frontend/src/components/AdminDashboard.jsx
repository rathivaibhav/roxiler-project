import React, { useEffect, useState } from 'react';
import api from '../api';

export default function AdminDashboard() {
    const [stats, setStats] = useState({ totalUsers: 0, totalStores: 0, totalRatings: 0 });
    const [users, setUsers] = useState([]);
    const [stores, setStores] = useState([]);

    const [filterInput, setFilterInput] = useState({ q: '', role: '' });
    const [appliedFilter, setAppliedFilter] = useState({ q: '', role: '' });

    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState(null);
    const [err, setErr] = useState(null);

    const [newUser, setNewUser] = useState({ name: '', email: '', password: '', address: '', roleName: 'USER' });
    const [creating, setCreating] = useState(false);
    const [newStore, setNewStore] = useState({ name: '', address: '', email: '', ownerId: '' });
    const [creatingStore, setCreatingStore] = useState(false);
    const [roleAssign, setRoleAssign] = useState({});

    useEffect(() => {
        loadData();
    }, [appliedFilter]);

    async function loadData() {
        setLoading(true);
        setErr(null);
        try {
            const userParams = { query: appliedFilter.q || null, role: appliedFilter.role || null };
            const storeParams = { query: appliedFilter.q || null };

            const [statsRes, usersRes, storesRes] = await Promise.all([
                api.get('/admin/stats'),
                api.get('/admin/users', { params: userParams }),
                api.get('/admin/stores', { params: storeParams })
            ]);

            setStats(statsRes.data);
            setUsers(usersRes.data || []);
            setStores(storesRes.data || []);
        } catch (e) {
            console.error('Failed to load admin data', e);
            setErr('Failed to load dashboard data. Are you logged in as an Admin?');
        } finally {
            setLoading(false);
        }
    }

    async function handleCreateUser(e) {
        e.preventDefault();
        setCreating(true);
        setMsg(null);
        setErr(null);

        if (!newUser.name || !newUser.email || !newUser.password) {
            setErr('Name, email and password are required');
            setCreating(false);
            return;
        }

        try {
            await api.post('/admin/create-user', newUser);
            setMsg('User created successfully');
            setNewUser({ name: '', email: '', password: '', address: '', roleName: 'USER' });
            await loadData(); // Reload data
            setTimeout(() => setMsg(null), 3000);
        } catch (e) {
            console.error('Failed to create user', e);
            setErr(e?.response?.data?.message || e?.response?.data || 'Failed to create user. Check validation rules.');
        } finally {
            setCreating(false);
        }
    }

    async function handleCreateStore(e) {
        e.preventDefault();
        setCreatingStore(true);
        setMsg(null);
        setErr(null);

        if (!newStore.name) {
            setErr('Store name is required');
            setCreatingStore(false);
            return;
        }

        try {
            const payload = { ...newStore };
            if (payload.ownerId === '') delete payload.ownerId;

            await api.post('/admin/create-store', payload);
            setMsg('Store created successfully');
            setNewStore({ name: '', address: '', email: '', ownerId: '' });
            await loadData();
            setTimeout(() => setMsg(null), 3000);
        } catch (e) {
            console.error('Failed to create store', e);
            setErr(e?.response?.data || 'Failed to create store');
        } finally {
            setCreatingStore(false);
        }
    }

    async function assignRole(userId, role) {
        if (!role) return;
        setMsg(null);
        setErr(null);

        try {
            await api.post('/admin/assign-role', { userId, roleName: role });
            setMsg('Role assigned successfully');
            await loadData();
            setTimeout(() => setMsg(null), 2000);
        } catch (e) {
            console.error('Failed to assign role', e);
            setErr(e?.response?.data || 'Failed to assign role');
        } finally {
            setRoleAssign(prev => ({ ...prev, [userId]: '' }));
        }
    }

    function handleSearch() {
        setAppliedFilter(filterInput);
    }
    const storeOwners = users.filter(u =>
        (u.roles || []).some(r => (r.name || r) === 'STORE_OWNER')
    );

    if (loading && users.length === 0) {
        return <div className="p-4">Loading dashboard...</div>;
    }

    return (
        <div className="p-4 max-w-7xl mx-auto">
            <h2 className="text-2xl font-semibold mb-6">System Administrator Dashboard</h2>

            {msg && <div className="mb-4 p-3 bg-green-100 text-green-800 rounded">{msg}</div>}
            {err && <div className="mb-4 p-3 bg-red-100 text-red-800 rounded">{err}</div>}

            {/* Stats Cards */}
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-6">
                <div className="p-4 bg-white rounded shadow">
                    <div className="text-sm text-gray-600">Total Users</div>
                    <div className="text-2xl font-bold text-blue-600">{stats.totalUsers}</div>
                </div>
                <div className="p-4 bg-white rounded shadow">
                    <div className="text-sm text-gray-600">Total Stores</div>
                    <div className="text-2xl font-bold text-green-600">{stats.totalStores}</div>
                </div>
                <div className="p-4 bg-white rounded shadow">
                    <div className="text-sm text-gray-600">Total Ratings</div>
                    <div className="text-2xl font-bold text-purple-600">{stats.totalRatings}</div>
                </div>
            </div>

            {/* Create User and Store Forms */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
                {/* Create User Form */}
                <div className="bg-white p-4 rounded shadow">
                    <h3 className="text-lg font-medium mb-3">Create New User</h3>
                    <form onSubmit={handleCreateUser} className="space-y-3">
                        <input
                            placeholder="Name * (min 20 chars)"
                            required
                            value={newUser.name}
                            onChange={e => setNewUser({ ...newUser, name: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <input
                            placeholder="Email *"
                            type="email"
                            required
                            value={newUser.email}
                            onChange={e => setNewUser({ ...newUser, email: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <input
                            placeholder="Password * (8-16 chars, 1 UC, 1 Special)"
                            type="password"
                            required
                            value={newUser.password}
                            onChange={e => setNewUser({ ...newUser, password: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <input
                            placeholder="Address"
                            value={newUser.address}
                            onChange={e => setNewUser({ ...newUser, address: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <select
                            value={newUser.roleName}
                            onChange={e => setNewUser({ ...newUser, roleName: e.target.value })}
                            className="w-full p-2 border rounded"
                        >
                            <option value="USER">Normal User</option>
                            <option value="STORE_OWNER">Store Owner</option>
                            <option value="SYSTEM_ADMIN">System Admin</option>
                        </select>
                        <button
                            type="submit"
                            disabled={creating}
                            className="w-full bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 disabled:opacity-50"
                        >
                            {creating ? 'Creating...' : 'Create User'}
                        </button>
                    </form>
                </div>
                {/* Create Store Form */}
                <div className="bg-white p-4 rounded shadow">
                    <h3 className="text-lg font-medium mb-3">Create New Store</h3>
                    <form onSubmit={handleCreateStore} className="space-y-3">
                        <input
                            placeholder="Store Name *"
                            required
                            value={newStore.name}
                            onChange={e => setNewStore({ ...newStore, name: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <input
                            placeholder="Store Email"
                            type="email"
                            value={newStore.email}
                            onChange={e => setNewStore({ ...newStore, email: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <input
                            placeholder="Address"
                            value={newStore.address}
                            onChange={e => setNewStore({ ...newStore, address: e.target.value })}
                            className="w-full p-2 border rounded"
                        />
                        <select
                            value={newStore.ownerId}
                            onChange={e => setNewStore({ ...newStore, ownerId: e.target.value })}
                            className="w-full p-2 border rounded"
                        >
                            <option value="">No Owner (Admin-managed)</option>
                            {storeOwners.map(owner => (
                                <option key={owner.id} value={owner.id}>
                                    {owner.name} ({owner.email})
                                </option>
                            ))}
                        </select>
                        <button
                            type="submit"
                            disabled={creatingStore}
                            className="w-full bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700 disabled:opacity-50"
                        >
                            {creatingStore ? 'Creating...' : 'Create Store'}
                        </button>
                    </form>
                </div>
            </div>

            {/* Filters */}
            <div className="mb-6 bg-white p-4 rounded shadow">
                <h3 className="text-lg font-medium mb-3">Search & Filter</h3>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                    <input
                        placeholder="Search by name, email, or address..."
                        value={filterInput.q}
                        onChange={e => setFilterInput({ ...filterInput, q: e.target.value })}
                        className="p-2 border rounded"
                    />
                    <input
                        placeholder="Filter by role (e.g., USER)"
                        value={filterInput.role}
                        onChange={e => setFilterInput({ ...filterInput, role: e.target.value })}
                        className="p-2 border rounded"
                    />
                    <button
                        onClick={handleSearch}
                        className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
                    >
                        Search
                    </button>
                </div>
            </div>

            {/* User and Store Lists */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                {/* Users List */}
                <div className="bg-white p-4 rounded shadow">
                    <h3 className="text-lg font-medium mb-3">Users ({users.length})</h3>
                    <div className="space-y-3 max-h-96 overflow-auto">
                        {users.map(u => (
                            <div key={u.id} className="p-3 border rounded">
                                <div className="font-medium">{u.name}</div>
                                <div className="text-sm text-gray-600">{u.email || 'No email'}</div>
                                {u.address && <div className="text-xs text-gray-500">{u.address}</div>}
                                <div className="text-xs text-blue-600 mt-1">
                                    {(u.roles || []).map(r => r.name || r).join(', ')}
                                </div>
                                {u.stores && u.stores.length > 0 && (
                                    <div className="text-xs text-green-600 mt-1">
                                        Owns: {u.stores.join(', ')}
                                    </div>
                                )}
                                <div className="flex gap-2 items-center mt-2">
                                    <select
                                        value={roleAssign[u.id] || ''}
                                        onChange={e => setRoleAssign(prev => ({ ...prev, [u.id]: e.target.value }))}
                                        className="p-1 border rounded text-sm flex-1"
                                    >
                                        <option value="">Assign role...</option>
                                        <option value="USER">USER</option>
                                        <option value="STORE_OWNER">STORE_OWNER</option>
                                        <option value="SYSTEM_ADMIN">SYSTEM_ADMIN</option>
                                    </select>
                                    <button
                                        onClick={() => assignRole(u.id, roleAssign[u.id])}
                                        disabled={!roleAssign[u.id]}
                                        className="px-3 py-1 bg-blue-600 text-white rounded text-sm hover:bg-blue-700 disabled:opacity-50"
                                    >
                                        Assign
                                    </button>
                                </div>
                            </div>
                        ))}
                        {users.length === 0 && (
                            <div className="text-gray-500 text-center py-4">No users found</div>
                        )}
                    </div>
                </div>

                {/* Stores List */}
                <div className="bg-white p-4 rounded shadow">
                    <h3 className="text-lg font-medium mb-3">Stores ({stores.length})</h3>
                    <div className="space-y-3 max-h-96 overflow-auto">
                        {stores.map(s => (
                            <div key={s.id} className="p-3 border rounded">
                                <div className="flex justify-between items-start">
                                    <div className="flex-1">
                                        <div className="font-medium">{s.name}</div>
                                        {s.address && <div className="text-sm text-gray-600">{s.address}</div>}
                                        {s.email && <div className="text-sm text-gray-500">{s.email}</div>}
                                        {s.owner && (
                                            <div className="text-xs text-gray-500 mt-1">
                                                Owner: {s.owner.name}
                                            </div>
                                        )}
                                    </div>
                                    <div className="text-right ml-4">
                                        <div className="text-sm font-semibold text-purple-600">
                                            {s.avgRating ? `★ ${s.avgRating}` : 'No ratings'}
                                        </div>
                                        <div className="text-xs text-gray-500">
                                            {s.ratingCount} rating{s.ratingCount !== 1 ? 's' : ''}
                                        </div>
                                    </div>
                                </div>
                            </div>
                        ))}
                        {stores.length === 0 && (
                            <div className="text-gray-500 text-center py-4">No stores found</div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}