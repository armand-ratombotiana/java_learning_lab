# Module 56: Jakarta EE - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Compare Jakarta EE and Spring Boot. When would you use one over the other?
**Answer**:
- **Jakarta EE** is a *specification* (a set of API interfaces). You write code against these interfaces, and a separate Application Server (like WildFly, WebLogic, Payara) provides the actual implementation at runtime. This keeps the application artifact (`.war`) extremely small, but managing and clustering heavy application servers can be operationally complex.
- **Spring Boot** is a concrete *framework*. It embeds the web server (Tomcat) directly into the application, producing a standalone, runnable "fat jar." It heavily utilizes many Jakarta EE specifications (like JPA, Bean Validation, Servlets) but provides its own proprietary implementations for dependency injection and REST routing. Spring Boot is generally preferred today for Microservices and Cloud-Native architectures due to its ease of standalone deployment via Docker.

### Q2: Explain the significance of the `persistence.xml` file in JPA.
**Answer**:
The `persistence.xml` file (located in `META-INF`) is the standard configuration file for the Java Persistence API. It defines one or more "Persistence Units". A persistence unit specifies the data source (database connection details), the JPA provider to use (e.g., Hibernate, EclipseLink), which entity classes belong to this unit, and various properties like schema generation strategies (e.g., automatically generating tables on startup) and SQL logging.

### Q3: What is CDI (Contexts and Dependency Injection)?
**Answer**:
CDI is the standard dependency injection framework for Jakarta EE. It manages the lifecycle of stateful components (Beans) and injects them into other components using the `@Inject` annotation. It also provides a robust Event architecture (Pub/Sub within the JVM) and Interceptors (AOP) for cross-cutting concerns like security or transaction management. 

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Refactoring to `@Transactional`
**Problem**: An interviewer shows you a legacy JDBC snippet where a developer manually opens a connection, calls `conn.setAutoCommit(false)`, executes three inserts, calls `conn.commit()`, and has a massive `catch` block to call `conn.rollback()`. They ask you to rewrite this logic idiomatically for Jakarta EE.

**Solution**:
The manual transaction management is verbose, error-prone, and violates the separation of concerns. In Jakarta EE, you delegate transaction management to the container using JTA (Jakarta Transactions).

```java
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.inject.Inject;

public class OrderService {
    
    @Inject
    private EntityManager em;

    // The container automatically begins a transaction before this method executes,
    // commits it when the method returns, and rolls back if a RuntimeException is thrown.
    @Transactional
    public void placeOrder(Order order, Payment payment) {
        em.persist(order);
        em.persist(payment);
    }
}
```

### Scenario 2: JAX-RS Exception Mapping
**Problem**: You have a REST endpoint that finds a user by ID. If the user doesn't exist, it throws a `UserNotFoundException`. How do you intercept this exception globally in JAX-RS and return a clean `HTTP 404 Not Found` JSON response to the client instead of a 500 Stack Trace?

**Solution**:
Implement an `ExceptionMapper`.

```java
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider // Registers this class with the JAX-RS runtime
public class UserNotFoundMapper implements ExceptionMapper<UserNotFoundException> {

    @Override
    public Response toResponse(UserNotFoundException exception) {
        ErrorPayload payload = new ErrorPayload("USER_NOT_FOUND", exception.getMessage());
        
        return Response.status(Response.Status.NOT_FOUND)
                       .entity(payload)
                       .build();
    }
}
```