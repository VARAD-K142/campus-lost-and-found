import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import StatusBadge from '../components/StatusBadge';
import {
  FileText,
  Clock,
  CheckCircle,
  XCircle,
  PlusCircle,
  ArrowRight,
  ShieldCheck,
  User,
  Package
} from 'lucide-react';

const UserDashboardPage = () => {
  const { user } = useAuth();
  const [dashboardData, setDashboardData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    dashboardApi.getUserDashboard()
      .then((res) => {
        if (res.data.success) {
          setDashboardData(res.data.data);
        }
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load user dashboard metrics');
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <LoadingSpinner message="Calculating your campus records..." />;
  if (error) return <div className="container main-content"><div className="alert alert-danger">{error}</div></div>;

  return (
    <div className="container main-content">
      {/* Welcome Banner */}
      <div style={{
        backgroundColor: '#ffffff',
        border: '1px solid var(--border-color)',
        borderRadius: 'var(--radius-lg)',
        padding: '2rem',
        marginBottom: '2rem',
        boxShadow: 'var(--shadow-sm)',
        display: 'flex',
        flexWrap: 'wrap',
        justifyContent: 'space-between',
        alignItems: 'center',
        gap: '1.5rem'
      }}>
        <div>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.375rem', color: 'var(--accent)', fontSize: '0.8125rem', fontWeight: 700, textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            <User size={14} /> Student Dashboard
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--gray-900)' }}>
            Welcome back, {user?.fullName}!
          </h1>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
            ID: <strong>{user?.studentId || 'N/A'}</strong> &bull; Department: <strong>{user?.department || 'N/A'}</strong>
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <Link to="/items/new" className="btn btn-primary btn-sm">
            <PlusCircle size={16} /> File New Report
          </Link>
          <Link to="/items" className="btn btn-secondary btn-sm">
            Browse All Listings
          </Link>
        </div>
      </div>

      {/* Metric Cards Grid */}
      <div className="grid-cols-4" style={{ marginBottom: '2rem' }}>
        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            My Reports Filed
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--primary)' }}>
            {dashboardData?.totalMyReports || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Lost: {dashboardData?.totalMyLostReports || 0} &bull; Found: {dashboardData?.totalMyFoundReports || 0}
          </div>
        </div>

        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Pending Claims
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--warning)' }}>
            {dashboardData?.pendingClaims || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Awaiting administrator review
          </div>
        </div>

        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Approved Claims
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--success)' }}>
            {dashboardData?.approvedClaims || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Ready for physical collection
          </div>
        </div>

        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Collected / Reunited
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: '#7c3aed' }}>
            {dashboardData?.collectedItems || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Handover successfully completed
          </div>
        </div>
      </div>

      {/* Navigation Quick Links */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2.5rem' }}>
        <Link to="/my-reports" className="card" style={{ padding: '1.5rem', textDecoration: 'none', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.25rem' }}>
              My Property Reports
            </h3>
            <p style={{ fontSize: '0.8125rem', color: 'var(--gray-500)' }}>
              Manage, edit, or check resolution status of reports you created.
            </p>
          </div>
          <ArrowRight size={20} style={{ color: 'var(--accent)' }} />
        </Link>

        <Link to="/my-claims" className="card" style={{ padding: '1.5rem', textDecoration: 'none', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.25rem' }}>
              My Ownership Claims
            </h3>
            <p style={{ fontSize: '0.8125rem', color: 'var(--gray-500)' }}>
              Track review status, admin verification notes, and collection eligibility.
            </p>
          </div>
          <ArrowRight size={20} style={{ color: 'var(--accent)' }} />
        </Link>
      </div>

      {/* Recent Reports Table */}
      <div style={{ marginBottom: '1.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--gray-900)' }}>
            Recent Reports You Created
          </h2>
          <Link to="/my-reports" style={{ fontSize: '0.875rem', fontWeight: 600 }}>
            View all &rarr;
          </Link>
        </div>

        {(!dashboardData?.recentReports || dashboardData.recentReports.length === 0) ? (
          <div className="card" style={{ padding: '2rem', textAlign: 'center', color: 'var(--gray-500)' }}>
            You haven't filed any property reports yet.
          </div>
        ) : (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Title</th>
                  <th>Category</th>
                  <th>Campus Location</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {dashboardData.recentReports.slice(0, 5).map((item) => (
                  <tr key={item.id}>
                    <td><StatusBadge type={item.reportType} /></td>
                    <td style={{ fontWeight: 600 }}>{item.title}</td>
                    <td>{item.category}</td>
                    <td>{item.location}</td>
                    <td>{item.incidentDate}</td>
                    <td><StatusBadge status={item.status} /></td>
                    <td>
                      <Link to={`/items/${item.id}`} className="btn btn-secondary btn-sm">
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default UserDashboardPage;
