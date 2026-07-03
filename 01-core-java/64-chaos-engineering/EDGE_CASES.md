# Module 64: Chaos Engineering - Edge Cases & Pitfalls

---

## Pitfall 1: Uncontrolled Blast Radius

### ❌ Wrong
Running a Chaos experiment on day one that terminates the primary database cluster in the production environment during peak business hours, causing a massive, unrecoverable global outage and losing thousands of dollars in revenue.

### ✅ Correct
Always minimize the **Blast Radius**. Start your experiments in a staging or UAT environment. When you eventually move to production, use canary traffic (e.g., affecting only 1% of users) or restrict the chaos injection to non-critical, redundant microservices (like a recommendation engine) before ever touching core financial databases.

---

## Pitfall 2: Chaos Without Observability

### ❌ Wrong
Injecting chaos into a system (e.g., adding 5 seconds of latency to an API) when the company has no centralized logging, no distributed tracing, and no metrics dashboards set up. The system breaks, but no one knows *why* or *where* the bottleneck cascaded.

### ✅ Correct
Chaos Engineering is useless without **Observability** (Module 54). You must have a clearly defined "Steady State" dashboard (e.g., in Grafana) so you can scientifically measure exactly how the latency injected into Service A caused the Thread Pool in Service B to exhaust.

---

## Pitfall 3: Assuming Chaos Replaces Traditional Testing

### ❌ Wrong
Skipping Unit Tests, Integration Tests, and E2E Tests because the team relies purely on running Chaos Monkey in staging to catch bugs.

### ✅ Correct
Chaos Engineering does not test if a function calculates math correctly. It tests the architectural resilience of the distributed system (e.g., verifying if Circuit Breakers, Auto-Scaling groups, and database failovers work). It sits at the absolute top of the testing pyramid and is only effective if the underlying code is already thoroughly unit-tested.