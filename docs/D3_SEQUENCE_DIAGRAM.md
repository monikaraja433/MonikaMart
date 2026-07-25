# D3: Sequence Diagram — Place Order & Checkout Flow
**Project:** MonikaMart E-Commerce Platform  
**Workflow:** End-to-End Cart Checkout, Inventory Reservation, and Transactional Fulfillment  
**Curriculum:** Anna University R2025, Semester 3 Capstone  

---

## 1. Checkout Sequence Diagram (Mermaid)

```mermaid
sequenceDiagram
    autonumber
    actor Buyer as 👤 Buyer
    participant Browser as 🌐 Client Browser / UI
    participant Filter as 🛡️ AuthFilter
    participant Servlet as ⚙️ OrderServlet
    participant OrdSvc as 💼 OrderService
    participant CartDAO as 🛒 CartDAO
    participant ProdDAO as 📦 ProductDAO
    participant OrdDAO as 💳 OrderDAO
    participant Database as 🗄️ Database (H2 / HikariCP)

    Buyer->>Browser: Fill Shipping Address & Click "Confirm Order & Pay"
    Browser->>Filter: HTTP POST /checkout/place (Session Cookie, address, paymentMethod)
    
    Filter->>Filter: Verify valid HttpSession & Buyer Role
    alt Session Invalid / Unauthenticated
        Filter-->>Browser: Redirect 302 to /login?redirect=/checkout
    else Session Authorized
        Filter->>Servlet: Forward to doPost(req, resp)
    end

    Servlet->>OrdSvc: checkout(buyerId, shippingAddress, paymentMethod)
    
    %% Input Validation
    OrdSvc->>OrdSvc: Validate non-empty address & format
    OrdSvc->>CartDAO: findByBuyerId(buyerId)
    CartDAO->>Database: SELECT cart items joined with products
    Database-->>CartDAO: List<CartItem>
    CartDAO-->>OrdSvc: List<CartItem>

    alt Cart Is Empty
        OrdSvc-->>Servlet: throw ValidationException("Cannot checkout: Cart is empty")
        Servlet-->>Browser: Re-render checkout.jsp with field error message
    end

    %% Stock Pre-Verification
    loop For each item in cart
        OrdSvc->>ProdDAO: findById(productId)
        ProdDAO->>Database: SELECT * FROM products WHERE id = ?
        Database-->>ProdDAO: Product
        ProdDAO-->>OrdSvc: Product
        OrdSvc->>OrdSvc: Check if active == true AND stockQty >= requestedQty
        alt Insufficient Inventory
            OrdSvc-->>Servlet: throw ValidationException("Insufficient stock for Product")
            Servlet-->>Browser: Re-render checkout.jsp with out-of-stock warning
        end
    end

    %% Atomic Transaction Execution
    OrdSvc->>OrdDAO: createOrderWithItems(Order, List<OrderItem>)
    OrdDAO->>Database: Connection conn = DBUtil.getConnection(); conn.setAutoCommit(false);
    
    Note over OrdDAO,Database: BEGIN ATOMIC TRANSACTION
    
    OrdDAO->>Database: INSERT INTO orders (...) VALUES (?, ?, ?, ...)
    Database-->>OrdDAO: Generated order_id (#MKM-xxx)

    loop For each item
        OrdDAO->>Database: UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?
        OrdDAO->>Database: INSERT INTO order_items (order_id, product_id, seller_id, qty, price) VALUES (...)
    end

    OrdDAO->>Database: INSERT INTO order_status_history (order_id, CONFIRMED, notes) VALUES (...)
    OrdDAO->>Database: conn.commit();
    Note over OrdDAO,Database: TRANSACTION COMMITTED
    
    OrdDAO-->>OrdSvc: Created Order object (#MKM-xxx)
    
    %% Cart Clearance
    OrdSvc->>CartDAO: clearCart(buyerId)
    CartDAO->>Database: DELETE FROM cart_items WHERE buyer_id = ?
    Database-->>CartDAO: Rows deleted
    CartDAO-->>OrdSvc: Cart Cleared
    
    OrdSvc-->>Servlet: Return confirmed Order
    Servlet-->>Browser: Redirect 302 to /order-detail?id=MKM-xxx&success=OrderPlaced
    Browser->>Buyer: Render order-detail.jsp with Tracking Stepper (CONFIRMED)
```

---

## 2. Technical Guarantees in Place-Order Flow

1. **ACID Atomicity:** The entire checkout sequence operates under a manual JDBC transaction (`conn.setAutoCommit(false)`). If database connectivity drops or any single product lacks sufficient inventory, `conn.rollback()` executes immediately, preventing partial inventory decrements.
2. **Race Condition Prevention:** The update query enforces concurrency protection at the database engine level:
   ```sql
   UPDATE products SET stock_qty = stock_qty - ? WHERE id = ? AND stock_qty >= ?;
   ```
   If another buyer purchases the last unit milliseconds earlier, `affectedRows == 0`, triggering an immediate rollback.
3. **Session Replay Immunity:** Because the cart is cleared transactionally upon successful order creation, pressing browser refresh or re-submitting will detect an empty cart and reject duplicate double-charges.
