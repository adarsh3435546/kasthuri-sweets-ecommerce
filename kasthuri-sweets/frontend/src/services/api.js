import { getToken } from "../utils/auth";

const API_BASE_URL = "http://localhost:8080/api";

// ---------- AUTH ----------
export async function loginUser(email, password) {
  const res = await fetch(`${API_BASE_URL}/auth/login`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) throw new Error("Login failed");
  return res.json(); // { token: "..." }
}

export async function registerUser(email, password) {
  const res = await fetch(`${API_BASE_URL}/auth/register`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, password }),
  });

  if (!res.ok) throw new Error("Register failed");
  return res.json();
}

// ---------- PRODUCTS ----------
export async function fetchProducts() {
  const res = await fetch(`${API_BASE_URL}/products`);
  return res.json();
}

// ---------- CART ----------
export async function addToCart(productId) {
  const token = getToken();

  const res = await fetch(`${API_BASE_URL}/cart/add/${productId}`, {
    method: "POST",
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  if (!res.ok) throw new Error("Unauthorized");
}
