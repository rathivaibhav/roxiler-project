import React, { useEffect, useState } from 'react';
import api from '../api';

export default function StoreList() {
    const [stores, setStores] = useState([]);
    const [loading, setLoading] = useState(true);
    const [err, setErr] = useState(null);
    const [searchName, setSearchName] = useState('');
    const [searchAddress, setSearchAddress] = useState('');

    useEffect(() => {
        loadStores();
    }, [searchName, searchAddress]);

    async function loadStores() {
        setLoading(true);
        setErr(null);
        try {
            const params = {
                name: searchName || null,
                address: searchAddress || null,
            };
            const resp = await api.get('/stores', { params });
            setStores(resp.data || []);
        } catch (e) {
            console.error('could not load stores', e);
            setErr(e?.response?.data || 'Failed to load stores.');
        } finally {
            setLoading(false);
        }
    }

    function handleRatingUpdate(storeId, newScore) {
        setStores(currentStores =>
            currentStores.map(store =>
                store.id === storeId
                    ? { ...store, userSubmittedRating: newScore }
                    : store
            )
        );
    }

    if (loading && stores.length === 0) {
        return <div className="p-4">Loading stores...</div>;
    }

    return (
        <div className="p-4 max-w-4xl mx-auto">
            <h2 className="text-2xl font-semibold mb-6">Rate Stores</h2>

            {/* Search Filters */}
            <div className="mb-6 bg-white p-4 rounded shadow grid sm:grid-cols-2 gap-4">
                <input
                    type="text"
                    placeholder="Search by name..."
                    value={searchName}
                    onChange={e => setSearchName(e.target.value)}
                    className="w-full p-2 border rounded"
                />
                <input
                    type="text"
                    placeholder="Search by address..."
                    value={searchAddress}
                    onChange={e => setSearchAddress(e.target.value)}
                    className="w-full p-2 border rounded"
                />
            </div>

            {err && <div className="p-4 bg-red-100 text-red-700 rounded mb-4">{err}</div>}

            <div className="space-y-4">
                {stores.length === 0 && !loading && (
                    <div className="p-4 bg-white rounded shadow text-center text-gray-500">
                        No stores found.
                    </div>
                )}

                {stores.map(store => (
                    <StoreCard
                        key={store.id}
                        store={store}
                        onRatingUpdated={handleRatingUpdate}
                    />
                ))}
            </div>
        </div>
    );
}

function StoreCard({ store, onRatingUpdated }) {
    const [selectedScore, setSelectedScore] = useState(store.userSubmittedRating || 0);
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function handleSubmitRating() {
        if (selectedScore < 1 || selectedScore > 5) return;

        setIsSubmitting(true);
        try {
            await api.post('/ratings', {
                storeId: store.id,
                score: selectedScore
            });
            onRatingUpdated(store.id, selectedScore);
        } catch (e) {
            console.error('Failed to submit rating', e);
            alert('Failed to submit rating.');
        } finally {
            setIsSubmitting(false);
        }
    }

    useEffect(() => {
        setSelectedScore(store.userSubmittedRating || 0);
    }, [store.userSubmittedRating]);

    return (
        <div className="p-4 bg-white rounded shadow flex flex-col sm:flex-row items-start sm:items-center justify-between">
            <div className="flex-1 mb-4 sm:mb-0">
                <div className="text-xl font-semibold">{store.name}</div>
                <div className="text-sm text-gray-600">{store.address}</div>
                <div className="text-sm text-gray-500">{store.email}</div>
                <div className="text-lg font-bold text-blue-600 mt-2">
                    Overall Rating: {store.overallRating ? `★ ${store.overallRating}` : 'Not Rated'}
                </div>
            </div>

            <div className="w-full sm:w-auto sm:ml-4 flex flex-col items-start">
                <div className="text-sm font-medium mb-1">
                    {store.userSubmittedRating ? 'Your Rating:' : 'Rate this store:'}
                </div>
                <div className="flex items-center gap-2">
                    <div className="flex space-x-1">
                        {[1, 2, 3, 4, 5].map(star => (
                            <button
                                key={star}
                                onClick={() => setSelectedScore(star)}
                                className={`text-2xl ${selectedScore >= star ? 'text-yellow-400' : 'text-gray-300'}`}
                                disabled={isSubmitting}
                            >
                                ★
                            </button>
                        ))}
                    </div>
                    <button
                        onClick={handleSubmitRating}
                        disabled={isSubmitting || selectedScore === 0}
                        className="px-3 py-1 bg-blue-600 text-white rounded text-sm disabled:opacity-50"
                    >
                        {isSubmitting ? '...' : (store.userSubmittedRating ? 'Update' : 'Submit')}
                    </button>
                </div>
            </div>
        </div>
    );
}