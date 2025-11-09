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
    - Order status can be tracked (e.g., IN_PROGRESS, PAID, SHIPPED, DELIVERED)

* User & Role Management
    - Register new users
    - Assign roles to users for access control

* User Authentication
    - Simple login (email + hashed password)

# Main Entities
Entity      Description
Product     Contains id, name, unitPrice, stockQuantity, list of categories
Category    Contains id, name, list of products
Order       Contains id, orderDate, user, list of OrderLine, status
OrderLine   Links a product to an order with a quantity
User        Contains id, email, password (hashed), phone, address, and roles
Role        Contains id and name

# Key Endpoints

Endpoint                   Method  Description
/auth/register             POST    Create a new user account
/auth/login                POST    Log in

/products                  GET     List all products
/product                   POST    Create a product
/product/{id}              PUT     Update a product
/product/{id}              DELETE  Delete a product

/categories                GET     List all categories
/categorie                 POST    Create a category
/categorie/{id}            PUT     Update a category
/categorie/{id}            DELETE  Delete a category

/order                     POST    Create an order
/order/{id}                GET     Get an order by ID

/users                     GET     List all users
/user                      POST    Register a new user
/user/{id}                 PUT     Update a user
/user/{id}                 DELETE  Delete a user

# Prerequisites

* Java 17+
* Maven
* Relational database (e.g., MySQL, PostgreSQL) configured in `application.properties`

# Running the Project
mvn spring-boot:run

# Possible Future Improvements

* Implement full user authentication (JWT)
* Complete role and permission management

# Contact

For any questions or suggestions, feel free to contact me.
