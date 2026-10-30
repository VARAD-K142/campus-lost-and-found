-- ============================================================================
-- CAMPUS CLAIM-REVIEW MICROSERVICE - DATABASE SCHEMA
-- Target Database: campus_claim_review (MySQL 8.0)
-- Character Set: utf8mb4 | Collation: utf8mb4_unicode_ci
-- ============================================================================

USE campus_claim_review;

DROP TABLE IF EXISTS claim_records;

-- ----------------------------------------------------------------------------
-- Table: claim_records
-- Authoritative owner of claim submissions, proof data, and admin review verdicts
-- ----------------------------------------------------------------------------
CREATE TABLE claim_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL,
    claimant_user_id BIGINT NOT NULL,
    claimant_name VARCHAR(100) NOT NULL,
    claimant_email VARCHAR(150) NOT NULL,
    claimant_phone VARCHAR(25) NULL,
    claimant_student_id VARCHAR(50) NULL,
    claim_explanation TEXT NOT NULL,
    distinguishing_features TEXT NOT NULL,
    proof_description TEXT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    admin_notes TEXT NULL,
    reviewed_by_admin_id BIGINT NULL,
    reviewed_by_admin_name VARCHAR(100) NULL,
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_at TIMESTAMP NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_claims_item (item_id),
    INDEX idx_claims_claimant (claimant_user_id),
    INDEX idx_claims_status (status),
    INDEX idx_claims_submitted_at (submitted_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
