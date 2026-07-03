# Module 37: Clean Architecture - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the core principle of Clean Architecture?
**Answer**:
The core principle is **The Dependency Rule**. It states that source code dependencies must *only* point inward, toward higher-level policies (the core domain). Outer layers (like Web, UI, and Database frameworks) can depend on inner layers (Use Cases and Entities), but an inner layer can *never* know anything about an outer layer. This ensures the business logic remains pure, highly testable, and completely framework-agnostic.

### Q2: Why is it an anti-pattern to use `@Entity` or `@Table` annotations from JPA inside your Domain Entities?
**Answer**:
Adding JPA or Hibernate annotations to Domain Entities forces the core business layer to depend on the database infrastructure layer. This violates the Dependency Rule. If the team decides to switch from a Relational Database to a NoSQL database, the core domain entities have to be rewritten. 
In Clean Architecture, Domain Entities should be pure Java POJOs. The infrastructure layer should have its own separate database entities (e.g., `UserDbEntity`) and use a mapping layer to convert between the database entity and the pure domain entity.

### Q3: Explain the role of "Ports and Adapters" in Clean Architecture.
**Answer**:
"Ports and Adapters" (also known as Hexagonal Architecture) is a specific implementation of Clean Architecture. 
- **Ports**: Are interfaces defined in the inner domain layer. They dictate how the outside world can interact with the application (Driving/Inbound Ports) and how the application needs to interact with the outside world (Driven/Outbound Ports, like an `OrderRepository` interface).
- **Adapters**: Are concrete implementations that live in the outermost layer. They translate data between the external world and the format required by the Ports. For example, a REST Controller is a Driving Adapter, and a MySQL Repository class is a Driven Adapter.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring a Monolithic Controller
**Problem**: An interviewer shows you a Spring Boot `@RestController` that receives an HTTP request, performs complex validation, applies business logic (calculating discounts), executes a JDBC query to save the order, and sends an email. Why is this bad, and how do you fix it using Clean Architecture?

**Solution Strategy**:
1. **Identify the violation**: The controller violates the Single Responsibility Principle and combines the Web Layer, Business Rules, and Database/Infrastructure layers into one class. It is completely untestable without spinning up a web server and a database.
2. **Extract the Domain**: Move the discount calculation and validation into a pure Java `Order` entity.
3. **Extract the Use Case**: Create an `OrderInteractor` (Service) that orchestrates the flow. It takes the `Order`, executes the business logic, and delegates saving/emailing.
4. **Define Outbound Ports**: Create interfaces for `OrderRepository` and `EmailService` inside the Use Case layer.
5. **Implement Adapters**: Move the JDBC code and Email code into classes that implement the outbound ports, placing them in the outermost Infrastructure layer.
6. **Controller Role**: The REST Controller is reduced to simply accepting the HTTP request, mapping it to a Command object, and passing it to the `OrderInteractor`.