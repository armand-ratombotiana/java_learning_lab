# Mock Interview: API Versioning & Documentation (Lab 17)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Why is API versioning necessary and what's your preferred approach?

**Candidate:** API versioning is necessary because APIs evolve over time — new fields, changed behavior, removed endpoints. Without versioning, changes break existing clients. My preferred approach is **URI-based versioning** (`/api/v1/orders`, `/api/v2/orders`) because:
- Most explicit and visible to developers
- Easy to test and curl
- Simple to route and deploy
- Well-understood by API consumers

With a Spring Boot implementation using separate controller packages per version, sharing a common service layer.

**Interviewer:** How do you implement API versioning in Spring Boot?

**Candidate:** Multiple approaches in Spring Boot:

1. **URI versioning:** Different controller packages per version
```java
@RestController @RequestMapping("/api/v1/orders")
public class OrderControllerV1 { ... }
@RestController @RequestMapping("/api/v2/orders")
public class OrderControllerV2 { ... }
```

2. **Header versioning:** Custom `@RequestMapping` with `headers` attribute
```java
@RequestMapping(value = "/orders", headers = "X-API-Version=2")
```

3. **Content negotiation (Accept header):**
```java
@RequestMapping(value = "/orders", produces = "application/vnd.company.v2+json")
```

4. **Custom versioning resolver:** Implement `WebMvcConfigurer.addArgumentResolvers` with a custom `@ApiVersion` annotation

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you handle the transition from v1 to v2 without breaking existing clients?

**Candidate:** A phased deprecation strategy:

1. **Announce deprecation** — Add `Sunset` and `Deprecation` HTTP headers to v1 responses
2. **Migration window** — Maintain v1 for at least 6-12 months
3. **Graceful degradation** — New features go to v2 only, v1 only gets critical bug fixes
4. **Monitoring** — Track v1 vs v2 usage via metrics
5. **Sunset policy** — Clear communication: "v1 deprecated on Date X, will be removed on Date Y"

```java
@GetMapping("/v1/orders")
public ResponseEntity<List<Order>> getOrders() {
    return ResponseEntity.ok()
        .header("Deprecation", "true")
        .header("Sunset", "Sat, 1 Jan 2025 00:00:00 GMT")
        .header("Link", "</api/v2/orders>; rel=\"successor-version\"")
        .body(orderService.getOrders());
}
```

**Interviewer:** How do you test API backward compatibility in CI/CD?

**Candidate:** Use OpenAPI diff tools in CI:
```yaml
pipeline:
  - script: openapi-diff v1-spec.yaml v2-spec.yaml
    check: 
      - no-removed-endpoints
      - no-removed-parameters
      - no-changed-request-bodies
      - only-added-optional-fields
```

SpringDoc + OpenAPI Diff can be automated. Any breaking change fails the build and requires version bump.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design an API life cycle management system for a platform with 50+ APIs.

**Candidate:** A comprehensive API life cycle:

```
Design → Develop → Test → Deploy → Version → Deprecate → Retire
  │          │        │        │         │          │         │
  └─OpenAPI   └─Code  └─Contract└─Canary  └─v1→v2→v3 └─Sunset  └─404
```

**Phases:**
1. **Design phase:** OpenAPI contract reviewed by API guild, all teams
2. **Development phase:** Code generated from contract (contract-first)
3. **Testing phase:** Contract tests verify spec compliance; OpenAPI diff against previous version
4. **Deployment phase:** Canary release, A/B testing, traffic mirroring
5. **Version management:** Semantic versioning for API; Major version = breaking change
6. **Deprecation phase:** Sunset headers, migration guides, usage metrics tracked
7. **Retirement phase:** Endpoint returns 410 Gone with link to successor

**Implementation:**
```java
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiLifecycle {
    Stage value() default Stage.CURRENT;
    String sunsetDate() default "";
    String successorVersion() default "";
    enum Stage { DRAFT, CURRENT, DEPRECATED, SUNSET, RETIRED }
}
```

An `ApiLifecycleInterceptor` checks the annotation and adds appropriate headers or returns 410 for retired endpoints. Metrics track usage per lifecycle stage.

---

## Interviewer Feedback

**Strengths:** Clear versioning strategy, good deprecation handling, lifecycle management  
**Areas to Improve:** Could discuss gRPC versioning (package-based vs field-level)  
**Verdict:** Hire

---

*Lab 17 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
