import React from 'react';
import { Compass, GraduationCap, Github } from 'lucide-react';

const Footer = () => {
  return (
    <footer style={{
      backgroundColor: '#ffffff',
      borderTop: '1px solid var(--border-color)',
      paddingTop: '3rem',
      paddingBottom: '2.5rem',
      marginTop: 'auto'
    }}>
      <div className="container">
        <div style={{
          display: 'grid',
          gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
          gap: '2.5rem',
          marginBottom: '2.5rem'
        }}>
          {/* Col 1: Project Info */}
          <div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.625rem', marginBottom: '1rem' }}>
              <div style={{
                width: '2rem',
                height: '2rem',
                borderRadius: 'var(--radius-sm)',
                backgroundColor: 'var(--primary)',
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                color: '#ffffff'
              }}>
                <Compass size={18} />
              </div>
              <span style={{ fontWeight: 800, color: 'var(--gray-900)' }}>Campus Lost-and-Found</span>
            </div>
            <p style={{ fontSize: '0.875rem', color: 'var(--gray-600)', lineHeight: 1.6, marginBottom: '1rem' }}>
              Digitizing campus lost property recovery, ownership claim verification, and authorized collection management.
            </p>
            <div style={{ fontSize: '0.8125rem', color: 'var(--gray-500)' }}>
              SRM Institute of Science and Technology<br />
              Tiruchirappalli Campus
            </div>
          </div>

          {/* Col 2: Course Outcomes */}
          <div>
            <div style={{ fontWeight: 700, color: 'var(--gray-900)', marginBottom: '1rem', fontSize: '0.9375rem' }}>
              Course Outcome Evidence
            </div>
            <ul style={{ listStyle: 'none', fontSize: '0.8125rem', color: 'var(--gray-600)', display: 'flex', flexDirection: 'column', gap: '0.5rem' }}>
              <li><strong>CO-1:</strong> Semantic HTML5 & Responsive CSS3</li>
              <li><strong>CO-2:</strong> React 18, Hooks, State & Routing</li>
              <li><strong>CO-3:</strong> MySQL 8 Relational CRUD & JDBC</li>
              <li><strong>CO-4:</strong> Spring Boot 3 Three-Tier Services</li>
              <li><strong>CO-5:</strong> REST API & Microservice Integration</li>
            </ul>
          </div>

          {/* Col 3: Academic Credits */}
          <div>
            <div style={{ fontWeight: 700, color: 'var(--gray-900)', marginBottom: '1rem', fontSize: '0.9375rem' }}>
              Academic Assignment
            </div>
            <p style={{ fontSize: '0.8125rem', color: 'var(--gray-600)', lineHeight: 1.6 }}>
              <strong>Course:</strong> 21CSE354T — Full Stack Web Development<br />
              <strong>Task:</strong> Task 4 — Team Project Assignment<br />
              <strong>Topic:</strong> No. 7, Campus Lost-and-Found Portal<br />
              <strong>Completed by:</strong> Individual Submitter
            </p>
          </div>
        </div>

        <div style={{
          borderTop: '1px solid var(--border-color)',
          paddingTop: '1.5rem',
          display: 'flex',
          flexWrap: 'wrap',
          alignItems: 'center',
          justifyContent: 'space-between',
          gap: '1rem',
          fontSize: '0.8125rem',
          color: 'var(--gray-500)'
        }}>
          <div>
            &copy; 2026 SRM Institute of Science and Technology. All rights reserved.
          </div>
          <div style={{ display: 'flex', gap: '1.5rem' }}>
            <span>Java 21 LTS</span>
            <span>Spring Boot 3.3.4</span>
            <span>MySQL 8.0</span>
            <span>React 18</span>
            <span>Vite</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
