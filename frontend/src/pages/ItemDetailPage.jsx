import React, { useState, useEffect } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import { itemsApi, claimsApi, collectionsApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import StatusBadge from '../components/StatusBadge';
import LoadingSpinner from '../components/LoadingSpinner';
import ConfirmModal from '../components/ConfirmModal';
import {
  MapPin,
  Calendar,
  Tag,
  User,
  ShieldCheck,
  AlertCircle,
  FileCheck,
  Edit,
  Trash2,
  ArrowLeft,
  CheckCircle2,
  Clock
} from 'lucide-react';

const ItemDetailPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAuthenticated, isAdmin } = useAuth();

  const [item, setItem] = useState(null);
  const [collectionRecord, setCollectionRecord] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Claim modal/form state
  const [showClaimForm, setShowClaimForm] = useState(false);
  const [claimExplanation, setClaimExplanation] = useState('');
  const [distinguishingFeatures, setDistinguishingFeatures] = useState('');
  const [proofDescription, setProofDescription] = useState('');
  const [claimSubmitting, setClaimSubmitting] = useState(false);
  const [claimError, setClaimError] = useState(null);
  const [claimSuccess, setClaimSuccess] = useState(null);

  // Delete modal state
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const fetchItemDetail = async () => {
    setLoading(true);
    try {
      const res = await itemsApi.getById(id);
      if (res.data.success) {
        setItem(res.data.data);

        // If item is resolved, check for collection record
        if (res.data.data.status === 'RESOLVED') {
          try {
            const colRes = await collectionsApi.getByItemId(id);
            if (colRes.data.success) {
              setCollectionRecord(colRes.data.data);
            }
          } catch (e) {
            // No collection record or unauthenticated
          }
        }
      }
    } catch (err) {
      console.error(err);
      setError('Item not found or failed to load.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItemDetail();
  }, [id]);

  const handleClaimSubmit = async (e) => {
    e.preventDefault();
    setClaimSubmitting(true);
    setClaimError(null);
    try {
      const payload = {
        claimExplanation: claimExplanation.trim(),
        distinguishingFeatures: distinguishingFeatures.trim(),
        proofDescription: proofDescription.trim() || undefined
      };

      const res = await claimsApi.submit(id, payload);
      if (res.data.success) {
        setClaimSuccess('Your claim has been submitted to campus administrators for verification!');
        setShowClaimForm(false);
        fetchItemDetail(); // refresh item status to CLAIM_IN_PROGRESS
      }
    } catch (err) {
      const msg = err.response?.data?.error || err.response?.data?.message || 'Failed to submit claim. You may already have a pending claim.';
      setClaimError(msg);
    } finally {
      setClaimSubmitting(false);
    }
  };

  const handleDeleteItem = async () => {
    setDeleteLoading(true);
    try {
      await itemsApi.delete(id);
      navigate('/items');
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to delete item report');
      setShowDeleteModal(false);
    } finally {
      setDeleteLoading(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading report specifications..." />;
  if (error || !item) {
    return (
      <div className="container main-content">
        <div className="alert alert-danger">{error || 'Item not found.'}</div>
        <Link to="/items" className="btn btn-secondary btn-sm">
          <ArrowLeft size={16} /> Back to Listings
        </Link>
      </div>
    );
  }

  const isOwner = user && user.id === item.reporterId;
  const isFound = item.reportType === 'FOUND';
  const isEligibleToClaim = isAuthenticated && isFound && !isOwner && item.status !== 'RESOLVED' && item.status !== 'CLOSED';

  return (
    <div className="container main-content" style={{ maxWidth: '900px' }}>
      {/* Back button */}
      <div style={{ marginBottom: '1.5rem' }}>
        <Link to="/items" className="btn btn-secondary btn-sm" style={{ display: 'inline-flex' }}>
          <ArrowLeft size={16} /> Back to Listings
        </Link>
      </div>

      {claimSuccess && (
        <div className="alert alert-success" style={{ marginBottom: '1.5rem' }}>
          <CheckCircle2 size={18} />
          <div>{claimSuccess}</div>
        </div>
      )}

      {/* Main Item Card */}
      <div className="card" style={{ padding: '2rem', marginBottom: '2rem' }}>
        {/* Header Bar */}
        <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem', marginBottom: '1.25rem' }}>
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '0.5rem' }}>
              <StatusBadge type={item.reportType} />
              <StatusBadge status={item.status} />
            </div>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--gray-900)' }}>
              {item.title}
            </h1>
          </div>

          {/* Action Buttons for Owner / Admin */}
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            {isOwner && item.status !== 'RESOLVED' && (
              <Link to={`/items/${item.id}/edit`} className="btn btn-secondary btn-sm">
                <Edit size={15} /> Edit
              </Link>
            )}
            {(isOwner || isAdmin) && item.status !== 'RESOLVED' && (
              <button onClick={() => setShowDeleteModal(true)} className="btn btn-danger btn-sm">
                <Trash2 size={15} /> Delete
              </button>
            )}
          </div>
        </div>

        {/* Description */}
        <div style={{ marginBottom: '1.75rem' }}>
          <h3 style={{ fontSize: '1rem', fontWeight: 700, color: 'var(--gray-700)', marginBottom: '0.5rem' }}>
            Description
          </h3>
          <p style={{ fontSize: '0.9375rem', color: 'var(--gray-800)', lineHeight: 1.6, whiteSpace: 'pre-line' }}>
            {item.description}
          </p>
        </div>

        {/* Metadata Details Grid */}
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))',
          gap: '1.25rem',
          padding: '1.25rem',
          backgroundColor: 'var(--gray-50)',
          borderRadius: 'var(--radius-md)',
          marginBottom: '1.75rem',
          border: '1px solid var(--border-color)'
        }}>
          <div>
            <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--gray-500)', textTransform: 'uppercase' }}>
              Category
            </div>
            <div style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--gray-900)', marginTop: '0.25rem' }}>
              {item.category}
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--gray-500)', textTransform: 'uppercase' }}>
              Campus Location
            </div>
            <div style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--gray-900)', marginTop: '0.25rem' }}>
              {item.location}
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--gray-500)', textTransform: 'uppercase' }}>
              Incident Date
            </div>
            <div style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--gray-900)', marginTop: '0.25rem' }}>
              {item.incidentDate}
            </div>
          </div>

          <div>
            <div style={{ fontSize: '0.75rem', fontWeight: 600, color: 'var(--gray-500)', textTransform: 'uppercase' }}>
              Safe Contact Preference
            </div>
            <div style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--gray-900)', marginTop: '0.25rem' }}>
              {item.contactPreference}
            </div>
          </div>
        </div>

        {/* Reporter Privacy-Safe Section */}
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', padding: '0.875rem 1rem', backgroundColor: '#ffffff', border: '1px solid var(--border-color)', borderRadius: 'var(--radius-md)', marginBottom: '1.5rem' }}>
          <div style={{ width: '2.25rem', height: '2.25rem', borderRadius: '50%', backgroundColor: 'var(--gray-100)', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--gray-600)' }}>
            <User size={18} />
          </div>
          <div>
            <div style={{ fontSize: '0.875rem', fontWeight: 700, color: 'var(--gray-900)' }}>
              Reported by {item.reporterName || 'Campus Member'}
            </div>
            <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)' }}>
              {item.reporterDepartment || 'SRM IST Department'} &bull; Filed on {item.reportDate}
            </div>
          </div>
        </div>

        {/* Claim Action Box */}
        {isFound && (
          <div style={{
            padding: '1.5rem',
            backgroundColor: '#eff6ff',
            borderRadius: 'var(--radius-md)',
            border: '1px solid #bfdbfe'
          }}>
            <h3 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--primary)', marginBottom: '0.5rem' }}>
              Is this your lost property?
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-700)', lineHeight: 1.5, marginBottom: '1.25rem' }}>
              If you misplaced this item on campus, you can submit an ownership claim providing distinguishing marks or evidence. Campus security administrators will verify your claim before physical handover.
            </p>

            {isAuthenticated ? (
              isOwner ? (
                <div style={{ fontSize: '0.875rem', color: 'var(--gray-500)', fontStyle: 'italic' }}>
                  You are the reporter of this found item.
                </div>
              ) : item.status === 'RESOLVED' ? (
                <div style={{ fontSize: '0.875rem', color: 'var(--success)', fontWeight: 600 }}>
                  This item has already been collected and reunited with its owner.
                </div>
              ) : (
                !showClaimForm && (
                  <button onClick={() => setShowClaimForm(true)} className="btn btn-primary">
                    <FileCheck size={18} /> Submit Ownership Claim
                  </button>
                )
              )
            ) : (
              <div style={{ display: 'flex', gap: '0.75rem', alignItems: 'center' }}>
                <Link to="/login" className="btn btn-primary btn-sm">
                  Sign In to Claim
                </Link>
                <span style={{ fontSize: '0.8125rem', color: 'var(--gray-600)' }}>
                  Authentication required for fraud prevention.
                </span>
              </div>
            )}
          </div>
        )}

        {/* Claim Submission Form */}
        {showClaimForm && (
          <div style={{ marginTop: '1.5rem', padding: '1.5rem', border: '1px solid var(--border-color)', borderRadius: 'var(--radius-md)', backgroundColor: '#ffffff' }}>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, marginBottom: '1rem' }}>
              Submit Ownership Verification Claim
            </h3>

            {claimError && <div className="alert alert-danger">{claimError}</div>}

            <form onSubmit={handleClaimSubmit}>
              <div className="form-group">
                <label className="form-label">
                  Explanation of Loss *
                </label>
                <textarea
                  className="form-control"
                  rows="3"
                  placeholder="Explain where, when, and how you lost this belonging on campus..."
                  value={claimExplanation}
                  onChange={(e) => setClaimExplanation(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">
                  Distinguishing Characteristics & Features *
                </label>
                <textarea
                  className="form-control"
                  rows="3"
                  placeholder="Specify confidential identifiers: stickers, wallpaper, scratches, serial numbers, content details..."
                  value={distinguishingFeatures}
                  onChange={(e) => setDistinguishingFeatures(e.target.value)}
                  required
                />
                <div style={{ fontSize: '0.75rem', color: 'var(--gray-500)', marginTop: '0.25rem' }}>
                  This information is kept confidential between you and the campus administrator.
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">
                  Proof Description (Optional)
                </label>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. Purchase invoice number, lock combination, photos available on request"
                  value={proofDescription}
                  onChange={(e) => setProofDescription(e.target.value)}
                />
              </div>

              <div style={{ display: 'flex', gap: '0.75rem', justifyContent: 'flex-end', marginTop: '1rem' }}>
                <button
                  type="button"
                  onClick={() => setShowClaimForm(false)}
                  className="btn btn-secondary btn-sm"
                  disabled={claimSubmitting}
                >
                  Cancel
                </button>
                <button type="submit" className="btn btn-accent btn-sm" disabled={claimSubmitting}>
                  {claimSubmitting ? 'Submitting to Microservice...' : 'Submit Claim'}
                </button>
              </div>
            </form>
          </div>
        )}

        {/* Collection Handover Verification Details */}
        {collectionRecord && (
          <div style={{ marginTop: '1.5rem', padding: '1.25rem', backgroundColor: '#f0fdf4', border: '1px solid #bbf7d0', borderRadius: 'var(--radius-md)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', color: '#15803d', fontWeight: 700, marginBottom: '0.5rem' }}>
              <ShieldCheck size={20} />
              Formal Collection Record Confirmed
            </div>
            <div style={{ fontSize: '0.875rem', color: '#166534', lineHeight: 1.5 }}>
              Handed over to: <strong>{collectionRecord.claimantName}</strong> (Verification ID: <code>{collectionRecord.collectorVerificationId}</code>)<br />
              Admin Notes: {collectionRecord.handoverNotes}<br />
              Handover Timestamp: {collectionRecord.collectedAt}
            </div>
          </div>
        )}
      </div>

      <ConfirmModal
        isOpen={showDeleteModal}
        title="Delete Item Report"
        message="Are you sure you want to permanently delete this item report? This action cannot be undone."
        confirmText="Delete Report"
        confirmVariant="danger"
        loading={deleteLoading}
        onConfirm={handleDeleteItem}
        onCancel={() => setShowDeleteModal(false)}
      />
    </div>
  );
};

export default ItemDetailPage;
