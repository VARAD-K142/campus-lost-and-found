import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom';
import { claimsApi, collectionsApi } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';
import StatusBadge from '../components/StatusBadge';
import {
  FileCheck,
  ArrowLeft,
  Clock,
  ShieldCheck,
  AlertCircle,
  CheckCircle2,
  XCircle,
  User,
  Package
} from 'lucide-react';

const ClaimDetailPage = () => {
  const { id } = useParams();
  const [claim, setClaim] = useState(null);
  const [collection, setCollection] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchClaim = async () => {
      setLoading(true);
      try {
        const res = await claimsApi.getById(id);
        if (res.data.success) {
          setClaim(res.data.data);

          // If approved, check if collection is recorded
          if (res.data.data.status === 'APPROVED') {
            try {
              const colRes = await collectionsApi.getByItemId(res.data.data.itemId);
              if (colRes.data.success) {
                setCollection(colRes.data.data);
              }
            } catch (e) {
              // Not yet collected
            }
          }
        }
      } catch (err) {
        console.error(err);
        setError('Failed to load claim details from microservice.');
      } finally {
        setLoading(false);
      }
    };
    fetchClaim();
  }, [id]);

  if (loading) return <LoadingSpinner message="Querying claim record..." />;
  if (error || !claim) {
    return (
      <div className="container main-content">
        <div className="alert alert-danger">{error || 'Claim record not found.'}</div>
        <Link to="/my-claims" className="btn btn-secondary btn-sm">
          <ArrowLeft size={16} /> Back to My Claims
        </Link>
      </div>
    );
  }

  return (
    <div className="container main-content" style={{ maxWidth: '800px' }}>
      <div style={{ marginBottom: '1.5rem' }}>
        <Link to="/my-claims" className="btn btn-secondary btn-sm" style={{ display: 'inline-flex' }}>
          <ArrowLeft size={16} /> Back to My Claims
        </Link>
      </div>

      <div className="card" style={{ padding: '2rem', marginBottom: '2rem' }}>
        {/* Header */}
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.5rem', paddingBottom: '1.25rem', borderBottom: '1px solid var(--border-color)' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.25rem' }}>
              <span style={{ fontSize: '0.8125rem', fontWeight: 700, color: 'var(--gray-500)', textTransform: 'uppercase' }}>
                Claim ID
              </span>
              <span style={{ fontFamily: 'var(--font-mono)', fontWeight: 700, color: 'var(--gray-800)' }}>
                #{claim.id}
              </span>
            </div>
            <h1 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--gray-900)' }}>
              Claim for: <Link to={`/items/${claim.itemId}`} style={{ color: 'var(--primary)' }}>{claim.itemTitle || `Item #${claim.itemId}`}</Link>
            </h1>
          </div>

          <StatusBadge status={claim.status} />
        </div>

        {/* Status Callout Banner */}
        {claim.status === 'PENDING' && (
          <div className="alert alert-info" style={{ marginBottom: '1.75rem' }}>
            <Clock size={20} />
            <div>
              <strong>Pending Review:</strong> Your ownership claim is currently in the verification queue. Campus security administrators review serial numbers, distinguishing marks, and proof of purchase.
            </div>
          </div>
        )}

        {claim.status === 'APPROVED' && (
          <div className="alert alert-success" style={{ marginBottom: '1.75rem' }}>
            <CheckCircle2 size={20} />
            <div>
              <strong>Claim Approved!</strong> The campus administrator has verified your claim.
              {collection ? (
                <span> Physical collection has been recorded and completed.</span>
              ) : (
                <span> Please visit the Campus Security Desk with your SRM Student ID to collect your property.</span>
              )}
            </div>
          </div>
        )}

        {claim.status === 'REJECTED' && (
          <div className="alert alert-danger" style={{ marginBottom: '1.75rem' }}>
            <XCircle size={20} />
            <div>
              <strong>Claim Rejected:</strong> The administrator could not verify ownership based on the evidence provided. See administrator review notes below.
            </div>
          </div>
        )}

        {/* Claim Content Details */}
        <div style={{ marginBottom: '1.75rem' }}>
          <h3 style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--gray-600)', textTransform: 'uppercase', marginBottom: '0.5rem' }}>
            Claim Explanation
          </h3>
          <p style={{ fontSize: '0.9375rem', color: 'var(--gray-800)', lineHeight: 1.6, padding: '1rem', backgroundColor: 'var(--gray-50)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
            {claim.claimExplanation}
          </p>
        </div>

        <div style={{ marginBottom: '1.75rem' }}>
          <h3 style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--gray-600)', textTransform: 'uppercase', marginBottom: '0.5rem' }}>
            Confidential Distinguishing Features
          </h3>
          <p style={{ fontSize: '0.9375rem', color: 'var(--gray-800)', lineHeight: 1.6, padding: '1rem', backgroundColor: 'var(--gray-50)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
            {claim.distinguishingFeatures}
          </p>
        </div>

        {claim.proofDescription && (
          <div style={{ marginBottom: '1.75rem' }}>
            <h3 style={{ fontSize: '0.9375rem', fontWeight: 700, color: 'var(--gray-600)', textTransform: 'uppercase', marginBottom: '0.5rem' }}>
              Proof Documentation Reference
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-800)', padding: '0.875rem 1rem', backgroundColor: 'var(--gray-50)', borderRadius: 'var(--radius-md)', border: '1px solid var(--border-color)' }}>
              {claim.proofDescription}
            </p>
          </div>
        )}

        {/* Administrator Verdict Section */}
        {claim.reviewedAt && (
          <div style={{
            padding: '1.5rem',
            backgroundColor: '#f8fafc',
            border: '1px solid var(--border-color)',
            borderRadius: 'var(--radius-md)',
            marginBottom: '1.75rem'
          }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <ShieldCheck size={18} color="var(--primary)" /> Administrator Review Details
            </h3>
            <div style={{ fontSize: '0.875rem', color: 'var(--gray-700)', lineHeight: 1.6 }}>
              Review Verdict: <strong>{claim.status}</strong><br />
              Admin Notes: <em>{claim.adminNotes || 'No specific notes recorded.'}</em><br />
              Reviewed by: <strong>{claim.reviewedByAdminName || 'Security Admin'}</strong> on {new Date(claim.reviewedAt).toLocaleString()}
            </div>
          </div>
        )}

        {/* Physical Handover Record */}
        {collection && (
          <div style={{
            padding: '1.5rem',
            backgroundColor: '#ecfdf5',
            border: '1px solid #a7f3d0',
            borderRadius: 'var(--radius-md)'
          }}>
            <h3 style={{ fontSize: '1rem', fontWeight: 700, color: '#065f46', marginBottom: '0.5rem', display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <CheckCircle2 size={18} color="#059669" /> Physical Handover & Collection Confirmed
            </h3>
            <div style={{ fontSize: '0.875rem', color: '#065f46', lineHeight: 1.6 }}>
              Collector Identity: <strong>{collection.claimantName}</strong> (Verification ID: <code>{collection.collectorVerificationId}</code>)<br />
              Handover Timestamp: {new Date(collection.collectedAt).toLocaleString()}<br />
              Admin Handover Officer: <strong>{collection.verifiedByAdminName}</strong><br />
              Handover Notes: {collection.handoverNotes}
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default ClaimDetailPage;
