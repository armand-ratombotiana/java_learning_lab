# Module 53: Containers & Docker - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between a Virtual Machine (VM) and a Docker Container?
**Answer**:
A VM virtualizes the hardware and runs a full guest operating system (OS) on top of a hypervisor. This makes VMs heavy, slow to start, and resource-intensive.
A container virtualizes the OS. It runs directly on the host machine's kernel but in an isolated environment (using namespaces and cgroups). This makes containers extremely lightweight, fast to start, and efficient, allowing you to run many more containers than VMs on the same hardware.

### Q2: What is a Multi-Stage Docker Build, and why is it important for Java applications?
**Answer**:
A multi-stage build allows you to use multiple `FROM` statements in a single Dockerfile.
For Java, compiling the code requires Maven/Gradle and the full JDK, which are massive dependencies. However, running the compiled `.jar` only requires a lightweight JRE.
By using a multi-stage build, you can compile the app in "Stage 1", and then copy *only* the final `.jar` file into a tiny "Stage 2" JRE image. The final production image discards all the heavy build tools, resulting in a much smaller image size, faster deployment times, and a reduced security attack surface.

### Q3: Explain the `depends_on` directive in Docker Compose. Does it guarantee the database is ready to accept connections?
**Answer**:
`depends_on` controls the startup order of services. If Service A depends on Service B, Docker Compose will start Service B before Service A.
However, it **only** guarantees that the *container* has started. It does **not** guarantee that the *application inside the container* (like PostgreSQL) has finished initializing and is ready to accept connections. To handle this, your application should implement retry logic on startup, or you should use healthchecks in Docker Compose.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Debugging an OOMKilled Java Container
**Problem**: You deploy a Spring Boot application to a Kubernetes pod with a memory limit of 512MB. The application keeps crashing with the reason `OOMKilled`. You inspect the Dockerfile and see `ENTRYPOINT ["java", "-Xmx1024m", "-jar", "app.jar"]`. What is wrong, and how do you fix it?

**Solution**:
The JVM is configured to use up to 1024MB of heap (`-Xmx1024m`), but the container itself is limited by the OS (via Kubernetes/Docker cgroups) to 512MB. When the JVM tries to allocate more memory than the container limit, the Linux kernel's Out Of Memory killer terminates the container immediately.
**Fix**: Remove the hardcoded `-Xmx` limit. Instead, use `-XX:MaxRAMPercentage=75.0`. This tells the JVM to dynamically calculate its max heap size based on the container's 512MB limit, ensuring it never exceeds the bound and leaving 25% for non-heap memory (stack, metaspace, off-heap buffers).