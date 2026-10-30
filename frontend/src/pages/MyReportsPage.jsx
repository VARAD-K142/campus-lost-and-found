import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { itemsApi } from '../api/client';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import StatusBadge from '../components/StatusBadge';
import ConfirmModal from '../components/ConfirmModal';
import { PlusCircle, Edit, Trash2, Eye, ChevronLeft, ChevronRight } from 'lucide-react';

const MyReportsPage = () => {
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);

  // Delete modal state
  const [selectedItemToDelete, setSelectedItemToDelete] = useState(null);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const fetchMyItems = async () => {
    setLoading(true);
    try {
      const res = await itemsApi.getMyItems({ page, size: 10 });
      if (res.data.success) {
        setItems(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
      }
    } catch (err) {
      console.error(err);
      setError('Failed to load your personal reports');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMyItems();
  }, [page]);

  const handleDeleteConfirm = async () => {
    if (!selectedItemToDelete) return;
    setDeleteLoading(true);
    try {
      await itemsApi.delete(selectedItemToDelete.id);
      setSelectedItemToDelete(null);
      fetchMyItems();
    } catch (err) {
      alert(err.response?.data?.error || 'Failed to delete item report');
    } finally {
      setDeleteLoading(false);
    }
  };

  return (
    <div className="container main-content">
      <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: '1rem', marginBottom: '2rem' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800, color: 'var(--gray-900)' }}>
            My Property Reports
          </h1>
          <p style={{ color: 'var(--gray-500)', fontSize: '0.875rem', marginTop: '0.25rem' }}>
            All lost and found listings created under your university account.
          </p>
        </div>

        <Link to="/items/new" className="btn btn-primary btn-sm">
          <PlusCircle size={16} /> File New Report
        </Link>
      </div>

      {loading ? (
        <LoadingSpinner message="Fetching your filed reports..." />
      ) : error ? (
        <div className="alert alert-danger">{error}</div>
      ) : items.length === 0 ? (
        <EmptyState
          title="No reports created yet"
          description="You haven't posted any lost or found belongings yet. Report an item to begin."
          actionText="File Your First Report"
          actionLink="/items/new"
        />
      ) : (
        <>
          <div className="table-container">
            <table className="table">
              <thead>
                <tr>
                  <th>Type</th>
                  <th>Title</th>
                  <th>Category</th>
                  <th>Location</th>
                  <th>Incident Date</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {items.map((item) => (
                  <tr key={item.id}>
                    <td><StatusBadge type={item.reportType} /></td>
                    <td style={{ fontWeight: 600 }}>{item.title}</td>
                    <td>{item.category}</td>
                    <td>{item.location}</td>
                    <td>{item.incidentDate}</td>
                    <td><StatusBadge status={item.status} /></td>
                    <td>
                      <div style={{ display: 'flex', gap: '0.375rem' }}>
                        <Link to={`/items/${item.id}`} className="btn btn-secondary btn-sm" title="View Details">
                          <Eye size={14} /> View
                        </Link>
                        {item.status !== 'RESOLVED' && (
                          <Link to={`/items/${item.id}/edit`} className="btn btn-secondary btn-sm" title="Edit Report">
                            <Edit size={14} /> Edit
                          </Link>
                        )}
                        {item.status !== 'RESOLVED' && (
                          <button
                            onClick={() => setSelectedItemToDelete(item)}
                            className="btn btn-danger btn-sm"
                            title="Delete Report"
                          >
                            <Trash2 size={14} />
                          </button>
                        )}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>

          {totalPages > 1 && (
            <div className="pagination">
              <button
                onClick={() => setPage(page - 1)}
                disabled={page === 0}
                className="btn btn-secondary btn-sm"
              >
                <ChevronLeft size={16} /> Previous
              </button>
              <span style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--gray-700)', padding: '0 0.5rem' }}>
                Page {page + 1} of {totalPages}
              </span>
              <button
                onClick={() => setPage(page + 1)}
                disabled={page >= totalPages - 1}
                className="btn btn-secondary btn-sm"
              >
                Next <ChevronRight size={16} />
              </button>
            </div>
          )}
        </>
      )}

      <ConfirmModal
        isOpen={!!selectedItemToDelete}
        title="Delete Item Report"
        message={`Are you sure you want to permanently delete '${selectedItemToDelete?.title}'? This action removes the report from public listings.`}
        confirmText="Confirm Delete"
        confirmVariant="danger"
        loading={deleteLoading}
        onConfirm={handleDeleteConfirm}
        onCancel={() => setSelectedItemToDelete(null)}
      />
    </div>
  );
};

export default MyReportsPage;
