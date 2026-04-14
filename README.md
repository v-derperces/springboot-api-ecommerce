# Product & Order Management API

## Description

This Spring Boot API manages the core functionalities of an e-commerce application.
It allows managing users, their roles, products, categories, and orders.

## Main Features

* Product Management
    - Full CRUD (Create, Read, Update, Delete)
    - Products can belong to multiple categories

* Category Management
    - Full CRUD for product categories

* Order Management
    - Create orders containing a list of products with quantities
    - Order status can be tracked (e.g., CREATED, PAID, SHIPPED, DELIVERED)

* User & Role Management
    - Register new users
    - Assign roles to users for access control

* User Authentication
    - Simple login (email + hashed password)

# Prerequisites

* Java 17+
* Maven
* Relational database (e.g., MySQL, PostgreSQL) configured in `application.properties`

# Running the Project
mvn spring-boot:run

# Contact

For any questions or suggestions, feel free to contact me.
