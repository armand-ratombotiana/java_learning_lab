# Mock Interview: GraalVM Native Images (Lab 25)

**Role:** Backend Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is GraalVM and what are native images?

**Candidate:** GraalVM is a high-performance JDK distribution that supports:
- **Ahead-of-time (AOT) compilation:** Compiles Java bytecode to native machine code
- **Native images:** Standalone executables that include the application, libraries, and a minimized VM
- **Polyglot capabilities:** Run JavaScript, Python, Ruby, and LLVM languages in the same VM

Native images provide:
- **Fast startup:** 50-100ms vs 2-3s for JVM
- **Low memory:** 10-30MB vs 100-200MB for JVM
- **Small footprint:** ~20MB binary vs ~200MB JAR+JRE
- **Instant peak performance:** No JIT warmup needed

**Interviewer:** How does Spring Boot integrate with GraalVM for native images?

**Candidate:** Spring Boot 3.x + Spring AOT engine provides native image support:
1. AOT processing runs at build time: processes annotations, generates reflection hints, pre-computes property sources
2. The `native-maven-plugin` or `native-gradle-plugin` invokes GraalVM's `native-image` tool
3. Build command: `mvn -Pnative native:compile`
4. Output: platform-specific executable (e.g., `demo-0.0.1`)

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** What are the limitations and challenges of native images?

**Candidate:** Key limitations:

1. **Reflection:** Requires explicit configuration. Spring AOT generates hints automatically, but custom reflection needs `@RegisterReflectionForBinding` or `reflect-config.json`.
2. **Dynamic class loading:** Not supported. All classes must be known at build time (closed-world assumption).
3. **Runtime proxies:** Spring AOT generates proxies at build time; JDK dynamic proxies with unknown interfaces fail.
4. **Serialization:** Java serialization requires configuration. Prefer JSON (Jackson which is AOT-compatible).
5. **External libraries:** Not all libraries have GraalVM-compatible versions.
6. **Debugging:** Native images are harder to debug; JFR and JMX are limited.
7. **Build time:** Native compilation is slow (minutes instead of seconds).
8. **Cross-compilation:** Must build on target platform (or use Docker multi-arch builds).

**Interviewer:** How does Spring AOT handle reflection configuration for native images?

**Candidate:** Spring AOT (Ahead-of-Time) engine scans the application context at build time:
1. Analyzes `@Configuration`, `@Bean`, `@Autowired`, `@Value` annotations
2. Processes all `spring.factories` and auto-configurations
3. Generates `RuntimeHints` classes that register:
   - Reflection access for beans and configuration properties
   - Resource bundles and class path resources
   - Serialization configuration for application classes
   - JNI configuration for native interop
4. Custom hints via `RuntimeHintsRegistrar`:
```java
@Configuration
public class MyRuntimeHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        hints.reflection().registerType(MyClass.class, MemberCategory.values());
        hints.resources().registerPattern("my-resource/*");
        hints.serialization().registerType(MySerializable.class);
    }
}
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your team wants to migrate a critical Spring Boot service to native image for AWS Lambda. The service uses Hibernate, Kafka, and custom reflection. How do you approach this?

**Candidate:** 

**Migration strategy:**

**Phase 1 — Compatibility audit:**
1. Check Hibernate 6.x with GraalVM: OK with `hibernate-graalvm` dependency
2. Check Kafka client: OK for synchronous operations; Spring Kafka listeners need AOT configuration
3. Custom reflection: Wrap in `@RegisterReflectionForBinding` or `RuntimeHintsRegistrar`

**Phase 2 — Configuration:**
```xml
<plugin>
    <groupId>org.graalvm.buildtools</groupId>
    <artifactId>native-maven-plugin</artifactId>
    <configuration>
        <imageName>my-lambda-function</imageName>
        <buildArgs>
            --enable-url-protocols=http,https
            --initialize-at-build-time=org.hibernate
            --trace-class-initialization=com.example
        </buildArgs>
    </configuration>
</plugin>
```

**Phase 3 — Lambda integration:**
```java
public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static final SpringLambdaContainerHandler handler = initializeHandler();
    
    private static SpringLambdaContainerHandler initializeHandler() {
        try {
            return SpringLambdaContainerHandler.forAwsProxyEvent(
                MyApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent input, Context context) {
        return handler.proxy(input, context);
    }
}
```

**Phase 4 — Testing:**
1. Build and test locally: `./mvnw -Pnative native:compile && ./target/my-lambda-function`
2. Cold start test: First invocation should complete under 500ms (vs. 5-10s for JVM on Lambda)
3. Memory test: Lambda with 512MB should suffice (vs. 1-2GB for JVM)

**Interviewer:** What's the difference between Spring Boot GraalVM native and Quarkus native in terms of developer experience?

**Candidate:** 

| Aspect | Spring Boot + GraalVM | Quarkus (native first) |
|--------|---------------------|----------------------|
| AOT approach | Retrofit (Spring AOT engine) | Built from ground up |
| Dev experience | Same Spring Boot; additional build step | Designed for native; live reload |
| Reflection hints | Auto-generated + manual hints | Minimal need (compile-time DI) |
| Build time | 3-5 minutes | 2-3 minutes |
| Startup time | ~100ms | ~50ms |
| Integration complexity | Higher for complex Spring features | Lower (extensions handle it) |
| Ecosystem | All Spring features (some need config) | Quarkus extensions only |

Spring Boot native is the right choice if you need the Spring ecosystem and are willing to handle GraalVM compatibility. Quarkus native is the right choice if you want a first-class native experience and can work within Quarkus's extension model.

---

## Interviewer Feedback

**Strengths:** Deep GraalVM understanding, practical Lambda migration strategy, honest framework comparison  
**Areas to Improve:** Could discuss CRaC (Coordinated Restore at Checkpoint) as alternative to native  
**Verdict:** Strong Hire

---

*Lab 25 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
