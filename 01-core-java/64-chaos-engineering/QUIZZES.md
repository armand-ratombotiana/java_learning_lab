# Module 64: Chaos Engineering - Quizzes

---

## Q1: The Goal of Chaos Engineering
What is the primary goal of Chaos Engineering?

A) To hack into the system to find security vulnerabilities.
B) To intentionally inject controlled failures into a system to uncover systemic weaknesses and build confidence in its resilience mechanisms.
C) To randomly delete code to see if it was necessary.
D) To test how fast the application compiles.

**Answer**: B
**Explanation**: Systems fail. By proactively injecting failures (like network latency or node crashes) under controlled conditions, engineers verify that fallbacks, circuit breakers, and failover architectures actually work *before* a real, uncontrolled outage occurs at 3 AM.

---

## Q2: The Blast Radius
In Chaos Engineering, what does "minimizing the blast radius" mean?

A) Ensuring the experiment only runs for 1 second.
B) Keeping the scope and impact of an experiment as small as possible initially (e.g., affecting only one non-critical server or 1% of users) to prevent massive customer outages if the system fails to recover as hypothesized.
C) Running the experiment only on developer laptops.
D) Deleting the database backups.

**Answer**: B
**Explanation**: A core tenet of Chaos Engineering is safety. You want to learn how the system breaks without causing a catastrophic outage for all customers simultaneously.

---

## Q3: Application-Level Chaos
If you want to test how your Spring Boot application handles a slow database, without actually taking down the physical database server, which tool could you use?

A) Kubernetes
B) Docker
C) Chaos Monkey for Spring Boot
D) Apache Maven

**Answer**: C
**Explanation**: Chaos Monkey for Spring Boot (CM4SB) uses Aspect-Oriented Programming (AOP) to intercept calls to `@Repository` or `@Service` beans and injects artificial latency or exceptions directly inside the JVM, simulating a broken database without affecting the actual infrastructure.