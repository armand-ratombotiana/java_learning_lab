# Mock Interview: Micronaut (Lab 08)

**Role:** Backend Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Micronaut and how does it differ from Spring Boot?

**Candidate:** Micronaut is a modern, JVM-based framework designed for building modular, easily testable microservices. Key differences from Spring Boot:

| Aspect | Spring Boot | Micronaut |
|--------|------------|-----------|
| DI mechanism | Runtime reflection + proxies | Compile-time annotation processing |
| Startup time | Seconds | Milliseconds |
| Memory footprint | Higher | Lower (optimized for GraalVM) |
| AOT support | Spring AOT (since 3.x) | Native compile-time processing |
| HTTP server | Tomcat/Jetty/Undertow | Netty (built-in) |
| Configuration | Spring Environment | Micronaut's configuration system |
| GraalVM | Supported (with config) | First-class support |

The compile-time DI is Micronaut's key differentiator — it generates the DI wiring at compilation time rather than using reflection at runtime, resulting in faster startup and lower memory usage.

**Interviewer:** How does Micronaut's compile-time DI work?

**Candidate:** Micronaut processes `@Singleton`, `@Inject`, `@Named`, and other DI annotations at compile time using Java annotation processors. It generates:
- Bean definition classes for each bean
- A `BeanContext` that wires everything together
- No reflection-based component scanning at runtime
- Applications start up in milliseconds because there's no classpath scanning, reflection, or proxy generation at boot

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you create a REST API with Micronaut? Compare with Spring Boot.

**Candidate:** Micronaut uses a similar annotation model but without the runtime overhead:

```java
@Controller("/api/users")
public class UserController {
    
    @Get("/{id}")
    public User getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @Post
    @Status(HttpStatus.CREATED)
    public User createUser(@Body @Valid UserCreateCommand cmd) {
        return userService.create(cmd);
    }
}
```

Key differences from Spring Boot:
- `@Get` instead of `@GetMapping` (though `@GetMapping` also works via compatibility)
- `@Body` instead of `@RequestBody`
- `@Status` for HTTP response status
- No `ResponseEntity` needed — return the object directly
- Validation uses `@Valid` like Spring, but Micronaut compiles validation into the bytecode

**Interviewer:** How does Micronaut handle configuration and environment properties?

**Candidate:** Micronaut has a robust configuration system similar to Spring Boot:
- `application.yml` / `application.properties`
- Profile-specific configs: `application-dev.yml`
- Environment variables: `MICRONAUT_APPLICATION_JSON`
- Distributed configuration: Consul, Kubernetes ConfigMap, Oracle Cloud Vault
- `@ConfigurationProperties` with `@ConfigurationBuilder` for type-safe properties
- `@Value` for individual property injection
- Property placeholder resolution with `${...}` syntax

Config is also optimized at compile time — properties accessed via `@Value` are resolved eagerly when possible.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** When would you choose Micronaut over Spring Boot for a new project? What are the trade-offs?

**Candidate:** I'd choose Micronaut when:
1. **Serverless / FaaS:** Startup time is critical (AWS Lambda cold starts)
2. **IoT / Edge devices:** Memory constraints (64-256MB RAM)
3. **Low-latency services:** Every millisecond of startup matters in auto-scaling scenarios
4. **GraalVM native compilation:** Micronaut's compile-time approach works naturally with GraalVM
5. **High-density deployment:** 50+ microservices per Kubernetes node

I'd choose Spring Boot when:
1. **Rich ecosystem needed:** Spring Cloud, Spring Security, Spring Data integration
2. **Team familiarity:** Larger talent pool for Spring developers
3. **Complex integrations:** Spring has broader library support
4. **Maturity and documentation:** Spring Boot is more battle-tested
5. **Legacy system integration:** Existing Spring/Java EE applications

**Trade-offs:**
- Micronaut has a smaller ecosystem (fewer third-party integrations)
- Less community support and fewer learning resources
- Annotation processing can cause IDE issues
- Spring Boot's runtime reflection enables more flexibility (hot-reload of configuration)
- Micronaut's compile-time approach means changes require recompilation

**Interviewer:** How does Micronaut handle AOT compilation and GraalVM integration differently from Spring Boot 3.x?

**Candidate:** Micronaut was designed from the ground up for AOT compilation. Every annotation processed at compile time generates Java source code. When building a GraalVM native image:
- Micronaut generates all reflection metadata automatically
- No need for `--initialize-at-build-time` or `reflect-config.json` (in most cases)
- The resulting native image starts in ~10ms with ~10MB memory

Spring Boot 3.x added Spring AOT engine for GraalVM support, but it's an afterthought — the engine analyzes the application and generates hints for GraalVM. It works well but is less seamless than Micronaut's approach. Spring AOT requires configuration hints for dynamic features (proxies, serialization, reflection). Micronaut avoids these entirely through compile-time processing.

---

## Interviewer Feedback

**Strengths:** Clear understanding of compile-time DI, good framework comparison, practical use case analysis  
**Areas to Improve:** Could discuss Micronaut's HTTP client and declarative client annotations  
**Verdict:** Hire

---

*Lab 08 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
