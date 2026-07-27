# Migration Guide: Jakarta EE from Java EE, Modernization, and Framework Migration

Interviewers love migration questions because they test:
- **Depth of knowledge**: Do you understand what changed between versions?
- **Practical experience**: Have you actually done a migration?
- **Architectural thinking**: Do you understand trade-offs and risks?

---

## Table of Contents

1. [Java EE → Jakarta EE: What Changed](#1-java-ee--jakarta-ee-what-changed)
2. [Migration Steps: javax.* to jakarta.*](#2-migration-steps-javax-to-jakarta)
3. [Jakarta EE → Spring Boot Migration](#3-jakarta-ee--spring-boot-migration)
4. [Database Migration with JPA](#4-database-migration-with-jpa)
5. [Modernizing Legacy Jakarta EE Applications](#5-modernizing-legacy-jakarta-ee-applications)
6. [Containerization and Cloud Migration](#6-containerization-and-cloud-migration)
7. [Common Pitfalls and Risks](#7-common-pitfalls-and-risks)

---

## 1. Java EE → Jakarta EE: What Changed

### The Namespace Change

In 2017, Oracle transferred Java EE to the Eclipse Foundation. The name changed to **Jakarta EE**. The critical change: the namespace shifted from `javax.*` to `jakarta.*`.

| Old (Java EE) | New (Jakarta EE) |
|---------------|------------------|
| `javax.servlet` | `jakarta.servlet` |
| `javax.persistence` | `jakarta.persistence` |
| `javax.ejb` | `jakarta.ejb` |
| `javax.jms` | `jakarta.jms` |
| `javax.enterprise.context` | `jakarta.enterprise.context` |
| `javax.ws.rs` | `jakarta.ws.rs` |
| `javax.validation` | `jakarta.validation` |
| `javax.transaction` | `jakarta.transaction` |
| `javax.annotation.security` | `jakarta.annotation.security` |

### Version Mapping

| Java EE | Jakarta EE |
|---------|------------|
| Java EE 8 (last Oracle version) | Jakarta EE 8 (identical, renamed) |
| — | Jakarta EE 9 (namespace `javax.*` → `jakarta.*`, no API changes) |
| — | Jakarta EE 9.1 (added JDK 11 support) |
| — | Jakarta EE 10 (new features, CDI 4.0, JAX-RS 3.0, Servlet 6.0) |
| — | Jakarta EE 11 (latest, JDK 21 baseline) |

### Direct Package Mapping Table for Migration

```java
// Before (Java EE 8)
import javax.persistence.Entity;
import javax.ws.rs.GET;
import javax.servlet.annotation.WebServlet;

// After (Jakarta EE 9+)
import jakarta.persistence.Entity;
import jakarta.ws.rs.GET;
import jakarta.servlet.annotation.WebServlet;
```

---

## 2. Migration Steps: javax.* to jakarta.*

### Automated Migration with Eclipse Transformer

```xml
<!-- Maven plugin for automatic namespace migration -->
<plugin>
    <groupId>org.eclipse.transformer</groupId>
    <artifactId>org.eclipse.transformer.maven</artifactId>
    <version>1.0.0</version>
    <executions>
        <execution>
            <goals><goal>run</goal></goals>
        </execution>
    </executions>
</plugin>
```

### Manual Migration Steps

1. **Update Maven/Gradle dependencies**: Replace Java EE 8 APIs with Jakarta EE 10+ APIs:

```xml
<!-- Old -->
<dependency>
    <groupId>javax</groupId>
    <artifactId>javaee-api</artifactId>
    <version>8.0</version>
    <scope>provided</scope>
</dependency>

<!-- New -->
<dependency>
    <groupId>jakarta.platform</groupId>
    <artifactId>jakarta.jakartaee-api</artifactId>
    <version>10.0.0</version>
    <scope>provided</scope>
</dependency>
```

2. **Replace all imports**: Use sed/find-and-replace:
   - `javax.persistence` → `jakarta.persistence`
   - `javax.ejb` → `jakarta.ejb`
   - `javax.servlet` → `jakarta.servlet`
   - `javax.ws.rs` → `jakarta.ws.rs`
   - `javax.enterprise` → `jakarta.enterprise`
   - `javax.validation` → `jakarta.validation`
   - `javax.transaction` → `jakarta.transaction`
   - `javax.annotation` → `jakarta.annotation`
   - `javax.jms` → `jakarta.jms`
   - `javax.faces` → `jakarta.faces`
   - `javax.mail` → `jakarta.mail`

3. **Update persistence.xml**:

```xml
<!-- Old -->
<persistence xmlns="http://xmlns.jcp.org/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/persistence
                                 http://xmlns.jcp.org/xml/ns/persistence/persistence_2_2.xsd"
             version="2.2">

<!-- New -->
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
                                 https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
```

4. **Update web.xml**:

```xml
<!-- Old -->
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                             http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

<!-- New -->
<web-app xmlns="https://jakarta.ee/xml/ns/jakartaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="https://jakarta.ee/xml/ns/jakartaee
                             https://jakarta.ee/xml/ns/jakartaee/web-app_6_0.xsd"
         version="6.0">
```

### Testing After Migration

1. **Compile**: The namespace change is source-incompatible but binary-compatible. Build first.
2. **Runtime**: Deploy to Jakarta EE 10+ server (WildFly 27+, Payara 6+, TomEE 9+, GlassFish 7+).
3. **Integration tests**: Verify JPA queries, JMS connectivity, EJBs, security, and transactions.

**Common issue:** Third-party libraries that still depend on `javax.*` — versions 4.0+ of Hibernate, Weld, etc. support Jakarta EE.

---

## 3. Jakarta EE → Spring Boot Migration

### Why Companies Migrate

- Faster development cycles (Spring Boot's auto-configuration)
- Easier microservice architecture
- Smaller footprint (embedded Tomcat vs full app server)
- Easier cloud deployment
- Larger developer pool (Spring Boot is more widely adopted)

### Migration Strategy: Step by Step

#### Phase 1: Assessment

```java
// Identify all Jakarta EE components in your application:
// 1. EJBs → Service classes + @Transactional
// 2. JPA Entities (these stay — JPA is same API)
// 3. JMS → Spring JmsTemplate + @JmsListener
// 4. JAX-RS → Spring MVC @RestController
// 5. CDI Beans → Spring @Component / @Service
// 6. Security → Spring Security
// 7. Batch → Spring Batch
```

#### Phase 2: Replace EJBs with Spring Services

```java
// Jakarta EE (EJB)
@Stateless
public class UserService {
    @PersistenceContext
    private EntityManager em;

    public User findById(Long id) {
        return em.find(User.class, id);
    }
}

// Spring Boot
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
}
```

#### Phase 3: Replace JAX-RS with Spring MVC

```java
// Jakarta EE (JAX-RS)
@Path("/api/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    @Inject private UserService userService;

    @GET
    public List<User> getAll() { return userService.findAll(); }
}

// Spring Boot (Spring MVC)
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() { return userService.findAll(); }
}
```

#### Phase 4: Replace JMS with Spring JMS

```java
// Jakarta EE (JMS + MDB)
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup",
                              propertyValue = "jms/queue")
})
public class OrderConsumer implements MessageListener {
    public void onMessage(Message msg) { /* process */ }
}

// Spring Boot (JmsTemplate + @JmsListener)
@Component
public class OrderConsumer {
    @JmsListener(destination = "order.queue")
    public void receiveOrder(String orderJson) { /* process */ }
}
```

#### Phase 5: Replace Security

```java
// Jakarta EE
@WebServlet("/admin/*")
@ServletSecurity(@HttpConstraint(rolesAllowed = {"ADMIN"}))
public class AdminServlet extends HttpServlet { }

// Spring Boot
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        );
        return http.build();
    }
}
```

### The Strangler Fig Pattern

For gradual migration without downtime:

1. **Identify a bounded context** to extract first (e.g., "User Management").
2. **Create an anti-corruption layer** — a translation layer between old and new systems.
3. **Extract the module** into a Spring Boot service.
4. **Route traffic** to the new service using a proxy or load balancer.
5. **Repeat** until the monolith is hollowed out.

```java
// Anti-corruption layer — bridges Jakarta EE and Spring Boot
@Stateless
public class LegacyUserFacade {
    @EJB // Points to old EJB while new service is being built
    private UserServiceOld oldService;

    @Resource
    private Context ctx; // JNDI to look up new Spring Boot service via EJB or REST

    public User findUser(Long id) {
        try {
            // Try to use new service first
            UserServiceNew newService = (UserServiceNew)
                ctx.lookup("java:global/spring-service/UserServiceNew");
            return newService.findById(id);
        } catch (NamingException e) {
            // Fall back to old implementation
            return oldService.findById(id);
        }
    }
}
```

---

## 4. Database Migration with JPA

### Schema Migration When Changing ORM

```java
// Jakarta EE JPA (Hibernate as provider)
// — Schema managed by Hibernate ddl-auto or Flyway/Liquibase

// Spring Boot JPA
// — Same JPA annotations, but Spring Data JPA adds repository abstraction
```

For database-level migrations (changing from Jakarta EE app to Spring Boot app):

1. **Keep the same database**: JPA entities are the same — no schema change needed.
2. **Use Flyway or Liquibase** for version-controlled schema changes.
3. **Test with production-like data volume** — query plans may differ between Hibernate configs.

---

## 5. Modernizing Legacy Jakarta EE Applications

### Modernization Approaches

| Approach | Effort | Risk | Benefit |
|----------|--------|------|---------|
| **Rehost (Lift & Shift)** | Low | Low | Quick cloud migration, minimal changes |
| **Replatform** | Medium | Low | Optimize for cloud (containers, managed DB) |
| **Refactor** | High | Medium | Modularize monolith into microservices |
| **Re-architect** | Very high | High | Full redesign |
| **Replace** | Very high | Very high | Buy COTS or SaaS |

### Incremental Modernization Checklist

1. **Containerize first** — package the Jakarta EE app in Docker without code changes.
2. **Externalize configuration** — move JNDI lookups to environment variables / ConfigMap.
3. **Replace JNDI with DI** — JNDI lookups are hard to test; replace with `@Inject` or `@Autowired`.
4. **Extract stateless services** — start with services that have no EJB state.
5. **Replace EJBs with CDI** — CDI beans are lighter and more testable.
6. **Add health checks and metrics** — `@Health` (MicroProfile) or Spring Actuator.
7. **Move from XML to annotations** — eliminate `web.xml`, `ejb-jar.xml` deployment descriptors where possible.

### Legacy Code Patterns to Refactor

```java
// ANTI-PATTERN: JNDI lookup everywhere
public class ReportGenerator {
    public void generate() {
        try {
            Context ctx = new InitialContext();
            UserService userService = (UserService) ctx.lookup("java:global/app/UserService");
            // ...
        } catch (NamingException e) { /* handle */ }
    }
}

// REFACTORED: Dependency injection
@ApplicationScoped
public class ReportGenerator {
    @Inject
    private UserService userService;

    public void generate() { /* ... */ }
}
```

---

## 6. Containerization and Cloud Migration

### Dockerfile for Jakarta EE App (WildFly)

```dockerfile
FROM quay.io/wildfly/wildfly:27.0.0.Final-jdk17

ENX WILDFLY_HOME=/opt/jboss/wildfly
ENX DEPLOYMENT_DIR=${WILDFLY_HOME}/standalone/deployments/

COPY target/myapp.ear ${DEPLOYMENT_DIR}

# Add datasource and JMS configuration via CLI script
COPY configure.cli /tmp/
RUN $WILDFLY_HOME/bin/jboss-cli.sh --file=/tmp/configure.cli

CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0"]
```

### Kubernetes Deployment Challenges

| Jakarta EE Feature | K8s Challenge | Solution |
|-------------------|---------------|----------|
| Session replication | Pods are ephemeral | External session store (Redis) |
| JMS / MDB | JMS broker outside cluster | Deploy ActiveMQ/Artemis in cluster |
| XA transactions | Coordinator must survive restarts | Narayana transaction manager in pod |
| Clustered EJBs | Pod-to-pod communication | Headless service + DNS discovery |
| JNDI | Not meaningful in K8s | CDI / Spring DI instead |
| Node IP / port mapping | Dynamic IPs | Service discovery via DNS |

### Cloud Migration: Three Phases

**Phase 1: Lift and Shift (Rehost)**
- Dockerize the Jakarta EE application server
- Move to VMs or container services in cloud
- Minimal code changes, but limited benefits

**Phase 2: Optimize (Replatform)**
- Replace application server with embedded runtime (Tomcat, Undertow)
- Move from JMS to cloud messaging (Azure Service Bus, AWS SQS)
- Switch from JPA to managed DB services (RDS, Cloud SQL)

**Phase 3: Modernize (Refactor)**
- Break monolith into microservices
- Replace EJBs with REST services
- Replace JTA with saga pattern
- Move to Spring Boot or Quarkus

---

## 7. Common Pitfalls and Risks

### Pitfall 1: Classloader Issues

Jakarta EE application servers have complex classloader hierarchies. When migrating between servers or to Spring Boot, classloader issues (ClassNotFoundException, NoSuchMethodError) are common.

**Solution:** Check dependency versions. Use `mvn dependency:tree` or `gradle dependencies` to identify conflicts.

### Pitfall 2: Transaction Management Differences

| Jakarta EE | Spring Boot |
|------------|-------------|
| `@TransactionAttribute(REQUIRED)` | `@Transactional(REQUIRED)` |
| `UserTransaction.begin()` | `TransactionTemplate.execute()` |
| JTA (UserTransaction) | Spring Transaction Manager |
| XA via JTA + app server | XA via Atomikos/ Bitronix/ Narayana |

**Solution:** In Spring Boot, you need a JTA transaction manager explicitly (Atomikos, Narayana) for XA. Simple single-resource transactions only need `@Transactional` + `DataSourceTransactionManager`.

### Pitfall 3: Security

Jakarta EE declarative security (`web.xml`, `@RolesAllowed`) does not map directly to Spring Security. Spring Security uses a filter chain approach.

**Solution:** Rewrite security configuration using Spring Security's `SecurityFilterChain` DSL.

### Pitfall 4: JNDI Dependencies

Many Jakarta EE apps rely on JNDI for data sources, JMS connection factories, and EJB lookups. Spring Boot uses application.properties/yml.

**Solution:** Externalize JNDI lookups and replace with `@ConfigurationProperties` or `@Value`.

```java
// Jakarta EE JNDI data source
@Resource(lookup = "jdbc/primaryDS")
private DataSource ds;

// Spring Boot data source
spring.datasource.url=jdbc:postgresql://localhost:5432/mydb
spring.datasource.username=admin
spring.datasource.password=${DB_PASSWORD}
```

### Pitfall 5: Testing

Jakarta EE applications are harder to unit test because of container dependencies (JNDI, EJB container, persistence context). Spring Boot's testing support is far superior.

**Solution:** Use Arquillian for Jakarta EE integration tests, or migrate to Spring Boot for easier testing.
