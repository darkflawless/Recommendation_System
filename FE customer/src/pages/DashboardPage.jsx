import { useState, useCallback, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import { productAPI, trackingAPI } from '../services/api';
import styles from './Dashboard.module.css';

// ── Fallback placeholder khi ảnh lỗi ──────────────────────────────────────
const PLACEHOLDER =
  'data:image/svg+xml,%3Csvg xmlns="http://www.w3.org/2000/svg" width="300" height="200" viewBox="0 0 300 200"%3E%3Crect width="300" height="200" fill="%231e293b"/%3E%3Ctext x="50%25" y="50%25" dominant-baseline="middle" text-anchor="middle" font-size="14" fill="%2364748b"%3ENo Image%3C/text%3E%3C/svg%3E';

const DashboardPage = () => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const [keyword, setKeyword] = useState('');
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(false);
  const [isSearchMode, setIsSearchMode] = useState(false); // false = đang hiện recommendations
  const [error, setError] = useState('');

  const handleLogout = () => {
    logout();
    navigate('/login', { replace: true });
  };

  // ── Load recommendations ngay khi vào dashboard ───────────────────────────
  useEffect(() => {
    const loadRecommendations = async () => {
      setLoading(true);
      setError('');
      try {
        const res = await productAPI.getRecommendations();
        setProducts(res.data);
      } catch (err) {
        setError('Không thể tải sản phẩm gợi ý.');
        console.error(err);
      } finally {
        setLoading(false);
      }
    };
    loadRecommendations();
  }, []); // chạy 1 lần khi mount

  // ── Search ────────────────────────────────────────────────────────────────
  const handleSearch = useCallback(async (e) => {
    e?.preventDefault();
    // Nếu keyword trống → quay về recommendations
    if (!keyword.trim()) {
      setIsSearchMode(false);
      setLoading(true);
      setError('');
      try {
        const res = await productAPI.getRecommendations();
        setProducts(res.data);
      } catch (err) {
        setError('Không thể tải sản phẩm gợi ý.');
      } finally {
        setLoading(false);
      }
      return;
    }
    setLoading(true);
    setError('');
    setIsSearchMode(true);
    try {
      const res = await productAPI.search(keyword.trim(), 0, 8);
      setProducts(res.data);
    } catch (err) {
      setError('Không thể tìm kiếm sản phẩm. Vui lòng thử lại.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  }, [keyword]);

  // ── Click tracking — đúng với ClickEvent.java ─────────────────────────────
  const handleProductClick = async (product) => {
    const payload = {
      id: `${user?.userId ?? 'anon'}_${product.id}`,   // composite key: userId_productId
      userId: user?.userId ?? null,
      productId: product.id,
      productName: product.name,
      productImageUrl: product.imageUrl ?? null,
      categoryId: product.category?.id ?? null,         // từ Category object server trả về
      categoryName: product.category?.name ?? null,     // từ Category object server trả về
      clickType: isSearchMode ? 'SEARCH_RESULT' : 'RECOMMENDATION',
      createdAt: new Date().toISOString(),
    };
    try {
      await trackingAPI.sendClick(payload);
    } catch (err) {
      // Lỗi tracking không chặn UX
      console.warn('Tracking click failed:', err);
    }
  };

  const fmt = (price) =>
    new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(price);

  const sectionTitle = isSearchMode
    ? `Kết quả tìm kiếm cho "${keyword}"`
    : '⭐ Sản phẩm gợi ý cho bạn';

  return (
    <div className={styles.dashboardLayout}>
      {/* ── Sidebar ─────────────────────────────────────────────────────── */}
      <aside className={styles.sidebar}>
        <div className={styles.sidebarLogo}>
          <span className={styles.logoText}>ShopApp</span>
        </div>
        <nav className={styles.navMenu}>
          <button className={`${styles.navItem} ${styles.navItemActive}`} id="nav-dashboard">
            Dashboard
          </button>
        </nav>
        <button id="btn-logout" className={styles.logoutBtn} onClick={handleLogout}>
          Đăng xuất
        </button>
      </aside>

      {/* ── Main ────────────────────────────────────────────────────────── */}
      <main className={styles.mainContent}>
        {/* Top bar */}
        <header className={styles.topBar}>
          <div>
            <h1 className={styles.pageTitle}>Khám phá sản phẩm</h1>
            <p className={styles.pageSubtitle}>Xin chào, {user?.email}</p>
          </div>
          <div className={styles.userBadge}>
            <span className={styles.rolePill}>{user?.role?.replace('ROLE_', '')}</span>
            <div className={styles.avatar}>{user?.email?.[0]?.toUpperCase()}</div>
          </div>
        </header>

        {/* ── Search bar ──────────────────────────────────────────────── */}
        <form className={styles.searchBar} onSubmit={handleSearch} id="form-search">
          <input
            id="input-search"
            type="text"
            className={styles.searchInput}
            placeholder="Tìm kiếm sản phẩm... (vd: Chuot, Laptop)"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
          <button id="btn-search" type="submit" className={styles.searchBtn} disabled={loading}>
            {loading ? '...' : '🔍 Tìm kiếm'}
          </button>
        </form>

        {/* ── Error ───────────────────────────────────────────────────── */}
        {error && <p className={styles.errorMsg}>{error}</p>}

        {/* ── Section title ───────────────────────────────────────────── */}
        {!loading && (
          <p className={styles.sectionTitle}>{sectionTitle}</p>
        )}

        {/* ── Loading spinner ──────────────────────────────────────────── */}
        {loading && (
          <div className={styles.emptyState}>
            <p className={styles.emptyDesc}>Đang tải...</p>
          </div>
        )}

        {/* ── Product Grid ────────────────────────────────────────────── */}
        {!loading && products.length > 0 && (
          <section className={styles.productGrid}>
            {products.map((p) => (
              <div
                key={p.id}
                id={`product-card-${p.id}`}
                className={styles.productCard}
                onClick={() => handleProductClick(p)}
                role="button"
                tabIndex={0}
                onKeyDown={(e) => e.key === 'Enter' && handleProductClick(p)}
              >
                <img
                  src={p.imageUrl || PLACEHOLDER}
                  alt={p.name}
                  className={styles.productImg}
                  onError={(e) => { e.target.src = PLACEHOLDER; }}
                />
                <div className={styles.productInfo}>
                  <h3 className={styles.productName}>{p.name}</h3>
                  <p className={styles.productDesc}>{p.description}</p>
                  <div className={styles.productFooter}>
                    <span className={styles.productPrice}>{fmt(p.price)}</span>
                    <span className={`${styles.stockBadge} ${p.stockQuantity > 0 ? styles.inStock : styles.outStock}`}>
                      {p.stockQuantity > 0 ? `Còn ${p.stockQuantity}` : 'Hết hàng'}
                    </span>
                  </div>
                </div>
              </div>
            ))}
          </section>
        )}

        {/* ── Empty search result ──────────────────────────────────────── */}
        {!loading && isSearchMode && products.length === 0 && (
          <div className={styles.emptyState}>
            <h2 className={styles.emptyTitle}>Không tìm thấy sản phẩm</h2>
            <p className={styles.emptyDesc}>Thử tìm với từ khóa khác nhé.</p>
          </div>
        )}
      </main>
    </div>
  );
};

export default DashboardPage;
