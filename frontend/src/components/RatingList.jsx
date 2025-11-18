import React, { useEffect, useState } from "react";
import API from "../api";

export default function RatingList() {
    const [ratings, setRatings] = useState([]);
    const [stores, setStores] = useState([]);
    const [form, setForm] = useState({ storeId: "", score: 5, comment: "" });
    const [loading, setLoading] = useState(true);
    const [msg, setMsg] = useState(null);

    useEffect(() => {
        loadAll();
    }, []);

    async function loadAll() {
        setLoading(true);
        try {
            const [rRes, sRes] = await Promise.all([API.get("/ratings"), API.get("/stores")]);
            setRatings(rRes.data || []);
            setStores(sRes.data || []);
        } catch (err) {
            console.error(err);
            setMsg("Error loading data");
        } finally {
            setLoading(false);
        }
    }

    async function submit(e) {
        e.preventDefault();
        setMsg(null);
        if (!form.storeId) return setMsg("Select store");
        try {
            await API.post("/ratings", {
                storeId: Number(form.storeId),
                score: Number(form.score),
                comment: form.comment,
            });
            setForm({ storeId: "", score: 5, comment: "" });
            loadAll();
        } catch (err) {
            setMsg(String(err.response?.data || err.message));
        }
    }

    return (
        <div>
            <h2>Ratings</h2>
            {msg && <div className="small" style={{ color: "red" }}>{msg}</div>}
            <div style={{ display: "flex", gap: 12 }}>
                <div style={{ flex: 1 }}>
                    <form onSubmit={submit} className="card">
                        <label>Store</label>
                        <select
                            className="input"
                            value={form.storeId}
                            onChange={(e) => setForm({ ...form, storeId: e.target.value })}
                        >
                            <option value="">Select store</option>
                            {stores.map((s) => (
                                <option key={s.id} value={s.id}>
                                    {s.name}
                                </option>
                            ))}
                        </select>

                        <label>Score</label>
                        <input
                            type="number"
                            min="1"
                            max="5"
                            className="input"
                            value={form.score}
                            onChange={(e) => setForm({ ...form, score: e.target.value })}
                        />
                        <label>Comment</label>
                        <textarea
                            rows="3"
                            className="input"
                            value={form.comment}
                            onChange={(e) => setForm({ ...form, comment: e.target.value })}
                        />
                        <button className="btn" type="submit">Add Rating</button>
                    </form>
                </div>

                <div style={{ flex: 1 }}>
                    <div className="card">
                        {loading ? (
                            <div>Loading...</div>
                        ) : ratings.length === 0 ? (
                            <div className="small">No ratings yet</div>
                        ) : (
                            ratings.map((rt) => (
                                <div key={rt.id} className="list-item">
                                    <div style={{ fontWeight: 600 }}>
                                        {rt.store?.name || `Store ${rt.store?.id}`} — {rt.score}/5
                                    </div>
                                    <div className="small">{rt.comment}</div>
                                    <div className="small">{new Date(rt.createdAt).toLocaleString()}</div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
