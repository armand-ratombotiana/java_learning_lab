# Module 25: Spring Boot Basics - Edge Cases & Pitfalls

---

## Pitfall 1: Fat Jar Size

### ❌ Wrong
Including too many unnecessary starter dependencies, causing the generated "fat jar" (which includes embedded Tomcat) to balloon in size and slow down startup time.

### ✅ Correct
Only include the starters you actually need. If deploying in a microservices environment, consider optimizing dependencies.

---

## Pitfall 2: Component Scanning Issues

### ❌ Wrong
Placing `@Component`, `@Service`, or `@RestController` classes outside the package hierarchy of the class annotated with `@SpringBootApplication`. Spring Boot only scans the current package and its sub-packages by default.

### ✅ Correct
Ensure the main application class is in a root package above all other classes, or explicitly use `@ComponentScan("com.example.otherpackage")`.

---

## Pitfall 3: Port Conflicts

### ❌ Wrong
Running multiple Spring Boot applications locally without changing the default port (8080). This results in `Web server failed to start. Port 8080 was already in use`.

### ✅ Correct
Override the port in `application.properties` (e.g., `server.port=8081`) or set `server.port=0` to let Spring Boot pick a random available port.