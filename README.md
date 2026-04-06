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
Role        Contains id and name
User        Contains id, email, password (hashed), phone, address, and roles
Category    Contains id, name,
Product     Contains id, name, price, description, list of image URLs, stock available, list of categories
Order       Contains id, orderDate, user, list of OrderLine, status
OrderLine   Links a product to an order with a quantity

# Key Endpoints

Endpoint                    Method      Description
/api/auth/register          POST    Create a new user account
/api/auth/login             POST    Log in

/me                         GET     Get profil information
/me                         PUT     Change profil information
/me/password                PUT     Change password

/users                      GET     List all users
/users                      POST    Create a new user with roles
/users/{id}                 PUT     Update user information
/users/{id}                 DELETE  Delete user (only allower if there are no orders associated)

/products                   GET     List all products
/products                   POST    Create a product
/products/{id}              PUT     Update a product
/products/{id}              DELETE  Delete a product

/categories                 GET     List all categories
/categories                 POST    Create a category
/categories/{id}            PUT     Update a category
/categories/{id}            DELETE  Delete a category

/order                      POST    Create an order
/order/{id}                 GET     Get an order by ID

# Prerequisites

* Java 17+
* Maven
* Relational database (e.g., MySQL, PostgreSQL) configured in `application.properties`

# Running the Project
mvn spring-boot:run

# Possible Future Improvements

* Complete role and permission management
* Add full audit/logging of user actions
* Implement pagination and sorting for endpoints

# Contact

For any questions or suggestions, feel free to contact me.
