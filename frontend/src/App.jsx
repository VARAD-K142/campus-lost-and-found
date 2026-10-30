import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { AdminRoute, ProtectedRoute } from './components/ProtectedRoute';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import LandingPage from './pages/LandingPage';
import ItemsListPage from './pages/ItemsListPage';
import ItemDetailPage from './pages/ItemDetailPage';
import ReportItemPage from './pages/ReportItemPage';
import EditItemPage from './pages/EditItemPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import UserDashboardPage from './pages/UserDashboardPage';
import MyReportsPage from './pages/MyReportsPage';
import MyClaimsPage from './pages/MyClaimsPage';
import ClaimDetailPage from './pages/ClaimDetailPage';
import AdminDashboardPage from './pages/AdminDashboardPage';

const protectedPage = (page) => <ProtectedRoute>{page}</ProtectedRoute>;
const adminPage = (page) => <AdminRoute>{page}</AdminRoute>;

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Navbar />
        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/items" element={<ItemsListPage />} />
          <Route path="/items/new" element={protectedPage(<ReportItemPage />)} />
          <Route path="/items/:id/edit" element={protectedPage(<EditItemPage />)} />
          <Route path="/items/:id" element={<ItemDetailPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/dashboard" element={protectedPage(<UserDashboardPage />)} />
          <Route path="/my-reports" element={protectedPage(<MyReportsPage />)} />
          <Route path="/my-claims" element={protectedPage(<MyClaimsPage />)} />
          <Route path="/claims/:id" element={protectedPage(<ClaimDetailPage />)} />
          <Route path="/admin/dashboard" element={adminPage(<AdminDashboardPage />)} />
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
        <Footer />
      </AuthProvider>
    </BrowserRouter>
  );
}
