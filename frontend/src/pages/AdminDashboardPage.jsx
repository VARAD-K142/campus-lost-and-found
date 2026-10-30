import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';
import StatusBadge from '../components/StatusBadge';
import {
  ShieldCheck,
  Package,
  FileCheck,
  CheckCircle2,
  Users,
  Clock,
  Layers,
  ArrowRight,
  Database,
  ExternalLink
} from 'lucide-react';

const AdminDashboardPage = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [jdbcResult, setJdbcResult] = useState(null);
  const [jdbcLoading, setJdbcLoading] = useState(false);

  useEffect(() => {
    dashboardApi.getAdminDashboard()
      .then((res) => {
        if (res.data.success) {
          setData(res.data.data);
        }
      })
      .catch((err) => {
        console.error(err);
        setError('Failed to load administrative analytics');
      })
      .finally(() => setLoading(false));
  }, []);

  const runJdbcVerification = async () => {
    setJdbcLoading(true);
    try {
      const res = await dashboardApi.getJdbcTest();
      if (res.data.success) {
        setJdbcResult(res.data.data);
      }
    } catch (err) {
      alert('JDBC Test Failed: ' + (err.response?.data?.error || err.message));
    } finally {
      setJdbcLoading(false);
    }
  };

  if (loading) return <LoadingSpinner message="Calculating administrative metrics from MySQL..." />;
  if (error) return <div className="container main-content"><div className="alert alert-danger">{error}</div></div>;

  return (
    <div className="container main-content">
      {/* Header Banner */}
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
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: '0.375rem', color: 'var(--primary)', fontSize: '0.8125rem', fontWeight: 700, textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            <ShieldCheck size={16} /> Campus Administrator Portal
          </div>
          <h1 style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--gray-900)' }}>
            System Operations Dashboard
          </h1>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
            Real-time MySQL 8 persistence analytics, claim review queue, and physical collection management.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <Link to="/admin/claims" className="btn btn-primary btn-sm">
            <Clock size={16} /> Review Pending Claims ({data?.pendingClaims || 0})
          </Link>
          <Link to="/admin/collections" className="btn btn-secondary btn-sm">
            <CheckCircle2 size={16} /> Record Handover
          </Link>
          <button onClick={runJdbcVerification} className="btn btn-outline btn-sm" disabled={jdbcLoading}>
            <Database size={16} /> {jdbcLoading ? 'Querying JDBC...' : 'Run CO-3 JDBC Test'}
          </button>
        </div>
      </div>

      {/* CO-3 JDBC Live Result Banner */}
      {jdbcResult && (
        <div className="card" style={{ padding: '1.25rem 1.5rem', marginBottom: '2rem', backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontWeight: 700, color: '#15803d', marginBottom: '0.5rem' }}>
            <Database size={18} /> CO-3 JDBC Execution Verification Results (Live MySQL Connection)
          </div>
          <div style={{ fontSize: '0.8125rem', color: '#166534', fontFamily: 'var(--font-mono)' }}>
            Product: <strong>{jdbcResult.databaseProductName}</strong> {jdbcResult.databaseProductVersion} &bull; Driver: <strong>{jdbcResult.driverName}</strong><br />
            Connection URL: <code>{jdbcResult.url}</code> &bull; Active Items fetched via PreparedStatement JOIN: <strong>{jdbcResult.activeItemsFoundViaJdbcJoin}</strong>
          </div>
        </div>
      )}

      {/* Metrics Grid */}
      <div className="grid-cols-4" style={{ marginBottom: '2rem' }}>
        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Total Items Reported
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--primary)' }}>
            {data?.totalItems || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Lost: {data?.lostItems || 0} &bull; Found: {data?.foundItems || 0}
          </div>
        </div>

        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Pending Claim Queue
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--warning)' }}>
            {data?.pendingClaims || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Requires admin verification
          </div>
        </div>

        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Physical Collections
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--success)' }}>
            {data?.totalCollections || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Handover records stored
          </div>
        </div>

        <div className="card" style={{ padding: '1.25rem' }}>
          <div style={{ fontSize: '0.75rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase', marginBottom: '0.25rem' }}>
            Registered Users
          </div>
          <div style={{ fontSize: '1.875rem', fontWeight: 800, color: '#6366f1' }}>
            {data?.totalUsers || 0}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
            Students, faculty & staff
          </div>
        </div>
      </div>

      {/* Category Distribution Summary */}
      <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
        <h3 style={{ fontSize: '1.0625rem', fontWeight: 700, marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
          <Layers size={18} /> Property Distribution by Category
        </h3>
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fill, minmax(130px, 1fr))',
          gap: '0.75rem'
        }}>
          {data?.itemsByCategory && Object.entries(data.itemsByCategory).map(([cat, count]) => (
            <div key={cat} style={{
              backgroundColor: 'var(--gray-50)',
              padding: '0.75rem 1rem',
              borderRadius: 'var(--radius-md)',
              border: '1px solid var(--border-color)',
              textAlign: 'center'
            }}>
              <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--gray-500)', textTransform: 'uppercase' }}>
                {cat}
              </div>
              <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--gray-900)', marginTop: '0.25rem' }}>
                {count}
              </div>
            </div>
          ))}
        </div>
      </div>

      {/* Pending Claims Queue Table */}
      <div style={{ marginBottom: '2.5rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2 style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--gray-900)' }}>
            Pending Claims Queue (Awaiting Verification)
          </h2>
          <Link to="/admin/claims" style={{ fontSize: '0.875rem', fontWeight: 600 }}>
            Open Claim Review Queue &rarr;
          </Link>
        </div>

        {(!data?.pendingClaimsQueue || data.pendingClaimsQueue.length === 0) ? (
          <div className="card" style={{ padding: '2rem', textAlign: 'center', color: 'var(--gray-500)' }}>
            No pending claims in queue. All ownership claims have been reviewed!
          </div>
        ) : (
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Claim ID</th>
                  <th>Item Title</th>
                  <th>Claimant Name</th>
                  <th>Student ID</th>
                  <th>Submission Date</th>
                  <th>Action</th>
                </tr>
              </thead>
              <tbody>
                {data.pendingClaimsQueue.slice(0, 5).map((claim) => (
                  <tr key={claim.id}>
                    <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>#{claim.id}</td>
                    <td style={{ fontWeight: 600 }}>{claim.itemTitle || `Item #${claim.itemId}`}</td>
                    <td>{claim.claimantName}</td>
                    <td>{claim.claimantStudentId || 'N/A'}</td>
                    <td>{claim.submittedAt ? new Date(claim.submittedAt).toLocaleDateString() : 'N/A'}</td>
                    <td>
                      <Link to="/admin/claims" className="btn btn-primary btn-sm">
                        Review Claim
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Navigation Quick Links */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: '1.25rem' }}>
        <Link to="/admin/items" className="card" style={{ padding: '1.5rem', textDecoration: 'none' }}>
          <h3 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.25rem' }}>
            Moderate Listings
          </h3>
          <p style={{ fontSize: '0.8125rem', color: 'var(--gray-500)' }}>
            Audit, edit status, or close lost/found reports directly.
          </p>
        </Link>

        <Link to="/admin/claims" className="card" style={{ padding: '1.5rem', textDecoration: 'none' }}>
          <h3 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.25rem' }}>
            Verify Ownership Claims
          </h3>
          <p style={{ fontSize: '0.8125rem', color: 'var(--gray-500)' }}>
            Approve or reject ownership claims with confidential review notes.
          </p>
        </Link>

        <Link to="/admin/collections" className="card" style={{ padding: '1.5rem', textDecoration: 'none' }}>
          <h3 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.25rem' }}>
            Handover & Collections
          </h3>
          <p style={{ fontSize: '0.8125rem', color: 'var(--gray-500)' }}>
            Record physical collection with student verification ID and notes.
          </p>
        </Link>
      </div>
    </div>
  );
};

export default AdminDashboardPage;
