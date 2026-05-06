import { useEffect, useMemo, useState } from 'react';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import { getOrderHistory, getSearchHistory, getUserInfo } from '../HistoryApi';
import OrderHistoryTable from './OrderHistoryTable';
import SearchHistoryTable from './SearchHistoryTable';

const PAGE_SIZE = 5;

function formatDateTime(value) {
  if (!value) return '-';
  return new Date(value).toLocaleString('vi-VN');
}

export default function UserHistory() {
  const navigate = useNavigate();
  const { customerId } = useParams();
  const location = useLocation();

  const [user, setUser] = useState(location.state?.user || null);
  const [tab, setTab] = useState('orders');
  const [page, setPage] = useState(0);
  const [rows, setRows] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (user) return;
    getUserInfo(customerId)
      .then(setUser)
      .catch(() => setError('Khong tai duoc thong tin user.'));
  }, [customerId, user]);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError('');

    const loader = tab === 'orders' ? getOrderHistory : getSearchHistory;

    loader(customerId, page, PAGE_SIZE)
      .then((data) => {
        if (!cancelled) setRows(Array.isArray(data) ? data : []);
      })
      .catch(() => {
        if (!cancelled) {
          setRows([]);
          setError('Khong tai duoc lich su.');
        }
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });

    return () => {
      cancelled = true;
    };
  }, [customerId, tab, page]);

  const canGoNext = useMemo(() => rows.length === PAGE_SIZE, [rows.length]);

  return (
    <main className="page-wrap">
      <section className="hero-panel compact">
        <p className="hero-eyebrow">Admin Console</p>
        <h1>Lich Su Khach Hang</h1>
        {user && (
          <p className="hero-subtext">
            UserName: <strong>{user.name}</strong> | Email: <strong>{user.email}</strong> | Phone:{' '}
            <strong>{user.phone || '-'}</strong> | MemStatus: <strong>{user.memStatus || '-'}</strong>
          </p>
        )}
      </section>

      <section className="card">
        <div className="tabs">
          <button
            className={`tab-btn ${tab === 'orders' ? 'active' : ''}`}
            onClick={() => {
              setTab('orders');
              setPage(0);
            }}
          >
            Lich su mua hang
          </button>
          <button
            className={`tab-btn ${tab === 'search' ? 'active' : ''}`}
            onClick={() => {
              setTab('search');
              setPage(0);
            }}
          >
            Lich su tim kiem
          </button>
        </div>

        {error && <p className="error-text">{error}</p>}

        <div className="table-shell">
          {tab === 'orders' ? (
            <OrderHistoryTable
              rows={rows}
              loading={loading}
              formatDateTime={formatDateTime}
              onOpenOrder={(order) =>
                navigate(`/users/${customerId}/orders/${order.id}`, {
                  state: { order, user },
                })
              }
            />
          ) : (
            <SearchHistoryTable rows={rows} loading={loading} formatDateTime={formatDateTime} />
          )}
        </div>

        <div className="pager">
          <button className="btn btn-ghost" onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page === 0}>
            Trang truoc
          </button>
          <span>Trang {page + 1}</span>
          <button className="btn btn-ghost" onClick={() => setPage((p) => p + 1)} disabled={!canGoNext}>
            Trang sau
          </button>
        </div>
      </section>
    </main>
  );
}
