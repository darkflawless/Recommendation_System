import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import './App.css'
import SystemStats from './pages/SystemStats'
import ProductStats from './pages/ProductStats'
import ProductAllTimeStats from './pages/ProductAllTimeStats'

function App() {
  return (
    <BrowserRouter>
      <main className="app-shell">
        <Routes>
          <Route path="/" element={<Navigate to="/system-stats" replace />} />
          <Route path="/system-stats" element={<SystemStats />} />
          <Route path="/product-stats" element={<ProductStats />} />
          <Route path="/product-all-time-stats" element={<ProductAllTimeStats />} />
        </Routes>
      </main>
    </BrowserRouter>
  )
}

export default App
