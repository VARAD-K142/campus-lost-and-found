import React from 'react';
import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge';
import { MapPin, Calendar, Tag, ArrowRight } from 'lucide-react';

const ItemCard = ({ item }) => {
  return (
    <div className="card" style={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '0.5rem', marginBottom: '0.875rem' }}>
        <StatusBadge type={item.reportType} />
        <StatusBadge status={item.status} />
      </div>

      <h3 style={{ fontSize: '1.0625rem', fontWeight: 700, color: 'var(--gray-900)', marginBottom: '0.5rem', lineHeight: 1.3 }}>
        <Link to={`/items/${item.id}`} style={{ color: 'inherit' }}>
          {item.title}
        </Link>
      </h3>

      <p style={{
        fontSize: '0.8125rem',
        color: 'var(--gray-600)',
        marginBottom: '1rem',
        lineHeight: 1.5,
        display: '-webkit-box',
        WebkitLineClamp: 2,
        WebkitBoxOrient: 'vertical',
        overflow: 'hidden'
      }}>
        {item.description}
      </p>

      <div style={{ marginTop: 'auto', display: 'flex', flexDirection: 'column', gap: '0.375rem', fontSize: '0.75rem', color: 'var(--gray-500)', paddingTop: '0.75rem', borderTop: '1px solid var(--border-color)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
          <Tag size={13} style={{ color: 'var(--gray-400)' }} />
          <span>Category: <strong style={{ color: 'var(--gray-700)' }}>{item.category}</strong></span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
          <MapPin size={13} style={{ color: 'var(--gray-400)' }} />
          <span style={{ textOverflow: 'ellipsis', overflow: 'hidden', whiteSpace: 'nowrap' }}>{item.location}</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.375rem' }}>
          <Calendar size={13} style={{ color: 'var(--gray-400)' }} />
          <span>Incident: {item.incidentDate}</span>
        </div>
      </div>

      <div style={{ marginTop: '1rem' }}>
        <Link to={`/items/${item.id}`} className="btn btn-secondary btn-sm" style={{ width: '100%', justifyContent: 'space-between' }}>
          <span>View Details</span>
          <ArrowRight size={14} />
        </Link>
      </div>
    </div>
  );
};

export default ItemCard;
