import { useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { searchUsers } from '../HistoryApi';

const PAGE_SIZE = 5;

export default function UserManagement() {
  const navigate = useNavigate();
  const [keyword, setKeyword] = useState('');
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const totalPages = Math.max(1, Math.ceil(users.length / PAGE_SIZE));
  const visibleUsers = useMemo(() => {
    const start = page * PAGE_SIZE;
    return users.slice(start, start + PAGE_SIZE);
  }, [users, page]);

  async function handleSearch(event) {
    event.preventDefault();
    const query = keyword.trim();
    if (!query) return;

    setLoading(true);
    setError('');

    try {
      const result = await searchUsers(query);
      setUsers(Array.isArray(result) ? result : []);
      setPage(0);
    } catch (err) {
      setError('Khong tim duoc du lieu. Vui long thu lai.');
      setUsers([]);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="page-wrap">
      <section className="hero-panel">
        <p className="hero-eyebrow">Admin Console</p>
        <h1>Quan Ly Khach Hang</h1>
        <p className="hero-subtext">
          Tim kiem theo userName hoac email, sau do chon user de xem lich su hoat dong.
        </p>
      </section>

      <section className="card">
        <form className="search-row" onSubmit={handleSearch}>
          <input
            className="input"
            value={keyword}
            onChange={(event) => setKeyword(event.target.value)}
            placeholder="Nhap userName hoac email..."
          />
          <button className="btn" type="submit" disabled={loading}>
            {loading ? 'Dang tim...' : 'Tim kiem'}
          </button>
        </form>

        {error && <p className="error-text">{error}</p>}

        <div className="table-shell">
          <table>
            <thead>
              <tr>
                <th>UserName</th>
                <th>Email</th>
                <th>Phone</th>
                <th>MemStatus</th>
                <th>Thao tac</th>
              </tr>
            </thead>
            <tbody>
              {visibleUsers.map((user) => (
                <tr key={user.id}>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>{user.phone || '-'}</td>
                  <td>{user.memStatus || '-'}</td>
                  <td>
                    <button
                      className="btn btn-small"
                      onClick={() => navigate(`/users/${user.id}`, { state: { user } })}
                    >
                      Xem lich su
                    </button>
                  </td>
                </tr>
              ))}
              {!loading && visibleUsers.length === 0 && (
                <tr>
                  <td colSpan="5" className="empty-cell">
                    Chua co ket qua. Hay nhap tu khoa va tim kiem.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        <div className="pager">
          <button className="btn btn-ghost" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
            Trang truoc
          </button>
          <span>Trang {users.length === 0 ? 0 : page + 1} / {users.length === 0 ? 0 : totalPages}</span>
          <button
            className="btn btn-ghost"
            onClick={() => setPage((p) => Math.min(totalPages - 1, p + 1))}
            disabled={page >= totalPages - 1 || users.length === 0}
          >
            Trang sau
          </button>
        </div>
      </section>
    </main>
  );
}
