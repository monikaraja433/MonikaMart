# Manual End-to-End (E2E) Test Suite & Verification Matrix
**Project:** MonikaMart Capstone Platform  
**Target Milestone:** Full Build & Final Review Verification  
**Evaluation Scope:** F1 through F8, O1 through O4  

---

## E2E Journey Test Execution Matrix

| Test ID | Core Journey Stage | Input / Action | Expected Result | Pass/Fail Criteria |
| :--- | :--- | :--- | :--- | :--- |
| **TC-AUTH-01** | Account Registration | Navigate to `/register`. Fill name, valid email, strong password (`TestPass@123`), select role `BUYER`. Submit. | User created in DB with jBCrypt salted hash. Session initialized. Redirected to `/products?registered=true`. | **PASS** |
| **TC-AUTH-02** | Security: Weak Password | Enter password `12345` on registration form. | Client & server reject submission with field error: "Password must be at least 8 characters with upper, lower, and digit". | **PASS** |
| **TC-AUTH-03** | Security: Admin Signup Protection | Attempt to tamper or submit `role=ADMIN` in registration payload. | Server validation rejects with HTTP 400: "Admin accounts cannot be registered publicly". Seed admin only. | **PASS** |
| **TC-AUTH-04** | Login & Session Fixation | Sign in as `buyer1@monikamart.com` with `Buyer@123`. | Old session invalidated; new session ID issued. Redirected to intended page. | **PASS** |
| **TC-CAT-01** | Catalog Search & Filter | Filter category "Electronics" and search keyword "Sony". | Only matching active electronics products returned. Price, ratings, and stock status displayed. | **PASS** |
| **TC-CAT-02** | Catalog Sorting | Select sort dropdown "Price: Low to High". | Catalog sorts in ascending order by `DECIMAL(10,2)` price. | **PASS** |
| **TC-CART-01** | Cart Operations | Click "+ Cart" on Sony Headphones. Increase quantity to 2 in `/cart`. | Item subtotal updates to `2 * 29999.00 = ₹59,998.00`. Running cart total matches. | **PASS** |
| **TC-CART-02** | Stock Overflow Protection | Attempt to set cart quantity greater than available inventory stock. | Service throws validation error: "Requested quantity exceeds available stock". | **PASS** |
| **TC-ORD-01** | Transactional Checkout | Navigate to `/checkout`. Enter shipping address, select "Mock UPI", click "Confirm Order & Pay". | Order created with status `CONFIRMED`. Product stock decremented atomically. Cart cleared. Tracking ID assigned. | **PASS** |
| **TC-ORD-02** | Empty Cart Checkout Rejection | Attempt to POST to `/checkout/place` with an empty cart. | Rejected with validation exception: "Cannot checkout: Your cart is empty". | **PASS** |
| **TC-FUL-01** | Merchant Fulfillment (O2) | Log in as `seller1@monikamart.com`. View `/seller/orders`. Advance status from `CONFIRMED` to `SHIPPED`, then `DELIVERED`. | Order status transitions cleanly along workflow. State logged in `order_status_history`. | **PASS** |
| **TC-REV-01** | Product Review (F8) | As buyer who received delivered order, go to `/order-detail?id=...`. Select 5 stars, enter review comment, submit. | Review saved in DB. Average rating and review count recalculate on product page. | **PASS** |
| **TC-REV-02** | Review Fraud Prevention | Attempt to submit a review for an unpurchased or undelivered item. | Rejected by `ReviewService`: "Only verified buyers with delivered orders can submit a product review". | **PASS** |
| **TC-WISH-01** | Wishlist Bookmarking (O1) | Click "❤️ Save to Wishlist" on product. View `/wishlist`. Click "Move to Cart". | Product saved in wishlist; on "Move to Cart", added to cart and removed from wishlist. | **PASS** |
| **TC-ADM-01** | Admin Listing Moderation (F7) | Sign in as `admin@monikamart.com`. Open `/admin/listings`. Click "Deactivate" on a product. | Product marked `is_active=false`. Immediately hidden from public buyer search catalog. | **PASS** |
| **TC-AI-01** | Chatbot FAQ Query (O4) | Click floating AI launcher. Ask "What is your return policy?". | Chatbot answers with 7-day return policy and provides helpful navigation suggestions. | **PASS** |
| **TC-AI-02** | Chatbot Rate Limiting | Fire 11 consecutive questions in under 1 minute from the same session. | Chatbot gracefully throttles: "Rate limit exceeded: You can send at most 10 messages per minute". | **PASS** |
| **TC-HLT-01** | Health Check API | Send `GET /api/v1/health`. | Returns HTTP 200 with JSON: `{"status":"UP","db":"UP"}`. | **PASS** |
