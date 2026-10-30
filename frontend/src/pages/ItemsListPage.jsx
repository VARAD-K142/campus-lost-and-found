import React, { useState, useEffect } from 'react';
import { useSearchParams } from 'react-router-dom';
import { itemsApi } from '../api/client';
import ItemCard from '../components/ItemCard';
import LoadingSpinner from '../components/LoadingSpinner';
import EmptyState from '../components/EmptyState';
import { Search, Filter, RotateCcw, ChevronLeft, ChevronRight } from 'lucide-react';

const CATEGORIES = [
  'ALL',
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

const STATUSES = [
  { label: 'All Statuses', value: '' },
  { label: 'Active Listings', value: 'ACTIVE' },
  { label: 'Under Review', value: 'CLAIM_IN_PROGRESS' },
  { label: 'Approved (Pending Handover)', value: 'CLAIMED' },
  { label: 'Collected / Resolved', value: 'RESOLVED' }
];

const ItemsListPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Filters state
  const [keyword, setKeyword] = useState(searchParams.get('keyword') || '');
  const [reportType, setReportType] = useState(searchParams.get('reportType') || '');
  const [category, setCategory] = useState(searchParams.get('category') || 'ALL');
  const [status, setStatus] = useState(searchParams.get('status') || '');
  const [location, setLocation] = useState(searchParams.get('location') || '');
  const [sortBy, setSortBy] = useState(searchParams.get('sortBy') || 'newest');
  const [page, setPage] = useState(parseInt(searchParams.get('page') || '0', 10));

  // Pagination metadata
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);

  const fetchItems = async () => {
    setLoading(true);
    setError(null);
    try {
      const params = {
        page,
        size: 9,
        sortBy,
        keyword: keyword.trim() || undefined,
        reportType: reportType || undefined,
        category: category !== 'ALL' ? category : undefined,
        status: status || undefined,
        location: location.trim() || undefined
      };

      const res = await itemsApi.search(params);
      if (res.data.success) {
        setItems(res.data.data.content);
        setTotalPages(res.data.data.totalPages);
        setTotalElements(res.data.data.totalElements);
      }
    } catch (err) {
      console.error('Error fetching items', err);
      setError('Unable to load listings. Please check server connectivity.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchItems();
  }, [page, sortBy, reportType, category, status]);

  const handleSearchSubmit = (e) => {
    e.preventDefault();
    setPage(0);
    fetchItems();
  };

  const handleResetFilters = () => {
    setKeyword('');
    setReportType('');
    setCategory('ALL');
    setStatus('');
    setLocation('');
    setSortBy('newest');
    setPage(0);
  };

  return (
    <div className="container main-content">
      {/* Header */}
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.875rem', fontWeight: 800, color: 'var(--gray-900)', marginBottom: '0.5rem' }}>
          Campus Lost & Found Listings
        </h1>
        <p style={{ color: 'var(--gray-600)', fontSize: '0.9375rem' }}>
          Explore published property reports from SRM IST students and campus security.
        </p>
      </div>

      {/* Filter / Search Bar */}
      <div className="card" style={{ padding: '1.5rem', marginBottom: '2rem' }}>
        <form onSubmit={handleSearchSubmit}>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1rem', marginBottom: '1.25rem' }}>
            {/* Keyword Search */}
            <div>
              <label className="form-label">Search Keyword</label>
              <div style={{ position: 'relative' }}>
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. calculator, mouse, bag..."
                  value={keyword}
                  onChange={(e) => setKeyword(e.target.value)}
                />
              </div>
            </div>

            {/* Report Type */}
            <div>
              <label className="form-label">Report Type</label>
              <select
                className="form-control"
                value={reportType}
                onChange={(e) => { setReportType(e.target.value); setPage(0); }}
              >
                <option value="">All Reports (Lost & Found)</option>
                <option value="LOST">Lost Belongings</option>
                <option value="FOUND">Found Belongings</option>
              </select>
            </div>

            {/* Category */}
            <div>
              <label className="form-label">Category</label>
              <select
                className="form-control"
                value={category}
                onChange={(e) => { setCategory(e.target.value); setPage(0); }}
              >
                {CATEGORIES.map((cat) => (
                  <option key={cat} value={cat}>
                    {cat === 'ALL' ? 'All Categories' : cat}
                  </option>
                ))}
              </select>
            </div>

            {/* Status */}
            <div>
              <label className="form-label">Status</label>
              <select
                className="form-control"
                value={status}
                onChange={(e) => { setStatus(e.target.value); setPage(0); }}
              >
                {STATUSES.map((st) => (
                  <option key={st.value} value={st.value}>{st.label}</option>
                ))}
              </select>
            </div>
          </div>

          <div style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-between', alignItems: 'center', gap: '1rem', paddingTop: '1rem', borderTop: '1px solid var(--border-color)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', flexWrap: 'wrap' }}>
              <button type="submit" className="btn btn-primary btn-sm">
                <Search size={16} /> Apply Search
              </button>
              <button type="button" onClick={handleResetFilters} className="btn btn-secondary btn-sm">
                <RotateCcw size={16} /> Reset Filters
              </button>
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', fontSize: '0.875rem' }}>
              <span style={{ color: 'var(--gray-600)', fontWeight: 600 }}>Sort by:</span>
              <select
                className="form-control"
                style={{ width: 'auto', padding: '0.375rem 0.75rem' }}
                value={sortBy}
                onChange={(e) => { setSortBy(e.target.value); setPage(0); }}
              >
                <option value="newest">Newest First</option>
                <option value="oldest">Oldest First</option>
                <option value="title">Title (A-Z)</option>
              </select>
            </div>
          </div>
        </form>
      </div>

      {/* Results Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem' }}>
        <div style={{ fontSize: '0.875rem', color: 'var(--gray-600)', fontWeight: 600 }}>
          Showing {items.length} of {totalElements} listings
        </div>
      </div>

      {/* Listing Content */}
      {loading ? (
        <LoadingSpinner message="Querying campus database records..." />
      ) : error ? (
        <div className="alert alert-danger">{error}</div>
      ) : items.length === 0 ? (
        <EmptyState
          title="No items found"
          description="Try broadening your search keywords or resetting category filters to see more results."
          actionText="Clear All Filters"
          onAction={handleResetFilters}
        />
      ) : (
        <div className="grid-cards">
          {items.map((item) => (
            <ItemCard key={item.id} item={item} />
          ))}
        </div>
      )}

      {/* Pagination Bar */}
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
    </div>
  );
};

export default ItemsListPage;
