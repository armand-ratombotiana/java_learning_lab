# Mock Interview: API Documentation (Lab 12)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Why is API documentation important, and what tools does Spring Boot provide?

**Candidate:** API documentation serves as the contract between the API producer and consumers. It enables:
- Client development without waiting for server implementation
- Automated testing and validation
- Self-service discovery of API capabilities
- Version management and deprecation communication

Spring Boot integrates with **SpringDoc OpenAPI** which automatically generates OpenAPI 3.0 specifications from `@RestController` annotations. It provides a Swagger UI at `/swagger-ui.html` and the OpenAPI spec at `/v3/api-docs`.

**Interviewer:** How do you customize the generated OpenAPI documentation?

**Candidate:** SpringDoc provides annotations for customization:
- `@Operation` — describes a specific endpoint
- `@ApiResponse` — documents possible responses
- `@Schema` — describes model fields with examples
- `@SecurityRequirement` — documents authentication requirements
- `@Tag` — groups endpoints logically

Global configuration via `application.yml`:
```yaml
springdoc:
  api-docs.path: /api-docs
  swagger-ui.path: /swagger-ui.html
  packages-to-scan: com.example.controller
  cache.disabled: true
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** What is contract-first API development? How does it differ from code-first?

**Candidate:** 

| Aspect | Code-First | Contract-First |
|--------|-----------|----------------|
| Process | Write code → Generate spec | Write spec → Generate code |
| Spec is | Output (documentation) | Input (source of truth) |
| Changes | Code changes may break contract | Contract changes drive implementation |
| Collaboration | Backend-owned | Backend + Frontend + Product |
| Validation | Spec may drift from reality | Spec always matches implementation |
| Tooling | SpringDoc, Swagger annotations | OpenAPI Generator, AsyncAPI |

Contract-first ensures API design is intentional and cross-team reviewed before implementation. Changes to the contract are deliberate and versioned. Tools like OpenAPI Generator produce Spring Boot controller interfaces.

**Interviewer:** How would you design an API with backward compatibility in mind?

**Candidate:** Principles for backward-compatible API evolution:
1. **Never remove fields** — add new fields only (clients ignore unknown fields by default)
2. **Never change field types** — if needed, add a new field with new name
3. **Never change endpoint semantics** — a `GET /users` should always return users
4. **Use optional fields for new data** — don't make new required fields
5. **Additive changes are safe** — new endpoints, new parameters, new enum values
6. **Versioning strategy:** URL versioning (`/v2/users`) or header versioning
7. **Deprecation headers:** `Sunset` and `Deprecation` HTTP headers
8. **Migration window:** Maintain old version for at least 6-12 months

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design an API documentation and governance strategy for an organization with 20+ microservices.

**Candidate:** A centralized API governance approach:

**Central API Registry:**
- Every service publishes its OpenAPI spec to a central registry (Apicurio or SpringDoc aggregator)
- Registry validates specs against organizational standards
- Provides a unified API catalog with search and discovery

**Standardization:**
```yaml
# Common organizational spec standards
openapi: 3.0.3
info:
  x-organization: company-group
  x-sla: 99.9
  x-owner: team-payments
components:
  schemas:
    ErrorResponse:
      required: [code, message, requestId]
      properties:
        requestId: { type: string, format: uuid }
        code: { type: string }
        message: { type: string }
        details: { type: array, items: { $ref: '#/components/schemas/ErrorDetail' } }
```

**Automated governance checks in CI:**
1. Spec is valid OpenAPI 3.0
2. All endpoints have `@Operation` descriptions
3. All request/response bodies have `@Schema` descriptions
4. Required fields are documented
5. Error responses follow the standard `ErrorResponse` format
6. Pagination follows standard format: `page`, `size`, `totalElements`, `totalPages`
7. No breaking changes compared to previous version (using OpenAPI Diff tool)

**Self-service developer portal:** Backstage or custom portal showing:
- API catalog with search
- Interactive API console (Swagger UI)
- API changelog with semantic versioning
- Deprecation timeline
- SLA and ownership information

---

## Interviewer Feedback

**Strengths:** Clear contract-first vs code-first understanding, good backward compatibility knowledge  
**Areas to Improve:** Could discuss AsyncAPI for event-driven API documentation  
**Verdict:** Hire

---

*Lab 12 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
