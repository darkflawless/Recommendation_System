import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import UserManagement from './pages/UserManagement';
import UserHistory from './pages/UserHistory';
import OrderDetail from './pages/OrderDetail';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/users" replace />} />
        <Route path="/users" element={<UserManagement />} />
        <Route path="/users/:customerId" element={<UserHistory />} />
        <Route path="/users/:customerId/orders/:orderId" element={<OrderDetail />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
