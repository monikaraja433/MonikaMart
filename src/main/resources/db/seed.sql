-- MonikaMart Demo Seed Data
-- Demo User accounts are initialized in DBUtil with live BCrypt hashing:
-- admin@monikamart.com -> Admin@123 (Role: ADMIN)
-- seller1@monikamart.com -> Seller@123 (Role: SELLER)
-- seller2@monikamart.com -> Seller@123 (Role: SELLER)
-- buyer1@monikamart.com -> Buyer@123 (Role: BUYER)
-- buyer2@monikamart.com -> Buyer@123 (Role: BUYER)

INSERT INTO products (id, seller_id, name, description, price, stock_qty, category, image_url, is_active) VALUES
(1, 2, 'Sony WH-1000XM5 Wireless Headphones', 'Industry Leading Noise Canceling with 2 processors and 8 microphones. Up to 30-hour battery life and ultra-comfortable lightweight design.', 29999.00, 25, 'Electronics', 'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=600', TRUE),
(2, 2, 'Logitech MX Master 3S Wireless Mouse', 'Ergonomic performance wireless mouse with 8K DPI any-surface tracking and quiet clicks.', 8495.00, 40, 'Electronics', 'https://images.unsplash.com/photo-1615663245857-ac93bb7c39e7?w=600', TRUE),
(3, 2, 'Mechanical Gaming Keyboard RGB', 'Customizable RGB backlighting, tactile blue switches, detachable USB-C braided cable.', 4599.00, 15, 'Electronics', 'https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=600', TRUE),
(4, 3, 'Clean Code: A Handbook of Agile Software Craftsmanship', 'By Robert C. Martin. Even bad code can function, but if code isn''t clean, it can bring a development organization to its knees.', 2450.00, 50, 'Books', 'https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=600', TRUE),
(5, 3, 'Design Patterns: Elements of Reusable Object-Oriented Software', 'Classic Gang of Four reference work on cataloging reusable design patterns in modern software architecture.', 3150.00, 30, 'Books', 'https://images.unsplash.com/photo-1532012164546-f432f2e3777f?w=600', TRUE),
(6, 3, 'Premium Hardcover Dotted Journal', 'A5 Size 160 GSM ultra-thick bamboo paper notebook with pen loop, pocket, and elastic ribbon.', 699.00, 100, 'Stationery', 'https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=600', TRUE),
(7, 2, 'Dell 27-inch 4K UHD USB-C Hub Monitor', 'Brilliant 4K resolution with IPS technology, 99% sRGB color coverage, 65W power delivery USB-C hub.', 34990.00, 12, 'Electronics', 'https://images.unsplash.com/photo-1527443224154-c4a3942d3acf?w=600', TRUE),
(8, 3, 'Ergonomic Memory Foam Lumbar Cushion', 'Premium high-density memory foam cushion with breathable mesh cover for posture support.', 1899.00, 45, 'Home & Office', 'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=600', TRUE);

INSERT INTO orders (id, buyer_id, total_amount, status, shipping_address, payment_method, payment_status, created_at) VALUES
(1, 4, 32449.00, 'DELIVERED', '42 Tech Avenue, Guindy, Chennai, Tamil Nadu', 'MOCK_PAYMENT', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '5' DAY),
(2, 4, 8495.00, 'SHIPPED', '42 Tech Avenue, Guindy, Chennai, Tamil Nadu', 'MOCK_PAYMENT', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '2' DAY),
(3, 5, 2450.00, 'CONFIRMED', '15 Green Park Road, Madurai, Tamil Nadu', 'MOCK_PAYMENT', 'COMPLETED', CURRENT_TIMESTAMP - INTERVAL '1' DAY);

INSERT INTO order_items (id, order_id, product_id, seller_id, quantity, unit_price) VALUES
(1, 1, 1, 2, 1, 29999.00),
(2, 1, 4, 3, 1, 2450.00),
(3, 2, 2, 2, 1, 8495.00),
(4, 3, 4, 3, 1, 2450.00);

INSERT INTO reviews (id, order_id, product_id, buyer_id, rating, comment) VALUES
(1, 1, 1, 4, 5, 'Exceptional noise cancellation and clarity! Totally worth the price for programming sessions.'),
(2, 1, 4, 4, 5, 'Every software engineering student should read Clean Code. Delivered in mint condition.');
