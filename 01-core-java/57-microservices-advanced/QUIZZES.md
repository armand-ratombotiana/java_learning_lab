# Module 57: Advanced Microservices Patterns - Quizzes

---

## Q1: The Dual Write Problem
What problem does the "Transactional Outbox" pattern solve?

A) It compresses database tables to save space.
B) It solves the "Dual Write" problem, ensuring that a local database update and a message broker publish operation happen atomically without requiring slow two-phase commits (2PC).
C) It allows REST APIs to return data faster.
D) It routes traffic through an API gateway.

**Answer**: B
**Explanation**: By writing both the business data and the event payload into two separate tables within the same relational database transaction, atomicity is guaranteed. A background process then reads the outbox table and guarantees delivery to the message broker.

---

## Q2: Anti-Corruption Layer
When migrating from a legacy monolith to a modern microservices architecture, what is the purpose of an Anti-Corruption Layer (ACL)?

A) To prevent hackers from corrupting the database.
B) To act as a firewall against SQL injection.
C) To isolate the new microservice's clean domain model by translating archaic or messy legacy data formats into modern domain concepts at the system boundary.
D) To backup data automatically.

**Answer**: C
**Explanation**: The ACL prevents the technical debt and poor modeling choices of the legacy system from "leaking" into and corrupting the pristine Domain-Driven Design of the new microservices.

---

## Q3: Bulkhead Pattern
What is the primary goal of the Bulkhead Pattern?

A) To encrypt HTTP traffic.
B) To partition resources (like thread pools) so that a failure or bottleneck in one part of the system does not cascade and consume all resources, keeping the rest of the system operational.
C) To deploy applications faster using Docker.
D) To merge microservices back into a monolith.

**Answer**: B
**Explanation**: Just as a ship's hull is divided into watertight bulkheads so a single breach doesn't sink the entire vessel, segregating thread pools ensures that a slow downstream dependency only exhausts its dedicated threads, leaving the rest of the app responsive.