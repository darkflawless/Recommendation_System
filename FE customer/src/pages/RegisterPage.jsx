import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import styles from './Auth.module.css';

const RegisterPage = () => {
  const { register } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({ fullName: '', email: '', password: '', confirmPassword: '' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (e) => {
    setForm((prev) => ({ ...prev, [e.target.name]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (form.password !== form.confirmPassword) {
      setError('Mật khẩu xác nhận không khớp.');
      return;
    }
    setLoading(true);
    try {
      await register({ fullName: form.fullName, email: form.email, password: form.password });
      navigate('/dashboard', { replace: true });
    } catch (err) {
      setError(err.response?.data?.message || 'Đăng ký thất bại. Vui lòng thử lại.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.authContainer}>
      <div className={styles.card}>
        <div className={styles.cardHeader}>
          <h1 className={styles.title}>Tạo tài khoản</h1>
          <p className={styles.subtitle}>Bắt đầu hành trình của bạn ngay hôm nay</p>
        </div>

        <form onSubmit={handleSubmit} className={styles.form} noValidate>
          <div className={styles.formGroup}>
            <label htmlFor="reg-fullName" className={styles.label}>Họ và tên</label>
            <div className={styles.inputWrapper}>
              <input
                id="reg-fullName"
                name="fullName"
                type="text"
                placeholder="Nguyễn Văn A"
                value={form.fullName}
                onChange={handleChange}
                className={styles.input}
                required
                autoComplete="name"
              />
            </div>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="reg-email" className={styles.label}>Email</label>
            <div className={styles.inputWrapper}>
              <input
                id="reg-email"
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
            <label htmlFor="reg-password" className={styles.label}>Mật khẩu</label>
            <div className={styles.inputWrapper}>
              <input
                id="reg-password"
                name="password"
                type="password"
                placeholder="Tối thiểu 6 ký tự"
                value={form.password}
                onChange={handleChange}
                className={styles.input}
                required
                minLength={6}
                autoComplete="new-password"
              />
            </div>
          </div>

          <div className={styles.formGroup}>
            <label htmlFor="reg-confirmPassword" className={styles.label}>Xác nhận mật khẩu</label>
            <div className={styles.inputWrapper}>
              <input
                id="reg-confirmPassword"
                name="confirmPassword"
                type="password"
                placeholder="Nhập lại mật khẩu"
                value={form.confirmPassword}
                onChange={handleChange}
                className={styles.input}
                required
                autoComplete="new-password"
              />
            </div>
          </div>

          {error && <p className={styles.errorMsg}>{error}</p>}

          <button
            id="btn-register-submit"
            type="submit"
            className={styles.submitBtn}
            disabled={loading}
          >
            {loading ? <span className={styles.spinner} /> : 'Đăng ký'}
          </button>
        </form>

        <p className={styles.switchText}>
          Đã có tài khoản?{' '}
          <Link to="/login" className={styles.switchLink}>Đăng nhập ngay</Link>
        </p>
      </div>
    </div>
  );
};

export default RegisterPage;
