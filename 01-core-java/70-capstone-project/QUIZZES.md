# Module 70: Enterprise Capstone Project - Quizzes

---

## Q1: System Architecture Assessment
In the Capstone Architecture, why is the `Ledger Context` separated from the `Trading Context`?

A) Because the database cannot hold both tables.
B) The Trading Context is optimized for high-throughput decision-making and low-latency reads via CQRS. The Ledger Context serves a completely different bounded context: immutable financial compliance, auditing, and reconciliation, which relies on strict Event Sourcing. Decoupling them allows independent scaling and prevents lock contention.
C) To force developers to write more code.
D) Because Kubernetes requires at least 4 microservices.

**Answer**: B
**Explanation**: Bounded Contexts separate domains based on their specific business language and scaling requirements. Trading is about speed; Ledgers are about absolute historical accuracy.

---

## Q2: Resilience and Observability
During the evaluation phase, an automated script will randomly delete pods in your Kubernetes cluster. What combination of technologies ensures your Capstone survives this without customer impact?

A) Java 21 and Spring Boot.
B) ArgoCD and Maven.
C) Kubernetes Deployments (ReplicaSets ensuring desired state), Istio (automatic traffic rerouting away from dead pods), and Resilience4j (Circuit breakers preventing cascading thread exhaustion).
D) GraphQL and PostgreSQL.

**Answer**: C
**Explanation**: Resilience is achieved at multiple layers. K8s handles infrastructure healing, Istio handles network routing around failures, and Resilience4j handles application-level thread safety.

---

## Q3: Security Compliance
If the Capstone requirements state that database passwords cannot be stored in Git and must be automatically rotated every 30 days, which technology must be integrated into your CI/CD pipeline?

A) HashiCorp Vault (or an equivalent dynamic Secret Manager).
B) Base64 Encoding.
C) Kubernetes ConfigMaps.
D) Logback masking.

**Answer**: A
**Explanation**: Neither Git, Base64, nor native Kubernetes ConfigMaps/Secrets support automated cryptographic rotation or dynamic, short-lived database credential generation. An external Key Management system like Vault is required.