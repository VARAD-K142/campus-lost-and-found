import React from 'react';
import { Inbox } from 'lucide-react';
import { Link } from 'react-router-dom';

const EmptyState = ({
  title = 'No records found',
  description = 'There are no items matching the selected criteria.',
  actionText,
  actionLink,
  onAction
}) => {
  return (
    <div className="card" style={{
      textAlign: 'center',
      padding: '3.5rem 1.5rem',
      backgroundColor: '#ffffff',
      borderStyle: 'dashed'
    }}>
      <div style={{
        width: '3.5rem',
        height: '3.5rem',
        borderRadius: '50%',
        backgroundColor: 'var(--gray-100)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        color: 'var(--gray-400)',
        margin: '0 auto 1.25rem'
      }}>
        <Inbox size={28} />
      </div>
      <h3 style={{ fontSize: '1.125rem', fontWeight: 700, color: 'var(--gray-800)', marginBottom: '0.5rem' }}>
        {title}
      </h3>
      <p style={{ fontSize: '0.875rem', color: 'var(--gray-500)', maxWidth: '400px', margin: '0 auto 1.5rem', lineHeight: 1.5 }}>
        {description}
      </p>

      {actionText && actionLink && (
        <Link to={actionLink} className="btn btn-primary btn-sm">
          {actionText}
        </Link>
      )}

      {actionText && onAction && (
        <button onClick={onAction} className="btn btn-secondary btn-sm">
          {actionText}
        </button>
      )}
    </div>
  );
};

export default EmptyState;
