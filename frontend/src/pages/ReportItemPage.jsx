import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { itemsApi } from '../api/client';
import { PlusCircle, AlertCircle } from 'lucide-react';

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

const ReportItemPage = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'ELECTRONICS',
    reportType: 'LOST',
    location: '',
    incidentDate: new Date().toISOString().split('T')[0],
    imageUrl: '',
    contactPreference: 'IN_APP'
  });

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [submitting, setSubmitting] = useState(false);

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
    } else {
      const selected = new Date(formData.incidentDate);
      const today = new Date();
      if (selected > today) {
        errs.incidentDate = 'Incident date cannot be in the future';
      }
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
      const res = await itemsApi.create({
        title: formData.title.trim(),
        description: formData.description.trim(),
        category: formData.category,
        reportType: formData.reportType,
        location: formData.location.trim(),
        incidentDate: formData.incidentDate,
        imageUrl: formData.imageUrl.trim() || undefined,
        contactPreference: formData.contactPreference
      });

      if (res.data.success) {
        navigate(`/items/${res.data.data.id}`);
      }
    } catch (err) {
      const msg = err.response?.data?.error || err.response?.data?.message || err.message || 'Failed to submit report';
      setServerError(msg);
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="container main-content" style={{ maxWidth: '680px' }}>
      <div className="card" style={{ padding: '2.5rem 2rem' }}>
        <div style={{ marginBottom: '2rem' }}>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--gray-900)', marginBottom: '0.5rem' }}>
            Report Campus Belonging
          </h1>
          <p style={{ fontSize: '0.875rem', color: 'var(--gray-500)' }}>
            Submit an official report for a lost personal belonging or an item you discovered on campus.
          </p>
        </div>

        {serverError && (
          <div className="alert alert-danger">
            <AlertCircle size={18} />
            <div>{serverError}</div>
          </div>
        )}

        <form onSubmit={handleSubmit}>
          {/* Report Type Selector */}
          <div className="form-group">
            <label className="form-label">Report Classification *</label>
            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <button
                type="button"
                onClick={() => setFormData({ ...formData, reportType: 'LOST' })}
                style={{
                  padding: '1rem',
                  borderRadius: 'var(--radius-md)',
                  border: formData.reportType === 'LOST' ? '2px solid var(--danger)' : '1px solid var(--border-color)',
                  backgroundColor: formData.reportType === 'LOST' ? 'var(--danger-bg)' : '#ffffff',
                  color: formData.reportType === 'LOST' ? 'var(--danger)' : 'var(--gray-700)',
                  fontWeight: 700,
                  cursor: 'pointer',
                  textAlign: 'center'
                }}
              >
                LOST ITEM
                <div style={{ fontSize: '0.75rem', fontWeight: 400, marginTop: '0.25rem' }}>
                  I misplaced my property
                </div>
              </button>

              <button
                type="button"
                onClick={() => setFormData({ ...formData, reportType: 'FOUND' })}
                style={{
                  padding: '1rem',
                  borderRadius: 'var(--radius-md)',
                  border: formData.reportType === 'FOUND' ? '2px solid var(--success)' : '1px solid var(--border-color)',
                  backgroundColor: formData.reportType === 'FOUND' ? 'var(--success-bg)' : '#ffffff',
                  color: formData.reportType === 'FOUND' ? 'var(--success)' : 'var(--gray-700)',
                  fontWeight: 700,
                  cursor: 'pointer',
                  textAlign: 'center'
                }}
              >
                FOUND ITEM
                <div style={{ fontSize: '0.75rem', fontWeight: 400, marginTop: '0.25rem' }}>
                  I found someone's property
                </div>
              </button>
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Item Title *</label>
            <input
              type="text"
              className="form-control"
              placeholder="e.g. Casio fx-991EX Scientific Calculator"
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
              placeholder="e.g. Central Library 2nd Floor, Tech Park Lab 3, Hostel B Mess"
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
              placeholder="Describe distinguishing colors, brand, serial markings, condition, or items inside..."
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
              <option value="IN_APP">In-App Portal Claims Only (Highest Privacy)</option>
              <option value="SECURITY_OFFICE">Handed Over to Campus Security Office</option>
              <option value="DEPARTMENT_OFFICE">Handed Over to Department HOD Office</option>
            </select>
          </div>

          <div style={{ display: 'flex', gap: '1rem', justifyContent: 'flex-end', marginTop: '2rem' }}>
            <button
              type="button"
              onClick={() => navigate(-1)}
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
              {submitting ? 'Saving to Database...' : 'Submit Official Report'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default ReportItemPage;
