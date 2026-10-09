# MonikaMart Live Demo Walkthrough Script & Backup Video Guide
**Curriculum:** Anna University R2025 Semester 3 Capstone
**Target Window:** Final Review (October 10, 2026)
**Presenter:** Monika Raja
**Total Target Duration:** 5 to 7 minutes (Rehearsed Presentation)

---

## 🎬 Act 1: Introduction & Architecture (0:00 – 1:00)
- **Speaker Script:**  
  *"Good morning respected evaluators and faculty committee. I am Monika Raja presenting my Semester 3 Capstone project: **MonikaMart**. MonikaMart is a production-grade, multi-role e-commerce web platform built on classical Java Enterprise technologies: Java 17, Java Servlets 4.0, JDBC with HikariCP connection pooling, an H2/MySQL database, and an AI Shopping Concierge. Let us begin our live demonstration with the core buyer shopping journey."*
- **Action on Screen:**  
  Display the application homepage at `http://localhost:8080/products`. Point out the Lavender-themed catalog, category filter, and search bar.

---

## 🎬 Act 2: Buyer Journey — Search, Cart & Transactional Checkout (1:00 – 2:30)
- **Speaker Script:**  
  *"I will click the 'Sign In' button to open our clean login form. I will sign in using a registered Buyer account. The application regenerates the session ID immediately upon authentication to prevent session fixation."*
- **Action on Screen:**  
  Enter Buyer email and password on `/login` and click "Sign In".
- **Speaker Script:**  
  *"Now on the catalog, let's search for 'Sony'. We see the Sony WH-1000XM5 headphones with stock levels and customer ratings. I will add 1 unit to my cart, which confirms 'Product added to cart successfully!'. Moving to the Shopping Cart, we see real-time line-item and grand total calculations. Let's proceed to checkout."*
- **Action on Screen:**  
  Navigate to `/cart`, click "Proceed to Checkout". Enter delivery address and select "Mock UPI". Click "Confirm Order & Pay".
- **Speaker Script:**  
  *"Upon clicking confirm, the OrderService executes an atomic database transaction. The order is inserted, inventory is decremented with concurrency checks, the cart is cleared, and 'Order placed successfully!' is displayed with tracking number #MKM. Notice our visual 4-stage Order Status Stepper."*

---

## 🎬 Act 3: Merchant Hub & Order Status Workflow (2:30 – 3:45)
- **Speaker Script:**  
  *"Now let's switch personas to the merchant who sells this product. I will log out and sign in using a registered Seller account."*
- **Action on Screen:**  
  Log out and sign in with the Seller account via `/login`. Open `/seller/dashboard`.
- **Speaker Script:**  
  *"In the Seller Hub, the merchant sees real-time metrics: active listings, incoming orders, and seller-specific gross revenue. Under 'Incoming Orders', the seller views the order placed by our buyer. Through our order workflow state machine (Feature O2), the seller advances the order from CONFIRMED to SHIPPED, and subsequently to DELIVERED."*
- **Action on Screen:**  
  Advance order status to `SHIPPED`, then `DELIVERED`.

---

## 🎬 Act 4: Verified Buyer Reviews & Wishlist (3:45 – 4:45)
- **Speaker Script:**  
  *"Let's return to the buyer persona. Now that the order has reached DELIVERED status, the platform unlocks the Verified Buyer Review submission form (Feature F8). Unverified or undelivered purchases are strictly prevented from posting reviews."*
- **Action on Screen:**  
  Sign back in as the Buyer, open the Order Detail page. Select 5 stars, write feedback: *"Superb build quality and acoustics!"*, and submit.
- **Speaker Script:**  
  *"The review is instantly persisted and dynamically recalculates the product's average rating and review counter on the product catalog. Additionally, our Wishlist (Feature O1) allows saving items and transferring them directly into the cart with one click."*

---

## 🎬 Act 5: AI Shopping Concierge & Rate Limiting (4:45 – 6:00)
- **Speaker Script:**  
  *"Next, let's examine our AI Shopping Concierge (Feature O4). Located at the bottom right is our responsive floating chat widget. It is engineered with the Strategy design pattern, supporting both live Google Gemini LLM queries and an intelligent offline FAQ engine."*
- **Action on Screen:**  
  Click the floating launcher button. Click suggestion chip "Return Policy".
- **Speaker Script:**  
  *"The bot immediately answers detailing our 7-day return policy. If I ask 'Where is my order?', it guides the buyer directly to their tracking page. Notice our security and resilience safeguards: the API key is strictly server-side, identical queries are cached in-memory per session, and firing rapid requests demonstrates our sliding-window rate limit of 10 messages per minute."*

---

## 🎬 Act 6: Administrator Governance & Health API (6:00 – 7:00)
- **Speaker Script:**  
  *"Lastly, our single designated Administrator account (`monikaraja433@gmail.com`), authenticated strictly via the `ADMIN_PASSWORD` environment variable, provides platform oversight. The Admin dashboard displays total users, catalog products, platform revenue, and live activity notifications for logins, registrations, product actions, orders, and reviews. Admins can audit all registered accounts and moderate product listings with one click. Furthermore, our `/api/v1/health` endpoint reports `status: UP, db: UP` for zero-downtime uptime monitoring."*
- **Action on Screen:**  
  Sign in as `monikaraja433@gmail.com` using the environment-configured password, demonstrate `/admin/dashboard` activity feed, `/admin/listings` deactivate toggle, and open `/api/v1/health` in a browser tab.
- **Speaker Script:**  
  *"In conclusion, MonikaMart fulfills 100% of the Anna University R2025 Semester 3 Capstone objectives, passing all automated tests with zero security flaws. Thank you, and I look forward to your questions."*

---

## 📹 Backup Video Recording Guidance
- **Tool:** OBS Studio or Windows Game Bar (`Win + G`).
- **Resolution:** 1080p (1920x1080) at 30 fps or 60 fps.
- **Audio:** Clear microphone narration following the cues above.
- **Storage:** Export to MP4 format (`monikamart_demo_backup.mp4`) and upload to Google Drive or YouTube (Unlisted) as a backup submission link.
