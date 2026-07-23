# Mock Interview: Quarkus (Lab 10)

**Role:** Backend Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is Quarkus and what problem does it solve?

**Candidate:** Quarkus is a Kubernetes-native Java framework tailored for GraalVM and OpenJDK HotSpot. It addresses the traditional Java problems in containerized environments: slow startup times and high memory usage. Quarkus achieves this through:
- **Build-time processing:** Annotation processing at build time (like Micronaut)
- **GraalVM native compilation:** AOT compilation for instant startup (~50ms)
- **Substrate VM:** Reduced memory footprint (~10MB per instance)
- **Live reload:** Development mode with hot reload (< 1 second iteration)

**Interviewer:** How does Quarkus compare to Spring Boot for building microservices?

**Candidate:** Quarkus excels in container-first environments. Key advantages:
- Startup: ~50ms (native) vs ~3s (Spring Boot)
- Memory: ~10MB vs ~100MB
- Image size: ~20MB vs ~200MB

However, Spring Boot has richer ecosystem, more community support, and broader third-party integrations. Quarkus uses standard APIs (JAX-RS, CDI, JPA) with extensions for popular libraries. For new cloud-native microservices in Kubernetes, Quarkus is compelling. For complex enterprise applications with many integrations, Spring Boot is often safer.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Quarkus achieve its fast startup through build-time processing?

**Candidate:** Quarkus shifts work from runtime to build time:
1. **Annotation processing:** CDI bean discovery, JPA entity scanning, REST endpoint registration all happen at build time
2. **Bytecode recording:** Instead of runtime reflection, Quarkus records bytecode during build that recreates the same state at startup
3. **Closed-world assumption:** Quarkus assumes all beans and configurations are known at build time (no dynamic class loading)
4. **Native compilation:** GraalVM's `native-image` compiles everything ahead-of-time, including the JDK itself
5. **Static initialization:** Classes that can be initialized at build time are, reducing runtime startup work

The result: startup becomes a simple class loading exercise — no reflection, no classpath scanning, no dynamic proxy generation.

**Interviewer:** How do you create a REST API with Quarkus? Compare with Spring Boot.

**Candidate:** Quarkus uses JAX-RS annotations (like Helidon MP):

```java
@Path("/api/users")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    
    @Inject
    UserService userService;
    
    @GET
    @Path("/{id}")
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
    
    @POST
    @Transactional
    public Response createUser(User user) {
        userService.create(user);
        return Response.status(201).entity(user).build();
    }
}
```

The main differences from Spring Boot:
- `@Path` instead of `@RequestMapping`
- `@GET`/`@POST` instead of `@GetMapping`/`@PostMapping`
- `@PathParam` instead of `@PathVariable`
- `@Inject` instead of `@Autowired`
- `Response` instead of `ResponseEntity`

Quarkus also supports Spring Web annotations compatibility via the `quarkus-spring-web` extension.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your team is migrating a Spring Boot monolith to Quarkus microservices. How do you approach this migration?

**Candidate:** Migration strategy:

**Phase 1: Assessment**
1. Catalog all Spring Boot features used (Security, Data JPA, Cloud, etc.)
2. Check Quarkus extension availability for each (quarkus-spring-data-jpa, quarkus-spring-security)
3. Identify Spring-specific patterns that have no Quarkus equivalent

**Phase 2: Proof of Concept**
1. Create a Quarkus project with `@GetMapping` compatibility (via `quarkus-spring-web`)
2. Migrate one bounded context (e.g., User Service) as a POC
3. Measure: startup time, memory usage, throughput

**Phase 3: Migration (per microservice)**
1. Replace `@SpringBootApplication` with Quarkus application
2. Replace `application.yml` with `application.properties`
3. Replace `@Autowired` with `@Inject` (or keep using `@Autowired` via extension)
4. Replace `@RestController` with `@Path` + JAX-RS annotations
5. Replace `@Service` with `@ApplicationScoped`
6. Replace `@Repository` with `@ApplicationScoped` + Panache repository
7. Replace Spring Data JPA with Hibernate ORM with Panache
8. Replace Spring Security annotations with Quarkus Security (JWT/OIDC)

**Phase 4: Native compilation**
1. Add GraalVM native-image build step
2. Test for reflection issues (Java serialization, dynamic proxies)
3. Configure `reflect-config.json` if needed (rare with Quarkus)

**Interviewer:** How does Quarkus handle reactive programming with project Loom (virtual threads)?

**Candidate:** Quarkus 3.x+ supports virtual threads natively. The `@RunOnVirtualThread` annotation enables virtual thread execution for JAX-RS endpoints:

```java
@GET
@RunOnVirtualThread
public User getUser(@PathParam("id") Long id) {
    // Runs on a virtual thread, not a platform thread
    return userService.findById(id);
}
```

This combines the simplicity of imperative code with the scalability of reactive. Quarkus also supports Mutiny for fully reactive flows. The choice between virtual threads and reactive depends on the workload:
- **Virtual threads:** I/O-bound with blocking operations (JPA, JDBC, REST calls)
- **Mutiny (reactive):** High-throughput streaming, WebSockets, event processing

Quarkus's best-in-class GraalVM integration means virtual threads in native images are fully supported, providing a "write blocking code, get reactive performance" paradigm.

---

## Interviewer Feedback

**Strengths:** Clear understanding of build-time processing, practical migration strategy, good reactive/virtual threads knowledge  
**Areas to Improve:** Could discuss Quarkus extensions ecosystem in more detail  
**Verdict:** Hire

---

*Lab 10 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
