import React, {useState} from 'react';
import api from '../api';
import { useNavigate } from 'react-router-dom';

export default function CreateStore(){
    const [name, setName] = useState('');
    const [address, setAddress] = useState('');
    const [busy, setBusy] = useState(false);
    const [error, setError] = useState(null);
    const nav = useNavigate();

    async function submit(e){
        e.preventDefault();
        setBusy(true);
        setError(null);
        try {
            await api.post('/stores', { name, address });
            nav('/stores');
        } catch (e) {
            console.error(e);
            setError(e?.response?.data || 'Failed to create store');
        } finally {
            setBusy(false);
        }
    }

    return (
        <div className="max-w-lg mx-auto bg-white rounded p-6 shadow">
            <h3 className="text-xl font-semibold text-blue-700 mb-4">Create a Store</h3>
            <form onSubmit={submit} className="flex flex-col gap-3">
                <input placeholder="Store name" value={name} onChange={e=>setName(e.target.value)} className="border p-2 rounded" />
                <input placeholder="Address" value={address} onChange={e=>setAddress(e.target.value)} className="border p-2 rounded" />
                <button className="bg-blue-600 text-white py-2 rounded" disabled={busy}>{busy? 'Saving...' : 'Create'}</button>
                {error && <div className="text-red-600 mt-2">{error}</div>}
            </form>
        </div>
    );
}
