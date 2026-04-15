# Product & Order Management API

## Description

This Spring Boot API provides the core backend functionality for an e-commerce application.

It allows management of:

- Users and authentication (JWT-based)
- Roles and access control
- Products and categories
- Orders and order items

The architecture follows a layered design:
Controller → Service → Repository, with DTOs and Mappers for data separation.

---

## Features

### Product Management

- Create, update, delete products (admin)
- Retrieve active products for public catalog
- Retrieve products with visibility filters (admin)
- Products can belong to multiple categories
- SKU generation for products

### Category Management

- Create, update, delete categories (admin)
- Retrieve all categories (public)

### Order Management

- Create orders with product items and quantities
- Track order status (CREATED, PAID, SHIPPED, DELIVERED)
- Retrieve user orders

### User Management

- User registration
- Profile retrieval and update
- Password change functionality

### Authentication & Security

- Login with JWT token generation
- Password encryption
- Role-based access control (USER / ADMIN)
- Stateless authentication

---

## Prerequisites

Before running the project, ensure you have installed:

- Java 17+
- Maven 3+
- A relational database (MySQL / PostgreSQL recommended)

Database configuration is done in [application.properties](/src/main/resources/application.properties).

---

## Installation

Clone the repository:

> git clone <https://github.com/v-derperces/springboot-api-ecommerce>

Move into the project directory:

> cd <ecommerce>

Install dependencies and build the project:

> mvn clean install

---

## Running the Application

Start the Spring Boot application:

> mvn spring-boot:run

The application will start on the port defined in application.properties.

By default: http://localhost:8080

---

## API Documentation

Once the application is running, the API documentation is available at: http://localhost:8080/swagger-ui/index.html

Swagger provides:

- Interactive API exploration
- Request/response testing
- Endpoint descriptions

---

## Project Structure

controller/ → REST endpoints<br>
service/ → Business logic<br>
repository/ → Database access layer<br>
model/ → JPA entities<br>
dto/ → Request and response objects<br>
mapper/ → Entity <-> DTO conversion<br>
security/ → JWT and authentication logic

---

## Security Overview

- JWT-based authentication
- Public endpoints:
    - Product listing
    - Category listing
    - Authentication endpoints
- Protected endpoints:
    - Admin product/category management
    - User profile management
    - Order management (user and admin)
- Passwords are securely hashed

---

## API Overview

### Authentication

- POST /api/v1/auth/register
- POST /api/v1/auth/login

### Users

- GET /api/v1/users
- PUT /api/v1/users
- PUT /api/v1/users/password

### Products

- GET /api/v1/products
- GET /api/v1/products/{id}
- POST /api/v1/admin/products
- PUT /api/v1/admin/products/{id}
- DELETE /api/v1/admin/products/{id}

### Orders

- POST /api/v1/orders
- POST /api/v1/orders/{orderId}/pay
- POST /api/v1/orders/{orderId}/cancel
- GET /api/v1/orders
- GET /api/v1/orders/{orderId}
- GET /api/v1/admin/orders
- GET /api/v1/admin/orders/{orderId}
- POST /api/v1/admin/orders/{orderId}/cancel
- PATCH /api/v1/admin/orders/{orderId}

### Categories

- GET /api/v1/categories
- GET /api/v1/categories/{id}
- POST /api/v1/admin/categories
- PUT /api/v1/admin/categories/{id}
- DELETE /api/v1/admin/categories/{id}

---

## Possible Future Improvements

The current version covers core e-commerce features, but several improvements are planned:

- Payment integration (Stripe, etc.)
- Notification system (email for authentication, orders and shipping updates)
- Advanced inventory management (stock reservation, concurrency handling)
- Order tracking timeline and history auditing
- Caching layer for performance optimization (Redis)

---

## Contact

v.derperces-dev@outlook.com
