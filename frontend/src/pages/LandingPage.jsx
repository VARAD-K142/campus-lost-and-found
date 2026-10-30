import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { dashboardApi } from '../api/client';
import {
  Compass,
  Search,
  CheckCircle2,
  ShieldCheck,
  ArrowRight,
  PlusCircle,
  FileSearch,
  HandMetal,
  GraduationCap
} from 'lucide-react';

const LandingPage = () => {
  const [stats, setStats] = useState({
    totalItemsReported: 0,
    activeListings: 0,
    resolvedReunions: 0,
    successfulHandouts: 0
  });
  const [loadingStats, setLoadingStats] = useState(true);

  useEffect(() => {
    dashboardApi.getPublicStats()
      .then((res) => {
        if (res.data.success) {
          setStats(res.data.data);
        }
      })
      .catch((err) => console.error('Failed to load public stats', err))
      .finally(() => setLoadingStats(false));
  }, []);

  return (
    <div>
      {/* Hero Section */}
      <section style={{
        background: 'linear-gradient(135deg, #1e3a8a 0%, #1e40af 50%, #2563eb 100%)',
        color: '#ffffff',
        padding: '5rem 0 4rem',
        position: 'relative',
        overflow: 'hidden'
      }}>
        <div className="container" style={{ position: 'relative', zIndex: 10 }}>
          <div style={{ maxWidth: '780px', margin: '0 auto', textAlign: 'center' }}>
            <div style={{
              display: 'inline-flex',
              alignItems: 'center',
              gap: '0.5rem',
              backgroundColor: 'rgba(255, 255, 255, 0.15)',
              padding: '0.375rem 0.875rem',
              borderRadius: 'var(--radius-full)',
              fontSize: '0.8125rem',
              fontWeight: 600,
              marginBottom: '1.5rem',
              backdropFilter: 'blur(4px)'
            }}>
              <GraduationCap size={16} />
              SRM IST Tiruchirappalli &bull; 21CSE354T Full Stack Task 4
            </div>

            <h1 style={{
              fontSize: 'clamp(2rem, 5vw, 3.25rem)',
              fontWeight: 800,
              color: '#ffffff',
              lineHeight: 1.15,
              marginBottom: '1.25rem',
              letterSpacing: '-0.02em'
            }}>
              Campus Lost-and-Found Portal
            </h1>

            <p style={{
              fontSize: '1.125rem',
              lineHeight: 1.6,
              color: 'rgba(255, 255, 255, 0.9)',
              marginBottom: '2.25rem'
            }}>
              The centralized digital platform to report lost belongings, browse found items across campus, submit verified ownership claims, and track administrative physical handover.
            </p>

            <div style={{
              display: 'flex',
              flexWrap: 'wrap',
              justifyContent: 'center',
              gap: '1rem'
            }}>
              <Link to="/items" className="btn btn-lg" style={{
                backgroundColor: '#ffffff',
                color: 'var(--primary)',
                fontWeight: 700,
                boxShadow: 'var(--shadow-md)'
              }}>
                <Search size={18} />
                Browse Lost & Found
              </Link>
              <Link to="/items/new" className="btn btn-lg" style={{
                backgroundColor: 'rgba(255, 255, 255, 0.2)',
                color: '#ffffff',
                border: '1px solid rgba(255, 255, 255, 0.4)',
                fontWeight: 600,
                backdropFilter: 'blur(4px)'
              }}>
                <PlusCircle size={18} />
                Report an Item
              </Link>
            </div>
          </div>
        </div>
      </section>

      {/* Live Campus Stats Bar */}
      <section style={{
        backgroundColor: '#ffffff',
        borderBottom: '1px solid var(--border-color)',
        padding: '2rem 0',
        boxShadow: 'var(--shadow-sm)'
      }}>
        <div className="container">
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
            gap: '1.5rem',
            textAlign: 'center'
          }}>
            <div>
              <div style={{ fontSize: '2.25rem', fontWeight: 800, color: 'var(--primary)' }}>
                {loadingStats ? '...' : stats.totalItemsReported}
              </div>
              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--gray-600)' }}>
                Total Reports Filed
              </div>
            </div>
            <div>
              <div style={{ fontSize: '2.25rem', fontWeight: 800, color: 'var(--accent)' }}>
                {loadingStats ? '...' : stats.activeListings}
              </div>
              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--gray-600)' }}>
                Active Campus Listings
              </div>
            </div>
            <div>
              <div style={{ fontSize: '2.25rem', fontWeight: 800, color: 'var(--success)' }}>
                {loadingStats ? '...' : stats.resolvedReunions}
              </div>
              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--gray-600)' }}>
                Resolved Belongings
              </div>
            </div>
            <div>
              <div style={{ fontSize: '2.25rem', fontWeight: 800, color: '#7c3aed' }}>
                {loadingStats ? '...' : stats.successfulHandouts}
              </div>
              <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--gray-600)' }}>
                Handover Collections
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* How it Works / Workflow Steps */}
      <section className="container" style={{ padding: '4rem 1.25rem' }}>
        <div style={{ textAlign: 'center', maxWidth: '650px', margin: '0 auto 3.5rem' }}>
          <h2 style={{ fontSize: '2rem', fontWeight: 800, marginBottom: '0.75rem' }}>
            Structured 4-Step Recovery Workflow
          </h2>
          <p style={{ color: 'var(--gray-600)', fontSize: '1rem', lineHeight: 1.6 }}>
            Eliminating fragmented group chats and paper logs with an authoritative database-backed workflow.
          </p>
        </div>

        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
          gap: '1.75rem'
        }}>
          {/* Step 1 */}
          <div className="card" style={{ position: 'relative' }}>
            <div style={{
              width: '3rem',
              height: '3rem',
              borderRadius: 'var(--radius-md)',
              backgroundColor: 'var(--primary-light)',
              color: 'var(--primary)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '1.25rem',
              marginBottom: '1.25rem'
            }}>
              1
            </div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Report Property
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-600)', lineHeight: 1.5 }}>
              Students post detailed reports specifying category, date, and exact campus location (e.g. Central Library, TP Lab 3).
            </p>
          </div>

          {/* Step 2 */}
          <div className="card" style={{ position: 'relative' }}>
            <div style={{
              width: '3rem',
              height: '3rem',
              borderRadius: 'var(--radius-md)',
              backgroundColor: '#eff6ff',
              color: 'var(--accent)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '1.25rem',
              marginBottom: '1.25rem'
            }}>
              2
            </div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Search & Filter
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-600)', lineHeight: 1.5 }}>
              Perform indexed SQL queries combining keywords, report types (LOST/FOUND), categories, and status filters.
            </p>
          </div>

          {/* Step 3 */}
          <div className="card" style={{ position: 'relative' }}>
            <div style={{
              width: '3rem',
              height: '3rem',
              borderRadius: 'var(--radius-md)',
              backgroundColor: '#fffbeb',
              color: 'var(--warning)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '1.25rem',
              marginBottom: '1.25rem'
            }}>
              3
            </div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Submit Claim
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-600)', lineHeight: 1.5 }}>
              Found an item matching your lost belonging? Submit distinguishing features and evidence through the claim-review microservice.
            </p>
          </div>

          {/* Step 4 */}
          <div className="card" style={{ position: 'relative' }}>
            <div style={{
              width: '3rem',
              height: '3rem',
              borderRadius: 'var(--radius-md)',
              backgroundColor: '#ecfdf5',
              color: 'var(--success)',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              fontWeight: 800,
              fontSize: '1.25rem',
              marginBottom: '1.25rem'
            }}>
              4
            </div>
            <h3 style={{ fontSize: '1.125rem', fontWeight: 700, marginBottom: '0.5rem' }}>
              Admin Verification
            </h3>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-600)', lineHeight: 1.5 }}>
              Security Admin reviews claim evidence. Upon approval, physical handover is formally recorded with identity verification ID.
            </p>
          </div>
        </div>
      </section>

      {/* CTA Box */}
      <section className="container" style={{ paddingBottom: '4rem' }}>
        <div style={{
          backgroundColor: 'var(--primary)',
          color: '#ffffff',
          borderRadius: 'var(--radius-lg)',
          padding: '3rem 2rem',
          textAlign: 'center',
          boxShadow: 'var(--shadow-lg)'
        }}>
          <h2 style={{ color: '#ffffff', fontSize: '1.75rem', fontWeight: 800, marginBottom: '0.75rem' }}>
            Ready to Find or Return Campus Property?
          </h2>
          <p style={{ color: 'rgba(255, 255, 255, 0.85)', maxWidth: '600px', margin: '0 auto 1.75rem', fontSize: '0.9375rem' }}>
            Join your fellow SRM IST students and staff in maintaining an honest, secure campus community.
          </p>
          <div style={{ display: 'flex', justifyContent: 'center', gap: '1rem', flexWrap: 'wrap' }}>
            <Link to="/register" className="btn btn-accent btn-lg" style={{ backgroundColor: '#ffffff', color: 'var(--primary)' }}>
              Create Student Account
            </Link>
            <Link to="/items" className="btn btn-outline btn-lg" style={{ borderColor: '#ffffff', color: '#ffffff' }}>
              Search Published Listings
            </Link>
          </div>
        </div>
      </section>
    </div>
  );
};

export default LandingPage;
