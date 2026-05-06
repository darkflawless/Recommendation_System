export default function SearchHistoryTable({ rows, loading, formatDateTime }) {
  return (
    <table>
      <thead>
        <tr>
          <th>Tu khoa tim kiem</th>
          <th>So ket qua</th>
          <th>Thoi gian</th>
        </tr>
      </thead>
      <tbody>
        {rows.map((entry) => (
          <tr key={entry.id}>
            <td>{entry.keyword || '-'}</td>
            <td>{entry.resultsCount ?? '-'}</td>
            <td>{formatDateTime(entry.createdAt)}</td>
          </tr>
        ))}
        {!loading && rows.length === 0 && (
          <tr>
            <td colSpan="3" className="empty-cell">Khong co du lieu.</td>
          </tr>
        )}
      </tbody>
    </table>
  );
}
