-- Demo Users (password: "password")
INSERT INTO users (user_type, email, password, full_name, role, phone, address) VALUES
                                                                                    ('ADMIN', 'admin@demo.com', '$2a$10$YqqCsNKNd9nSWlp4h0SQb.fJDe4YeudrEYuCWRPOdZdgSsTfwhrSq', 'Admin User', 'ADMIN', NULL, NULL),
                                                                                    ('CUSTOMER', 'customer@demo.com', '$2a$10$YqqCsNKNd9nSWlp4h0SQb.fJDe4YeudrEYuCWRPOdZdgSsTfwhrSq', 'Jane Customer', 'CUSTOMER', '+57 3001234567', '123 Main St');

-- Categories
INSERT INTO categories (name) VALUES
                                  ('Electronics'),
                                  ('Books'),
                                  ('Clothing'),
                                  ('Home'),
                                  ('Sports');

-- Products (12 products across categories)
INSERT INTO products (name, description, price, stock, image_url, category_id) VALUES
                                                                                   ('Wireless Noise-Cancelling Headphones', 'Over-ear Bluetooth headphones with 30h battery life', 149.99, 25, 'https://placehold.co/400x300?text=Headphones', (SELECT id FROM categories WHERE name = 'Electronics')),
                                                                                   ('Mechanical Keyboard RGB', 'Compact 75% mechanical keyboard with tactile switches', 89.50, 40, 'https://placehold.co/400x300?text=Keyboard', (SELECT id FROM categories WHERE name = 'Electronics')),
                                                                                   ('4K USB-C Monitor 27"', 'IPS display with HDR10 and 65W USB-C power delivery', 329.00, 12, 'https://placehold.co/400x300?text=Monitor', (SELECT id FROM categories WHERE name = 'Electronics')),
                                                                                   ('Clean Architecture Book', 'A Craftsman Guide to Software Structure and Design', 38.00, 50, 'https://placehold.co/400x300?text=Clean+Arch', (SELECT id FROM categories WHERE name = 'Books')),
                                                                                   ('Designing Data-Intensive Applications', 'The big ideas behind reliable, scalable, and maintainable systems', 45.00, 35, 'https://placehold.co/400x300?text=DDIA+Book', (SELECT id FROM categories WHERE name = 'Books')),
                                                                                   ('Cotton Crewneck T-Shirt', '100% organic heavyweight cotton t-shirt', 24.99, 100, 'https://placehold.co/400x300?text=T-Shirt', (SELECT id FROM categories WHERE name = 'Clothing')),
                                                                                   ('Waterproof Lightweight Jacket', 'Breathable windbreaker jacket for everyday outdoor wear', 79.00, 30, 'https://placehold.co/400x300?text=Jacket', (SELECT id FROM categories WHERE name = 'Clothing')),
                                                                                   ('Ceramic Pour-Over Coffee Maker', 'Handcrafted dripper with reusable stainless steel filter', 34.50, 20, 'https://placehold.co/400x300?text=Coffee+Maker', (SELECT id FROM categories WHERE name = 'Home')),
                                                                                   ('Ergonomic Desk Lamp', 'LED task lamp with adjustable color temperature', 42.00, 18, 'https://placehold.co/400x300?text=Desk+Lamp', (SELECT id FROM categories WHERE name = 'Home')),
                                                                                   ('Adjustable Dumbbell Set', 'Pair of compact dumbbells adjustable from 5 to 25 lbs', 119.00, 15, 'https://placehold.co/400x300?text=Dumbbells', (SELECT id FROM categories WHERE name = 'Sports')),
                                                                                   ('Non-Slip Yoga Mat', 'High-density eco-friendly exercise mat with carrying strap', 29.99, 45, 'https://placehold.co/400x300?text=Yoga+Mat', (SELECT id FROM categories WHERE name = 'Sports')),
                                                                                   ('Insulated Stainless Water Bottle', '32oz vacuum insulated bottle keeps drinks cold 24h', 22.00, 60, 'https://placehold.co/400x300?text=Water+Bottle', (SELECT id FROM categories WHERE name = 'Sports'));