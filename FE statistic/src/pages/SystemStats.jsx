import { useNavigate } from 'react-router-dom'

function SystemStats() {
  const navigate = useNavigate()

  const openProductStats = () => {
    navigate('/product-stats')
  }

  const openProductAllTimeStats = () => {
    navigate('/product-all-time-stats')
  }

  return (
    <section className="panel">
      <header className="panel-head">
        <p className="eyebrow">Thong ke he thong</p>
        <h1>SystemStats</h1>
        <p className="sub">Man hinh tong quan. Bam nut de vao thong ke top san pham theo khoang ngay.</p>
      </header>

      <button className="primary-btn" onClick={openProductStats}>
        Mo ProductStats
      </button>

      <button className="ghost-btn" onClick={openProductAllTimeStats}>
        Mo ProductAllTimeStats
      </button>
    </section>
  )
}

export default SystemStats
