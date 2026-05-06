export default function OrderHistoryTable({ rows, loading, onOpenOrder, formatDateTime }) {
  return (
    <table>
      <thead>
        <tr>
          <th>Thoi gian dat</th>
          <th>Tong tien</th>
          <th>Trang thai</th>
          <th>Ghi chu</th>
          <th>Chi tiet</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((order) => (
          <tr key={order.id}>
            <td>{formatDateTime(order.orderDate)}</td>
            <td>{Number(order.totalAmount || 0).toLocaleString('vi-VN')} VND</td>
            <td>{order.status || '-'}</td>
            <td style={{ whiteSpace: 'pre-wrap' }}>{order.note || '-'}</td>
            <td>
              <button className="btn btn-small" onClick={() => onOpenOrder(order)}>
                Xem don
              </button>
            </td>
          </tr>
        ))}
        {!loading && rows.length === 0 && (
          <tr>
            <td colSpan="5" className="empty-cell">Khong co du lieu.</td>
          </tr>
        )}
      </tbody>
    </table>
  );
}
