import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { fetchPagedProducts } from '../api/statsApi'

function toIsoDate(date) {
  return date.toISOString().slice(0, 10)
}

function defaultRange() {
  const today = new Date()
  const sevenDaysAgo = new Date(today)
  sevenDaysAgo.setDate(today.getDate() - 7)

  return {
    startDate: toIsoDate(sevenDaysAgo),
    endDate: toIsoDate(today),
  }
}

function ProductStats() {
  const navigate = useNavigate()
  const initialRange = useMemo(() => defaultRange(), [])
  const [startDate, setStartDate] = useState(initialRange.startDate)
  const [endDate, setEndDate] = useState(initialRange.endDate)
  const [sortType, setSortType] = useState('TOP_VIEW')
  const [page, setPage] = useState(0)
  const [size, setSize] = useState(5)
  const [rows, setRows] = useState([])
  const [totalElements, setTotalElements] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  const loadData = async (targetPage = page) => {
    setLoading(true)
    setError('')

    try {
      const data = await fetchPagedProducts({
        startDate,
        endDate,
        sortType,
        page: targetPage,
        size,
      })
      setRows(Array.isArray(data.content) ? data.content : [])
      setTotalElements(Number(data.totalElements || 0))
      setTotalPages(Number(data.totalPages || 0))
      setPage(Number(data.number || 0))
    } catch (err) {
      setRows([])
      setTotalElements(0)
      setTotalPages(0)
      setError(err.message || 'Co loi khi tai du lieu')
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    loadData()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const handleSubmit = async (event) => {
    event.preventDefault()
    await loadData(0)
  }

  const goToPrevPage = async () => {
    if (loading || page <= 0) {
      return
    }
    await loadData(page - 1)
  }

  const goToNextPage = async () => {
    if (loading || page + 1 >= totalPages) {
      return
    }
    await loadData(page + 1)
  }

  const isInvalidRange = startDate && endDate && startDate > endDate

  return (
    <section className="panel">
      <header className="panel-head panel-head-row">
        <div>
          <p className="eyebrow">Thong ke theo san pham</p>
          <h1>ProductStats</h1>
          <p className="sub">Loc theo ngay bat dau, ngay ket thuc va xem top san pham.</p>
        </div>
        <button className="ghost-btn" onClick={() => navigate('/system-stats')}>
          Quay lai SystemStats
        </button>
      </header>

      <form className="filters" onSubmit={handleSubmit}>
        <label>
          Tu ngay
          <input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
        </label>

        <label>
          Den ngay
          <input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
        </label>

        <label>
          Sap xep
          <select value={sortType} onChange={(e) => setSortType(e.target.value)}>
            <option value="TOP_VIEW">Views</option>
            <option value="TOP_RECOMMENDATION">Recommendations</option>
            <option value="TOP_VIEW_RATE">View rate</option>
          </select>
        </label>

        <label>
          Kich thuoc trang
          <select value={size} onChange={(e) => setSize(Number(e.target.value))}>
            <option value="5">5</option>
            <option value="10">10</option>
            <option value="15">15</option>
          </select>
        </label>

        <button className="primary-btn" type="submit" disabled={loading || isInvalidRange}>
          {loading ? 'Dang tai...' : 'Tai du lieu phan trang'}
        </button>
      </form>

      {isInvalidRange && <p className="error">Ngay bat dau khong duoc lon hon ngay ket thuc.</p>}
      {error && <p className="error">{error}</p>}

      {!loading && !error && rows.length === 0 && <p className="empty">Khong co du lieu trong khoang ngay da chon.</p>}

      {!error && totalElements > 0 && (
        <p className="sub">
          Tong san pham: {totalElements} | Trang {page + 1}/{Math.max(totalPages, 1)}
        </p>
      )}

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Product</th>
              <th>Total Recommendations</th>
              <th>Total Views</th>
              <th>View Rate</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((row) => (
              <tr key={row.productId}>
                <td>{row.productName || 'Unknown'}</td>
                <td>{row.totalRecommendations}</td>
                <td>{row.totalViews}</td>
                <td>{Number(row.viewRate || 0).toFixed(2)}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="panel-head-row" style={{ marginTop: 12 }}>
        <button className="ghost-btn" onClick={goToPrevPage} disabled={loading || page <= 0}>
          Trang truoc
        </button>
        <button className="ghost-btn" onClick={goToNextPage} disabled={loading || page + 1 >= totalPages}>
          Trang sau
        </button>
      </div>
    </section>
  )
}

export default ProductStats
