# Sprint Retrospectives (RETRO.md)
**Project:** MonikaMart Capstone (Anna University R2025, Semester 3)  
**Format:** What worked &bull; What didn't &bull; One change for next sprint  

---

### Sprint 0: Kickoff (Jul 24 – Jul 27, 2026)
- **What worked:** Project skeleton and Maven configuration established cleanly targeting JDK 17 and Tomcat 9.0; initial D2 Use Case diagram mapped.
- **What didn't:** Initial database connection required clear separation of properties from source code.
- **One change for next sprint:** Move all database credentials into externalized `.env` and `config.properties` files with strict `.gitignore` rules.

---

### Sprint 1: Authentication & Base DAO (Jul 27 – Aug 2, 2026)
- **What worked:** jBCrypt password hashing and single `ServletContextListener` owning HikariCP connection pool; JUnit 5 embedded H2 test runner stood up.
- **What didn't:** Session fixation vulnerability was possible if the session ID was not invalidated upon authentication.
- **One change for next sprint:** Explicitly call `session.invalidate()` and issue a newly regenerated session ID upon every successful user login.

---

### Sprint 2: Core Shopping Flow (Aug 3 – Aug 9, 2026)
- **What worked:** Seller product listing, buyer catalog search, cart item management, and atomic checkout placed with mock payment confirmation.
- **What didn't:** Direct object mutations in controllers blurred the DAO/Service separation.
- **One change for next sprint:** Strictly prohibit SQL queries in servlets and enforce business rule validations exclusively within the Service layer.

---

### Sprint 3: Review Feedback & Seller Dashboard (Aug 10 – Aug 16, 2026)
- **What worked:** DTO abstractions successfully separated from internal database entities (`UserResponseDTO` never exposes `passwordHash`).
- **What didn't:** Error responses from servlets lacked a standardized envelope format across web and JSON endpoints.
- **One change for next sprint:** Standardize all API responses with the `{ "success": boolean, "data": ..., "error": ... }` response envelope.

---

### Sprint 4: Admin Panel & Order History (Aug 17 – Aug 23, 2026)
- **What worked:** Role-based access controls fully enforced via `AuthFilter`; admin dashboard displays global platform metrics.
- **What didn't:** Seller could view orders belonging to other merchants if not filtered by individual `seller_id`.
- **One change for next sprint:** Scope all seller queries strictly by `oi.seller_id = ?` to maintain strict multi-tenant merchant data isolation.

---

### Sprint 5: Search Refinement & Order Workflow (Aug 24 – Aug 30, 2026)
- **What worked:** Keyword search cleanly combined with category filtering, sorting, and pagination; order status tracking workflow (O2) implemented.
- **What didn't:** Schema modification for status tracking was initially done in base schema rather than a separate migration script.
- **One change for next sprint:** Keep all schema evolutions in dedicated versioned Flyway-style migration files (`V2__order_status_workflow.sql`).

---

### Sprint 6: Reviews, Ratings & Hardening (Aug 31 – Sep 6, 2026)
- **What worked:** Product reviews and star ratings (F8) enabled for verified delivered orders; empty cart checkout edge cases blocked.
- **What didn't:** Unverified buyers could attempt to submit reviews without having received the delivered package.
- **One change for next sprint:** Require `orderDAO.hasUserPurchasedProduct(buyerId, productId)` check and `status == DELIVERED` before accepting any review.

---

### Sprint 7: Security Hardening & Full Test Coverage (Sep 7 – Sep 13, 2026)
- **What worked:** 100% PreparedStatement audit verified; custom error pages in `web.xml` configured to never leak stack traces; JUnit 5 suite expanded.
- **What didn't:** JMeter load test scripts were unversioned in local folders.
- **One change for next sprint:** Store the full JMeter test plan (`load_test.jmx`) and runner scripts directly in `docs/load-test/` within the repository.

---

### Sprint 8: Full Build & Deployment Review (Sep 14 – Sep 20, 2026)
- **What worked:** Multi-stage Dockerfile and Docker Compose established; `GET /api/v1/health` returning `{"status":"UP","db":"UP"}` verified; tagged `v1.0.0`.
- **What didn't:** Database in Docker container risked losing data when container was destroyed.
- **One change for next sprint:** Mount host volume `./data:/usr/local/tomcat/data` to guarantee file-based database persistence across container lifecycles.

---

### Sprint 9: AI Chatbot Backend Proxy (Sep 21 – Sep 27, 2026)
- **What worked:** `ChatProvider` interface defined; `MockChatProvider` and `GeminiChatProvider` implemented behind `ai.chatbot.provider` config flag; tagged `v1.1.0`.
- **What didn't:** Consecutive requests from the same user session risked exceeding third-party API rate limits and quotas.
- **One change for next sprint:** Enforce a sliding-window rate limit (10 messages/minute) and in-memory question caching per session in `ChatService`.

---

### Sprint 10: Chatbot UI, Polish & Documentation (Sep 28 – Oct 4, 2026)
- **What worked:** Floating responsive chat widget created with quick FAQ chips; comprehensive architecture diagrams (D1, D2, D3) produced.
- **What didn't:** Floating chat widget overlapped slightly with mobile footer on smaller screen viewports.
- **One change for next sprint:** Apply CSS media queries adjusting chat window dimensions and offset for screens under 480px width.

---

### Sprint 11: Final Regression, Report & Capstone Demo (Oct 5 – Oct 10, 2026)
- **What worked:** Full 25-test regression pass executed with zero failures; final project report, slide deck, and rehearsed demo scripts finalized; tagged `v1.2.0`.
- **What didn't:** Video backup files are large and cannot be committed directly to GitHub git history.
- **One change for next sprint:** Document demo walkthrough steps with timestamped script cues in `docs/DEMO_SCRIPT.md` and link backup video hosting.
