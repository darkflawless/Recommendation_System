import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import styles from './Auth.module.css';

const LoginPage = () => {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ email: '', password: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await login({ email: form.email, password: form.password });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || 'Email hoặc mật khẩu không đúng.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.authContainer}>
      <div className={styles.card}>
        <div className={styles.cardHeader}>
          <h1 className={styles.title}>Chào mừng trở lại</h1>
          <p className={styles.subtitle}>Đăng nhập để tiếp tục quản trị</p>
        </div>

        <form onSubmit={handleSubmit} className={styles.form} noValidate>
          <div className={styles.formGroup}>
            <label htmlFor="login-email" className={styles.label}>Email</label>
            <div className={styles.inputWrapper}>
              <input
                id="login-email"
                name="email"
                type="email"
                placeholder="ban@example.com"
                value={form.email}
                onChange={handleChange}
                className={styles.input}
                required
                autoComplete="email"
              />
            </div>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="login-password" className={styles.label}>Mật khẩu</label>
            <div className={styles.inputWrapper}>
              <input
                id="login-password"
                name="password"
                type="password"
                placeholder="Nhập mật khẩu"
                value={form.password}
                onChange={handleChange}
                className={styles.input}
                required
                autoComplete="current-password"
              />
            </div>
          </div>

          {error && <p className={styles.errorMsg}>{error}</p>}

          <button
            id="btn-login-submit"
            type="submit"
            className={styles.submitBtn}
            disabled={loading}
          >
            {loading ? <span className={styles.spinner} /> : 'Đăng nhập'}
          </button>
        </form>

        <p className={styles.switchText}>
          Chưa có tài khoản?{' '}
          <Link to="/register" className={styles.switchLink}>Đăng ký ngay</Link>
        </p>
      </div>
    </div>
  );
};

export default LoginPage;
