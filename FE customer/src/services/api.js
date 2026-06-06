import axios from 'axios';

// ============================================================
// BASE URL - Thay đổi theo backend của bạn
// ============================================================
const BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080';

// ============================================================
// AXIOS INSTANCE
// ============================================================
const api = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

// ============================================================
// REQUEST INTERCEPTOR — tự động đính kèm JWT token
// ============================================================
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// ============================================================
// RESPONSE INTERCEPTOR — xử lý lỗi toàn cục
// ============================================================
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token hết hạn hoặc không hợp lệ → xóa storage & về login
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// ============================================================
// AUTH ENDPOINTS
// Response mẫu:
// {
//   token: "...",
//   type: "Bearer",
//   userId: 14,
//   email: "a1@gmail.com",
//   role: "ROLE_USER"
// }
// ============================================================
export const authAPI = {
  /**
   * Đăng ký tài khoản mới
   * @param {{ email: string, password: string, fullName?: string }} data
   */
  register: (data) => api.post('/api/auth/register', data),

  /**
   * Đăng nhập
   * @param {{ email: string, password: string }} data
   */
  login: (data) => api.post('/api/auth/login', data),

  /**
   * Đăng xuất (nếu backend có endpoint này)
   */
  logout: () => api.post('/api/auth/logout'),
};

// ============================================================
// USER ENDPOINTS (mở rộng sau)
// ============================================================
export const userAPI = {
  getProfile: () => api.get('/api/users/me'),
  updateProfile: (data) => api.put('/api/users/me', data),
};

// ============================================================
// PRODUCT ENDPOINTS
// ============================================================
export const productAPI = {
  /**
   * Lấy sản phẩm gợi ý (hiển thị ngay khi vào dashboard)
   */
  getRecommendations: () => api.get('/api/products/recommendations'),

  /**
   * Tìm kiếm sản phẩm theo keyword
   * @param {string} keyword
   * @param {number} page - 0-based
   * @param {number} size
   */
  search: (keyword, page = 0, size = 8) =>
    api.get('/api/products/search', { params: { keyword, page, size } }),
};

// ============================================================
// TRACKING ENDPOINTS (qua Gateway → tracking_service:8095)
// Gateway route: /api/tracking/** → strip → /tracking/**
// TrackingEventController mapping: /tracking/events/click
// ============================================================
export const trackingAPI = {
  /**
   * Gửi click event khi user click vào sản phẩm
   * ClickEvent fields: id (uuid), userId, productId, timestamp, ...
   */
  sendClick: (payload) => api.post('/api/tracking/events/click', payload),
};

// ============================================================
// Xuất instance gốc để dùng khi cần gọi custom
// ============================================================
export default api;
