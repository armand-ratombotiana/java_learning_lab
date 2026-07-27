# Java Version Evolution — Interview Questions

> 70+ questions covering version comparisons, feature specifics, migration strategy, and company-specific topics.

---

## Category 1: Version Comparison

### Q1: "What's the difference between Java 8 and Java 17?"

Java 17 is the modern LTS replacement for Java 8. Key differences:

| Aspect | Java 8 | Java 17 |
|--------|--------|---------|
| Release | March 2014 | September 2021 |
| License | Oracle Binary (commercial) | Oracle No-Fee Terms / OpenJDK |
| Language | Lambdas, streams, Optional | + records, sealed classes, text blocks, pattern matching, switch expressions |
| Concurrency | CompletableFuture, parallel streams | + virtual threads (preview), structured concurrency |
| Modules | Classpath | Module path (Jigsaw) |
| GC | G1 (default), Parallel, CMS | ZGC, Shenandoah, G1 improved |
| Startup | JAR class loading | CDS, jlink, AppCDS |
| String storage | UTF-16 always | Latin-1 when possible (compact strings) |
| Removed | — | Java EE modules, Nashorn, Applet, Security Manager (deprecated) |
| HTTP | HttpURLConnection | java.net.http.HttpClient |
| Default charset | Platform-dependent | UTF-8 (Java 18, but planned earlier) |
| Nest access | Synthetic bridge methods | JVM-level nest-based access |
| Date/Time | java.time new | Same (stabilized) |
| Type inference | — | var (Java 10+), pattern matching (Java 16+) |
| Records | — | Yes (Java 14 preview, 16 stable) |

**Answer strategy**: Start with the biggest practical differences (records, sealed classes, modules, GC, HTTP Client), then discuss licensing and support timeline. Mention migration complexity: most Java 8 code runs on Java 17 with minor changes.

---

### Q2: "What features from Java 9–21 do you actually use in production?"

**Good answer** (prioritize by frequency):

1. **Java 9**: `List.of()`, `Set.of()`, `Map.of()` — every day for immutable collections. `Stream.takeWhile`/`dropWhile` — occasionally.
2. **Java 10**: `var` — daily for obvious types (`var list = new ArrayList<String>()`). Avoid for unclear types.
3. **Java 11**: `HttpClient` — for new HTTP services. `Files.readString`/`writeString` — for quick file I/O. `String.isBlank()`/`strip()` — daily.
4. **Java 12–13**: Nothing stable (switch expressions still preview).
5. **Java 14**: `Switch expressions` — daily now. Helpful NPE messages — appreciated.
6. **Java 15**: Text blocks — daily for SQL, JSON, HTML in code.
7. **Java 16**: `instanceof pattern matching` — daily. `Stream.toList()` — very frequent.
8. **Java 17**: Records for DTOs, value objects — daily. Sealed classes for domain models — weekly.
9. **Java 18–20**: Nothing major stable.
10. **Java 21**: Virtual threads — every new service. Pattern matching for switch — daily. Sequenced collections — occasionally.

**Weak answer**: Listing features without usage context. "I use records" (✓ with example), vs "I know records exist" (✗).

---

### Q3: "Why did Oracle change the release cadence from 3 years to 6 months?"

Oracle changed from the 3-year LTS cycle (Java 7 → 8 → 11 → 17) to a 6-month rapid-release cadence (Java 9+) for several reasons:

1. **Faster feature delivery**: Developers no longer wait years for language improvements.
2. **Competitive pressure**: Kotlin, Scala, and Go were innovating faster than Java's old cadence.
3. **Smaller, safer releases**: 6-month windows force scope management; no more monolithic 3-year releases with integration nightmares.
4. **Predictability**: March and September releases on schedule, every year.
5. **LTS every 3 years**: Enterprises still get stability; innovators get early access.

**Answer strategy**: Acknowledge the tradeoff — frequent releases mean more migration work but faster innovation. Mention that most companies target LTS versions (8, 11, 17, 21, 25) while keeping up via preview features.

---

### Q4: "Which LTS version should a company choose and why?"

Decision framework:
- **Starting new project in 2025-2026**: Java 21 or Java 25 (next LTS). Java 21 is proven, 25 will be the latest.
- **Migrating from Java 8**: Java 17 or 21. Both are modern LTS. 21 has virtual threads.
- **Risk-averse / regulated industry**: Java 17 (3+ years of production hardening).
- **Cutting-edge / startup**: Latest non-LTS (27) or Java 25 LTS.

Factors:
1. **Library support**: Does your ecosystem (Spring, Hibernate, Quarkus) support the version?
2. **Container footprint**: jlink in Java 9+ reduces Docker image size significantly.
3. **GC needs**: ZGC in 21+ for sub-millisecond pauses; G1 for throughput.
4. **Licensing**: Oracle JDK 17+ uses NFTC (free for production); OpenJDK builds (Adoptium, Corretto) are free.
5. **Workforce**: Can you hire developers familiar with the version?

---

### Q5: "How do you handle upgrading from Java 8 to Java 21?"

**Phased approach**:

1. **Audit**: Use `jdeps` to find internal API usage (sun.misc.Unsafe, etc.). Check for Java EE dependencies.
2. **Build tool upgrade**: Maven 3.9+, Gradle 8+. Ensure CI supports multi-JDK builds.
3. **First stop: Java 11**: Add module flags. Remove JAXB/JAX-WS or add explicit dependencies. Replace HttpURLConnection. Test extensively.
4. **Second stop: Java 17**: Enable records, sealed classes, pattern matching. Add `--add-opens` flags for reflection. Test GC behavior.
5. **Final: Java 21**: Enable virtual threads (test thread safety). Convert to records. Add pattern matching for switch.
6. **Production validation**: Canary releases, shadow traffic, GC log analysis, performance benchmarks.

**Key risks**: Reflection on JDK internals (blocks in 17+), thread-local issues with virtual threads, broken third-party libraries, CMS GC removal (Java 14+).

---

## Category 2: Feature-Specific

### Q6: "How do virtual threads work? How are they different from platform threads?"

**Core concept**: Virtual threads are lightweight JVM-managed threads (Project Loom) that multiplex on platform (OS) threads. They are "virtual" because many share a few carrier threads.

**Key differences**:

| Attribute | Platform Thread | Virtual Thread |
|-----------|----------------|----------------|
| Management | OS kernel | JVM |
| Creation cost | ~1MB stack + OS handle | ~hundreds of bytes |
| Max per JVM | ~thousands | ~millions |
| Stack size | Fixed (configurable) | Resizable |
| Context switch | OS syscall (~microseconds) | JVM yield (~nanoseconds) |
| Pinning | Always on OS thread | Unmounts during blocking I/O |
| Debugging | Standard | Same tools, new APIs |
| Synchronized | Works normally | Pins carrier (use ReentrantLock) |

**When to use**: I/O-heavy workloads (web servers, database clients, API gateways). Not for CPU-bound tasks (use platform threads with parallelism equal to cores).

```java
// Virtual thread creation
Thread vThread = Thread.ofVirtual().name("handler-").start(() -> handleRequest());

// Virtual thread executor
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    // Submit millions of tasks
}
```

**Interview trap**: Do NOT say virtual threads replace all platform threads. CPU-bound work still needs platform threads matching core count.

---

### Q7: "What are records and when should you use them?"

Records (Java 14 preview → 16 stable) are transparent carriers for immutable data. They auto-generate:
- Canonical constructor (with validation via compact constructor)
- Accessor methods (`x()` not `getX()`)
- `equals()`, `hashCode()`, `toString()`

**When to use**:
1. DTOs / Value Objects / Transfer Objects
2. Multi-value returns (replacing tuples or Object[])
3. Pattern matching targets (record patterns in Java 21+)
4. Local data aggregations inside methods
5. Map keys (immutable by default)

**When NOT to use**:
1. JPA entities (need mutation, no-arg constructor, proxies)
2. Classes with behavior beyond accessors (use regular class)
3. Classes that need inheritance (records are implicitly final)
4. Serializable objects with custom serialization (possible but awkward)

```java
// Good: immutable data carrier
public record Address(String street, String city, String zip) {}

// With validation
public record PositivePoint(int x, int y) {
    public PositivePoint {
        if (x < 0 || y < 0) throw new IllegalArgumentException();
    }
}

// Bad: needs mutation
// public record Counter(int value) {
//     public void increment() { /* can't — fields are final */ }
// }
```

---

### Q8: "Explain sealed classes and why they matter for domain modeling"

Sealed classes (Java 15 preview → 17 stable) restrict which classes may extend/implement them. The `permits` clause lists allowed subtypes, which must be `final`, `sealed`, or `non-sealed`.

**Why they matter**:

1. **Exhaustive pattern matching**: The compiler knows all possible subtypes. Pattern matching for switch can be exhaustive without `default`.
2. **Domain model integrity**: Prevents unintended subclasses across package/API boundaries.
3. **API design**: Library authors control the inheritance hierarchy.
4. **Type-safe ADTs**: Algebraic data types (like Scala's sealed traits or Kotlin's sealed classes).

```java
// Domain modeling with sealed classes
public sealed class OrderStatus permits Pending, Shipped, Delivered, Cancelled { }

public final class Pending extends OrderStatus {
    private final Instant createdAt;
}
public final class Shipped extends OrderStatus {
    private final Instant shippedAt;
    private final String trackingNumber;
}
public final class Delivered extends OrderStatus { }
public non-sealed class Cancelled extends OrderStatus {
    private final String reason;
}

// Exhaustive switch (no default needed — all cases covered)
String describe(OrderStatus status) {
    return switch (status) {
        case Pending p -> "Pending since " + p.createdAt();
        case Shipped s -> "Shipped at " + s.shippedAt() + " (tracking: " + s.trackingNumber() + ")";
        case Delivered d -> "Delivered";
        case Cancelled c -> "Cancelled: " + c.reason();
    };
}
```

---

### Q9: "How has the switch statement evolved across versions?"

**Evolution timeline**:

| Version | Change | Status |
|---------|--------|--------|
| Java 1.0 | `switch` on `int`, `char` (no String) | Original |
| Java 5 | Enum support | Stable |
| Java 7 | String support | Stable |
| Java 12 | Arrow syntax, expression form | Preview |
| Java 13 | `yield` statement | 2nd Preview |
| Java 14 | Switch expressions | **Stable** |
| Java 17 | Pattern matching (type patterns) | Preview |
| Java 19–20 | Record patterns, refinement | Preview |
| Java 21 | Pattern matching + record patterns + guards | **Stable** |
| Java 22+ | Exhaustiveness requirements | Refinement |

```java
// Java 7: traditional switch on String
switch (day) {
    case "MONDAY":
    case "TUESDAY":
        System.out.println("Work day");
        break;
    case "SATURDAY":
    case "SUNDAY":
        System.out.println("Weekend");
        break;
    default:
        System.out.println("Invalid");
}

// Java 14: switch expression with arrow syntax
String type = switch (day) {
    case "MONDAY", "TUESDAY" -> "Work day";
    case "SATURDAY", "SUNDAY" -> "Weekend";
    default -> "Invalid";
};

// Java 21: pattern matching with guards
String result = switch (obj) {
    case Integer i when i > 0 -> "Positive: " + i;
    case Integer i -> "Non-positive: " + i;
    case String s -> "String: " + s;
    case null -> "null";
    default -> "Other";
};
```

---

### Q10: "What are pattern matching and when did it become stable?"

Pattern matching in Java evolved through several JEPs:

- **Pattern Matching for instanceof**: Preview in Java 14, **stable in Java 16** (JEP 394)
- **Pattern Matching for switch**: Preview in Java 17, **stable in Java 21** (JEP 441)
- **Record Patterns**: Preview in Java 19, **stable in Java 21** (JEP 440)

**Three forms**:

```java
// 1. instanceof pattern (Java 16+)
if (obj instanceof String s) {
    System.out.println(s.length());
}

// 2. switch pattern matching (Java 21+)
String formatted = switch (obj) {
    case Integer i -> String.format("int %d", i);
    case Long l    -> String.format("long %d", l);
    case Double d  -> String.format("double %f", d);
    case String s  -> String.format("String %s", s);
    case null      -> "null";
    default        -> "Unknown type: " + obj.getClass().getName();
};

// 3. Record patterns (Java 21+)
if (obj instanceof Point(int x, int y)) {
    System.out.println("Point at " + x + "," + y);
}
```

---

## Category 3: Migration

### Q11: "You have a Java 8 codebase. What's your upgrade strategy to Java 21?"

**6-phase strategy**:

**Phase 1 — Analysis** (2-4 weeks):
- Run `jdeps -jdkinternals` on all JARs to find internal API usage
- Inventory deprecated API usage (Date, SimpleDateFormat, HttpURLConnection, etc.)
- Test all third-party libraries for Java 21 compatibility
- Identify module-path readiness

**Phase 2 — Infrastructure** (1-2 weeks):
- CI/CD tools must support JDK 21 (Maven 3.9+, Gradle 8+, Jenkins plugins)
- Build configuration updates (compiler source/target levels)
- Docker base images change (use eclipse-temurin:21)

**Phase 3 — Java 11 Intermediate** (4-8 weeks):
- Add `--add-reads` and `--add-opens` for reflection
- Replace/remove JAXB, JAX-WS, CORBA dependencies
- Replace HttpURLConnection with HttpClient
- Update to Spring Boot 2.7+ (compatible with Java 17+)
- Fix JavaDoc generation issues
- Fix JDK internal calls (sun.misc.Unsafe → safer alternatives)

**Phase 4 — Java 17 Intermediate** (4-8 weeks):
- Remove `--add-opens` flags that are no longer needed
- Enable records, sealed classes, text blocks in new code
- Update GC flags (remove CMS flags; use G1 or ZGC)
- Test with pattern matching preview
- Run full regression suite

**Phase 5 — Java 21** (4-8 weeks):
- Convert thread pools to virtual threads (test thread-local usage first)
- Replace simple DTOs with records
- Migrate legacy switch statements to switch expressions
- Add sequenced collection usage
- Performance tuning for ZGC

**Phase 6 — Production Rollout** (4-6 weeks):
- Blue-green deployment with new JDK
- Shadow traffic comparison (latency, throughput, GC metrics)
- Canary percentage ramp-up (1% → 5% → 25% → 100%)
- Rollback plan: keep old JRE, deploy old JARs

**Total**: 4-6 months for a mid-size monolith; longer for complex microservice ecosystems.

---

### Q12: "What broke between Java 8 and Java 9 (modules)?"

**The module system** (Jigsaw) was the biggest breaking change in Java history:

1. **Classpath visibility changes**: Internal JDK classes (com.sun.*, sun.*) are no longer accessible by default. Code using `sun.misc.Unsafe`, `com.sun.org.apache.xerces`, `com.sun.image.codec.jpeg` breaks.
2. **Split packages**: Two JARs containing the same package on the classpath now cause errors (previously silently merged).
3. **Java EE modules removed**: JAX-WS, JAXB, JAF are in Java EE modules and not resolved by default. Applications using them break at runtime.
4. **ClassLoader changes**: The application classloader is no longer a URLClassLoader. Code casting to URLClassLoader breaks.
5. **Security**: Reflection-based frameworks (Spring, Hibernate) need `--add-opens` to access reflective members.
6. **Platform classloader**: Boot/platform classloader separation breaks code that assumed a unified hierarchy.
7. **Thread contention**: New module system initialization can affect startup timing.

**Mitigation**: Use `--add-exports`, `--add-opens`, `--add-reads` flags. Add JAXB/JAX-WS as Maven/Gradle dependencies. Use `jdeps` to analyze.

---

### Q13: "How does the module system affect existing applications?"

- **Existing apps on classpath** still run (unnamed module) but cannot access JDK internals.
- **To access JDK internals**: `--add-opens java.base/java.lang=ALL-UNNAMED` (replaces illegal access flag).
- **Migration path**: Add `module-info.java` gradually. Libraries should add autolinks.
- **Benefits**: Strong encapsulation prevents accidental use of internal APIs. Reliable configuration eliminates classpath ordering issues.
- **Cost**: Education curve for developers. Module descriptors in every JAR. Build configuration complexity.
- **Strategy**: Most teams still use classpath with `--add-opens` flags. Full modularization is optional.

---

### Q14: "What deprecated APIs should you watch out for?"

| API | Deprecated in | Removed/Scheduled |
|-----|---------------|-------------------|
| Security Manager | Java 17 | Java 18+ (deprecated for removal) |
| finalization (finalize()) | Java 9 | Java 18 (deprecated for removal) |
| Thread.stop(), .suspend(), .resume() | Java 1.2 | Not removed (but should never use) |
| java.util.Date (most methods) | Java 1.1 | Not removed (use java.time) |
| java.util.Calendar | Never formally | Replace with java.time |
| SimpleDateFormat | Never formally | Replace with DateTimeFormatter |
| java.io.File (many methods) | Never formally | Replace with java.nio.file.Path/Files |
| HttpURLConnection | Java 9 | Not removed (use HttpClient) |
| JAXB / JAX-WS | Java 9 | Removed in Java 11 |
| Nashorn JavaScript engine | Java 11 | Removed in Java 15 |
| CORBA / RMI-IIOP | Java 9 | Removed in Java 11 |
| Applet API | Java 9 | Removed in Java 17 |
| AWT Desktop API (some) | Various | Still present |
| finalize() | Java 9 | Deprecated for removal |
| Thread.destroy() | Java 1.5 | Never implemented; removed |
| JNLP / Java Web Start | Java 9 | Removed in Java 11 |

---

## Category 4: Company-Specific

---

### Q15: "Java version strategy for Google's infrastructure"

**Google context**:
- Google uses Java extensively internally (Borg/Omega cluster management, Bigtable, Spanner, Ads, Search backend services)
- They maintain their own JDK distribution
- Google open-sourced Guava (pre-dates many JDK features)

**Interview angle**:
- Google's internal JDK is based on OpenJDK with custom patches
- They focus on performance, GC tuning, and large-scale JVM management
- Guava predates many JDK features: `ImmutableList` (pre-`List.of`), `Optional` (pre-`java.util.Optional`), `MoreCollectors` (pre-`Collectors.toList`)
- Android uses a separate Java subset (Java 8 features, no javax.sql, no java.nio.file, etc.)

**What they might ask**:
- "How do you choose between JDK features and Guava?"
- "What Java version targets Android vs server-side?"

---

### Q16: "Why does AWS Lambda support specific Java versions?"

**AWS context**:
- AWS Lambda supports Java 8, 11, 17, 21
- They provide custom runtimes based on Amazon Corretto

**Reasons for specific versions**:
1. **Cold start performance**: Lambda charges for duration. Startup time matters greatly. JDK 11+ improved startup with CDS, AppCDS, layered class loading.
2. **Memory footprint**: SnapStart (Lambda's snapshot-and-restore) works better with newer JDK GCs.
3. **Container-friendly**: jlink (Java 9+) reduces runtime image size. Smaller packages → faster deployment.
4. **Security support**: AWS only supports LTS versions with long-term patches.
5. **Customer demand**: Enterprises need LTS versions (8, 11, 17, 21) for compliance.

**Interview angle**:
- "Why might you choose Java 21 over Java 17 for Lambda?"
- "How do virtual threads help Lambda functions?"

---

### Q17: "Java SE licensing changes from 8 to 17+"

**Oracle licensing history**:
- **Java 8 (pre-2019)**: Free for general use, but Oracle JDK updates required a commercial license for business use from April 2019.
- **Java 9-10**: Same Binary Code License (BCL).
- **Java 11**: Oracle JDK under commercial license; OpenJDK builds under GPL.
- **Java 14**: Oracle introduced Oracle JDK free for development and testing only.
- **Java 17**: Oracle JDK under **NFTC (No-Fee Terms and Conditions)** — free for production. Sublicensing to Oracle required only when using features not in OpenJDK.

**Current landscape (2025-2026)**:
- **Oracle JDK 17+**: Free for all uses (NFTC)
- **OpenJDK builds**: Free (GPL+CE)
- **Azul Zulu**: Free, commercial support available
- **Amazon Corretto**: Free, AWS-supported
- **Adoptium (Eclipse Temurin)**: Free, community-supported
- **Microsoft Build of OpenJDK**: Free
- **IBM Semeru**: Free

**Interview strategy**: Know the licensing history. Explain that Oracle JDK 17+ is free again, but many companies still prefer OpenJDK builds to avoid any future licensing surprises.

---

### Q18: "How Netflix manages Java version upgrades at scale"

**Netflix context** (based on their tech blog):
- Thousands of microservices running on AWS
- Each service may be on a different Java version
- They automated the upgrade process

**Their approach**:
1. **No big bang upgrades**: Each service teams upgrades independently, not centrally mandated.
2. **Base image strategy**: Golden AMIs with JDK pre-installed. Teams pull latest JDK image.
3. **Feature gating**: New Java features behind toggles during migration.
4. **Compatibility testing suite**: They run tests against pre-release JDK builds.
5. **Canary analysis**: Production traffic with both old and new JVMs, comparing latencies, GC metrics, error rates.
6. **Early access adoption**: Netflix often runs on early-access JDK builds to catch issues early and work with the JDK team.
7. **GC tuning per service**: Different services (Zuul gateway, Cassandra, Kafka consumer) use different GC configurations per version.

**What they look for**: Deliberate, safety-first migration strategy. Experience with large-scale JVM operations. GC tuning per workload.

---

### Q19: "Adoptium, Azure Java support, VS Code Java at Microsoft"

**Adoptium (Eclipse Temurin)**:
- Microsoft contributes engineering resources to Adoptium
- Temurin builds are Microsoft's recommended OpenJDK distribution
- Free, GPL+CE licensed, TCK-tested

**Azure Java support**:
- Azure supports multiple JDKs: Azul Zulu, Microsoft Build of OpenJDK, Adoptium Temurin
- Azure App Service, Azure Functions, Azure Spring Apps all run Java
- Microsoft offers LTS versions with extended support

**VS Code Java**:
- VS Code Java extension pack (Language Support, Debugger, Test Runner, Maven/Gradle)
- Supports Java 8 through latest JDK
- Features include: IntelliSense, debugging, refactoring, JUnit/TestNG, Gradle/Maven integration
- Community-driven but Microsoft-maintained

**Interview angle**: Microsoft-related roles may ask about cross-platform Java (Windows + Linux + Azure), VS Code toolchain, and migration from IntelliJ/Eclipse to VS Code for Java.

---

### Q20: "Java on macOS, deprecation history"

**Apple's relationship with Java**:
- **Pre-2010**: Apple maintained its own JDK for macOS (Apple Java 6).
- **2010**: Apple deprecated its own Java runtime, handed maintenance to Oracle.
- **2012**: OS X 10.8 (Mountain Lion) removed Java runtime pre-installation.
- **2016**: macOS Sierra removed Java 6 runtime completely.
- **2018**: Java 11+ fully supports macOS.
- **2020**: Apple Silicon (M1) transition. Oracle JDK gained native M1 support.

**Key points for interviews**:
- Apple no longer ships Java; users download from Oracle/Adoptium/Azul
- Apple Silicon runs Intel JDK via Rosetta 2, but native ARM JDK is better
- Java on macOS is identical to Linux/Windows for development
- macOS is popular for Java development despite Apple's deprecation history

---

## Bonus: Scenario-Based Questions

### Q21: "Your team uses Java 8 and wants to adopt virtual threads. What do you say?"

**Answer**: Virtual threads require Java 21 minimum. The upgrade path is Java 8 → 11 → 17 → 21. Consider whether the application is I/O-heavy enough to benefit. If the team mostly does CPU-bound computation, virtual threads won't help. If they build web services, the benefit is substantial.

### Q22: "You see `var list = getData()` in a code review. What do you say?"

**Answer**: Depends on context. If `getData()` returns a clearly named type (e.g., `getUserList()` returns `List<User>`), `var` is fine. If the return type is unclear (`process()` returns `List<?>`), ask for explicit type. Add style guide rule: use var when the right-hand side makes the type obvious; avoid var when it obscures.

### Q23: "When should you NOT use Pattern Matching for switch?"

**Answer**: When the logic is simple (if-else is clearer), when you need exhaustive coverage without sealed types (default branch required), when performance-critical path matching (switch on integers is still faster), or when the patterns are deeply nested (readability suffers).

### Q24: "How do you test migration from Java 8 to 21?"

**Answer**: Unit tests (JUnit 5), compatibility test suite, dependency checks, integration tests with all external systems, performance regression tests (GC logs, throughput), canary deployment with traffic mirroring, soak tests (memory leaks), and tool-assisted diff of bytecode behavior.

### Q25: "What would you miss from Java 8 if you went directly to Java 21?"

**Nothing — Java 21 is a superset of Java 8 features.** All Java 8 APIs (lambdas, streams, Optional, CompletableFuture, Date/Time) are still present and unchanged. Java 21 adds features without removing Java 8's core improvements. Some APIs (like `HttpURLConnection`) still exist but are superseded. `CompletableFuture` works better with virtual threads in Java 21.

### Q26: "Explain the difference between JVM, JRE, JDK, and OpenJDK."

**JVM**: Java Virtual Machine — the runtime that executes bytecode. HotSpot (Oracle), OpenJ9 (IBM), etc.

**JRE**: Java Runtime Environment — JVM + core libraries. No longer distributed separately (Java 9+). Use `jlink` to create custom runtimes.

**JDK**: Java Development Kit — JRE + development tools (javac, javadoc, jshell, jlink, jar, etc.). What you download to build Java applications.

**OpenJDK**: The open-source reference implementation of the Java SE specification. The official version. Oracle JDK builds on OpenJDK with minor additions (installer, branding, commercial features).

**Key distinction for interviews**: Oracle JDK 17+ is now free under NFTC. OpenJDK builds are available from multiple vendors (Eclipse Adoptium, Amazon Corretto, Azul Zulu, Microsoft, BellSoft, etc.).

### Q27: "How do you keep up with new Java versions?"

**Answer**: Follow Inside Java (Oracle's Java YouTube), read JEPs on openjdk.org, subscribe to Java Magazine / Foojay.io / InfoQ Java, attend conferences (JavaOne, Devoxx, JCon), maintain a side project on the latest JDK preview builds, and participate in early access programs.

### Q28: "What is JDK Flight Recorder (JFR) and why was it significant?"

JFR is a profiling and event-collection framework built into the JDK. Originally a commercial Oracle JDK feature, it was open-sourced in Java 11 (JEP 328). It collects JVM-level events (GC, JIT compilation, thread contention, class loading, IO, exceptions, etc.) with very low overhead (typically <1%). You can start recording via `-XX:StartFlightRecording` or via `jcmd`. Data can be analyzed with JDK Mission Control or custom parsers. Significance: now every Java developer has production-grade profiling for free.

### Q29: "Compare ZGC, Shenandoah, G1, and Parallel GC."

| GC | Focus | Pause Target | When to Use |
|----|-------|-------------|-------------|
| Parallel | Throughput | 1s+ | Batch processing, background jobs |
| G1 | Balanced | 10-100ms | Default for most applications |
| ZGC | Low-latency | <1ms | User-facing services, trading, real-time |
| Shenandoah | Low-latency | <10ms | Similar to ZGC, different approach |

**Key difference**: ZGC (since Java 11 experimental, Java 15 production) and Shenandoah (since Java 12 experimental, Java 15 production) both aim for sub-millisecond pauses. ZGC uses colored pointers, Shenandoah uses Brooks pointers. G1 (Java 9 default) is the workhorse GC for most applications. Generational ZGC (Java 21+ preview) combines ZGC's low-latency with Generational G1's throughput.

### Q30: "What is jlink and why does it matter?"

jlink (Java 9+) creates custom JRE images containing only the modules your application needs. Instead of shipping a 300MB JDK, you can ship a 40MB runtime. This matters for Docker images (smaller → faster pulls), embedded systems, and microservices. Combined with jdeps (dependency analysis), you can create minimal runtimes.

```bash
# Create minimal runtime
jdeps --module-path target/classes --list-deps target/my-app.jar
# Output: java.base, java.logging, java.sql

jlink --add-modules java.base,java.logging,java.sql \
    --output my-custom-runtime \
    --strip-debug --compress 2 --no-header-files
```

**Company focus**: Container-heavy deployments (Kubernetes, Docker), microservices, AWS Lambda (reduced cold start).
