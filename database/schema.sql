-- ============================================================================
-- CAMPUS LOST-AND-FOUND PORTAL - CORE DATABASE SCHEMA
-- Target Database: campus_lost_and_found (MySQL 8.0)
-- Character Set: utf8mb4 | Collation: utf8mb4_unicode_ci
-- ============================================================================

USE campus_lost_and_found;

-- Drop child tables first to respect foreign key constraints
DROP TABLE IF EXISTS audit_logs;
DROP TABLE IF EXISTS collection_records;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

-- ----------------------------------------------------------------------------
-- Table: users
-- Stores registered university students, staff, and administrators
-- ----------------------------------------------------------------------------
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    student_id VARCHAR(50) NULL,
    department VARCHAR(100) NULL,
    phone_number VARCHAR(25) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_users_email (email),
    INDEX idx_users_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table: items
-- Stores posted lost and found belongings
-- ----------------------------------------------------------------------------
CREATE TABLE items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(150) NOT NULL,
    description TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    report_type VARCHAR(20) NOT NULL,
    location VARCHAR(150) NOT NULL,
    incident_date DATE NOT NULL,
    report_date DATE NOT NULL,
    image_url VARCHAR(500) NULL,
    contact_preference VARCHAR(100) NOT NULL DEFAULT 'IN_APP',
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    reporter_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_items_reporter FOREIGN KEY (reporter_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_items_status (status),
    INDEX idx_items_report_type (report_type),
    INDEX idx_items_category (category),
    INDEX idx_items_reporter (reporter_id),
    INDEX idx_items_incident_date (incident_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table: collection_records
-- Records the final physical handover/collection of verified found items
-- ----------------------------------------------------------------------------
CREATE TABLE collection_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL UNIQUE,
    claim_id BIGINT NOT NULL,
    claimant_id BIGINT NOT NULL,
    claimant_name VARCHAR(100) NOT NULL,
    claimant_student_id VARCHAR(50) NULL,
    handover_notes TEXT NOT NULL,
    collector_verification_id VARCHAR(100) NOT NULL,
    collected_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    verified_by_admin_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_collection_item FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE RESTRICT,
    CONSTRAINT fk_collection_admin FOREIGN KEY (verified_by_admin_id) REFERENCES users(id) ON DELETE RESTRICT,
    INDEX idx_collection_claimant (claimant_id),
    INDEX idx_collection_claim (claim_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ----------------------------------------------------------------------------
-- Table: audit_logs
-- Immutable security and activity log for key campus portal events
-- ----------------------------------------------------------------------------
CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    actor_id BIGINT NULL,
    actor_name VARCHAR(100) NULL,
    action VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id BIGINT NOT NULL,
    details TEXT NOT NULL,
    ip_address VARCHAR(45) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_actor FOREIGN KEY (actor_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_audit_action (action),
    INDEX idx_audit_entity (entity_type, entity_id),
    INDEX idx_audit_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
