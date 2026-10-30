import React from 'react';
import { AlertTriangle, X } from 'lucide-react';

const ConfirmModal = ({
  isOpen,
  title = 'Confirm Action',
  message = 'Are you sure you want to proceed?',
  confirmText = 'Confirm',
  confirmVariant = 'danger',
  onConfirm,
  onCancel,
  loading = false
}) => {
  if (!isOpen) return null;

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(15, 23, 42, 0.6)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      zIndex: 50,
      padding: '1rem',
      backdropFilter: 'blur(2px)'
    }}>
      <div className="card" style={{
        maxWidth: '450px',
        width: '100%',
        padding: '1.75rem',
        boxShadow: 'var(--shadow-xl)',
        animation: 'modalSlide 0.2s ease-out'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '1rem' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
            <div style={{
              width: '2.25rem',
              height: '2.25rem',
              borderRadius: '50%',
              backgroundColor: confirmVariant === 'danger' ? 'var(--danger-bg)' : 'var(--warning-bg)',
              color: confirmVariant === 'danger' ? 'var(--danger)' : 'var(--warning)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}>
              <AlertTriangle size={20} />
            </div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700 }}>{title}</h3>
          </div>
          <button
            onClick={onCancel}
            style={{ background: 'none', border: 'none', cursor: 'pointer', color: 'var(--gray-400)' }}
            disabled={loading}
          >
            <X size={20} />
          </button>
        </div>

        <p style={{ fontSize: '0.875rem', color: 'var(--gray-600)', marginBottom: '1.5rem', lineHeight: 1.5 }}>
          {message}
        </p>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
          <button onClick={onCancel} className="btn btn-secondary btn-sm" disabled={loading}>
            Cancel
          </button>
          <button
            onClick={onConfirm}
            className={`btn btn-${confirmVariant} btn-sm`}
            disabled={loading}
          >
            {loading ? 'Processing...' : confirmText}
          </button>
        </div>
      </div>
      <style>{`
        @keyframes modalSlide {
          from { opacity: 0; transform: translateY(-10px); }
          to { opacity: 1; transform: translateY(0); }
        }
      `}</style>
    </div>
  );
};

export default ConfirmModal;
