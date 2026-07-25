# MonikaMart Capstone Evaluation Slide Deck
**Candidate:** Monika Subramanian  
**Department:** Computer Science & Engineering / Information Technology  
**Curriculum:** Anna University R2025 Semester 3 Capstone  
**Target Checkpoint:** Final Review (October 10, 2026)  

---

## Slide 1: Title Slide
- **Title:** MonikaMart: Multi-Role Enterprise E-Commerce Platform with Generative AI Assistant
- **Course:** Anna University R2025 Regulations, Semester 3 Capstone
- **Builder / Solo Developer:** Monika Subramanian
- **Tech Stack:** Java 17 LTS, Servlets 4.0, Tomcat 9.0, HikariCP, H2/MySQL, Gemini AI

---

## Slide 2: Problem Statement & Motivation
- **Context:** E-commerce systems must balance transactional integrity, multi-role security boundaries (Buyer, Seller, Admin), and low-latency interactive responsiveness.
- **Goal:** Build an end-to-end e-commerce solution with classic Java Enterprise architectural rigor (MVC, DAO, strict layering, 100% PreparedStatement security) paired with a modern AI Shopping Concierge.
- **Scope:** 8 Core Features (F1–F8) + 4 Advanced Features (O1–O4) developed across an 11-week engineering roadmap.

---

## Slide 3: System Architecture & Three-Tier Layering
- **Client Tier:** Web Browser / Responsive View with JSTL `<c:out>` output escaping.
- **Controller Tier:** `EncodingFilter`, `AuthFilter` (RBAC), Java Servlets handling HTTP requests.
- **Service Tier:** Validation gates, transactional coordination, AI proxy.
- **Data Access Tier:** DAO pattern backed by HikariCP Connection Pool and H2 database.

---

## Slide 4: Database Design & Relational Model (D1)
- **Tables:** `users`, `products`, `orders`, `order_items`, `cart_items`, `reviews`, `wishlist_items`, `order_status_history`.
- **Normalization:** 3NF compliance with historical price preservation in `order_items.unit_price`.
- **Precision:** `DECIMAL(10,2)` for financial calculations to prevent floating-point inaccuracies.
- **Constraints:** Foreign key constraints, unique email indices, and automatic timestamps.

---

## Slide 5: Actor Use Cases & Boundaries (D2)
- **Buyer:** Browse catalog, search, filter, manage cart/wishlist, checkout with mock payment, track orders, post reviews.
- **Seller:** Dedicated merchant dashboard, inventory creation/updates, incoming order fulfillment.
- **Admin:** Seeded governance account, user directory audit, platform revenue tracking, listing moderation.

---

## Slide 6: Transactional Order Fulfillment Flow (D3)
- **ACID Atomicity:** JDBC transaction (`conn.setAutoCommit(false)`) encompassing order creation, item logging, stock decrement, and cart clearing.
- **Concurrency Defense:** Atomic conditional update:
  `UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?`
- **Session Replay Protection:** Transactional cart purge blocks double-charges on browser refresh.

---

## Slide 7: Design Patterns Applied
- **DAO Pattern:** Decouples persistence from business logic, enabling Mockito unit testing.
- **MVC / Front Controller:** Clean separation of concerns between Servlets and JSP views.
- **Singleton:** Shared thread-safe instances of HikariCP pool and Jackson ObjectMapper.
- **Factory & Strategy:** Configurable `ChatProvider` supporting live Gemini LLM and offline FAQ fallback.

---

## Slide 8: Security Checklist Compliance (Section 9)
- **100% PreparedStatements:** Audited via grep; zero string-concatenated SQL queries.
- **jBCrypt Hashing:** Passwords securely hashed with salted Blowfish; plain passwords never logged.
- **Session Regeneration:** Immediate invalidation and regeneration upon login to neutralize session fixation.
- **Information Masking:** Custom 404/500 error pages in `web.xml` suppress internal stack traces.

---

## Slide 9: AI Shopping Concierge Architecture (O4)
- **Strategy Pattern Engine:** `MockChatProvider` (offline FAQ) and `GeminiChatProvider` (live LLM).
- **Security:** API keys stored server-side only; never exposed to client browsers.
- **Resilience:** Per-session sliding-window rate limit (10 msgs/min), 500-char input cap, and in-memory question caching.
- **Graceful Fallback:** Catches network timeouts and delivers offline answers seamlessly.

---

## Slide 10: Testing & Quality Assurance
- **Automated Test Suite:** 25 passing JUnit 5 & Mockito test cases spanning all layers.
- **Load Testing:** JMeter test plan verifying 10 concurrent threads for 60 seconds with 0% error rate.
- **Continuous Integration:** GitHub Actions build pipeline executing `mvn -B clean verify` on every push.

---

## Slide 11: Deployment & DevOps Architecture
- **Packaging:** Standard `.war` artifact generated via Maven WAR Plugin.
- **Containerization:** Multi-stage Dockerfile deployed on Apache Tomcat 9.0 with OpenJDK 17.
- **Persistence:** Volume-mounted `./data` directory safeguarding H2 database files across container lifecycles.
- **Health Verification:** `/api/v1/health` endpoint reporting `{"status":"UP","db":"UP"}`.

---

## Slide 12: Live Demonstration Workflow
1. **Buyer Journey:** Login &rarr; Search "Sony" &rarr; Add to Cart &rarr; Checkout via Mock UPI &rarr; Track Order.
2. **Seller Fulfillment:** Login &rarr; View Incoming Order &rarr; Advance status Confirmed &rarr; Shipped &rarr; Delivered.
3. **Buyer Review:** Revisit order &rarr; Submit 5-star review &rarr; Verify updated rating on catalog.
4. **AI Concierge:** Ask questions &rarr; Experience intelligent suggestions & sliding-window rate limiting.
5. **Admin Governance:** View revenue metrics & user registry &rarr; Moderate listings.

---

## Slide 13: Sprint Timeline & Git Conventional Commits
- **33+ Commits:** Maintained across Jul 24 – Oct 10 window with conventional prefixes (`feat:`, `fix:`, `test:`, `docs:`).
- **Sprint Milestones:**
  - Checkpoint 1 (Jul 27): Problem Statement & Skeleton Locked
  - Checkpoint 2 (Aug 10): MVP Review (Full Buyer Journey Complete)
  - Checkpoint 3 (Sep 21): Full Build & Deployment Review (v1.0.0)
  - Checkpoint 4 (Oct 10): Final Review & AI Chatbot Integration (v1.2.0)

---

## Slide 14: Conclusion & Key Learnings
- **Academic Milestone:** Successfully demonstrated mastery of Java enterprise web development, transactional databases, and secure software development lifecycles.
- **Key Takeaways:** Hands-on experience with ACID transactions, RBAC filters, BCrypt hashing, and integrating modern LLMs into classical web frameworks.

---

## Slide 15: Q&A and Faculty Discussion
- Open for questions from faculty evaluation committee and project guide.
- Live demonstration link & backup video recording available.
