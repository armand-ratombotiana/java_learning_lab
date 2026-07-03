# Module 56: Jakarta EE & Enterprise Java - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-55  
**Estimated Reading Time**: 60 minutes  

---

## 📚 Table of Contents

1. [Java EE to Jakarta EE Transition](#transition)
2. [Jakarta RESTful Web Services (JAX-RS)](#jax-rs)
3. [Jakarta Contexts and Dependency Injection (CDI)](#cdi)
4. [Jakarta Persistence (JPA)](#jpa)
5. [Jakarta Enterprise Beans (EJB / CDI alternatives)](#ejb)

---

## 1. Java EE to Jakarta EE Transition <a name="transition"></a>
In 2017, Oracle transferred the governance of Java EE to the Eclipse Foundation. Because Oracle retained the "Java" trademark, the platform was renamed to **Jakarta EE**. 
The most significant change for developers occurred in Jakarta EE 9/10, where the fundamental package namespace changed from `javax.*` to `jakarta.*` (e.g., `javax.persistence.Entity` became `jakarta.persistence.Entity`).

---

## 2. Jakarta RESTful Web Services (JAX-RS) <a name="jax-rs"></a>
JAX-RS is the standard API for creating RESTful web services in enterprise Java (implemented by frameworks like Jersey or RESTEasy). It relies heavily on annotations.

```java
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/users")
public class UserResource {

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") Long id) {
        return new User(id, "John Doe");
    }
}
```

---

## 3. Jakarta Contexts and Dependency Injection (CDI) <a name="cdi"></a>
CDI is the standard dependency injection framework for Jakarta EE, conceptually similar to Spring's DI but part of the core Java specification.

```java
import jakarta.inject.Inject;
import jakarta.enterprise.context.RequestScoped;

@RequestScoped
public class UserService {
    
    @Inject
    private UserRepository repository;

    public void process() { ... }
}
```

---

## 4. Jakarta Persistence (JPA) <a name="jpa"></a>
JPA is the specification for Object-Relational Mapping (ORM) in Java. Hibernate is the most popular implementation. It manages the mapping of Java objects to database tables.

```java
import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
}
```

---

## 5. Jakarta Enterprise Beans (EJB / CDI alternatives) <a name="ejb"></a>
Historically, EJBs were the core of Java EE, providing transaction management, security, and concurrency. 
Today, standard CDI beans combined with Jakarta Transactions (`@Transactional`) and Jakarta Security have largely replaced heavy EJBs, providing a much lighter-weight, modern programming model.