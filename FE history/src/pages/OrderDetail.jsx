import { useEffect, useMemo, useState } from 'react';
import { Link, useLocation, useParams } from 'react-router-dom';
import { getOrderDetails } from '../HistoryApi';

function toMoney(value) {
  return Number(value || 0).toLocaleString('vi-VN');
}

export default function OrderDetail() {
  const { customerId, orderId } = useParams();
  const location = useLocation();

  const [details, setDetails] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    setLoading(true);
    setError('');

    getOrderDetails(customerId, orderId)
      .then((data) => setDetails(Array.isArray(data) ? data : []))
      .catch(() => {
        setDetails([]);
        setError('Khong tai duoc chi tiet don hang.');
      })
      .finally(() => setLoading(false));
  }, [customerId, orderId]);

  const grandTotal = useMemo(
    () => details.reduce((sum, item) => sum + Number(item.quantity || 0) * Number(item.unitPrice || 0), 0),
    [details],
  );

  return (
    <main className="page-wrap">
      <section className="hero-panel compact">
        <p className="hero-eyebrow">Admin Console</p>
        <h1>Chi Tiet Don Hang #{orderId}</h1>
        <p className="hero-subtext">
          <Link className="back-link" to={`/users/${customerId}`} state={{ user: location.state?.user }}>
            Quay lai lich su khach hang
          </Link>
        </p>
      </section>

      <section className="card">
        {error && <p className="error-text">{error}</p>}
        <div className="table-shell">
          <table>
            <thead>
              <tr>
                <th>Ten san pham</th>
                <th>Anh</th>
                <th>So luong</th>
                <th>Don gia</th>
                <th>Tong tien</th>
              </tr>
            </thead>
            <tbody>
              {details.map((item) => {
                const lineTotal = Number(item.quantity || 0) * Number(item.unitPrice || 0);
                return (
                  <tr key={item.id}>
                    <td>{item.product?.name || '-'}</td>
                    <td>
                      {item.product?.imageUrl ? (
                        <img className="thumb" src={item.product.imageUrl} alt={item.product?.name || 'product'} />
                      ) : (
                        <span>-</span>
                      )}
                    </td>
                    <td>{item.quantity || 0}</td>
                    <td>{toMoney(item.unitPrice)} VND</td>
                    <td>{toMoney(lineTotal)} VND</td>
                  </tr>
                );
              })}
              {!loading && details.length === 0 && (
                <tr>
                  <td colSpan="5" className="empty-cell">Khong co san pham trong don hang nay.</td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
        <div className="order-total">Tong gia tri don: <strong>{toMoney(grandTotal)} VND</strong></div>
      </section>
    </main>
  );
}
