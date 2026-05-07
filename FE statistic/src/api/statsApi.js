const BASE_PATH = '/api/admin/statistics/recommendation-products'

export async function fetchPagedProducts({
  startDate,
  endDate,
  page = 0,
  size = 5,
  sortType = 'TOP_VIEW',
}) {
  const params = new URLSearchParams({
    startDate,
    endDate,
    page: String(page),
    size: String(size),
    sortType,
  })

  const response = await fetch(`${BASE_PATH}?${params.toString()}`)

  if (!response.ok) {
    if (response.status === 400) {
      throw new Error('Khoang ngay khong hop le. Vui long kiem tra lai.')
    }
    throw new Error(`Khong the tai du lieu phan trang. HTTP ${response.status}`)
  }

  return response.json()
}

export async function fetchAllTimeProducts({ limit = 10, sortType = 'TOP_VIEW' } = {}) {
  const params = new URLSearchParams({
    limit: String(limit),
    sortType,
  })

  const response = await fetch(`${BASE_PATH}/products?${params.toString()}`)

  if (!response.ok) {
    throw new Error(`Khong the tai danh sach san pham. HTTP ${response.status}`)
  }

  return response.json()
}

export async function fetchProductAllTimeSummary(productId) {
  if (!productId) {
    throw new Error('Thieu productId')
  }

  const response = await fetch(`${BASE_PATH}/${productId}/all-time`)

  if (!response.ok) {
    throw new Error(`Khong the tai thong ke san pham. HTTP ${response.status}`)
  }

  return response.json()
}

export async function fetchProductSummaryByRange({ productId, startDate, endDate }) {
  if (!productId) {
    throw new Error('Thieu productId')
  }

  const params = new URLSearchParams({ startDate, endDate })
  const response = await fetch(`${BASE_PATH}/${productId}?${params.toString()}`)

  if (!response.ok) {
    if (response.status === 400) {
      throw new Error('Khoang ngay khong hop le. Vui long kiem tra lai.')
    }
    throw new Error(`Khong the tai summary theo khoang ngay. HTTP ${response.status}`)
  }

  return response.json()
}
