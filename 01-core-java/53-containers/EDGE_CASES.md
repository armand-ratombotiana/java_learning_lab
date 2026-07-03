# Module 53: Containers & Docker - Edge Cases & Pitfalls

---

## Pitfall 1: Hardcoding -Xmx in Containers

### ❌ Wrong
Setting a hardcoded heap size like `java -Xmx1024m -jar app.jar` inside a Docker container. If you deploy this image to a Kubernetes pod with a 512MB limit, the container will instantly OOMKilled when the JVM tries to allocate 1GB.

### ✅ Correct
Use `-XX:MaxRAMPercentage=75.0`. This tells the JVM to calculate its heap size as 75% of whatever memory limit the container (Docker/K8s) provides. It makes the Docker image environment-agnostic.

---

## Pitfall 2: Running as Root

### ❌ Wrong
Running the Java application as the default `root` user inside the Docker container. If there is a vulnerability in the application, an attacker could gain root privileges and potentially escape the container to access the host system.

### ✅ Correct
Always create a dedicated non-root user and group in the Dockerfile and switch to it before the `ENTRYPOINT`.
```dockerfile
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup
```

---

## Pitfall 3: Not Using .dockerignore

### ❌ Wrong
Running `COPY . .` in a Dockerfile without a `.dockerignore` file. This copies the entire `target` directory, `node_modules`, `.git` folder, and local IDE settings into the Docker build context, drastically slowing down the build and bloating the image.

### ✅ Correct
Create a `.dockerignore` file and exclude everything that isn't needed for the build.