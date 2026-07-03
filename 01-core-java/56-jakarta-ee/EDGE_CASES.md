# Module 56: Jakarta EE - Edge Cases & Pitfalls

---

## Pitfall 1: The `javax` vs `jakarta` Namespace Collision

### ❌ Wrong
Upgrading an application server (like Tomcat 10+ or WildFly 27+) but leaving legacy `javax.servlet.*` or `javax.persistence.*` imports in the Java source code. The application will fail to compile or throw `ClassNotFoundException` at runtime.

### ✅ Correct
When migrating to Jakarta EE 9+, you must execute a "Big Bang" refactoring, replacing all `javax.*` imports with `jakarta.*` (with the exception of core Java SE packages like `javax.sql` or `javax.crypto` which remain unchanged). Use automated migration tools provided by your IDE or build tools.

---

## Pitfall 2: Memory Leaks with `@Dependent` Scope in CDI

### ❌ Wrong
Using the default `@Dependent` pseudo-scope for a CDI bean that is injected into a long-lived bean (like `@ApplicationScoped`). If the `@Dependent` bean is created dynamically inside a loop or factory, it will never be garbage collected until the parent bean dies, causing a memory leak.

### ✅ Correct
Always explicitly define the scope of your CDI beans (e.g., `@RequestScoped`, `@SessionScoped`, or `@ApplicationScoped`). Avoid `@Dependent` unless you fully understand its lifecycle implications.

---

## Pitfall 3: Over-relying on Stateful Session Beans (EJBs)

### ❌ Wrong
Using `@Stateful` EJBs to hold user session data in a modern cloud-native or microservices environment. Stateful beans lock clients to specific server nodes, making horizontal scaling via Kubernetes nearly impossible without complex session replication.

### ✅ Correct
Design modern enterprise applications to be entirely **stateless** (using `@Stateless` or `@RequestScoped` CDI beans). Store any necessary session state in a distributed cache like Redis or encode it in a JWT token.