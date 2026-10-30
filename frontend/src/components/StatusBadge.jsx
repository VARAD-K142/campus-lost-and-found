import React from 'react';

const StatusBadge = ({ type, status }) => {
  if (type) {
    const isLost = type === 'LOST';
    return (
      <span className={`badge ${isLost ? 'badge-lost' : 'badge-found'}`}>
        {type}
      </span>
    );
  }

  if (status) {
    let badgeClass = 'badge-active';
    let label = status.replace(/_/g, ' ');

    switch (status) {
      case 'ACTIVE':
        badgeClass = 'badge-active';
        label = 'Active Listing';
        break;
      case 'CLAIM_IN_PROGRESS':
        badgeClass = 'badge-progress';
        label = 'Claim Under Review';
        break;
      case 'CLAIMED':
        badgeClass = 'badge-admin';
        label = 'Claim Approved (Pending Handover)';
        break;
      case 'RESOLVED':
        badgeClass = 'badge-resolved';
        label = 'Collected / Resolved';
        break;
      case 'CLOSED':
        badgeClass = 'badge-lost';
        label = 'Closed';
        break;
      case 'PENDING':
        badgeClass = 'badge-progress';
        label = 'Pending Review';
        break;
      case 'APPROVED':
        badgeClass = 'badge-resolved';
        label = 'Approved';
        break;
      case 'REJECTED':
        badgeClass = 'badge-lost';
        label = 'Rejected';
        break;
      default:
        badgeClass = 'badge-active';
    }

    return (
      <span className={`badge ${badgeClass}`}>
        {label}
      </span>
    );
  }

  return null;
};

export default StatusBadge;
