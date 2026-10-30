# Technology Stack Compliance and Environment Verification Report

**Project Topic:** No. 7 — Campus Lost-and-Found Portal  
**Institution:** SRM Institute of Science and Technology, Tiruchirappalli Campus  
**Department:** Computer Science and Engineering  
**Course:** 21CSE354T — Full Stack Web Development (Task 4)  
**Date of Audit:** October 28, 2026  
**Auditor:** Lead Full-Stack Architect & Quality Engineer  

---

## 1. Compliance Executive Summary

This compliance audit verifies that all required technologies for Course 21CSE354T Task 4 are strictly honored, installed, and validated in the execution environment. No prohibited substitutes (e.g., Express/Node backend, MongoDB, Firebase, Supabase, browser localStorage as primary database, or H2/mock in-memory databases) are utilized.

| Component | Required Specification | Selected & Verified Version | Compliance Status |
| :--- | :--- | :--- | :--- |
| **Backend Runtime** | Java 17+ (Java 21 LTS preferred) | OpenJDK 21.0.12.1+1 LTS (Temurin) | **COMPLIANT (PASS)** |
| **Java Compiler** | javac matching Java runtime | javac 21.0.12.1 | **COMPLIANT (PASS)** |
| **Build & Dependency Tool** | Apache Maven 3.8+ / Maven Wrapper | Apache Maven 3.10.0 + Maven Wrapper (3.9.6) | **COMPLIANT (PASS)** |
| **Core Backend Framework** | Spring Boot 3.x (Spring Web, Spring Security, Validation) | Spring Boot 3.3.4 (Java 21, Spring Security 6.3, Jakarta EE 10) | **COMPLIANT (PASS)** |
| **Persistence / ORM** | Spring Data JPA / Hibernate | Spring Data JPA 3.3.4 (Hibernate ORM 6.5.3.Final) | **COMPLIANT (PASS)** |
| **Database System** | MySQL Relational Database 8.x | MySQL Server 8.0.46 (InnoDB, UTF8mb4) | **COMPLIANT (PASS)** |
| **Microservice Boundary** | Independent Claim-Review Microservice (Spring Boot) | Spring Boot 3.3.4 Microservice on dedicated port (8081) | **COMPLIANT (PASS)** |
| **Frontend Framework** | React 18+ with Vite (ES6+, JSX) | React 18.3.1 + Vite 5.4.8 | **COMPLIANT (PASS)** |
| **Frontend Runtime/Tools**| Node.js & npm (for tooling/bundling only) | Node.js v24.19.0 / npm 11.17.0 | **COMPLIANT (PASS)** |
| **API Client** | Axios or Fetch API (used consistently) | Axios 1.7.7 | **COMPLIANT (PASS)** |
| **Styling & UI Icons** | Modern CSS3 + Lucide React | Custom CSS Tokens / Responsive Flexbox & Grid + Lucide React 0.447.0 | **COMPLIANT (PASS)** |

---

## 2. Actual Project Locations

Current repository structure (files not listed here may exist locally but are not part of this source tree):

```
Full Stack Task 4/
├── backend/                  # Core Spring Boot REST API Service (Port 8080)
│   ├── pom.xml
│   └── src/
├── claim-review-service/     # Independent Claim-Review Spring Boot Service (Port 8081)
│   ├── pom.xml
│   └── src/
├── frontend/                 # React 18 + Vite Frontend Application (Port 5173)
│   ├── package.json
│   ├── vite.config.js
│   └── src/
├── database/                 # Relational SQL schema scripts
│   ├── schema.sql
│   └── claim_review_schema.sql
└── docs/
    └── STACK_COMPLIANCE.md
```

---

## 3. Environment Prerequisites and Verification Commands

### 3.1 Java & Compiler Verification
- **Command:** `java -version`
  - **Output:** `openjdk version "21.0.12.1" 2026-08-18 LTS`, `OpenJDK 64-Bit Server VM Temurin-21.0.12.1+1`
- **Command:** `javac -version`
  - **Output:** `javac 21.0.12.1`
- **Result:** Java 21 LTS is active and verified.

### 3.2 Maven Verification
- **Command:** `mvn -v`
  - **Output:** `Apache Maven 3.10.0`, `Java version: 21.0.12.1`
- **Result:** Maven 3.10.0 is active and verified.

### 3.3 Node.js & npm Verification
- **Command:** `node -v`
  - **Output:** `v24.19.0`
- **Command:** `npm -v`
  - **Output:** `11.17.0`
- **Result:** Node.js toolchain verified for Vite build.

### 3.4 MySQL Database Connectivity Verification
- **Command:** Connect to the configured local MySQL instance using credentials supplied through the local environment; do not place passwords on the command line or in this document.
- **Databases Initialized:**
  - `campus_lost_and_found` (for Core Backend)
  - `campus_claim_review` (for Claim-Review Microservice)
- **Result:** Real MySQL 8.0.46 instance running on port 3307 with active network socket and transactional InnoDB engine.

---

## 4. Resolution of Environmental Prerequisites

- **MySQL Port & Service Isolation:** The local MySQL instance uses a project-local data directory. Database passwords belong in local environment configuration and must not be committed.
- **Maven Wrapper:** Provided in both backend services (`mvnw` / `mvnw.cmd`) to guarantee zero-installation portability for university examiners.

**Stack Compliance Result: FULLY VERIFIED & COMPLIANT.**
