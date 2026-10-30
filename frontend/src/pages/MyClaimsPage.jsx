import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { claimsApi } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import StatusBadge from '../components/StatusBadge';
import { FileCheck, ArrowRight, Clock, Eye } from 'lucide-react';

const MyClaimsPage = () => {
  const [claims, setClaims] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchClaims = async () => {
    setLoading(true);
    try {
      const res = await claimsApi.getMyClaims();
      if (res.data.success) {
        setClaims(res.data.data);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to load your submitted claims from the microservice.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchClaims();
  }, []);

  return (
    <div className="container main-content">
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--gray-900)' }}>
          My Ownership Claims
        </h1>
        <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
          Track the status and administrator review notes for your ownership claims.
        </p>
      </div>

      {loading ? (
        <LoadingSpinner message="Querying claims from microservice..." />
      ) : error ? (
        <div className="alert alert-danger">{error}</div>
      ) : claims.length === 0 ? (
        <EmptyState
          title="No claims submitted"
          description="You haven't filed any ownership claims yet. If you see your lost belonging listed as found, you can submit a claim from its details page."
          actionText="Browse Found Items"
          actionLink="/items?reportType=FOUND"
        />
      ) : (
        <div className="table-container">
          <table className="table">
            <thead>
              <tr>
                <th>Claim ID</th>
                <th>Target Item</th>
                <th>Submission Date</th>
                <th>Status</th>
                <th>Admin Review Verdict</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {claims.map((claim) => (
                <tr key={claim.id}>
                  <td style={{ fontFamily: 'var(--font-mono)', fontWeight: 600 }}>#{claim.id}</td>
                  <td style={{ fontWeight: 600 }}>
                    <Link to={`/items/${claim.itemId}`} style={{ color: 'var(--primary)' }}>
                      {claim.itemTitle || `Item #${claim.itemId}`}
                    </Link>
                  </td>
                  <td>{claim.submittedAt ? new Date(claim.submittedAt).toLocaleDateString() : 'N/A'}</td>
                  <td><StatusBadge status={claim.status} /></td>
                  <td style={{ maxWidth: '300px' }}>
                    {claim.adminNotes ? (
                      <span style={{ fontSize: '0.8125rem', color: 'var(--gray-700)' }}>{claim.adminNotes}</span>
                    ) : (
                      <span style={{ fontSize: '0.8125rem', color: 'var(--gray-400)', fontStyle: 'italic' }}>Awaiting administrator review</span>
                    )}
                  </td>
                  <td>
                    <Link to={`/claims/${claim.id}`} className="btn btn-secondary btn-sm">
                      <Eye size={14} /> View Claim
                    </Link>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default MyClaimsPage;
