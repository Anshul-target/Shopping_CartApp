# Shopping Cart App 🛒

A microservices-based e-commerce application built with **Spring Boot** and **Spring Cloud**. It includes core services, security, service discovery, and admin dashboard.

---

## 🚀 Architecture

- **Product Catalog Service** – Manages product data (MongoDB)    https://github.com/Anshul-target/ProductCatalog
- **Cart Service** – Handles users’ shopping carts (MongoDB)    https://github.com/Anshul-target/CartService
- **API Gateway** – Routes and secures all incoming requests
- **Eureka Discovery Server** – Manages dynamic service registration https://github.com/Anshul-target/Eureka-Server
- **Spring Cloud Config Server** – Centralized configuration management  https://github.com/Anshul-target/application-config
- **Frontend App** – Entry point coordinating backend services

---

## 🧰 Tech Stack

- Java & Spring Boot
- Spring Cloud Components:
  - Eureka Server (Service Discovery)
  - API Gateway
  - Config Server
- Databases:
  - MySQL (Product Catalog)
  - MongoDB (Cart Service)
- Spring Security (basic auth + role-based access control)
- Deployment via **Railway**

---

## 🧪 Features

- Full microservice architecture
- Service registration and discovery
- Routing and filtering via API Gateway
- Centralized configuration with Config Server
-  (SQL + NoSQL)
- Basic authentication and separate access for admin and users
- Admin dashboard for CRUD management of products

---

## 🛠️ Getting Started

### Prerequisites

- Java 17+
- MySQL & MongoDB instances
- Git & Maven

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/Anshul-target/Shopping_CartApp.git
   cd Shopping_CartApp
