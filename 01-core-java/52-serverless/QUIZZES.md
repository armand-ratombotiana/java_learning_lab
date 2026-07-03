# Module 52: Serverless Java & AWS Lambda - Quizzes

---

## Q1: Serverless Execution
What does the term "Cold Start" mean in the context of Serverless computing (like AWS Lambda)?

A) The time it takes to boot a physical server in an AWS data center.
B) The latency penalty incurred when a cloud provider has to spin up a brand new container, boot the JVM, and load application code to serve a request because no "warm" instances are available.
C) When the CPU reaches absolute zero temperature.
D) The time it takes for DNS to resolve the Lambda URL.

**Answer**: B
**Explanation**: Serverless platforms automatically scale down unused containers to save money. A "Cold Start" occurs when a new request arrives and the cloud provider must allocate new compute resources from scratch, causing significant delays.

---

## Q2: Resource Initialization
Where is the optimal place to initialize heavy, thread-safe resources (like a database connection pool or an AWS S3 Client) inside an AWS Lambda function written in Java?

A) Inside the `handleRequest()` method.
B) As a static field or inside the class constructor.
C) In a separate `Thread`.
D) In a `finally` block.

**Answer**: B
**Explanation**: The Cloud provider "freezes" the execution environment after a request is completed and "thaws" it for the next request. Static variables and constructor initializations are retained in memory across multiple invocations, preventing the heavy cost of re-establishing connections on every single request.

---

## Q3: GraalVM Native Image
How does GraalVM AOT (Ahead-of-Time) compilation specifically solve the Java Serverless Cold Start problem?

A) It compiles Java code down into JavaScript so it runs faster in the browser.
B) It runs the JVM in the cloud continuously so it never spins down.
C) It analyzes the application during the build phase and compiles it directly into a standalone native OS binary executable, completely eliminating the slow JVM boot time and runtime JIT compilation overhead.
D) It compresses the `.jar` file into a smaller ZIP archive.

**Answer**: C
**Explanation**: By skipping the JVM entirely, Native Images start in milliseconds and consume vastly less memory, making Java a top-tier language choice for bursty, Serverless environments.