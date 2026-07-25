# D1: Entity-Relationship (ER) Diagram
**Project:** MonikaMart E-Commerce Platform  
**Curriculum:** Anna University R2025, Semester 3 Capstone  
**Target Database:** MySQL / H2 Relational Database  

---

## 1. Architectural ER Model (Mermaid)

```mermaid
erDiagram
    USERS ||--o{ PRODUCTS : "lists / sells"
    USERS ||--o{ ORDERS : "places"
    USERS ||--o{ CART_ITEMS : "adds to"
    USERS ||--o{ REVIEWS : "writes"
    USERS ||--o{ WISHLIST_ITEMS : "bookmarks"
    
    PRODUCTS ||--o{ ORDER_ITEMS : "ordered in"
    PRODUCTS ||--o{ CART_ITEMS : "held in"
    PRODUCTS ||--o{ REVIEWS : "rated by"
    PRODUCTS ||--o{ WISHLIST_ITEMS : "saved in"

    ORDERS ||--|{ ORDER_ITEMS : "contains"
    ORDERS ||--o{ ORDER_STATUS_HISTORY : "tracked by"
    ORDERS ||--o{ REVIEWS : "generates"

    USERS {
        int id PK
        string name "Full Name"
        string email "Unique Email (RFC 5322)"
        string password_hash "jBCrypt Salted Hash"
        string role "BUYER | SELLER | ADMIN"
        string phone "Contact Phone"
        text address "Default Shipping Address"
        timestamp created_at "Registration Timestamp"
    }

    PRODUCTS {
        int id PK
        int seller_id FK "References USERS(id)"
        string name "Product Title"
        text description "Detailed Specifications"
        decimal price "DECIMAL(10,2) Currency"
        int stock_qty "Available Inventory"
        string category "Catalog Category"
        string image_url "Product Image Asset"
        boolean is_active "Moderation Visibility Status"
        timestamp created_at "Listing Timestamp"
    }

    ORDERS {
        int id PK
        int buyer_id FK "References USERS(id)"
        decimal total_amount "DECIMAL(10,2) Order Total"
        string status "PENDING | CONFIRMED | SHIPPED | DELIVERED | CANCELLED"
        text shipping_address "Physical Delivery Address"
        string payment_method "MOCK_UPI | MOCK_CARD | MOCK_NETBANKING"
        string payment_status "COMPLETED | FAILED | REFUNDED"
        string tracking_number "Tracking ID (MKM-timestamp)"
        timestamp created_at "Placement Timestamp"
        timestamp updated_at "Last State Change"
    }

    ORDER_ITEMS {
        int id PK
        int order_id FK "References ORDERS(id)"
        int product_id FK "References PRODUCTS(id)"
        int seller_id FK "References USERS(id)"
        int quantity "Purchased Units"
        decimal unit_price "DECIMAL(10,2) Locked Price"
        timestamp created_at "Record Timestamp"
    }

    CART_ITEMS {
        int id PK
        int buyer_id FK "References USERS(id)"
        int product_id FK "References PRODUCTS(id)"
        int quantity "Intended Units"
        timestamp created_at "Added Timestamp"
    }

    REVIEWS {
        int id PK
        int order_id FK "References ORDERS(id)"
        int product_id FK "References PRODUCTS(id)"
        int buyer_id FK "References USERS(id)"
        int rating "1 to 5 Star Integer"
        text comment "Customer Review Feedback"
        timestamp created_at "Review Timestamp"
    }

    WISHLIST_ITEMS {
        int id PK
        int user_id FK "References USERS(id)"
        int product_id FK "References PRODUCTS(id)"
        timestamp created_at "Saved Timestamp"
    }

    ORDER_STATUS_HISTORY {
        int id PK
        int order_id FK "References ORDERS(id)"
        string old_status "Previous State"
        string new_status "Transitioned State"
        text notes "Audit Log Note"
        int changed_by_user_id FK "Audit User Actor"
        timestamp created_at "Audit Timestamp"
    }
```

---

## 2. Relational Integrity & Constraints Analysis

1. **Monetary Precision:** All currency fields (`products.price`, `orders.total_amount`, `order_items.unit_price`) use `DECIMAL(10,2)` to prevent floating-point IEEE-754 rounding inaccuracies during financial checkout calculations.
2. **Cardinality & Relationships:**
   - A `User` (as Seller) owns `0..N` `Products`.
   - A `User` (as Buyer) places `0..N` `Orders` and `0..N` `Reviews`.
   - An `Order` contains `1..N` `OrderItems` and transitions through `1..N` `OrderStatusHistory` records.
   - A `Product` is referenced by `0..N` `OrderItems`, `0..N` `CartItems`, `0..N` `Reviews`, and `0..N` `WishlistItems`.
3. **Database Normalization:** Fully normalized to Third Normal Form (3NF). Historical unit prices are locked into `order_items.unit_price` so that subsequent price revisions by merchants do not mutate past transactional historical invoices.
4. **Referential Cascades:** Deleting a user cascade-removes their active cart items, while completed financial transactions maintain audit trails.
