import { useEffect, useMemo, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  fetchAllTimeProducts,
  fetchProductAllTimeSummary,
  fetchProductSummaryByRange,
} from '../api/statsApi'

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

function ProductAllTimeStats() {
  const navigate = useNavigate()
  const initialRange = useMemo(() => defaultRange(), [])
  const [products, setProducts] = useState([])
  const [searchKeyword, setSearchKeyword] = useState('')
  const [selectedProductId, setSelectedProductId] = useState('')
  const [summaryMode, setSummaryMode] = useState('ALL_TIME')
  const [startDate, setStartDate] = useState(initialRange.startDate)
  const [endDate, setEndDate] = useState(initialRange.endDate)
  const [summary, setSummary] = useState(null)
  const [loadingProducts, setLoadingProducts] = useState(false)
  const [loadingSummary, setLoadingSummary] = useState(false)
  const [error, setError] = useState('')
  const isInvalidRange = startDate && endDate && startDate > endDate

  const filteredProducts = useMemo(() => {
    const keyword = searchKeyword.trim().toLowerCase()
    if (!keyword) {
      return products
    }

    return products.filter((item) => {
      const name = (item.productName || '').toLowerCase()
      const id = String(item.productId || '')
      return name.includes(keyword) || id.includes(keyword)
    })
  }, [products, searchKeyword])

  const selectedProduct = useMemo(
    () => products.find((item) => String(item.productId) === String(selectedProductId)),
    [products, selectedProductId],
  )

  useEffect(() => {
    const loadProducts = async () => {
      setLoadingProducts(true)
      setError('')

      try {
        const data = await fetchAllTimeProducts({ limit: 20, sortType: 'TOP_VIEW' })
        const list = Array.isArray(data) ? data : []
        setProducts(list)

        if (list.length > 0) {
          setSelectedProductId(String(list[0].productId))
        }
      } catch (err) {
        setProducts([])
        setError(err.message || 'Co loi khi tai danh sach san pham')
      } finally {
        setLoadingProducts(false)
      }
    }

    loadProducts()
  }, [])

  useEffect(() => {
    if (filteredProducts.length === 0) {
      if (selectedProductId) {
        setSelectedProductId('')
      }
      return
    }

    const existsInFiltered = filteredProducts.some(
      (item) => String(item.productId) === String(selectedProductId),
    )

    if (!existsInFiltered) {
      setSelectedProductId(String(filteredProducts[0].productId))
    }
  }, [filteredProducts, selectedProductId])

  useEffect(() => {
    const loadSummary = async () => {
      if (!selectedProductId) {
        setSummary(null)
        return
      }

      if (summaryMode === 'RANGE' && isInvalidRange) {
        setSummary(null)
        setError('Ngay bat dau khong duoc lon hon ngay ket thuc.')
        return
      }

      setLoadingSummary(true)
      setError('')

      try {
        const data =
          summaryMode === 'ALL_TIME'
            ? await fetchProductAllTimeSummary(selectedProductId)
            : await fetchProductSummaryByRange({
                productId: selectedProductId,
                startDate,
                endDate,
              })
        setSummary(data)
      } catch (err) {
        setSummary(null)
        setError(err.message || 'Co loi khi tai summary san pham')
      } finally {
        setLoadingSummary(false)
      }
    }

    loadSummary()
  }, [selectedProductId, summaryMode, startDate, endDate, isInvalidRange])

  return (
    <section className="panel">
      <header className="panel-head panel-head-row">
        <div>
          <p className="eyebrow">Thong ke 1 san pham</p>
          <h1>ProductAllTimeStats</h1>
          <p className="sub">Chon 1 san pham trong Mongo va xem tong hop toan thoi gian.</p>
        </div>
        <button className="ghost-btn" onClick={() => navigate('/product-stats')}>
          Quay lai ProductStats
        </button>
      </header>

      <form className="filters" onSubmit={(e) => e.preventDefault()}>
        <label>
          Tim san pham
          <input
            type="text"
            value={searchKeyword}
            onChange={(e) => setSearchKeyword(e.target.value)}
            placeholder="Nhap ten hoac ID..."
            disabled={loadingProducts || products.length === 0}
          />
        </label>

        <label>
          San pham ({filteredProducts.length}/{products.length})
          <select
            value={selectedProductId}
            onChange={(e) => setSelectedProductId(e.target.value)}
            disabled={loadingProducts || filteredProducts.length === 0}
          >
            {filteredProducts.length === 0 && <option value="">Khong tim thay san pham</option>}
            {filteredProducts.map((item) => (
              <option key={item.productId} value={String(item.productId)}>
                {item.productName || 'Unknown'} (ID: {item.productId})
              </option>
            ))}
          </select>
        </label>

        <label>
          Kieu summary
          <select value={summaryMode} onChange={(e) => setSummaryMode(e.target.value)}>
            <option value="ALL_TIME">All time</option>
            <option value="RANGE">Theo khoang ngay</option>
          </select>
        </label>

        <label>
          Tu ngay
          <input
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
            disabled={summaryMode !== 'RANGE'}
          />
        </label>

        <label>
          Den ngay
          <input
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
            disabled={summaryMode !== 'RANGE'}
          />
        </label>
      </form>

      {loadingProducts && <p className="empty">Dang tai danh sach san pham...</p>}
      {loadingSummary && <p className="empty">Dang tai summary...</p>}
      {summaryMode === 'RANGE' && isInvalidRange && (
        <p className="error">Ngay bat dau khong duoc lon hon ngay ket thuc.</p>
      )}
      {error && <p className="error">{error}</p>}

      {!loadingProducts && products.length === 0 && !error && (
        <p className="empty">Chua co du lieu san pham trong Mongo.</p>
      )}

      {summary && !loadingSummary && (
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
              <tr>
                <td>{selectedProduct?.productName || summary.productName || 'Unknown'}</td>
                <td>{summary.totalRecommendations}</td>
                <td>{summary.totalViews}</td>
                <td>{Number(summary.viewRate || 0).toFixed(2)}%</td>
              </tr>
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}

export default ProductAllTimeStats
