import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { itemsApi } from '../api/client';
import { useAuth } from '../context/AuthContext';
import LoadingSpinner from '../components/LoadingSpinner';
import { Edit, AlertCircle, ArrowLeft } from 'lucide-react';

const CATEGORIES = [
  'ELECTRONICS',
  'BOOKS',
  'BAGS',
  'STATIONERY',
  'IDENTIFICATION',
  'CLOTHING',
  'ACCESSORIES',
  'KEYS',
  'OTHER'
];

const EditItemPage = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user, isAdmin } = useAuth();

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'ELECTRONICS',
    location: '',
    incidentDate: '',
    imageUrl: '',
    contactPreference: 'IN_APP'
  });

  const [loading, setLoading] = useState(true);
  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  useEffect(() => {
    itemsApi.getById(id)
      .then((res) => {
        if (res.data.success) {
          const item = res.data.data;
          // Ownership verification
          if (user && item.reporterId !== user.id && !isAdmin) {
            navigate(`/items/${id}`, { replace: true });
            return;
          }
          if (item.status === 'RESOLVED' || item.status === 'CLOSED') {
            alert('Resolved or closed items cannot be modified.');
            navigate(`/items/${id}`, { replace: true });
            return;
          }

          setFormData({
            title: item.title,
            description: item.description,
            category: item.category,
            location: item.location,
            incidentDate: item.incidentDate,
            imageUrl: item.imageUrl || '',
            contactPreference: item.contactPreference || 'IN_APP'
          });
        }
      })
      .catch((err) => {
        console.error(err);
        setServerError('Failed to load item report');
      })
      .finally(() => setLoading(false));
  }, [id, user, isAdmin, navigate]);

  const validate = () => {
    const errs = {};
    if (!formData.title.trim() || formData.title.trim().length < 3) {
      errs.title = 'Title must be between 3 and 150 characters';
    }
    if (!formData.description.trim() || formData.description.trim().length < 10) {
      errs.description = 'Please provide detailed description (at least 10 characters)';
    }
    if (!formData.location.trim()) {
      errs.location = 'Campus location is required';
    }
    if (!formData.incidentDate) {
      errs.incidentDate = 'Date of incident is required';
    }
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setServerError('');
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }
    setErrors({});
    setSubmitting(true);

    try {
      const res = await itemsApi.update(id, {
        title: formData.title.trim(),
        description: formData.description.trim(),
        category: formData.category,
        location: formData.location.trim(),
        incidentDate: formData.incidentDate,
        imageUrl: formData.imageUrl.trim() || undefined,
        contactPreference: formData.contactPreference
      });

      if (res.data.success) {
        navigate(`/items/${id}`);
      }
    } catch (err) {
      const msg = err.response?.data?.error || err.response?.data?.message || err.message || 'Failed to update report';
      setServerError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) return <LoadingSpinner message="Loading report for editing..." />;

  return (
    <div className="container main-content" style={{ maxWidth: '680px' }}>
      <div className="card" style={{ padding: '2.5rem 2rem' }}>
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--gray-900)', marginBottom: '0.5rem' }}>
            Edit Item Report #{id}
          </h1>
          <p style={{ fontSize: '0.875rem', color: 'var(--gray-500)' }}>
            Update description, location, or contact preferences for your report.
          </p>
        </div>

        {serverError && (
          <div className="alert alert-danger">
            <AlertCircle size={18} />
            <div>{serverError}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <label className="form-label">Item Title *</label>
            <input
              type="text"
              className="form-control"
              value={formData.title}
              onChange={(e) => setFormData({ ...formData, title: e.target.value })}
            />
            {errors.title && <div className="form-error">{errors.title}</div>}
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
            <div className="form-group">
              <label className="form-label">Category *</label>
              <select
                className="form-control"
                value={formData.category}
                onChange={(e) => setFormData({ ...formData, category: e.target.value })}
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat} value={cat}>{cat}</option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label className="form-label">Date of Incident *</label>
              <input
                type="date"
                className="form-control"
                value={formData.incidentDate}
                onChange={(e) => setFormData({ ...formData, incidentDate: e.target.value })}
              />
              {errors.incidentDate && <div className="form-error">{errors.incidentDate}</div>}
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Specific Campus Location *</label>
            <input
              type="text"
              className="form-control"
              value={formData.location}
              onChange={(e) => setFormData({ ...formData, location: e.target.value })}
            />
            {errors.location && <div className="form-error">{errors.location}</div>}
          </div>

          <div className="form-group">
            <label className="form-label">Detailed Description *</label>
            <textarea
              className="form-control"
              rows="4"
              value={formData.description}
              onChange={(e) => setFormData({ ...formData, description: e.target.value })}
            />
            {errors.description && <div className="form-error">{errors.description}</div>}
          </div>

          <div className="form-group">
            <label className="form-label">Safe Contact Preference</label>
            <select
              className="form-control"
              value={formData.contactPreference}
              onChange={(e) => setFormData({ ...formData, contactPreference: e.target.value })}
            >
              <option value="IN_APP">In-App Portal Claims Only</option>
              <option value="SECURITY_OFFICE">Handed Over to Campus Security Office</option>
              <option value="DEPARTMENT_OFFICE">Handed Over to Department HOD Office</option>
            </select>
          </div>

          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '2rem' }}>
            <button
              type="button"
              onClick={() => navigate(`/items/${id}`)}
              className="btn btn-secondary"
              disabled={submitting}
            >
              Cancel
            </button>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={submitting}
            >
              {submitting ? 'Saving Changes...' : 'Save Updates'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default EditItemPage;
