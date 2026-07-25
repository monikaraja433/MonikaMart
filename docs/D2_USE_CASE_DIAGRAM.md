# D2: Unified Use Case Diagram
**Project:** MonikaMart E-Commerce Platform  
**Curriculum:** Anna University R2025, Semester 3 Capstone  
**Actors:** Buyer, Seller, System Administrator, AI Chatbot Concierge  

---

## 1. System Use Case Diagram (Mermaid)

```mermaid
flowchart LR
    subgraph Actors
        Buyer["👤 Buyer"]
        Seller["🏪 Seller"]
        Admin["⚙️ Administrator"]
        AI["🤖 AI Assistant"]
    end

    subgraph Authentication_Module ["Authentication & Session (F1)"]
        UC_Reg["Register Account (Buyer/Seller)"]
        UC_Log["Login & Session Regeneration"]
        UC_Out["Logout & Invalidation"]
    end

    subgraph Catalog_Module ["Catalog & Search (F2, F3)"]
        UC_Browse["Browse Products by Category (F3)"]
        UC_Search["Search by Keyword & Sort (F3)"]
        UC_Listings["Create / Edit / Delete Listings (F2)"]
    end

    subgraph Commerce_Module ["Cart & Checkout (F4, F5, O1)"]
        UC_Cart["Manage Shopping Cart Items (F4)"]
        UC_Wishlist["Bookmark to Wishlist (O1)"]
        UC_Checkout["Place Order with Mock Payment (F5)"]
    end

    subgraph Fulfillment_Module ["Orders & Feedback (F6, F8, O2)"]
        UC_OrderHistory["View Past Orders & Status (F6-Buyer)"]
        UC_IncomingOrders["Fulfill Incoming Orders (F6-Seller)"]
        UC_StatusWorkflow["Advance Order Status (O2)"]
        UC_Review["Submit Star Rating & Review (F8)"]
    end

    subgraph Admin_Module ["Administration & Moderation (F7)"]
        UC_UserDir["Audit User Accounts (F7)"]
        UC_Moderate["Moderate Catalog Listings (F7)"]
        UC_Metrics["View Global Sales & Revenue"]
    end

    subgraph AI_Chatbot_Module ["AI Assistant Service (O4)"]
        UC_Chat["Ask Domain FAQ & Product Inquiries (O4)"]
        UC_Suggest["Click Intelligent Quick Chips"]
    end

    %% Buyer Connections
    Buyer --> UC_Reg
    Buyer --> UC_Log
    Buyer --> UC_Out
    Buyer --> UC_Browse
    Buyer --> UC_Search
    Buyer --> UC_Cart
    Buyer --> UC_Wishlist
    Buyer --> UC_Checkout
    Buyer --> UC_OrderHistory
    Buyer --> UC_Review
    Buyer --> UC_Chat

    %% Seller Connections
    Seller --> UC_Reg
    Seller --> UC_Log
    Seller --> UC_Out
    Seller --> UC_Listings
    Seller --> UC_IncomingOrders
    Seller --> UC_StatusWorkflow

    %% Admin Connections
    Admin --> UC_Log
    Admin --> UC_Out
    Admin --> UC_UserDir
    Admin --> UC_Moderate
    Admin --> UC_Metrics

    %% AI Assistant Interactions
    AI --> UC_Chat
    AI --> UC_Suggest
```

---

## 2. Actor Privilege & Security Boundary Specification

| Actor | Target Role in DB | Accessible Portals | Enforced Boundary Checks |
| :--- | :--- | :--- | :--- |
| **Buyer** | `BUYER` | `/products`, `/cart/*`, `/checkout/*`, `/orders/*`, `/wishlist/*`, `/reviews/add`, `/api/chat` | Cannot access merchant inventory forms or administrative system tables. Reviews restricted to verified delivered orders. |
| **Seller** | `SELLER` | `/seller/dashboard`, `/seller/products`, `/seller/orders`, `/seller/status-update` | Can only view, mutate, or fulfill products and orders containing items manufactured/sold by their own `seller_id`. |
| **Administrator** | `ADMIN` | `/admin/dashboard`, `/admin/users`, `/admin/listings`, `/admin/moderate` | Seed account only. Has global governance over listings, users, and transactions. |
| **Guest / Public** | `Unauthenticated` | `/login`, `/register`, `/products`, `/product-detail`, `/api/v1/health` | Read-only access to catalog. Write mutations and cart operations require authenticated HTTP session. |
