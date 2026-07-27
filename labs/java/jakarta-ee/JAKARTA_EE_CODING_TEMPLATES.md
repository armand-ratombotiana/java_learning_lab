# Jakarta EE Coding Templates

Reusable code templates for Jakarta EE interview problems. Each template includes production-ready code, explanation, and guidance on when to use it.

---

## Table of Contents

1. [JAX-RS REST Endpoint Template](#1-jax-rs-rest-endpoint-template)
2. [JPA Repository Pattern](#2-jpa-repository-pattern)
3. [CDI Producer / Injector Patterns](#3-cdi-producer--injector-patterns)
4. [EJB Session Facade Pattern](#4-ejb-session-facade-pattern)
5. [JMS Producer / Consumer](#5-jms-producer--consumer)
6. [Bean Validation Annotations](#6-bean-validation-annotations)
7. [Security Constraint Configuration](#7-security-constraint-configuration)
8. [Transaction Management Patterns](#8-transaction-management-patterns)
9. [Async Servlet Template](#9-async-servlet-template)
10. [WebSocket Endpoint Template](#10-websocket-endpoint-template)
11. [CDI Event Bus Template](#11-cdi-event-bus-template)
12. [JPA Criteria API Dynamic Query](#12-jpa-criteria-api-dynamic-query)
13. [File Upload / Download with JAX-RS](#13-file-upload--download-with-jax-rs)
14. [Exception Mapper for JAX-RS](#14-exception-mapper-for-jax-rs)
15. [JSON-B Custom Serializer](#15-json-b-custom-serializer)

---

## 1. JAX-RS REST Endpoint Template

### Full CRUD REST Resource

```java
package com.example.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.List;

@Path("/api/v1/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequestScoped
public class BookResource {

    @Inject
    private BookService bookService;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAll(@QueryParam("page") @DefaultValue("0") int page,
                           @QueryParam("size") @DefaultValue("20") int size) {
        List<Book> books = bookService.findAll(page, size);
        long total = bookService.count();
        return Response.ok(books)
                .header("X-Total-Count", total)
                .link(uriInfo.getAbsolutePath() + "?page=0&size=" + size, "first")
                .build();
    }

    @GET
    @Path("/{id}")
    public Response getById(@PathParam("id") Long id) {
        return bookService.findById(id)
                .map(book -> Response.ok(book).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    public Response create(@Valid Book book) {
        Book created = bookService.create(book);
        URI location = uriInfo.getAbsolutePathBuilder()
                .path(created.getId().toString())
                .build();
        return Response.created(location).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, @Valid Book book) {
        Book updated = bookService.update(id, book);
        return Response.ok(updated).build();
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        bookService.delete(id);
        return Response.noContent().build();
    }
}
```

**When to use:** Any RESTful API in Jakarta EE. This is the standard pattern JAX-RS 3.0 provides. Interviewers expect to see `@Context UriInfo` for HATEOAS links and proper HTTP status codes.

**Explanation:**
- `@Path` defines the resource's URL mapping.
- `@Produces`/`@Consumes` ensure correct content types are negotiated.
- `@Context UriInfo` is injected by JAX-RS runtime — it reflects the actual request URI.
- `Response` gives fine-grained control over status codes, headers, and entity body.

---

## 2. JPA Repository Pattern

### Generic Repository with EntityManager

```java
package com.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> {

    @PersistenceContext(unitName = "primary")
    protected EntityManager em;

    private final Class<T> entityClass;

    protected AbstractRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public T save(T entity) {
        em.persist(entity);
        return entity;
    }

    public T update(T entity) {
        return em.merge(entity);
    }

    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    public void delete(T entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }

    public void deleteById(ID id) {
        findById(id).ifPresent(this::delete);
    }

    public List<T> findAll(int page, int size) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        TypedQuery<T> query = em.createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long count() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        cq.select(cb.count(cq.from(entityClass)));
        return em.createQuery(cq).getSingleResult();
    }
}
```

### Concrete Repository

```java
package com.example.repository;

import jakarta.enterprise.context.Dependent;
import com.example.entity.Book;
import java.util.List;

@Dependent
public class BookRepository extends AbstractRepository<Book, Long> {

    public BookRepository() {
        super(Book.class);
    }

    public List<Book> findByAuthor(String author) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.author = :author", Book.class)
                .setParameter("author", author)
                .getResultList();
    }

    public Optional<Book> findByIsbn(String isbn) {
        return em.createQuery(
                "SELECT b FROM Book b WHERE b.isbn = :isbn", Book.class)
                .setParameter("isbn", isbn)
                .getResultStream()
                .findFirst();
    }
}
```

**When to use:** Any JPA-based data access layer. This pattern avoids repeating CRUD boilerplate.

**Explanation:**
- Abstract repository uses `@PersistenceContext` to inject the `EntityManager`.
- The entity class is passed via constructor using the concrete class.
- `save()` vs `update()`: `persist` for new, `merge` for detached entities being re-attached.
- `delete()` handles both managed and detached entities.

---

## 3. CDI Producer / Injector Patterns

### EntityManager Producer

```java
package com.example.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;

@ApplicationScoped
public class EntityManagerProducer {

    @PersistenceUnit(unitName = "primary")
    private EntityManagerFactory emf;

    @Produces
    @RequestScoped
    @Database
    public EntityManager create() {
        return emf.createEntityManager();
    }

    public void close(@Disposes @Database EntityManager em) {
        if (em.isOpen()) {
            em.close();
        }
    }
}
```

### Qualifier Annotation

```java
package com.example.config;

import jakarta.inject.Qualifier;
import java.lang.annotation.*;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
public @interface Database {
}
```

### Logger Producer

```java
package com.example.config;

import jakarta.enterprise.inject.Produces;
import jakarta.enterprise.inject.spi.InjectionPoint;
import java.util.logging.Logger;

@ApplicationScoped
public class LoggerProducer {

    @Produces
    public Logger produceLogger(InjectionPoint ip) {
        return Logger.getLogger(ip.getMember().getDeclaringClass().getName());
    }
}
```

**When to use:** Any time you need to inject resources that the container doesn't automatically inject. The `@Produces` annotation bridges CDI with Java EE resources.

**Explanation:**
- The `@Database` qualifier prevents ambiguity when multiple producers exist.
- `@Disposes` ensures cleanup when the bean's scope ends.
- The Logger producer uses `InjectionPoint` to get the declaring class — it always produces the correct logger for the injection target.

---

## 4. EJB Session Facade Pattern

### Stateless Session Facade

```java
package com.example.service;

import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionAttribute;
import jakarta.ejb.TransactionAttributeType;
import jakarta.inject.Inject;
import java.util.List;

@Stateless
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public class BookService {

    @Inject
    private BookRepository bookRepository;

    @Inject
    private AuditService auditService;

    public Book create(Book book) {
        if (book.getIsbn() == null || book.getIsbn().isBlank()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        Book saved = bookRepository.save(book);
        auditService.log("Book created: " + saved.getId());
        return saved;
    }

    public Book update(Long id, Book book) {
        Book existing = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Book not found: " + id));
        existing.setTitle(book.getTitle());
        existing.setAuthor(book.getAuthor());
        existing.setIsbn(book.getIsbn());
        Book updated = bookRepository.update(existing);
        auditService.log("Book updated: " + id);
        return updated;
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<Book> findAll(int page, int size) {
        return bookRepository.findAll(page, size);
    }

    public void delete(Long id) {
        bookRepository.deleteById(id);
        auditService.log("Book deleted: " + id);
    }
}
```

**When to use:** The Session Facade pattern encapsulates business logic and coordinates multiple DAO/repository operations. Standard for EJB 3.x applications.

**Explanation:**
- `@Stateless`: No conversational state, pooled by container.
- `@TransactionAttribute(REQUIRED)`: Ensures each method runs in a transaction. Read-only methods use `NOT_SUPPORTED` to avoid acquiring a transaction.
- The facade coordinates between the repository layer and cross-cutting services (audit, events, etc.).

---

## 5. JMS Producer / Consumer

### JMS Producer

```java
package com.example.messaging;

import jakarta.annotation.Resource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.jms.*;

@ApplicationScoped
public class OrderEventProducer {

    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "jms/orderQueue")
    private Queue orderQueue;

    public void sendOrderCreated(String orderId) {
        JMSProducer producer = jmsContext.createProducer();
        producer.setDeliveryMode(DeliveryMode.PERSISTENT);
        producer.setPriority(4);
        producer.setTimeToLive(86400000L); // 24 hours

        TextMessage message = jmsContext.createTextMessage(orderId);
        message.setStringProperty("eventType", "ORDER_CREATED");
        message.setLongProperty("timestamp", System.currentTimeMillis());

        producer.send(orderQueue, message);
    }
}
```

### JMS Consumer (MDB)

```java
package com.example.messaging;

import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.MessageDriven;
import jakarta.inject.Inject;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
                              propertyValue = "jms/orderQueue"),
    @ActivationConfigProperty(propertyName = "destinationType",
                              propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "acknowledgeMode",
                              propertyValue = "Auto-acknowledge"),
    @ActivationConfigProperty(propertyName = "maxSession",
                              propertyValue = "10")
})
public class OrderEventConsumer implements MessageListener {

    @Inject
    private OrderProcessingService orderProcessingService;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                String orderId = ((TextMessage) message).getText();
                String eventType = message.getStringProperty("eventType");
                orderProcessingService.process(orderId, eventType);
            }
        } catch (Exception e) {
            // Log error — message will be redelivered if exception is thrown
            throw new RuntimeException("Failed to process JMS message", e);
        }
    }
}
```

**When to use:** Asynchronous processing patterns — order processing, event notifications, background job triggering.

**Explanation:**
- JMS 2.0 simplified API (`JMSContext`, `JMSProducer`) reduces boilerplate compared to JMS 1.1.
- Delivery mode PERSISTENT ensures messages survive broker restart.
- MDBs are the standard Jakarta EE way to consume JMS messages. The container handles pooling and transaction management.
- Throwing a `RuntimeException` in `onMessage()` triggers redelivery (configurable with error queue - DLQ).

---

## 6. Bean Validation Annotations

### Custom Validation Annotations

```java
package com.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EitherOrValidator.class)
public @interface EitherOr {
    String message() default "Exactly one of the fields must be provided";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String first();
    String second();
}
```

### Validator Implementation

```java
package com.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class EitherOrValidator implements ConstraintValidator<EitherOr, Object> {

    private String firstField;
    private String secondField;

    @Override
    public void initialize(EitherOr annotation) {
        this.firstField = annotation.first();
        this.secondField = annotation.second();
    }

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
        try {
            Object first = getFieldValue(object, firstField);
            Object second = getFieldValue(object, secondField);
            return (first != null) ^ (second != null);
        } catch (Exception e) {
            return false;
        }
    }

    private Object getFieldValue(Object object, String fieldName)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(object);
    }
}
```

### Using the Custom Validator

```java
public class SearchRequest {
    @EitherOr(first = "category", second = "query",
              message = "Provide either category or query, not both or neither")
    private String category;
    private String query;
}
```

**When to use:** Domain-specific validation beyond standard annotations. Common in interview coding exercises.

**Explanation:**
- `@Constraint` links the annotation to its validator.
- `groups()` and `payload()` are required by the Bean Validation spec.
- Accessing fields via reflection is necessary for class-level validation.
- The `@EitherOr` annotation is a real-world example: "search by exact category or free-text query, but not both."

---

## 7. Security Constraint Configuration

### Web XML Security

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
             https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">

    <!-- Security Constraints -->
    <security-constraint>
        <web-resource-collection>
            <web-resource-name>Admin Area</web-resource-name>
            <url-pattern>/admin/*</url-pattern>
            <http-method>GET</http-method>
            <http-method>POST</http-method>
        </web-resource-collection>
        <auth-constraint>
            <role-name>ADMIN</role-name>
        </auth-constraint>
        <user-data-constraint>
            <transport-guarantee>CONFIDENTIAL</transport-guarantee>
        </user-data-constraint>
    </security-constraint>

    <security-constraint>
        <web-resource-collection>
            <web-resource-name>User Area</web-resource-name>
            <url-pattern>/user/*</url-pattern>
        </web-resource-collection>
        <auth-constraint>
            <role-name>USER</role-name>
            <role-name>ADMIN</role-name>
        </auth-constraint>
    </security-constraint>

    <!-- Login Configuration -->
    <login-config>
        <auth-method>FORM</auth-method>
        <form-login-config>
            <form-login-page>/login.html</form-login-page>
            <form-error-page>/login-error.html</form-error-page>
        </form-login-config>
    </login-config>

    <!-- Security Roles -->
    <security-role>
        <role-name>ADMIN</role-name>
    </security-role>
    <security-role>
        <role-name>USER</role-name>
    </security-role>

</web-app>
```

### Programmatic Security

```java
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

@Path("/api")
public class SecureResource {

    @GET
    @Path("/admin")
    @RolesAllowed("ADMIN")
    public String adminOnly() {
        return "Admin data";
    }

    @GET
    @Path("/me")
    public String currentUser(@Context SecurityContext ctx) {
        if (ctx.getUserPrincipal() != null) {
            return "Hello, " + ctx.getUserPrincipal().getName();
        }
        return "Anonymous";
    }
}
```

**When to use:** Every enterprise application needs security. Know both declarative (`web.xml`) and programmatic (`SecurityContext`) approaches.

**Explanation:**
- Declarative security in `web.xml` centralizes access control. Interviewers check if you know the XML syntax.
- `@RolesAllowed` on methods is the programmatic per-endpoint approach.
- `transport-guarantee CONFIDENTIAL` forces HTTPS — a common interview follow-up question.

---

## 8. Transaction Management Patterns

### JTA with @Transactional (CDI)

```java
package com.example.service;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.Transactional.TxType;

@ApplicationScoped
public class TransferService {

    @Inject
    private AccountRepository accountRepo;

    @Inject
    private AuditService auditService;

    @Transactional(TxType.REQUIRED)
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepo.findById(fromId)
                .orElseThrow(() -> new IllegalArgumentException("Source account not found"));
        Account to = accountRepo.findById(toId)
                .orElseThrow(() -> new IllegalArgumentException("Destination account not found"));

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient balance");
        }

        from.setBalance(from.getBalance().subtract(amount));
        to.setBalance(to.getBalance().add(amount));

        accountRepo.update(from);
        accountRepo.update(to);

        auditService.logTransfer(fromId, toId, amount);
    }

    @Transactional(TxType.REQUIRES_NEW)
    public void auditLogOnly(String message) {
        // Always commits in its own transaction, even if caller rolls back
        auditService.log(message);
    }

    @Transactional(TxType.SUPPORTS)
    public BigDecimal getBalance(Long accountId) {
        // Joins existing transaction if present, runs non-transactionally otherwise
        return accountRepo.findById(accountId)
                .map(Account::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}
```

### Manual Transaction Control (BMT)

```java
package com.example.service;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.ejb.TransactionManagement;
import jakarta.ejb.TransactionManagementType;
import jakarta.transaction.UserTransaction;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class ManualTransactionService {

    @Resource
    private UserTransaction utx;

    @Inject
    private OrderRepository orderRepo;

    @Inject
    private InventoryService inventoryService;

    public void placeOrder(Order order) throws Exception {
        try {
            utx.begin();

            orderRepo.save(order);
            inventoryService.reserveItems(order);

            utx.commit();
        } catch (Exception e) {
            utx.rollback();
            throw e;
        }
    }
}
```

**When to use:** `@Transactional` for CMT (Container-Managed Transactions) in CDI beans. `UserTransaction` for BMT (Bean-Managed Transactions) when you need fine-grained control.

**Explanation:**
- `TxType.REQUIRED`: run in current transaction or create new one — the most common.
- `TxType.REQUIRES_NEW`: suspend current and start new — useful for audit logging that must persist even if the main transaction rolls back.
- `TxType.SUPPORTS`: read-only operations that don't need a transaction but can participate in one.
- BMT gives you the ability to decide commit/rollback based on business logic.

---

## 9. Async Servlet Template

```java
package com.example.web;

import jakarta.servlet.AsyncContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@WebServlet(urlPatterns = "/async/report", asyncSupported = true)
public class AsyncReportServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        AsyncContext asyncCtx = req.startAsync(req, resp);
        asyncCtx.setTimeout(30000);

        CompletableFuture.runAsync(() -> {
            try {
                String report = generateReport();
                asyncCtx.getResponse().getWriter().write(report);
            } catch (Exception e) {
                asyncCtx.getResponse().setStatus(500);
            } finally {
                asyncCtx.complete();
            }
        });
    }

    private String generateReport() {
        // Simulate long operation (e.g., DB query, external API call)
        try { Thread.sleep(5000); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return "report data";
    }
}
```

**When to use:** Long-running requests that would block container threads (report generation, complex calculations, external API orchestration).

**Explanation:**
- `asyncSupported = true` is mandatory on both `@WebServlet` and any filters in the chain.
- `startAsync()` returns the container thread immediately.
- The long-running operation executes on a separate thread (or thread pool).
- `ctx.complete()` signals the container to send the response and clean up.

---

## 10. WebSocket Endpoint Template

```java
package com.example.websocket;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

@ServerEndpoint("/chat/{room}")
public class ChatEndpoint {

    private static final CopyOnWriteArraySet<Session> sessions = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        sessions.add(session);
        String room = session.getPathParameters().get("room");
        broadcast("User joined room: " + room);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        broadcast(session.getPathParameters().get("room") + ": " + message);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        sessions.remove(session);
        broadcast("User left");
    }

    @OnError
    public void onError(Session session, Throwable error) {
        sessions.remove(session);
        error.printStackTrace();
    }

    private void broadcast(String message) {
        sessions.stream()
                .filter(Session::isOpen)
                .forEach(session -> {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
    }
}
```

**When to use:** Real-time bidirectional communication — chat, live updates, notifications, streaming data.

**Explanation:**
- `@ServerEndpoint("/chat/{room}")` maps the endpoint and captures path parameters.
- Each method corresponds to a lifecycle event: open, message, close, error.
- `CopyOnWriteArraySet` is thread-safe for concurrent WebSocket sessions.
- Broadcasting iterates over all sessions — for production, use a concurrent map keyed by room.

---

## 11. CDI Event Bus Template

```java
package com.example.event;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.inject.Inject;
import jakarta.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

// Qualifiers for event types
@Qualifier @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
public @interface OrderEvent {}

@Qualifier @Target({FIELD, PARAMETER, METHOD}) @Retention(RUNTIME)
public @interface NotificationEvent {}

// Event payload
public class OrderPlaced {
    public final Long orderId;
    public final String customerEmail;

    public OrderPlaced(Long orderId, String customerEmail) {
        this.orderId = orderId;
        this.customerEmail = customerEmail;
    }
}

// Event producer
@ApplicationScoped
public class OrderEventProducer {

    @Inject @OrderEvent
    private Event<OrderPlaced> orderEvent;

    public void orderCreated(Long orderId, String email) {
        orderEvent.fire(new OrderPlaced(orderId, email));
    }
}

// Synchronous observer
@ApplicationScoped
public class EmailNotificationService {

    public void onOrderPlaced(@Observes @OrderEvent OrderPlaced event) {
        // Send email — runs in the same transaction
        sendEmail(event.customerEmail, "Order confirmed: " + event.orderId);
    }

    private void sendEmail(String to, String body) {
        // email logic
    }
}

// Async observer (Jakarta EE 10+)
@ApplicationScoped
public class AnalyticsService {

    public void trackOrder(@ObservesAsync @OrderEvent OrderPlaced event) {
        // Runs on a separate thread, non-transactional
        sendToAnalytics(event.orderId);
    }

    private void sendToAnalytics(Long orderId) { }
}

// Transaction-phase observer
@ApplicationScoped
public class InventoryService {

    public void reserveItems(@Observes(during = TransactionPhase.AFTER_SUCCESS)
                             @OrderEvent OrderPlaced event) {
        // Only runs if the transaction commits successfully
        deductInventory(event.orderId);
    }

    private void deductInventory(Long orderId) { }
}
```

**When to use:** Decoupled communication between beans within the same application. Replaces Publish-Subscribe without an external message broker.

**Explanation:**
- Qualifiers (`@OrderEvent`, `@NotificationEvent`) allow type-based filtering of events.
- `@Observes` — synchronous, same transaction.
- `@ObservesAsync` — asynchronous, different thread.
- `TransactionPhase.AFTER_SUCCESS` — observer fires only after a successful commit.
- Events are typesafe and do not require interfaces or configuration.

---

## 12. JPA Criteria API Dynamic Query

```java
package com.example.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookSearchRepository {

    @PersistenceContext
    private EntityManager em;

    public List<Book> searchBooks(BookSearchCriteria criteria) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> book = cq.from(Book.class);

        List<Predicate> predicates = new ArrayList<>();

        Optional.ofNullable(criteria.getTitle())
                .filter(s -> !s.isBlank())
                .ifPresent(title -> predicates.add(
                        cb.like(cb.lower(book.get("title")), "%" + title.toLowerCase() + "%")));

        Optional.ofNullable(criteria.getAuthor())
                .filter(s -> !s.isBlank())
                .ifPresent(author -> predicates.add(
                        cb.equal(cb.lower(book.get("author")), author.toLowerCase())));

        Optional.ofNullable(criteria.getMinPrice())
                .ifPresent(price -> predicates.add(
                        cb.greaterThanOrEqualTo(book.get("price"), price)));

        Optional.ofNullable(criteria.getMaxPrice())
                .ifPresent(price -> predicates.add(
                        cb.lessThanOrEqualTo(book.get("price"), price)));

        Optional.ofNullable(criteria.getCategory())
                .ifPresent(category -> predicates.add(
                        cb.equal(book.get("category"), category)));

        if (predicates.isEmpty()) {
            return List.of();
        }

        cq.where(predicates.toArray(new Predicate[0]));

        // Apply sorting
        if (criteria.getSortBy() != null) {
            Path<Object> sortPath = book.get(criteria.getSortBy());
            if (criteria.isAscending()) {
                cq.orderBy(cb.asc(sortPath));
            } else {
                cq.orderBy(cb.desc(sortPath));
            }
        }

        TypedQuery<Book> query = em.createQuery(cq);
        query.setFirstResult(criteria.getPage() * criteria.getSize());
        query.setMaxResults(criteria.getSize());

        return query.getResultList();
    }
}

// Criteria DTO
class BookSearchCriteria {
    private String title;
    private String author;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private String category;
    private String sortBy;
    private boolean ascending = true;
    private int page = 0;
    private int size = 20;
    // getters and setters
}
```

**When to use:** Dynamic search/filter APIs where the query structure depends on user input (e-commerce search, admin data tables).

**Explanation:**
- The Criteria API builds the WHERE clause programmatically using `Predicate` combinations.
- Only non-null criteria fields become query predicates — prevents SQL injection via concatenated JPQL.
- `Optional` streamlines the conditional predicate addition.
- Sorting is also dynamic — the `Path` is resolved by field name.

---

## 13. File Upload / Download with JAX-RS

```java
package com.example.rest;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Path("/api/files")
public class FileResource {

    private static final java.nio.file.Path UPLOAD_DIR =
            Paths.get(System.getProperty("java.io.tmpdir"), "uploads");

    public FileResource() throws IOException {
        Files.createDirectories(UPLOAD_DIR);
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response upload(@FormParam("file") InputStream fileStream,
                           @FormParam("fileName") String fileName,
                           @FormParam("description") String description) throws IOException {

        String fileId = UUID.randomUUID().toString();
        java.nio.file.Path targetPath = UPLOAD_DIR.resolve(fileId + "_" + fileName);

        try (OutputStream out = Files.newOutputStream(targetPath)) {
            fileStream.transferTo(out);
        }

        return Response.ok(new FileInfo(fileId, fileName, "File uploaded successfully")).build();
    }

    @GET
    @Path("/download/{fileId}/{fileName}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response download(@PathParam("fileId") String fileId,
                             @PathParam("fileName") String fileName) {

        java.nio.file.Path filePath = UPLOAD_DIR.resolve(fileId + "_" + fileName);

        if (!Files.exists(filePath)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        StreamingOutput stream = output -> Files.copy(filePath, output);

        return Response.ok(stream)
                .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
                .build();
    }

    // Inner class for file info response
    public static class FileInfo {
        public String fileId;
        public String fileName;
        public String message;

        public FileInfo(String fileId, String fileName, String message) {
            this.fileId = fileId;
            this.fileName = fileName;
            this.message = message;
        }
    }
}
```

**When to use:** Any application handling file uploads/downloads.

**Explanation:**
- `@Consumes(MediaType.MULTIPART_FORM_DATA)` for file uploads.
- `@FormParam("file")` with `InputStream` — JAX-RS handles the multipart parsing.
- `StreamingOutput` avoids loading entire files into memory during download.
- Content-Disposition header triggers browser download vs inline display.

---

## 14. Exception Mapper for JAX-RS

```java
package com.example.rest;

import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.time.LocalDateTime;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Exception> {

    @Context
    private UriInfo uriInfo;

    @Override
    public Response toResponse(Exception exception) {
        HttpStatus status = resolveStatus(exception);
        ErrorResponse error = new ErrorResponse(
                status.getCode(),
                status.getReason(),
                exception.getMessage(),
                uriInfo.getPath(),
                LocalDateTime.now()
        );
        return Response.status(status.getCode())
                .entity(error)
                .type(MediaType.APPLICATION_JSON)
                .build();
    }

    private HttpStatus resolveStatus(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        }
        if (e instanceof NotFoundException) {
            return HttpStatus.NOT_FOUND;
        }
        if (e instanceof WebApplicationException) {
            return HttpStatus.valueOf(
                    ((WebApplicationException) e).getResponse().getStatus());
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    // Error DTO
    public static class ErrorResponse {
        public int status;
        public String error;
        public String message;
        public String path;
        public String timestamp;

        public ErrorResponse(int status, String error, String message,
                            String path, LocalDateTime now) {
            this.status = status;
            this.error = error;
            this.message = message;
            this.path = path;
            this.timestamp = now.toString();
        }
    }
}
```

**When to use:** Consistent error handling across all REST endpoints.

**Explanation:**
- `@Provider` registers the mapper with JAX-RS.
- `Map` exception types to appropriate HTTP status codes.
- A structured `ErrorResponse` DTO gives consistent error format across the API.
- The pattern prevents stack traces from leaking to the client.

---

## 15. JSON-B Custom Serializer

```java
package com.example.json;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTransient;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

// Entity with JSON-B annotations
public class Employee {
    @JsonbProperty("employee_id")
    private Long id;

    @JsonbProperty("full_name")
    private String name;

    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate hireDate;

    @JsonbTransient
    private String internalNotes;

    private Optional<String> middleName; // Optional is mapped as nullable

    // getters and setters
}

// Custom adapter for Optional<String>
public class OptionalStringAdapter implements JsonbAdapter<Optional<String>, String> {
    @Override
    public String adaptToJson(Optional<String> obj) {
        return obj.orElse(null);
    }

    @Override
    public Optional<String> adaptFromJson(String obj) {
        return Optional.ofNullable(obj);
    }
}

// Configuration
JsonbConfig config = new JsonbConfig()
    .withFormatValues(true)
    .withAdapters(new OptionalStringAdapter())
    .withNullValues(true)
    .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);

Jsonb jsonb = JsonbBuilder.create(config);

// Serialize
String json = jsonb.toJson(employee);

// Deserialize
Employee emp = jsonb.fromJson(json, Employee.class);
```

**When to use:** Any JSON serialization/deserialization in Jakarta EE applications.

**Explanation:**
- `@JsonbProperty` maps Java field names to JSON property names.
- `@JsonbTransient` excludes fields from serialization.
- `@JsonbDateFormat` controls date formatting.
- `JsonbAdapter` handles type conversion for non-standard types (e.g., `Optional`).
- `JsonbConfig` centralizes formatting rules, naming strategies, and null handling.
