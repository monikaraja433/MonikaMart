# Anna University R2025 Semester 3 Capstone Final Project Report
**Project Title:** MonikaMart: A Multi-Role Enterprise E-Commerce Platform with Classical Java Servlets and Generative AI Assistant  
**Student Name / Builder:** Monika Subramanian  
**Department:** Computer Science & Engineering / Information Technology  
**Regulations:** Anna University R2025  
**Submission Date:** October 10, 2026  

---

## 1. Abstract

Modern electronic commerce architectures require robust transactional guarantees, high-performance data access, role-based security isolation, and intuitive conversational user engagement. **MonikaMart** is an enterprise-grade e-commerce application developed to satisfy the Anna University R2025 Semester 3 Capstone requirements. Built with foundational Java Enterprise technologies—specifically **Java Servlets (javax.servlet 4.0.1)**, **JDBC with HikariCP connection pooling**, an **H2/MySQL relational database**, and **JSTL-escaped JSP views**—the platform delivers complete functional workflows for Buyers, Merchants, and Platform Administrators.

In addition to core shopping flows (F1–F8) and workflow extensions (O1–O3), the platform introduces an **AI Shopping Concierge (O4)** engineered via the Strategy design pattern, featuring Google Gemini integration, a fallback offline FAQ engine, per-session sliding-window rate limiting, and in-memory question caching.

---

## 2. System Architecture & Layered Design

The platform strictly enforces the classical Three-Tier MVC (Model-View-Controller) enterprise architecture:

1. **Presentation Tier (View):** JSP pages rendered server-side utilizing JavaServer Pages Standard Tag Library (JSTL 1.2) for declarative logic and contextual output escaping (`<c:out>`) to prevent Cross-Site Scripting (XSS).
2. **Filter & Controller Tier:**
   - `EncodingFilter`: Guarantees UTF-8 character encoding across request and response streams.
   - `AuthFilter`: Enforces Role-Based Access Control (RBAC) across `/buyer/*`, `/seller/*`, and `/admin/*` protected routes.
   - Java Servlets: Act as Front Controllers dispatching actions to the service layer without containing direct SQL or presentation formatting.
3. **Business Logic Tier (Service):**
   - Implements validation gates, ensuring bad input triggers HTTP 400 responses with field-level error mappings before touching data layers.
   - Coordinates transactional business operations (such as checkout and inventory reservation).
4. **Data Access Tier (DAO):**
   - Encapsulates database communication through JDBC interfaces and implementations.
   - Enforces 100% `PreparedStatement` parameterized queries to neutralize SQL injection vulnerabilities.
5. **Persistence Tier:**
   - Managed via a single `HikariDataSource` connection pool owned by `AppContextListener`.
   - Backed by H2 database in MySQL-compatible file persistence mode.

---

## 3. Design Patterns Applied

| Design Pattern | Applied Location | Architecture Rationale & Benefit |
| :--- | :--- | :--- |
| **Data Access Object (DAO)** | `UserDAO`, `ProductDAO`, `OrderDAO`, `CartDAO`, `ReviewDAO`, `WishlistDAO` | Completely decouples domain business logic from physical database SQL queries, facilitating unit testing with Mockito. |
| **Model-View-Controller (MVC)** | Servlets (Controllers), Models/DTOs (Model), JSPs (Views) | Clear separation of presentation, data transformation, and routing concerns. |
| **Singleton** | `JsonUtil.getMapper()`, `DBUtil.getConnection()` | Centralizes costly resource lifecycles (Jackson ObjectMapper and HikariCP connection pool) to optimize memory and throughput. |
| **Factory Pattern** | `ChatProviderFactory.getProvider()` | Decouples chatbot instantiation from runtime caller, enabling seamless switching between mock and real LLM providers. |
| **Strategy Pattern** | `ChatProvider` interface (`MockChatProvider`, `GeminiChatProvider`) | Enables swapping the conversational intelligence engine at runtime based on environment configuration without modifying client servlets. |
| **Builder / Data Transfer Object (DTO)** | `UserResponseDTO`, `UserRegistrationDTO`, `ProductDTO`, `ApiResponse<T>` | Eliminates entity leaks (e.g., ensuring `passwordHash` is never exposed over HTTP) and standardizes response envelopes. |

---

## 4. Feature Implementation Review (F1–F8, O1–O4)

### F1: Authentication & RBAC
- Passwords salted and hashed with Blowfish crypt (`jBCrypt`, 10 rounds).
- HTTP session fixation defense via session invalidation and re-creation on login.
- Explicit 30-minute session expiration. Seeded Admin account protection.

### F2 & F3: Catalog Management & Buyer Search
- Seller listing lifecycle with real-time stock quantity adjustments.
- Multi-criteria filtering combining keywords, category classifications, and pricing/rating sorts.
- Server-side pagination preventing unbounded memory consumption.

### F4 & F5: Cart & Transactional Order Placement
- Cart item manipulation with running line-item and grand total calculations.
- Atomic ACID transaction placement: orders inserted, items recorded, stock decremented conditionally (`stock_qty >= ?`), and cart purged inside a single database transaction.

### F6 & O2: Order Tracking & Status Workflow
- Two-way visibility: Buyers inspect personal purchase history; Sellers manage incoming product fulfillment.
- Strict state-machine workflow: `PENDING` &rarr; `CONFIRMED` &rarr; `SHIPPED` &rarr; `DELIVERED`.
- State transitions recorded into `order_status_history` audit table.

### F7: Platform Administration & Moderation
- Platform-wide transaction metrics and gross merchandise revenue aggregation.
- Global user directory audit.
- Merchant listing moderation with one-click suspension and reactivation.

### F8: Verified Buyer Reviews
- Review submission gate: only buyers with confirmed `DELIVERED` status on purchased items may submit reviews.
- 1-to-5 star ratings aggregated dynamically into average rating and review counts.

### O1 & O3: Wishlist & Seller Analytics
- Persistent bookmarking with one-click transfer to shopping cart.
- Seller sales performance reporting tracking gross sales volume and stock thresholds.

### O4: Conversational AI Shopping Assistant
- Server-side API proxy shielding credentials from client exposure.
- Per-session sliding-window rate limit (10 messages/minute) preventing resource exhaustion.
- Session caching of repeated identical inquiries.
- Graceful degradation: network failures seamlessly fall back to the offline knowledge base.

---

## 5. Security & Verification Analysis

The project underwent an exhaustive 6-point security audit:
1. **Zero SQL Injection:** Audited all source files; zero instances of string-concatenated SQL queries found. 100% of queries use `PreparedStatement`.
2. **Cryptographic Protection:** Passwords securely hashed with `jBCrypt`; zero plaintext passwords persisted.
3. **Session Hardening:** HttpOnly cookie flags configured in `web.xml`; explicit session regeneration on authentication.
4. **Information Disclosure Prevention:** Custom error pages in `web.xml` intercept 404 and 500 errors, suppressing system stack traces.
5. **Credential Management:** Configuration secrets externalized to `config.properties` and `.env`, both enforced by `.gitignore`.
6. **Automated Testing:** 25 automated JUnit 5 & Mockito test cases execute on every push via GitHub Actions CI.

---

## 6. Known Limitations & Future Enhancements

1. **Payment Gateway Integration:** The current version utilizes simulated instant mock payments for academic grading; production deployment would integrate Razorpay or Stripe webhooks.
2. **Distributed Cache:** Session and chatbot caches are currently in-memory per JVM; multi-node scaling would benefit from external Redis caching.
3. **Image Object Storage:** Product images currently link to hosted CDN URLs; future versions could incorporate AWS S3 or Google Cloud Storage bucket uploads.

---

## 7. Conclusion

The MonikaMart Capstone project fulfills all instructional, architectural, security, and functional directives set forth by Anna University R2025. It demonstrates end-to-end software engineering excellence from requirement modeling (D1, D2, D3) to clean enterprise implementation and containerized deployment.
