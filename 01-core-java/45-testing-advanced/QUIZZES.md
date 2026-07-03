# Module 45: Advanced Testing Strategies - Quizzes

---

## Q1: Testcontainers
What is the primary benefit of using Testcontainers in an integration test?

A) It automatically writes unit tests for you.
B) It spins up lightweight, disposable Docker containers (like an actual PostgreSQL database) so tests run against real infrastructure instead of unreliable mocks or in-memory databases like H2.
C) It prevents you from needing Docker.
D) It makes the tests run completely instantly in microseconds.

**Answer**: B
**Explanation**: In-memory databases (like H2) have different syntax and constraints than production databases. Testcontainers allows you to test against the exact same database engine you use in production, catching SQL syntax errors early.

---

## Q2: Mutation Testing
How does Mutation Testing (e.g., PiTest) evaluate the quality of a test suite?

A) By measuring how many lines of code are executed during a test run.
B) By intentionally injecting small bugs (mutations) into the application source code and verifying that the test suite fails. If the tests pass despite the bug, the test is deemed weak.
C) By checking for SQL injection vulnerabilities.
D) By simulating network latency.

**Answer**: B
**Explanation**: Standard line coverage only checks if a line was executed. Mutation testing checks if the tests actually contain strong assertions that can detect when the logic has changed or broken.

---

## Q3: Chaos Engineering
What is the goal of Chaos Engineering?

A) To intentionally introduce faults (like terminating servers or spiking latency) into a production or staging system to ensure its self-healing and resilience mechanisms work as expected.
B) To hire hackers to attack your system.
C) To randomly delete user data to test backups.
D) To make the code harder to read.

**Answer**: A
**Explanation**: Systems fail. Chaos engineering helps teams proactively identify weaknesses before they cause a full-blown outage by simulating failures in a controlled manner.