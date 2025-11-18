import React, { useState, useEffect } from 'react';
import api from '../api';

export default function RatingButton({ storeId, onSuccess }) {
    const [selected, setSelected] = useState(0);
    const [existingId, setExistingId] = useState(null);
    const [loading, setLoading] = useState(false);
    const [msg, setMsg] = useState(null);
    const [error, setError] = useState(null);

    useEffect(() => {
        let mounted = true;
        api.get(`/ratings?storeId=${storeId}`)
            .then(r => {
                if (!mounted) return;
                const arr = r.data || [];
                const me = JSON.parse(localStorage.getItem('user') || '{}');
                const my = arr.find(rt => rt.user && me && rt.user.id === me.id);
                if (my) {
                    setExistingId(my.id);
                    setSelected(my.score);
                } else {
                    setExistingId(null);
                    setSelected(0);
                }
            })
            .catch(e => {
                console.error('fetch ratings failed', e);
            });
        return () => (mounted = false);
    }, [storeId]);

    async function submitRating() {
        setError(null);
        setMsg(null);

        if (selected === 0) {
            setError("Please select a rating (1–5).");
            return;
        }

        setLoading(true);
        try {
            if (existingId) {
                // update
                const resp = await api.put(`/ratings/${existingId}`, { storeId, score: selected, comment: `Updated to ${selected}` });
                setMsg('Rating updated!');
                if (onSuccess) onSuccess(resp.data);
            } else {
                // create
                const resp = await api.post('/ratings', { storeId, score: selected, comment: `Rated ${selected}` });
                setMsg('Rating submitted!');
                if (onSuccess) onSuccess(resp.data);
            }
        } catch (e) {
            console.error('Rating error', e);
            setError(e?.response?.data || e?.message || 'Rating failed');
        } finally {
            setLoading(false);
            setTimeout(() => setMsg(null), 2000);
        }
    }

    return (
        <div className="flex flex-col gap-2">
            <div className="flex gap-1">
                {[1,2,3,4,5].map(n => (
                    <span
                        key={n}
                        onClick={() => setSelected(n)}
                        className={`cursor-pointer text-2xl ${selected >= n ? 'text-yellow-400' : 'text-gray-300'}`}
                    >
            ★
          </span>
                ))}
            </div>

            <div className="mt-1 flex gap-2">
                <button onClick={submitRating} disabled={loading} className="bg-blue-600 text-white px-3 py-1 rounded">
                    {loading ? '...' : existingId ? 'Update' : 'Submit'}
                </button>
            </div>

            {msg && <div className="text-sm text-green-700">{msg}</div>}
            {error && <div className="text-sm text-red-700">{String(error)}</div>}
        </div>
    );
}
