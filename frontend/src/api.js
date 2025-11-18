// frontend/src/api.js
import axios from 'axios';

// --- PRODUCTION CONFIGURATION ---
// We use a relative URL '/api'.
// Nginx will grab this and proxy it to the backend on localhost:8080.
const API_BASE = '/api';

const api = axios.create({
    baseURL: API_BASE,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Helper to identify auth endpoints (where we shouldn't send a token)
function isAuthEndpoint(url = '') {
    if (!url) return false;
    const u = url.toString();
    return u.includes('/auth/login') || u.includes('/auth/register');
}

// Request Interceptor: Attach Token
api.interceptors.request.use(cfg => {
    try {
        const urlToCheck = cfg.url || '';
        // Don't attach token for login/register
        if (isAuthEndpoint(urlToCheck)) return cfg;

        const token = localStorage.getItem('token');
        if (token) {
            cfg.headers.Authorization = `Bearer ${token}`;
        }
    } catch (e) {
        console.error('api request interceptor error', e);
    }
    return cfg;
}, err => Promise.reject(err));

// Response Interceptor: Handle 401 (Unauthorized)
api.interceptors.response.use(
    res => res,
    err => {
        const status = err?.response?.status;

        // Optional: Log errors for debugging (ignoring 401s to keep console clean)
        try {
            if (status !== 401) {
                console.error('[api error]', {
                    url: err.config?.url,
                    status,
                    data: err.response?.data
                });
            }
        } catch (e) {}

        // If token is invalid or expired (401), clear it and redirect.
        // IMPORTANT: We check if we are already on /login to prevent infinite loops.
        if (status === 401) {
            localStorage.removeItem('token');
            localStorage.removeItem('user'); // Clear user data too

            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
        }
        return Promise.reject(err);
    }
);

export default api;