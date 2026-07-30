# C4 Architecture Model — Step-by-Step Guide

## 1. Context Diagram (Level 1)
- System: "Online Banking System"
- Actors: Customer, Admin
- External: Payment Gateway, Fraud Detection

## 2. Container Diagram (Level 2)
- Web App (React), Mobile App, API (Spring Boot), Database (PostgreSQL), Message Queue (Kafka)

## 3. Component Diagram (Level 3)
- API container broken into: `AccountController`, `PaymentService`, `TransactionRepository`

## 4. Code Diagram (Level 4)
- Class-level: the actual Java interfaces and classes shown in this lab.

## 5. Documentation
- Describe each element with technology, responsibilities, and interactions.

## Build & Run
```bash
javac --enable-preview -source 21 -d out src/com/architecture/deep/lab06/*.java
java --enable-preview -cp out com.architecture.deep.lab06.C4Lab
```
