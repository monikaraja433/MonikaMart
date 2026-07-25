# 🛒 MonikaMart — Capstone E-Commerce Platform

[![Java CI with Maven](https://github.com/monika/monikamart/actions/workflows/build.yml/badge.svg)](https://github.com/monika/monikamart/actions)
![Java](https://img.shields.io/badge/Java-17%20LTS-ED8B00?style=flat&logo=openjdk&logoColor=white)
![Tomcat](https://img.shields.io/badge/Apache%20Tomcat-9.0.x-F8DC75?style=flat&logo=apachetomcat&logoColor=black)
![H2 Database](https://img.shields.io/badge/H2-2.2.224-blue?style=flat)
![Status](https://img.shields.io/badge/Final%20Review-Green%20(Oct%2010)-success)

> **Anna University R2025 Regulations — Semester 3 Capstone Project**  
> **Student / Builder:** Monika Subramanian  
> **Package Namespace:** `com.monika.monikamart`  
> **Evaluation Window:** July 24, 2026 – October 10, 2026  

---

## 📌 Executive Summary

**MonikaMart** is a production-grade, multi-role e-commerce web platform engineered using the classic Java Enterprise architecture: **Java Servlets (javax.servlet 4.0.1)**, **JDBC with HikariCP Connection Pooling**, **H2/MySQL Relational Database**, **JSTL-escaped JSP views**, and an integrated **AI Shopping Assistant (O4)** backed by Google Gemini and an intelligent offline FAQ engine.

The system rigorously implements the Anna University capstone curriculum requirements across all 11 development sprints, featuring strict layered architecture (Controller &rarr; Service &rarr; DAO &rarr; DB), atomic transactions, comprehensive security hardening, 100% PreparedStatement audit, and zero-downtime containerized deployment.

---

## 🏛️ System Architecture

```
                       [ Client Browser / Mobile Web ]
                                      │
                         HTTP Requests / REST API
                                      │
                       ┌──────────────▼──────────────┐
                       │       Filter Pipeline       │
                       │  • EncodingFilter (UTF-8)   │
                       │  • CORSFilter (REST API)    │
                       │  • AuthFilter (Role RBAC)   │
                       └──────────────┬──────────────┘
                                      │
                       ┌──────────────▼──────────────┐
                       │     Controller / Servlets   │
                       │  • AuthServlet              │
                       │  • ProductServlet           │
                       │  • CartServlet              │
                       │  • OrderServlet             │
                       │  • SellerServlet            │
                       │  • AdminServlet             │
                       │  • ReviewServlet            │
                       │  • WishlistServlet          │
                       │  • ChatServlet              │
                       │  • HealthServlet            │
                       └──────────────┬──────────────┘
                                      │
                                      ▼
                       ┌─────────────────────────────┐  ┌───────────────────────┐
                       │        Service Layer        │  │   AI Chatbot Engine   │
                       │  • UserService              │  │  • ChatProvider (IF)  │
                       │  • ProductService           │◄─┤  • MockChatProvider   │
                       │  • CartService              │  │  • GeminiChatProvider │
                       │  • OrderService (ACID)      │  │  • RateLimit / Cache  │
                       │  • ReviewService            │  └───────────────────────┘
                       │  • WishlistService          │
                       └──────────────┬──────────────┘
                                      │
                                      ▼
                       ┌─────────────────────────────┐
                       │      DAO / Data Access      │
                       │  • UserDAO / ProductDAO     │
                       │  • OrderDAO / CartDAO       │
                       │  • ReviewDAO / WishlistDAO  │
                       │  (100% PreparedStatements)  │
                       └──────────────┬──────────────┘
                                      │
                                      ▼
                       ┌─────────────────────────────┐
                       │  HikariCP Connection Pool   │
                       └──────────────┬──────────────┘
                                      │
                                      ▼
                       ┌─────────────────────────────┐
                       │  H2 / MySQL Persistent DB   │
                       └─────────────────────────────┘
```

---

## 📑 Core Deliverables & Technical Diagrams

- **[D1 Entity-Relationship (ER) Diagram](docs/D1_ER_DIAGRAM.md)**: Complete database schema, 3NF normalization, and relationship constraints.
- **[D2 Use Case Diagram](docs/D2_USE_CASE_DIAGRAM.md)**: Actor interactions across Buyer, Seller, Administrator, and AI Assistant.
- **[D3 Sequence Diagram](docs/D3_SEQUENCE_DIAGRAM.md)**: Transactional place-order, inventory decrement, and mock payment flow.
- **[Manual E2E Test Cases](docs/E2E_TEST_CASES.md)**: 18-step verification test matrix with pass/fail criteria.
- **[Final Capstone Report](docs/FINAL_PROJECT_REPORT.md)**: Academic report detailing architecture, design patterns, and analysis.
- **[Presentation Slide Deck](docs/SLIDE_DECK.md)**: 15-slide capstone evaluation deck for faculty review.
- **[Rehearsed Demo Walkthrough Script](docs/DEMO_SCRIPT.md)**: Step-by-step presentation script and backup video guide.
- **[Sprint Retrospectives (RETRO.md)](RETRO.md)**: Weekly sprint retrospective logs across the entire development window.

---

## ✨ Features at a Glance

| Feature ID | Name | Module Description | Target Week | Status |
| :--- | :--- | :--- | :--- | :--- |
| **F1** | Authentication & Roles | Registration/login for Buyer & Seller, salted jBCrypt hashing, session fixation prevention. Seeded Admin account. | Week 1 | ✅ Completed |
| **F2** | Seller Catalog Management | Seller dashboard to create, update, delete product listings with image, description, price, and stock levels. | Week 2, 3 | ✅ Completed |
| **F3** | Buyer Browse & Search | Combined keyword search, category filtering, sort by price/rating/newest, and pagination. | Week 2, 5 | ✅ Completed |
| **F4** | Cart Management | Add, update quantity, remove items, calculate real-time running subtotal and order totals. | Week 2 | ✅ Completed |
| **F5** | Transactional Checkout | Mock payment gateway simulation (UPI, Cards, NetBanking), stock reservation, cart clearance. | Week 2 | ✅ Completed |
| **F6** | Order History & Tracking | Buyer order history and merchant view of incoming customer orders. | Week 4 | ✅ Completed |
| **F7** | Admin Governance | Platform dashboard with platform revenue, user directory, and catalog listing moderation. | Week 4 | ✅ Completed |
| **F8** | Reviews & Ratings | 1 to 5 star customer ratings and written feedback restricted to verified delivered orders. | Week 6 | ✅ Completed |
| **O1** | Wishlist / Save-for-Later | Bookmark favorite products and move directly to cart with one click. | Week 6 | ✅ Completed |
| **O2** | Order Status Workflow | State machine: `PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED` with audit history. | Week 5 | ✅ Completed |
| **O3** | Seller Sales Analytics | Real-time merchant dashboard reporting gross sales volume, orders count, and inventory alerts. | Week 6 | ✅ Completed |
| **O4** | AI Shopping Assistant | Floating chat widget with Google Gemini & offline FAQ fallback, per-session rate limit (10/min), and caching. | Week 9, 10 | ✅ Completed |

---

## 🛠️ Technology Stack Table

| Layer / Concern | Technology | Version | Purpose |
| :--- | :--- | :--- | :--- |
| **Programming Language** | Java (OpenJDK) | 17 LTS | Core application language |
| **Web Server / Servlet Container** | Apache Tomcat | 9.0.x | Servlet specification (javax.servlet 4.0.1) |
| **Connection Pool** | HikariCP | 5.1.0 | High-performance JDBC connection pooling |
| **Database Engine** | H2 Database | 2.2.224 | Zero-config embedded & persistent MySQL mode |
| **Security & Cryptography** | jBCrypt | 0.4 | Salted Blowfish password hashing |
| **JSON Serialization** | Jackson Databind | 2.17.2 | REST API and Chatbot payload serialization |
| **View Technology** | JavaServer Pages (JSP) + JSTL | 1.2 | Server-side rendering with XSS escaping |
| **Testing Framework** | JUnit 5 + Mockito | 5.10.3 / 5.12.0 | Unit and Service-layer mock test suites |
| **Containerization** | Docker & Docker Compose | Latest | Reproducible, portable container deployment |
| **Build Automation & CI** | Apache Maven & GitHub Actions | 3.9+ | Automated building, testing, and packaging |

---

## 🚀 Getting Started & Local Execution

### 1. Build & Run Tests
```bash
mvn clean test
```
*Executes all 25 unit and integration tests across DAOs, Services, Chatbot, and Validation layers.*

### 2. Package into WAR
```bash
mvn clean package
```
Generates standard deployable WAR at `target/monikamart.war`.

### 3. Run with Docker Compose
```bash
docker-compose up --build
```
Access the application at [http://localhost:8080/products](http://localhost:8080/products).

---

## 🔑 Demo Seed Accounts (One-Click Auto-Fill)

The database automatically initializes schema and demo accounts on first launch:

| Role | Email | Password | Intended Capabilities |
| :--- | :--- | :--- | :--- |
| **Buyer** | `buyer1@monikamart.com` | `Buyer@123` | Browse catalog, manage cart, place orders, review products |
| **Seller** | `seller1@monikamart.com` | `Seller@123` | Merchant hub, create listings, fulfill incoming orders |
| **Admin** | `admin@monikamart.com` | `Admin@123` | Platform oversight, user registry, listing moderation |

---

## 🛡️ Security Checklist Compliance (Section 9)

- [x] **100% Parameterized SQL:** Audited via `grep -rn "Statement)" src/` &mdash; all database interactions utilize `PreparedStatement`.
- [x] **Bcrypt Password Security:** Passwords hashed with salted BCrypt before storage; plain passwords never logged or persisted.
- [x] **Session Fixation Defense:** Old session invalidated and new session ID regenerated upon every authentication.
- [x] **XSS Output Sanitization:** User-supplied output consistently escaped via JSTL `<c:out>` and string sanitizers.
- [x] **Custom Error Pages:** `web.xml` maps 404 and 500 error pages omitting internal stack traces.
- [x] **Externalized Secrets:** `.env` and `config.properties` excluded from version control via `.gitignore`.
