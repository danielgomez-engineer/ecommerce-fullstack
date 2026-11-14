CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       user_type VARCHAR(31) NOT NULL,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       full_name VARCHAR(255) NOT NULL,
                       role VARCHAR(30) NOT NULL,
                       phone VARCHAR(30),
                       address VARCHAR(255),
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP
);

CREATE TABLE categories (
                            id BIGSERIAL PRIMARY KEY,
                            name VARCHAR(100) NOT NULL UNIQUE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at TIMESTAMP
);

CREATE TABLE products (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(150) NOT NULL,
                          description VARCHAR(1000),
                          price NUMERIC(12, 2) NOT NULL,
                          stock INTEGER NOT NULL,
                          image_url VARCHAR(500),
                          category_id BIGINT NOT NULL REFERENCES categories(id),
                          created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP
);

CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_products_price ON products(price);

CREATE TABLE orders (
                        id BIGSERIAL PRIMARY KEY,
                        status VARCHAR(30) NOT NULL,
                        total NUMERIC(12, 2) NOT NULL,
                        user_id BIGINT NOT NULL REFERENCES users(id),
                        created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP
);

CREATE INDEX idx_orders_user ON orders(user_id);

CREATE TABLE order_items (
                             id BIGSERIAL PRIMARY KEY,
                             order_id BIGINT NOT NULL REFERENCES orders(id),
                             product_id BIGINT NOT NULL REFERENCES products(id),
                             quantity INTEGER NOT NULL,
                             unit_price NUMERIC(12, 2) NOT NULL,
                             subtotal NUMERIC(12, 2) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP
);