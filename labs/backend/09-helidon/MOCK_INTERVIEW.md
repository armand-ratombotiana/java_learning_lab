# Mock Interview: Helidon (Lab 09)

**Role:** Backend Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Helidon and how does it compare with Spring Boot?

**Candidate:** Helidon is a collection of Java libraries for building microservices by Oracle. It comes in two flavors:
- **Helidon SE:** Functional, reactive, uses java.util.concurrent.Flow APIs — no reflection, no annotations, programmatic routing
- **Helidon MP:** MicroProfile-compatible, uses JAX-RS, CDI, JSON-P — familiar to Jakarta EE developers

Compared to Spring Boot, Helidon MP offers a standards-based approach (MicroProfile), while Spring Boot has its own ecosystem. Helidon is lighter weight and integrates well with Oracle Cloud and Oracle Database.

**Interviewer:** Explain the difference between Helidon SE and MP. When would you use each?

**Candidate:**

| Aspect | Helidon SE | Helidon MP |
|--------|-----------|------------|
| Programming model | Functional/reactive | Declarative/annotations |
| API surface | Minimal, explicit | JAX-RS, CDI, JSON-P |
| Startup time | ~70ms | ~500ms |
| Memory | ~10MB | ~30MB |
| Learning curve | Steeper (reactive) | Familiar (Java EE devs) |
| Best for | Performance-critical, reactive apps | Standards-compliant microservices |

Choose SE when you need maximum performance and minimal footprint. Choose MP when your team has Jakarta EE experience or you need MicroProfile compatibility (Config, Fault Tolerance, JWT).

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you build a REST API with Helidon SE?

**Candidate:** Helidon SE uses a builder-based, functional routing approach:

```java
public class Main {
    public static void main(String[] args) {
        WebServer.builder()
            .routing(routing -> routing
                .get("/api/users", (req, res) -> res.send(userService.findAll()))
                .get("/api/users/{id}", (req, res) -> {
                    String id = req.path().param("id");
                    res.send(userService.findById(id));
                })
                .post("/api/users", (req, res) -> {
                    User user = req.content().as(User.class);
                    res.status(201).send(userService.create(user));
                })
            )
            .port(8080)
            .build()
            .start();
    }
}
```

No controllers, no annotations, no reflection. Everything is explicit and reactive. Request/response handlers are `java.util.function.BiConsumer` lambdas.

**Interviewer:** How does Helidon integrate with MicroProfile specs?

**Candidate:** Helidon MP implements MicroProfile 6.0+ specs including:
- **MicroProfile Config:** Externalized configuration with multiple sources (system properties, env vars, custom)
- **MicroProfile Fault Tolerance:** `@Retry`, `@CircuitBreaker`, `@Bulkhead`, `@Timeout`, `@Fallback`
- **MicroProfile JWT:** RBAC with JWT tokens
- **MicroProfile OpenAPI:** OpenAPI specification generation
- **MicroProfile Rest Client:** Type-safe REST client interfaces
- **MicroProfile Health:** Health check procedures
- **MicroProfile Metrics:** Monitoring and metrics

This makes it attractive for Oracle Cloud deployments where MicroProfile is the standard.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your team is building a new microservice for Oracle Cloud. Compare Helidon, Spring Boot, and Quarkus for this use case.

**Candidate:** For Oracle Cloud deployment:

| Criterion | Helidon | Spring Boot | Quarkus |
|-----------|---------|-------------|---------|
| OCI Integration | Best (Oracle-built) | Good (SDK available) | Good (SDK available) |
| Oracle DB | Native (UCP, R2DBC) | Via JDBC/Hibernate | Via JDBC/Hibernate |
| MicroProfile | Native | Not supported (use Spring equivalents) | Supported |
| Startup time | 70ms (SE) | 2-3s | 500ms |
| Memory | 10MB (SE) | 100MB | 30MB |
| Learning curve | Moderate | Low (popular) | Moderate |

**Recommendation:**
- **Helidon SE** for Oracle Cloud Functions (FaaS), edge services, API gateways — where cold start and memory are critical
- **Helidon MP** for Oracle Cloud microservices when team knows Jakarta EE or requiring MicroProfile standards
- **Spring Boot** for Oracle Cloud when team is Spring-experienced and needs the full Spring ecosystem
- **Quarkus** as a middle ground — faster than Spring Boot, more ecosystem than Helidon

**Interviewer:** How would you integrate Helidon with Oracle Database 23c? What about the JSON-relational duality views?

**Candidate:** Helidon provides:
```java
// Using Helidon DB Client (reactive)
DbClient dbClient = DbClient.builder()
    .connection(dbConfig.connection())
    .build();

dbClient.execute()
    .createNamedQuery("find-users")
    .addParam("dept", "engineering")
    .execute()
    .forEach(data -> {
        String name = data.column("NAME").as(String.class);
        JsonObject details = data.column("DETAILS").as(JsonObject.class);
    });
```

For Oracle DB 23c JSON-relational duality views, Helidon's JSON-P support maps naturally. A duality view allows both relational and document access to the same data. The view definition would be in SQL, and Helidon consumes it as either relational tuples or JSON documents via `oracle.jdbc.OracleConnection` and `UCP` (Unified Connection Pool).

**Interviewer:** Compare Jakarta EE (through Helidon MP) vs Spring Boot for building enterprise microservices.

**Candidate:** See ACADEMY_INTERVIEW_GUIDE.md for the full comparison table. The key differences boil down to: Spring Boot offers a cohesive, opinionated ecosystem with richer documentation and community. Jakarta EE/MicroProfile offers a vendor-neutral standard that allows portability between implementations (Helidon, Open Liberty, WildFly, Payara).

For Oracle shops: Helidon MP is the natural choice given Oracle's investment in MicroProfile and Jakarta EE. For non-Oracle shops: Spring Boot's ecosystem and tooling are generally more productive.

---

## Interviewer Feedback

**Strengths:** Good framework comparison, practical OCI knowledge, understands reactive SE model  
**Areas to Improve:** Could discuss Helidon's gRPC and Kafka integration support  
**Verdict:** Hire

---

*Lab 09 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
