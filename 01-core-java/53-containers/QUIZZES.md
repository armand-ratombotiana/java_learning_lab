# Module 53: Containers & Docker - Quizzes

---

## Q1: Multi-Stage Builds
What is the primary benefit of using a Multi-Stage Dockerfile for a Java application?

A) It allows running multiple Java applications in the same container.
B) It builds the application faster.
C) It allows using a heavy JDK image to compile the code in the first stage, and copying only the final compiled `.jar` into a tiny JRE image for the final stage, reducing the final image size and attack surface.
D) It prevents Docker from caching layers.

**Answer**: C
**Explanation**: Multi-stage builds separate the build environment from the runtime environment, ensuring build tools and source code are not included in the final production image.

---

## Q2: JVM Memory Management
Why is it recommended to use `-XX:MaxRAMPercentage` instead of `-Xmx` when running Java inside a Docker container?

A) Because `-Xmx` only works on Windows.
B) Because `-XX:MaxRAMPercentage` makes the heap scale dynamically based on the memory limit assigned to the container by Docker/Kubernetes, making the image reusable across different environments.
C) Because `-Xmx` disables the Garbage Collector.
D) Because `-XX:MaxRAMPercentage` limits CPU usage.

**Answer**: B
**Explanation**: Hardcoding `-Xmx` locks the memory. If the container is given less memory than `-Xmx`, the OS will kill it (OOMKilled). Using percentages ensures the JVM respects the container's limits.

---

## Q3: Security
Why should you run your Java application as a non-root user inside a Docker container?

A) Because root users cannot run Java.
B) To save memory.
C) To follow the principle of least privilege. If an attacker breaches the container, running as non-root prevents them from easily escalating privileges and escaping to the host machine.
D) Because Docker requires it.

**Answer**: C
**Explanation**: Containers share the host kernel. A root user inside the container is fundamentally the root user on the host. Restricting privileges limits the blast radius of a security breach.