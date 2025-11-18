// frontend/src/auth.js

import jwt_decode from "jwt-decode";

export function saveAuth(token) {
    localStorage.setItem("jwt", token);
}

export function getToken() {
    return localStorage.getItem("jwt");
}

export function logout() {
    localStorage.removeItem("jwt");
}

export function parseJwt(token) {
    try {
        return jwt_decode(token);
    } catch (err) {
        return null;
    }
}

export function getRoleFromToken() {
    const token = getToken();
    if (!token) return [];
    const decoded = parseJwt(token);
    return decoded?.roles || [];
}

export function getUserIdFromToken() {
    const token = getToken();
    if (!token) return null;
    const decoded = parseJwt(token);
    return decoded?.sub || null;
}
