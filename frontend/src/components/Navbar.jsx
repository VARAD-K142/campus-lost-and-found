import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  Compass,
  PlusCircle,
  LayoutDashboard,
  ShieldCheck,
  LogOut,
  User,
  Menu,
  X,
  FileText
} from 'lucide-react';

const Navbar = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav style={{
      backgroundColor: '#ffffff',
      borderBottom: '1px solid var(--border-color)',
      position: 'sticky',
      top: 0,
      zIndex: 40,
      boxShadow: 'var(--shadow-sm)'
    }}>
      <div className="container" style={{
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        height: '4.25rem'
      }}>
        {/* Brand */}
        <Link to="/" style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', textDecoration: 'none' }}>
          <div style={{
            width: '2.5rem',
            height: '2.5rem',
            borderRadius: 'var(--radius-md)',
            backgroundColor: 'var(--primary)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            color: '#ffffff'
          }}>
            <Compass size={22} />
          </div>
          <div>
            <div style={{ fontSize: '1.125rem', fontWeight: 800, color: 'var(--gray-900)', lineHeight: 1.1 }}>
              SRM Campus
            </div>
            <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--accent)', letterSpacing: '0.05em' }}>
              LOST & FOUND PORTAL
            </div>
          </div>
        </Link>

        {/* Desktop Navigation Links */}
        <div style={{ display: 'none', alignItems: 'center', gap: '1.5rem' }} className="desktop-nav">
          <Link to="/items" style={{ fontWeight: 600, color: 'var(--gray-700)', display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
            <Compass size={18} />
            Browse Listings
          </Link>

          {isAuthenticated && (
            <>
              <Link to="/items/new" className="btn btn-accent btn-sm" style={{ display: 'inline-flex' }}>
                <PlusCircle size={16} />
                Report Item
              </Link>
              <Link to="/dashboard" style={{ fontWeight: 600, color: 'var(--gray-700)', display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
                <LayoutDashboard size={18} />
                User Dashboard
              </Link>
            </>
          )}

          {isAdmin && (
            <Link to="/admin/dashboard" style={{
              fontWeight: 600,
              color: 'var(--primary)',
              display: 'flex',
              alignItems: 'center',
              gap: '0.375rem',
              backgroundColor: 'var(--primary-light)',
              padding: '0.375rem 0.75rem',
              borderRadius: 'var(--radius-sm)'
            }}>
              <ShieldCheck size={18} />
              Admin Portal
            </Link>
          )}
        </div>

        {/* Auth Actions */}
        <div style={{ display: 'none', alignItems: 'center', gap: '0.875rem' }} className="desktop-auth">
          {isAuthenticated ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
              <div style={{ textAlign: 'right' }}>
                <div style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--gray-900)' }}>
                  {user?.fullName}
                </div>
                <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)' }}>
                  {isAdmin ? 'Campus Admin' : (user?.studentId || 'Student')}
                </div>
              </div>
              <button onClick={handleLogout} className="btn btn-secondary btn-sm" title="Log Out">
                <LogOut size={16} />
                Sign Out
              </button>
            </div>
          ) : (
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <Link to="/login" className="btn btn-secondary btn-sm">
                Sign In
              </Link>
              <Link to="/register" className="btn btn-primary btn-sm">
                Register
              </Link>
            </div>
          )}
        </div>

        {/* Mobile menu button */}
        <button
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          style={{
            background: 'none',
            border: 'none',
            padding: '0.5rem',
            cursor: 'pointer',
            color: 'var(--gray-700)',
            display: 'block'
          }}
          className="mobile-btn"
          aria-label="Toggle menu"
        >
          {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
        </button>
      </div>

      {/* Mobile Dropdown */}
      {mobileMenuOpen && (
        <div style={{
          backgroundColor: '#ffffff',
          borderTop: '1px solid var(--border-color)',
          padding: '1.25rem',
          display: 'flex',
          flexDirection: 'column',
          gap: '1rem'
        }}>
          <Link to="/items" onClick={() => setMobileMenuOpen(false)} style={{ fontWeight: 600, color: 'var(--gray-800)' }}>
            Browse Listings
          </Link>
          {isAuthenticated ? (
            <>
              <Link to="/items/new" onClick={() => setMobileMenuOpen(false)} style={{ fontWeight: 600, color: 'var(--accent)' }}>
                + Report New Item
              </Link>
              <Link to="/dashboard" onClick={() => setMobileMenuOpen(false)} style={{ fontWeight: 600, color: 'var(--gray-800)' }}>
                User Dashboard
              </Link>
              <Link to="/my-reports" onClick={() => setMobileMenuOpen(false)} style={{ fontWeight: 600, color: 'var(--gray-800)' }}>
                My Reports
              </Link>
              <Link to="/my-claims" onClick={() => setMobileMenuOpen(false)} style={{ fontWeight: 600, color: 'var(--gray-800)' }}>
                My Claims
              </Link>
              {isAdmin && (
                <Link to="/admin/dashboard" onClick={() => setMobileMenuOpen(false)} style={{ fontWeight: 700, color: 'var(--primary)' }}>
                  Admin Dashboard
                </Link>
              )}
              <button onClick={() => { setMobileMenuOpen(false); handleLogout(); }} className="btn btn-danger btn-sm" style={{ width: '100%', marginTop: '0.5rem' }}>
                <LogOut size={16} /> Sign Out
              </button>
            </>
          ) : (
            <div style={{ display: 'flex', gap: '0.75rem', marginTop: '0.5rem' }}>
              <Link to="/login" onClick={() => setMobileMenuOpen(false)} className="btn btn-secondary btn-sm" style={{ flex: 1 }}>
                Sign In
              </Link>
              <Link to="/register" onClick={() => setMobileMenuOpen(false)} className="btn btn-primary btn-sm" style={{ flex: 1 }}>
                Register
              </Link>
            </div>
          )}
        </div>
      )}

      <style>{`
        @media (min-width: 768px) {
          .desktop-nav { display: flex !important; }
          .desktop-auth { display: flex !important; }
          .mobile-btn { display: none !important; }
        }
      `}</style>
    </nav>
  );
};

export default Navbar;
