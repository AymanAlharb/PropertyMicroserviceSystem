# PropertyMicroservicesSystem

A real estate microservices system built with Spring Boot, enabling buyers to purchase properties, sellers to list them, and brokers to manage transactions. The system includes secure authentication, search capabilities, messaging, and distributed architecture.

---

## Overview

This platform enables:
- **Buyers** to browse and purchase properties
- **Sellers** to list properties for sale
- **Brokers** to manage and monitor transactions

Built using a microservices architecture, each service is independently deployable and connected via REST and messaging.

---

## Microservices

- **AuthService** – User registration, login, and authentication via Keycloak (JWT-based)
- **PropertyService** – Manages property listings and related operations
- **SearchPropertyService** – Provides Elasticsearch-powered property search
- **TransactionService** – Handles multi-step property transactions between buyers and sellers
- **PaymentService** – Processes payments and handles internal transfers
- **NotificationService** – Sends notifications (e.g. email) using RabbitMQ
- **ApiGateway** – Routes external requests to internal services securely
- **ServiceRegistry** – Eureka server for service discovery

---

## Security

- Authentication & authorization via **Keycloak** and **JWT**
- Role-based access control (Buyer, Seller, Broker)

---

## Communication

- **Feign Clients** for synchronous RESTful communication
- **RabbitMQ** for asynchronous messaging between services

---

## Technologies Used

- Java 17 + Spring Boot 3.x
- Spring Cloud (Eureka, OpenFeign)
- RabbitMQ (Message Broker)
- Elasticsearch (Search Engine)
- Keycloak (Authentication Server)
- MySQL (Databases per service)
- Docker (for local development)

