# Jakarta EE / Java EE Interview Guide

---

## Table of Contents

1. [Servlets](#1-servlets)
2. [JSP / JSTL](#2-jsp--jstl)
3. [JPA / Hibernate](#3-jpa--hibernate)
4. [EJB](#4-ejb)
5. [JMS](#5-jms)
6. [CDI](#6-cdi)
7. [Bean Validation](#7-bean-validation)
8. [JAX-RS / REST](#8-jax-rs--rest)
9. [JSON-B / JSON-P](#9-json-b--json-p)
10. [Security](#10-security)
11. [Transactions](#11-transactions)
12. [Company-Specific Sections](#12-company-specific-sections)
13. [Spring Comparison](#13-spring-comparison)

---

## 1. Servlets

### Q1: Describe the Servlet lifecycle.

**Answer:** The servlet container manages the lifecycle through four phases:

1. **Loading and Instantiation**: Container loads the servlet class and creates an instance (or instances for single-thread model).
2. **Initialization (`init()`)**: Called once after instantiation. The servlet gets its `ServletConfig`. Typically used for opening DB connections or loading config.
3. **Request Handling (`service()`)**: For each request, the container calls `service()`, which dispatches to `doGet()`, `doPost()`, etc. based on HTTP method.
4. **Destruction (`destroy()`)**: Called once before the servlet is taken out of service. Used for cleanup.

```java
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
    @Override
    public void init() throws ServletException {
        // Called once — load config, open resources
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        resp.getWriter().write("Hello, Jakarta EE!");
    }

    @Override
    public void destroy() {
        // Cleanup resources
    }
}
```

**Edge case:** `init()` can throw `UnavailableException` to indicate the servlet should not be loaded. If permanent, the container unloads the servlet; if temporary, it retries.

### Q2: What are Servlet filters and how do they differ from listeners?

**Answer:**

- **Filters** intercept requests before they reach the servlet and responses before they reach the client. Used for logging, authentication, compression, encoding.
- **Listeners** respond to lifecycle events in the servlet context, session, or request. Used for initializing shared resources, session tracking.

```java
// Filter
@WebFilter("/*")
public class LoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {
        System.out.println("Request received: " + ((HttpServletRequest) req).getRequestURI());
        chain.doFilter(req, resp);
        System.out.println("Response sent");
    }
}

// Listener
@WebListener
public class AppContextListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        sce.getServletContext().setAttribute("config", loadConfig());
    }
}
```

**Company-specific:** Oracle and IBM ask heavily about filter ordering and how to declare multiple filters for the same URL pattern. Filter order in `web.xml` matters; with annotations, order is non-deterministic.

### Q3: Explain async servlets. When would you use them?

**Answer:** Async servlets (introduced in Servlet 3.0) allow a thread to return to the container without completing the response, freeing up container threads while waiting for a long operation (e.g., external API call, JMS receive, streaming).

```java
@WebServlet(value = "/async", asyncSupported = true)
public class AsyncServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        AsyncContext ctx = req.startAsync();
        ctx.start(() -> {
            try {
                String result = longRunningOperation();
                ctx.getResponse().getWriter().write(result);
            } catch (Exception e) {
                // handle
            } finally {
                ctx.complete();
            }
        });
    }
}
```

**Edge case:** If `ctx.complete()` is never called, the request times out (configurable via `ctx.setTimeout()`). The container logs a warning. AsyncContext timeout vs servlet timeout — they are separate.

### Q4: What is the difference between forward() and sendRedirect()?

| Feature | `forward()` | `sendRedirect()` |
|---------|------------|------------------|
| Client involved | No (server-side) | Yes (client gets 302) |
| URL changes | No | Yes |
| Request objects | Same request preserved | New request |
| Scope | Server internal | Can redirect to external URLs |
| Speed | Faster | Slower (extra round trip) |

```java
// Forward - server-side
req.getRequestDispatcher("/target.jsp").forward(req, resp);

// Redirect - client-side
resp.sendRedirect("/target.jsp");
```

### Q5: What is the difference between ServletContext and ServletConfig?

**Answer:**

- **ServletConfig**: Per-servlet configuration. Each servlet has its own `ServletConfig` with init parameters from `web.xml` or `@WebInitParam`.
- **ServletContext**: Application-wide configuration shared across all servlets. Accessed via `getServletContext()`. Used for global attributes, MIME types, request dispatching.

```java
// ServletConfig — per servlet
String param = getServletConfig().getInitParameter("myParam");

// ServletContext — application-wide
String global = getServletContext().getInitParameter("globalParam");
getServletContext().setAttribute("sharedObj", new Data());
```

---

## 2. JSP / JSTL

### Q6: What are the JSP scopes and how do they differ?

**Answer:** Four scopes, from narrowest to widest:

1. **Page scope** — Only available within the current JSP page. Uses `pageContext`.
2. **Request scope** — Available within the current request (including forwarded pages). Uses `request`.
3. **Session scope** — Available throughout a user session. Uses `session`.
4. **Application scope** — Available to all users and pages. Uses `application`.

```jsp
<c:set var="data" value="value" scope="session" />
${sessionScope.data}
```

**Interview tip:** When asked "how do you pass data between servlets and JSPs", the answer is request scope with `setAttribute` in the servlet and `${requestScope.attribute}` in JSP.

### Q7: Explain custom tags in JSP. Write a simple custom tag.

**Answer:** Custom tags encapsulate reusable presentation logic. Three types: simple tags (SimpleTag), classic tags (Tag), and tag files.

```java
// Tag handler class
public class GreetingTag extends SimpleTagSupport {
    private String name;

    public void setName(String name) { this.name = name; }

    @Override
    public void doTag() throws JspException, IOException {
        getJspContext().getOut().write("Hello, " + name + "!");
    }
}
```

```xml
<!-- TLD (tag library descriptor) -> WEB-INF/tags/greet.tld -->
<tag>
    <name>greet</name>
    <tag-class>com.example.GreetingTag</tag-class>
    <body-content>empty</body-content>
    <attribute>
        <name>name</name>
        <required>true</required>
    </attribute>
</tag>
```

```jsp
<%@ taglib uri="/WEB-INF/tags/greet.tld" prefix="my" %>
<my:greet name="World" />
```

### Q8: What is Expression Language (EL) and how does it work with JSTL?

**Answer:** EL provides a simplified syntax for accessing Java objects in JSPs. Syntax: `${expression}`. JSTL provides tags like `<c:if>`, `<c:forEach>`, `<c:out>` that work seamlessly with EL.

```jsp
<c:forEach items="${users}" var="u">
    <tr>
        <td>${u.name}</td>
        <td>${u.email}</td>
    </tr>
</c:forEach>

<c:if test="${user.role == 'ADMIN'}">
    <a href="/admin">Admin Panel</a>
</c:if>
```

**Common interview trick:** The `[]` operator works for both maps and lists: `${map["key"]}` and `${list[0]}`. `.` is just syntactic sugar for `[]`.

### Q9: What are EL implicit objects? Name them and give an example.

**Answer:** EL has 11 implicit objects: `pageScope`, `requestScope`, `sessionScope`, `applicationScope`, `param`, `paramValues`, `header`, `headerValues`, `cookie`, `initParam`, `pageContext`.

```jsp
<%-- Access request parameter --%>
${param.username}

<%-- Access cookie --%>
${cookie.userId.value}

<%-- Access context init param --%>
${initParam.globalConfig}
```

---

## 3. JPA / Hibernate

### Q10: Explain the JPA entity lifecycle states.

**Answer:** Four states:

1. **New (Transient)** — Entity created but not associated with a persistence context.
2. **Managed (Persistent)** — Entity associated with a persistence context. Changes are tracked and synchronized automatically.
3. **Detached** — Entity was managed but the persistence context was closed. Changes are no longer tracked.
4. **Removed** — Entity marked for deletion. Will be removed from DB during flush.

```java
User user = new User();               // New (Transient)
entityManager.persist(user);           // Managed
entityManager.flush();                 // Still Managed
entityManager.detach(user);            // Detached
entityManager.merge(user);             // Back to Managed
entityManager.remove(user);            // Removed (not yet in DB until flush)
```

### Q11: Explain relationship mappings (@OneToOne, @OneToMany, @ManyToMany) with cascade and fetch types.

**Answer:**

```java
@Entity
public class Department {
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Employee> employees = new ArrayList<>();
}

@Entity
public class Employee {
    @Id @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dept_id")
    private Department department;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    @ManyToMany
    @JoinTable(name = "emp_project",
               joinColumns = @JoinColumn(name = "emp_id"),
               inverseJoinColumns = @JoinColumn(name = "proj_id"))
    private List<Project> projects;
}
```

**Interview edge case:** `FetchType.EAGER` on `@ManyToMany` can cause Cartesian products or fetching too much data. Always prefer LAZY unless certain of the trade-off. N+1 query problem occurs with LAZY if accessed outside a transaction.

**Company focus:** Oracle (WebLogic/TopLink) expects you to know the difference between JPA and Hibernate-specific annotations. Red Hat (WildFly/Hibernate) expects deep Hibernate internals knowledge.

### Q12: What is JPQL and how does it differ from native SQL and Criteria API?

**Answer:**

- **JPQL** — Entity-based query language. Works with entity names and field names, not table/column names.
- **Native SQL** — Raw SQL passed to the database. Use when JPQL can't express something.
- **Criteria API** — Type-safe, programmatic query building. Best for dynamic queries.

```java
// JPQL
List<User> users = em.createQuery(
    "SELECT u FROM User u WHERE u.email = :email", User.class)
    .setParameter("email", "test@test.com")
    .getResultList();

// Native SQL
List<Object[]> rows = em.createNativeQuery(
    "SELECT * FROM users WHERE email = ?1")
    .setParameter(1, "test@test.com")
    .getResultList();

// Criteria API
CriteriaBuilder cb = em.getCriteriaBuilder();
CriteriaQuery<User> cq = cb.createQuery(User.class);
Root<User> root = cq.from(User.class);
cq.select(root).where(cb.equal(root.get("email"), "test@test.com"));
List<User> users = em.createQuery(cq).getResultList();
```

### Q13: Explain the N+1 query problem and how to solve it.

**Answer:** The N+1 problem occurs when you fetch an entity (1 query) and then access its lazy-loaded association for each entity (N queries).

**Solutions:**

1. **JOIN FETCH in JPQL** — Eagerly fetch the association in a single query.
2. **Entity Graph** — Define fetch plans dynamically.
3. **@BatchSize** — Batch lazy loading together.

```java
// Solution 1: JOIN FETCH
List<Department> depts = em.createQuery(
    "SELECT d FROM Department d JOIN FETCH d.employees", Department.class)
    .getResultList();

// Solution 2: Entity Graph
EntityGraph<Department> graph = em.createEntityGraph(Department.class);
graph.addSubgraph("employees");
Map<String, Object> hints = new HashMap<>();
hints.put("jakarta.persistence.fetchgraph", graph);
Department dept = em.find(Department.class, 1L, hints);
```

### Q14: Explain first-level and second-level caching in JPA/Hibernate.

**Answer:**

- **First-Level Cache (L1)**: Per-persistence context (per EntityManager/session). Always enabled. If you load the same entity twice within the same session, the second call returns the cached instance.
- **Second-Level Cache (L2)**: Across persistence contexts (across EntityManagers). Must be explicitly configured with a provider (EHCache, Redis, etc.). Stores entities by ID.

```java
// Enable L2 cache for an entity
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product {
    // ...
}
```

**Edge case:** L1 cache can cause stale data if the DB is updated externally. L2 cache strategies (READ_ONLY, READ_WRITE, NONSTRICT_READ_WRITE, TRANSACTIONAL) have different concurrency guarantees. READ_WRITE uses soft locks; TRANSACTIONAL requires JTA.

### Q15: Explain optimistic vs pessimistic locking in JPA.

**Answer:**

- **Optimistic locking**: Assumes conflicts are rare. Uses a version column (`@Version`) and checks it on update. Throws `OptimisticLockException` if version changed.
- **Pessimistic locking**: Locks database rows when read. Uses `LockModeType.PESSIMISTIC_READ` or `PESSIMISTIC_WRITE`.

```java
// Optimistic locking
@Entity
public class Account {
    @Id private Long id;
    @Version private int version;
    private BigDecimal balance;
}

// Pessimistic locking
Account account = em.find(Account.class, id, LockModeType.PESSIMISTIC_WRITE);
account.setBalance(newBalance);
```

**Interview question:** "What happens when two users try to update the same entity simultaneously?" → Optimistic: second user gets `OptimisticLockException`. Pessimistic: second user waits until the first commits.

### Q16: What is inheritance mapping in JPA? Explain the strategies.

**Answer:** Three strategies:

1. **SINGLE_TABLE** — All classes in one table with a discriminator column. Best performance. Null columns for subclass-specific fields.
2. **TABLE_PER_CLASS** — Each concrete class has its own table. No discriminator. Polymorphic queries use UNIONs.
3. **JOINED** — Each class maps to its own table. Subclass tables join with parent table via foreign key.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "vehicle_type")
public class Vehicle {
    @Id @GeneratedValue private Long id;
    private String manufacturer;
}

@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {
    private int numberOfDoors;
}
```

**When to use:** SINGLE_TABLE → simple hierarchies, few subclass columns. JOINED → normalized data, complex hierarchies. TABLE_PER_CLASS → rarely used due to UNION performance issues.

### Q17: What is the difference between em.find() and em.getReference()?

| Feature | `find()` | `getReference()` |
|---------|----------|------------------|
| DB hit | Immediate (eager) | Lazy (proxy returned) |
| Entity state | Managed | Managed (proxy) |
| If not found | Returns null | Throws EntityNotFoundException on access |
| When to use | When you need the data | When you only need a reference (e.g., setting FK) |

### Q18: Explain the use of @ElementCollection.

**Answer:** Maps collections of basic types or embeddable types without creating a separate entity.

```java
@Entity
public class Employee {
    @Id @GeneratedValue private Long id;

    @ElementCollection
    @CollectionTable(name = "emp_phones", joinColumns = @JoinColumn(name = "emp_id"))
    @Column(name = "phone")
    private List<String> phoneNumbers;

    @ElementCollection
    @CollectionTable(name = "emp_addresses", joinColumns = @JoinColumn(name = "emp_id"))
    private List<Address> addresses;
}

@Embeddable
public class Address {
    private String street;
    private String city;
}
```

### Q19: What is a NamedQuery and what are its advantages?

**Answer:** `@NamedQuery` defines a JPQL query statically on an entity for reuse. Parsed at startup (compile-time check), not at runtime.

```java
@Entity
@NamedQueries({
    @NamedQuery(name = "User.findByEmail",
                query = "SELECT u FROM User u WHERE u.email = :email"),
    @NamedQuery(name = "User.findActive",
                query = "SELECT u FROM User u WHERE u.active = true")
})
public class User {
    @Id @GeneratedValue private Long id;
    private String email;
    private boolean active;
}

// Usage
User user = em.createNamedQuery("User.findByEmail", User.class)
    .setParameter("email", "test@test.com")
    .getSingleResult();
```

### Q20: What is the difference between CascadeType.REMOVE and orphanRemoval?

**Answer:**

- `CascadeType.REMOVE` — Removes child entities when the parent is explicitly removed.
- `orphanRemoval = true` — Removes child entities when they are removed from the parent's collection (the "orphan" concept). Also removes on parent delete.

```java
// orphanRemoval — removes employee from DB when removed from list
@OneToMany(mappedBy = "department", orphanRemoval = true)
private List<Employee> employees;

// Removing from collection triggers DELETE on flush
department.getEmployees().remove(0);
```

### Q21-30: Additional JPA questions (summary)

**Q21: What is `@MapsId` and when would you use it?** — For shared primary key in `@OneToOne`.
**Q22: Explain `PersistenceContext` and `PersistenceContextType.EXTENDED`.** — EXTENDED scoped to conversation (Stateful EJB), not transaction.
**Q23: What is the difference between `flush()` and `commit()`?** — Flush synchronizes with DB within the transaction; commit ends the transaction.
**Q24: How do you handle pagination in JPA?** — `setFirstResult()` + `setMaxResults()`.
**Q25: What is `LockModeType.OPTIMISTIC_FORCE_INCREMENT`?** — Like OPTIMISTIC but always increments version, even without changes.
**Q26: Explain JPQL `FETCH JOIN` vs `JOIN`.** — FETCH JOIN populates the entity's association; JOIN does not.
**Q27: What is the open session in view pattern?** — Keeping the EntityManager open during view rendering to allow lazy loading. Anti-pattern in many cases.
**Q28: How do you map enums in JPA?** — `@Enumerated(EnumType.ORDINAL)` (default, fragile) or `@Enumerated(EnumType.STRING)` (preferred).
**Q29: What is `@Converter` in JPA 2.1+?** — Custom attribute converter for non-standard types.
**Q30: Explain `EntityManager.clear()` and `EntityManager.flush()`.** — Clear detaches all managed entities; flush pushes pending changes.

---

## 4. EJB

### Q31: What are the types of Enterprise JavaBeans (EJB)?

**Answer:** Three types:

1. **Session Beans**:
   - **Stateless (@Stateless)**: No conversational state. Pooled. Best for stateless operations.
   - **Stateful (@Stateful)**: Conversational state retained across methods. Passivated to disk.
   - **Singleton (@Singleton)**: One instance per application. Concurrency managed via container.
2. **Message-Driven Beans (@MessageDriven)**: Async message consumers (JMS).
3. **Entity Beans**: Deprecated in EJB 3.0, replaced by JPA entities.

```java
@Stateless
public class CalculatorService {
    public int add(int a, int b) { return a + b; }
}

@Stateful
@StatefulTimeout(unit = TimeUnit.MINUTES, value = 30)
public class ShoppingCart {
    private List<Item> items = new ArrayList<>();
    public void addItem(Item item) { items.add(item); }
    public void checkout() { /* process items */ }
}

@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
public class ConfigManager {
    private Properties config;
    @Lock(LockType.READ)
    public String get(String key) { return config.getProperty(key); }
    @Lock(LockType.WRITE)
    public void set(String key, String val) { config.setProperty(key, val); }
}
```

### Q32: Explain EJB transaction management.

**Answer:** Declarative via `@TransactionAttribute`:

- `REQUIRED` (default) — Join existing tx, create if none.
- `REQUIRES_NEW` — Always create new tx, suspend existing.
- `SUPPORTS` — Join if exists, run non-transactional otherwise.
- `MANDATORY` — Must join existing tx, throw exception if none.
- `NOT_SUPPORTED` — Suspend existing tx, run non-transactional.
- `NEVER` — Throw exception if tx exists.

```java
@Stateless
public class PaymentService {
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void processPayment(Order order) {
        // Always runs in a new transaction
    }
}
```

### Q33: What are EJB timers and how do you use them?

```java
@Singleton
public class ScheduledTask {
    @Schedule(hour = "2", minute = "0", second = "0", persistent = false)
    public void nightlyCleanup() {
        // Runs at 2 AM daily
    }

    @Resource
    private TimerService timerService;

    public void createProgrammaticTimer() {
        timerService.createSingleActionTimer(5000, new TimerConfig("myTimer", false));
    }

    @Timeout
    public void handleTimeout(Timer timer) {
        System.out.println("Timer expired: " + timer.getInfo());
    }
}
```

### Q34: What is the difference between EJB interceptors and CDI interceptors?

**Answer:** EJB interceptors (`@AroundInvoke`) are specific to EJBs. CDI interceptors (`@InterceptorBinding`) work on any CDI-managed bean. EJB interceptors can be applied at class or method level; CDI interceptors require an annotation-based binding.

```java
// EJB Interceptor
@Stateless
@Interceptors(LoggingInterceptor.class)
public class MyService {
    public void doWork() { }
}

public class LoggingInterceptor {
    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        System.out.println("Before: " + ctx.getMethod().getName());
        return ctx.proceed();
    }
}
```

### Q35: Explain passivation and activation for Stateful Session Beans.

**Answer:**

- **Passivation**: Container serializes the bean's state to disk when resources are low or the bean is idle longer than `@StatefulTimeout`.
- **Activation**: Container deserializes the bean when it's needed again.
- Bean must implement `Serializable`. The container calls `@PrePassivate` and `@PostActivate` lifecycle callbacks.

```java
@Stateful
@StatefulTimeout(value = 30, unit = TimeUnit.MINUTES)
public class ShoppingCart implements Serializable {
    private List<Item> items;

    @PrePassivate
    public void beforePassivate() { /* release non-serializable resources */ }

    @PostActivate
    public void afterActivate() { /* reacquire resources */ }
}
```

### Q36: What are the limitations of Singleton EJBs compared to @Singleton CDI?

**Answer:** EJB Singletons guarantee:
- Exactly one instance per JVM
- Container-managed concurrency (`@Lock`, `@AccessTimeout`)
- Declarative transactions and security
CDI `@Singleton` (javax.inject.Singleton) does NOT provide concurrency management — you must handle it yourself.

### Q37: Explain Message-Driven Beans (MDB).

```java
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType",
                              propertyValue = "jakarta.jms.Queue"),
    @ActivationConfigProperty(propertyName = "destinationLookup",
                              propertyValue = "jms/orderQueue")
})
public class OrderProcessor implements MessageListener {
    @Override
    public void onMessage(Message msg) {
        try {
            TextMessage text = (TextMessage) msg;
            processOrder(text.getText());
        } catch (JMSException e) {
            // Rollback or DLQ
        }
    }
}
```

---

## 5. JMS

### Q38: What is the difference between JMS Queue and Topic?

| Feature | Queue (Point-to-Point) | Topic (Pub-Sub) |
|---------|----------------------|------------------|
| Delivery | One consumer receives each message | All subscribers receive each message |
| State | Message consumed and removed | Message delivered to all active subscribers |
| History | Survives consumer downtime | Durable subscription survives downtime |
| Load balancing | Multiple consumers compete | Each subscriber gets all messages |

### Q39: How do you create a JMS producer and consumer in JMS 2.0?

```java
// JMS 2.0 simplified API
@Stateless
public class MessageSender {
    @Inject
    private JMSContext context;

    @Resource(lookup = "jms/myQueue")
    private Queue queue;

    public void send(String text) {
        context.createProducer().send(queue, text);
    }
}

// Consumer (MDB)
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
                              propertyValue = "jms/myQueue"),
    @ActivationConfigProperty(propertyName = "destinationType",
                              propertyValue = "jakarta.jms.Queue")
})
public class MessageReceiver implements MessageListener {
    public void onMessage(Message msg) {
        // process
    }
}
```

### Q40: What is a durable subscription in JMS Topics?

```java
// Durable subscriber
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationType",
                              propertyValue = "jakarta.jms.Topic"),
    @ActivationConfigProperty(propertyName = "subscriptionDurability",
                              propertyValue = "Durable"),
    @ActivationConfigProperty(propertyName = "clientId",
                              propertyValue = "orderClient"),
    @ActivationConfigProperty(propertyName = "subscriptionName",
                              propertyValue = "orderSub")
})
public class DurableSubscriber implements MessageListener { }
```

Durable subscriptions survive consumer downtime. The broker stores messages for offline subscribers.

### Q41: How do you handle JMS redelivery and poison messages?

**Answer:** Configure a dead-letter queue (DLQ) in the JMS provider. After max redeliveries (e.g., 5), the message is moved to DLQ. In the MDB, throw a `RuntimeException` to trigger rollback/redelivery.

**Interview focus:** IBM WebSphere MQ has extensive redelivery configuration. Oracle WebLogic JMS has error destinations and automatic message retry.

---

## 6. CDI

### Q42: What is CDI and how does it relate to dependency injection?

**Answer:** CDI (Contexts and Dependency Injection) is Jakarta EE's DI standard. It provides typesafe dependency injection, contextual lifecycle management, events, interceptors, and decorators. JSR 299 (Weld is the reference implementation).

```java
@ApplicationScoped
public class UserService {
    @Inject
    private UserRepository userRepository;
}
```

### Q43: What are the CDI scopes?

| Scope | Duration |
|-------|----------|
| `@RequestScoped` | Per HTTP request |
| `@SessionScoped` | Per HTTP session |
| `@ApplicationScoped` | Application-wide (one instance) |
| `@ConversationScoped` | Multi-request conversation (must begin/end) |
| `@Dependent` | Default; lifecycle tied to injected bean |
| `@Singleton` | Plain singleton (no proxy, no context) |

### Q44: Explain CDI producers and disposers.

```java
@ApplicationScoped
public class Resources {
    @Produces
    @Database
    public EntityManager create(EntityManagerFactory emf) {
        return emf.createEntityManager();
    }

    public void close(@Disposes @Database EntityManager em) {
        em.close();
    }
}

// Usage
@Inject @Database
private EntityManager em;
```

### Q45: What are CDI interceptors and how do they differ from EJB interceptors?

```java
// Define interceptor binding
@InterceptorBinding
@Target({TYPE, METHOD})
@Retention(RUNTIME)
public @interface Logged {}

// Define interceptor
@Logged
@Interceptor
public class LoggingInterceptor {
    @AroundInvoke
    public Object log(InvocationContext ctx) throws Exception {
        System.out.println("Entering: " + ctx.getMethod().getName());
        return ctx.proceed();
    }
}

// Use
@ApplicationScoped
public class MyService {
    @Logged
    public void doSomething() { }
}
```

CDI interceptors require `beans.xml` to enable them, or `@Priority`. Multiple interceptors can stack via annotation order or `@InterceptorBinding` ordering.

### Q46: What are CDI decorators?

**Answer:** Decorators intercept all beans of a given interface type. Unlike interceptors (cross-cutting), decorators implement business logic.

```java
// Interface
public interface PaymentProcessor {
    void processPayment(Payment p);
}

// Decorator
@Decorator
@Priority(Interceptor.Priority.APPLICATION)
public class PaymentDecorator implements PaymentProcessor {
    @Inject @Delegate
    private PaymentProcessor delegate;

    public void processPayment(Payment p) {
        validate(p);
        delegate.processPayment(p);
        notify(p);
    }
}
```

### Q47: Explain the CDI event system.

```java
// Event payload
public class OrderPlaced {
    private Order order;
    // constructor, getters
}

// Firing event
@Inject
private Event<OrderPlaced> orderEvent;

public void placeOrder(Order order) {
    // ... business logic
    orderEvent.fire(new OrderPlaced(order));
}

// Observing event
@ApplicationScoped
public class EmailNotification {
    public void onOrderPlaced(@Observes OrderPlaced event) {
        sendEmail(event.getOrder().getCustomer());
    }
}
```

**Edge case:** `@Observes(during = TransactionPhase.AFTER_COMPLETION)` — observer runs after transaction completes.

---

## 7. Bean Validation

### Q48: What is Bean Validation (Jakarta Validation)?

**Answer:** Bean Validation provides a standard way to validate Java beans via annotations. JSR 380 (Bean Validation 2.0+) / Jakarta Validation.

```java
public class User {
    @NotNull
    @Size(min = 3, max = 50)
    private String name;

    @Email
    private String email;

    @Min(18) @Max(120)
    private int age;

    @Pattern(regexp = "\\d{3}-\\d{3}-\\d{4}")
    private String phone;

    @Positive
    private BigDecimal salary;
}
```

### Q49: How do you create a custom validator?

```java
// Annotation
@Target({FIELD, METHOD, PARAMETER})
@Retention(RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface ValidPassword {
    String message() default "Password must contain uppercase, lowercase, digit";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validator
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return false;
        return value.matches(".*[A-Z].*") &&
               value.matches(".*[a-z].*") &&
               value.matches(".*\\d.*");
    }
}
```

### Q50: What are validation groups?

```java
public interface Create {}
public interface Update {}

public class User {
    @NotNull(groups = Create.class)
    @Null(groups = Update.class)
    private Long id;

    @NotNull(groups = {Create.class, Update.class})
    private String name;
}

// Usage
Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
Set<ConstraintViolation<User>> violations = validator.validate(user, Create.class);
```

---

## 8. JAX-RS / REST

### Q51: How do you create a JAX-RS REST endpoint?

```java
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject
    private UserService userService;

    @GET
    public List<User> getAll(@QueryParam("page") @DefaultValue("0") int page) {
        return userService.findAll(page);
    }

    @GET
    @Path("/{id}")
    public User getById(@PathParam("id") Long id) {
        return userService.findById(id);
    }

    @POST
    public Response create(User user, @Context UriInfo uriInfo) {
        User created = userService.create(user);
        URI uri = uriInfo.getAbsolutePathBuilder().path(created.getId().toString()).build();
        return Response.created(uri).entity(created).build();
    }

    @PUT
    @Path("/{id}")
    public User update(@PathParam("id") Long id, User user) {
        return userService.update(id, user);
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        userService.delete(id);
        return Response.noContent().build();
    }
}
```

### Q52: What are JAX-RS filters and interceptors?

```java
// Request filter
@Provider
@Logged
public class LoggingFilter implements ContainerRequestFilter {
    @Override
    public void filter(ContainerRequestContext ctx) throws IOException {
        System.out.println("Request: " + ctx.getMethod() + " " + ctx.getUriInfo().getPath());
    }
}

// Response filter
@Provider
public class CORSFilter implements ContainerResponseFilter {
    @Override
    public void filter(ContainerRequestContext req, ContainerResponseContext resp) {
        resp.getHeaders().add("Access-Control-Allow-Origin", "*");
    }
}
```

### Q53: Explain HATEOAS in REST APIs.

**Answer:** HATEOAS (Hypermedia As The Engine Of Application State) includes links in API responses to guide clients.

```java
@GET
@Path("/{id}")
public Response getOrder(@PathParam("id") Long id) {
    Order order = orderService.findById(id);
    EntityModel<Order> model = EntityModel.of(order,
        linkTo(methodOn(OrderResource.class).getOrder(id)).withSelfRel(),
        linkTo(methodOn(OrderResource.class).cancel(id)).withRel("cancel"),
        linkTo(methodOn(OrderResource.class).getItems(id)).withRel("items")
    );
    return Response.ok(model).build();
}
```

---

## 9. JSON-B / JSON-P

### Q54: What is the difference between JSON-B and JSON-P?

**Answer:**

- **JSON-B (JSON Binding)**: High-level API for binding Java objects to/from JSON. Uses annotations like `@JsonbProperty`, `@JsonbDateFormat`.
- **JSON-P (JSON Processing)**: Low-level streaming and object model API. Works with `JsonObject`, `JsonArray`, `JsonParser`, `JsonGenerator`.

```java
// JSON-B
@GET
@Path("/user")
public User getUser() {
    return new User("John", "john@test.com");
}

// JSON-P
@GET
@Path("/jsonp")
public JsonObject getJson() {
    return Json.createObjectBuilder()
        .add("name", "John")
        .add("email", "john@test.com")
        .build();
}
```

---

## 10. Security

### Q55: How do you secure a Jakarta EE application with declarative security?

```java
// secured endpoint with @RolesAllowed
@Path("/admin")
@DeclareRoles({"ADMIN", "USER"})
public class AdminResource {
    @GET
    @RolesAllowed("ADMIN")
    public Response adminData() {
        return Response.ok("sensitive data").build();
    }
}
```

```xml
<!-- web.xml security constraints -->
<security-constraint>
    <web-resource-collection>
        <web-resource-name>Admin</web-resource-name>
        <url-pattern>/admin/*</url-pattern>
    </web-resource-collection>
    <auth-constraint>
        <role-name>ADMIN</role-name>
    </auth-constraint>
</security-constraint>
```

### Q56: What is JAAS (Java Authentication and Authorization Service)?

**Answer:** JAAS is a pluggable security framework. Components:
- **LoginModule**: Authenticates users against a store (LDAP, DB, file).
- **Subject**: Represents the authenticated user.
- **Principal**: Represents a named identity.

### Q57: Explain Jakarta Identity Stores.

```java
// LDAP identity store
@ApplicationScoped
public class LdapIdentityStore implements IdentityStore {
    @Override
    public CredentialValidationResult validate(Credential credential) {
        // Validate against LDAP
        return new CredentialValidationResult("user", Set.of("ADMIN"));
    }

    @Override
    public Set<String> getCallerGroups(CredentialValidationResult result) {
        return result.getCallerGroups();
    }
}
```

### Q58: How does OIDC (OpenID Connect) integrate with Jakarta Security?

**Answer:** Jakarta Security 2.0+ supports OpenID Connect via the `@OpenIdAuthenticationMechanismDefinition` annotation.

```java
@ApplicationScoped
@OpenIdAuthenticationMechanismDefinition(
    clientId = "${oidc.clientId}",
    clientSecret = "${oidc.secret}",
    providerURI = "${oidc.issuer}",
    redirectURI = "${baseURL}/callback",
    scopes = {"openid", "profile", "email"}
)
public class SecurityConfig { }
```

---

## 11. Transactions

### Q59: What is JTA and how does @Transactional work?

**Answer:** JTA (Java Transaction API) manages distributed transactions. In Jakarta EE, use `@Transactional` (from Jakarta Transactions) on CDI beans.

```java
@RequestScoped
public class OrderService {
    @Inject
    private OrderRepository orderRepo;

    @Inject
    private PaymentService paymentService;

    @Transactional(Transactional.TxType.REQUIRED)
    public void placeOrder(Order order) {
        orderRepo.save(order);
        paymentService.charge(order.getAmount());
    }
}
```

### Q60: What are XA transactions and when are they needed?

**Answer:** XA transactions span multiple resources (e.g., two databases, or a database and a JMS broker). They use a two-phase commit protocol: prepare phase (all resources agree) and commit phase (all commit). If any resource fails, all roll back.

```java
// Both JMS and JPA participate in the same XA transaction
@Stateless
public class OrderProcessor {
    @Inject
    private JMSContext jmsContext;

    @Resource(lookup = "jms/orderQueue")
    private Queue queue;

    @PersistenceContext
    private EntityManager em;

    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public void process(Order order) {
        em.persist(order);  // DB insert
        jmsContext.createProducer().send(queue, order.getId());  // JMS message
        // Both commit or both roll back
    }
}
```

---

## 12. Company-Specific Sections

### Oracle (WebLogic, EclipseLink, ADF)

- **Unique topics**: WebLogic-specific deployment descriptors (`weblogic.xml`), EclipseLink JPA extensions (customizer, descriptor events), ADF binding layer.
- **Interview questions**: "How do you configure WebLogic data sources?" "Explain EclipseLink cache coordination across clusters." "How does WebLogic JMS clustering work?"

### IBM (WebSphere, OpenLiberty)

- **Unique topics**: IBM HTTP Server, WOLA (WebSphere Optimized Local Adapter), SIP servlets, z/OS integration.
- **Interview questions**: "How do you deploy to WebSphere Network Deployment?" "What is the WebSphere classloader hierarchy?" "How does OpenLiberty differ from traditional WebSphere?"

### Red Hat (WildFly, JBoss EAP)

- **Unique topics**: WildFly management CLI, JBoss Modules (modular classloading), Hibernate as JPA provider.
- **Interview questions**: "How do you configure WildFly datasources using the CLI?" "Explain JBoss transaction isolation." "What is the WildFly Elytron security subsystem?"

### SAP (NetWeaver AS Java)

- **Unique topics**: SAP J2EE Engine, JCo (SAP Java Connector), Visual Composer.
- **Interview questions**: "How does SAP NetWeaver AS Java integrate with ABAP?" "What is the SAP JCo architecture?"

### Consulting Firms (Capgemini, Accenture)

- **Focus**: Architecture review, migration assessment, modernization.
- **Interview questions**: "How would you migrate a 10-year-old Java EE app to the cloud?" "Compare modernization strategies: re-platform vs re-architect vs replace."

### Financial Services (JPMorgan, Goldman Sachs, Banks)

- **Focus**: JMS and messaging (MQs), transaction management, high availability.
- **Interview questions**: "How do you ensure exactly-once delivery in JMS?" "Design a distributed transaction system for a funds transfer."

---

## 13. Spring Comparison

### Q61: How do you answer "Jakarta EE vs Spring" in an interview?

**Answer framework:**

1. **Agree both are valid**: "Both are mature, production-proven enterprise Java platforms."
2. **Different philosophies**: "Jakarta EE is a specification with multiple vendors; Spring is a single-vendor framework."
3. **Feature comparison**:
   - DI: CDI vs Spring IoC (very similar; Spring supports @Inject and CDI annotations)
   - REST: JAX-RS vs Spring MVC (Spring has broader ecosystem)
   - Persistence: Both use JPA (Spring adds Spring Data JPA)
   - Security: Jakarta Security vs Spring Security (Spring Security is more mature)
   - Microservices: Spring Boot/Cloud ecosystem is more developed
4. **Context matters**: "For a greenfield microservice, I'd choose Spring Boot. For maintaining an existing app on WebSphere, Jakarta EE is the right choice."
5. **Show depth**: "Jakarta EE 10+ has modernized significantly — CDI 4.0, JAX-RS 3.0, JSON-B 3.0. The gap is narrowing."

**Avoid:** Don't trash either technology. Show you understand the trade-offs and can work with both.
