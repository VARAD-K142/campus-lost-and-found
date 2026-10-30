import React from 'react';

const LoadingSpinner = ({ message = 'Loading records...' }) => {
  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '3rem 1.5rem',
      gap: '1rem',
      color: 'var(--gray-600)'
    }}>
      <div style={{
        width: '2.5rem',
        height: '2.5rem',
        border: '3px solid var(--gray-200)',
        borderTopColor: 'var(--primary)',
        borderRadius: '50%',
        animation: 'spin 0.8s linear infinite'
      }} />
      <div style={{ fontSize: '0.875rem', fontWeight: 600 }}>{message}</div>
      <style>{`
        @keyframes spin {
          to { transform: rotate(360deg); }
        }
      `}</style>
    </div>
  );
};

export default LoadingSpinner;
