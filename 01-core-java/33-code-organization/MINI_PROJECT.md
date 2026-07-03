# Module 33: Code Organization - Mini Project

**Project Name**: Hexagonal Architecture Refactoring  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Refactor a tightly coupled, monolithic "Big Ball of Mud" application into a Clean, Hexagonal Architecture (Ports and Adapters), demonstrating the Dependency Inversion Principle.

## 📝 Requirements

### Core Features

1. **The Starting State (Mock it up)**:
   - Imagine a legacy class `OrderProcessor` that directly imports and instantiates a `MySqlDatabase` class to save an order, and an `AwsSesEmailService` class to send a confirmation.
   - The business logic is tangled with SQL queries and API calls.

2. **The Core Domain (Center of the Hexagon)**:
   - Create a clean `Order` entity (a pure Java POJO or record).
   - Create an `OrderService` (The Use Case). It should contain the core business logic (e.g., applying discounts). 
   - **Crucially**: The `OrderService` must NOT import any database or web-related classes.

3. **Defining the Ports (Interfaces)**:
   - **Driven Ports (Outbound)**: Create an interface `OrderRepository` (with `save(Order o)`) and an interface `NotificationService` (with `send(Order o)`). Place these in the Core Domain package.
   - **Driving Ports (Inbound)**: The `OrderService` itself acts as the inbound port for the outside world to interact with.

4. **Implementing the Adapters**:
   - **Driven Adapters**: Create an `Infrastructure` package. Implement `PostgresOrderRepository` (implementing `OrderRepository`) and `SmtpEmailAdapter` (implementing `NotificationService`).
   - **Driving Adapters**: Create a `Web` package. Implement a REST Controller that receives HTTP requests and calls the `OrderService`.

5. **Wiring it Together**:
   - Use Dependency Injection (e.g., Spring) or manual constructor injection in a Main class to pass the concrete Adapters into the `OrderService`.

---

## 💡 Solution Blueprint

1. **The Domain (Isolated)**:
   ```java
   package com.example.core.domain;
   public record Order(String id, double total) {}
   
   package com.example.core.ports.out;
   public interface OrderRepository { void save(Order order); }
   public interface NotificationService { void notify(Order order); }

   package com.example.core.usecase;
   public class OrderService {
       private final OrderRepository repo;
       private final NotificationService notifier;
       
       public OrderService(OrderRepository repo, NotificationService notifier) {
           this.repo = repo;
           this.notifier = notifier;
       }
       
       public void placeOrder(Order order) {
           // Business logic here
           repo.save(order);
           notifier.notify(order);
       }
   }
   ```

2. **The Adapters (Dependent on Domain)**:
   ```java
   package com.example.infrastructure.db;
   import com.example.core.ports.out.OrderRepository;
   
   public class SqlOrderRepository implements OrderRepository {
       public void save(Order order) { System.out.println("Saving to SQL"); }
   }
   ```