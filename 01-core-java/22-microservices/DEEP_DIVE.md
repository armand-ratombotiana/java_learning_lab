# Module 22: Microservices Concepts - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-21  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Monolithic vs Microservices Architecture](#architecture)
2. [Key Principles of Microservices](#principles)
3. [Service Discovery and API Gateways](#discovery)
4. [Inter-Service Communication](#communication)
5. [Data Management in Microservices](#data)

---

## 1. Monolithic vs Microservices Architecture <a name="architecture"></a>
- **Monolithic Architecture**: All components of the application (UI, business logic, data access) are packaged and deployed as a single unit. It's simpler to develop initially but hard to scale and maintain as it grows.
- **Microservices Architecture**: The application is divided into a collection of small, independent, and loosely coupled services. Each service runs in its own process and communicates via lightweight mechanisms (like HTTP/REST).

---

## 2. Key Principles of Microservices <a name="principles"></a>
- **Single Responsibility Principle**: Each service should focus on one specific business capability.
- **Independent Deployment**: Services can be updated and deployed without affecting the rest of the system.
- **Decentralized Data Management**: Each service should manage its own database to ensure loose coupling.
- **Design for Failure**: Services must be resilient and handle failures of dependencies gracefully (e.g., using Circuit Breakers).

---

## 3. Service Discovery and API Gateways <a name="discovery"></a>
- **Service Discovery**: Allows services to find and communicate with each other without hardcoding IP addresses (e.g., Eureka, Consul).
- **API Gateway**: Acts as a single entry point for all clients. It handles routing, composition, authentication, and rate limiting (e.g., Spring Cloud Gateway).

---

## 4. Inter-Service Communication <a name="communication"></a>
- **Synchronous**: Typically implemented using HTTP/REST or gRPC. The caller waits for the response. Can lead to cascading failures if not careful.
- **Asynchronous**: Implemented using message brokers (Kafka, RabbitMQ). The caller does not wait, improving system resilience and decoupling.

---

## 5. Data Management in Microservices <a name="data"></a>
- **Database per Service**: Prevents services from being tightly coupled via a shared database schema.
- **Saga Pattern**: Used to manage distributed transactions across multiple services. Instead of two-phase commits (2PC), a Saga uses a sequence of local transactions and compensating transactions for rollbacks.
- **CQRS (Command Query Responsibility Segregation)**: Separates read and write operations into different models to optimize performance and scalability.