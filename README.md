# Full-Stack E-Commerce

## Project Overview

A full-stack e-commerce application with a Spring Boot REST API and a React single-page frontend. Customers can browse and filter a product catalog, manage a persistent shopping cart, place transactional orders and review their order history. Administrators can create, update and delete products.

Key capabilities:

- Stateless JWT authentication with role-based authorization (`CUSTOMER` and `ADMIN`).
- Product catalog with pagination and multi-criteria search (name, category, price range).
- Transactional checkout with stock validation and deduction.
- Order history per customer with a historical price snapshot for every line item.
- Interactive API documentation through Swagger UI.

## Tech Stack

### Backend

- Java 21
- Spring Boot 4.1.2
- Spring Security (stateless JWT)
- Spring Data JPA / Hibernate
- Flyway (schema migrations and seed data)
- PostgreSQL 16
- MapStruct and Lombok
- SpringDoc OpenAPI (Swagger)
- JUnit 5, MockMvc and JaCoCo (at least 80% line coverage enforced)

### Frontend

- React 19 and TypeScript
- Vite
- Redux Toolkit
- React Router v7
- Axios (with JWT interceptors)
- Material UI
- React Hook Form and Zod
- Vitest and React Testing Library

## Architecture & Key Design Decisions

### Monorepo layout

```
.
├── backend/              Spring Boot application (Gradle)
├── frontend/             React + Vite application
├── docker-compose.yml    PostgreSQL 16 for local development
├── .env.example          Environment variable template
└── README.md
```

### Feature-based modules

The backend is organized by feature rather than by technical layer, with packages `auth`, `user`, `product`, `order`, `shared` and `security`. Each feature owns its controllers, services, repositories, DTOs and mappers.

### JPA inheritance

- `BaseEntity` is a `@MappedSuperclass` that provides auditing fields shared by all entities.
- `User` uses `SINGLE_TABLE` inheritance with `Customer` and `Admin` subclasses, discriminated by role.

### Orders and stock

- Each `OrderItem` stores a snapshot of the unit price at purchase time, so historical orders are unaffected by later price changes.
- Order creation runs in a single transaction: stock is validated and deducted atomically, and the whole operation rolls back if any item cannot be fulfilled.

### Product search

Product filtering is built dynamically with JPA `Specification`, combining name, category and price range criteria only when they are provided, together with pagination.

### API conventions

- Every response is wrapped in a standardized envelope, `GeneralResponse<T>`.
- A centralized `GlobalExceptionHandler` maps domain and validation errors to consistent HTTP responses.

## Getting Started (Local Setup)

### Prerequisites

- Java 21
- Node.js 20 or newer
- Docker and Docker Compose

### Step 1: Environment setup

```bash
cp .env.example .env
```

Adjust the values in `.env` if needed (database credentials, JWT secret, allowed CORS origins).

### Step 2: Start PostgreSQL

```bash
docker compose up -d
```

The database is exposed on `localhost:5435`.

### Step 3: Run the backend

```bash
cd backend
./gradlew bootRun
```

The API runs on `http://localhost:8083` by default (override with the `SERVER_PORT` environment variable). Flyway applies the schema migrations and seed data automatically on startup.

### Step 4: Run the frontend

```bash
cd frontend
npm install
npm run dev
```

The app runs on `http://localhost:5173`. The API base URL is configured with `VITE_API_URL` (see `frontend/.env`) and must match the backend port.

## Demo Credentials

| Role     | Email               | Password   |
| -------- | ------------------- | ---------- |
| Admin    | `admin@demo.com`    | `password` |
| Customer | `customer@demo.com` | `password` |

## API Documentation & Endpoints

Swagger UI is available at `http://localhost:8083/swagger-ui.html`. Use the **Authorize** button and paste a JWT obtained from `/api/v1/auth/login` to call protected endpoints.

| Area       | Method and path                | Access             | Description                               |
| ---------- | ------------------------------ | ------------------ | ----------------------------------------- |
| Auth       | `POST /api/v1/auth/register`   | Public             | Register a new customer                   |
| Auth       | `POST /api/v1/auth/login`      | Public             | Authenticate and receive a JWT            |
| Auth       | `GET /api/v1/auth/me`          | Authenticated      | Current user profile                      |
| Products   | `GET /api/v1/products`         | Public             | Search and paginate products              |
| Products   | `GET /api/v1/products/{id}`    | Public             | Product details                           |
| Products   | `POST /api/v1/products`        | Admin              | Create a product                          |
| Products   | `PUT /api/v1/products/{id}`    | Admin              | Update a product                          |
| Products   | `DELETE /api/v1/products/{id}` | Admin              | Delete a product                          |
| Categories | `GET /api/v1/categories`       | Public             | List categories                           |
| Orders     | `POST /api/v1/orders`          | Authenticated      | Create an order and deduct stock          |
| Orders     | `GET /api/v1/orders/me`        | Authenticated      | Paginated order history of the caller     |
| Orders     | `GET /api/v1/orders/{id}`      | Authenticated      | Order details                             |

The product search endpoint accepts the query parameters `name`, `categoryId`, `minPrice`, `maxPrice`, `page` and `size`.

## Running Tests & Coverage

### Backend

```bash
cd backend
./gradlew check
```

Runs the JUnit 5 and MockMvc suites and verifies the JaCoCo minimum of 80% line coverage.

### Frontend

```bash
cd frontend
npm test
```

Runs the Vitest and React Testing Library suites once.
