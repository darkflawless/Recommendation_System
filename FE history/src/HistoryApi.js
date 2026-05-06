const API_BASE = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

async function request(path) {
  const response = await fetch(`${API_BASE}${path}`);
  if (!response.ok) {
    const text = await response.text();
    throw new Error(text || `Request failed: ${response.status}`);
  }
  return response.json();
}

export function searchUsers(keyword) {
  const encodedKeyword = encodeURIComponent(keyword.trim());
  return request(`/api/admin/customers/search?keyword=${encodedKeyword}`);
}

export function getUserInfo(customerId) {
  return request(`/api/admin/customers/${customerId}`);
}

export function getOrderHistory(customerId, page = 0, size = 5) {
  return request(`/api/admin/customers/${customerId}/orders?page=${page}&size=${size}`);
}

export function getSearchHistory(customerId, page = 0, size = 5) {
  return request(`/api/admin/customers/${customerId}/search-history?page=${page}&size=${size}`);
}

export function getOrderDetails(customerId, orderId) {
  return request(`/api/admin/customers/${customerId}/orders/${orderId}/details`);
}
