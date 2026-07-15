# Spring Data JPA Internals: The Proxy Mechanism

## 🔬 How Dynamic Repositories Work
When you define an interface like `public interface UserRepository extends JpaRepository<User, Long> { }`, you never write an implementation class. Yet, at runtime, Spring injects a fully functional object into your services. How?

### 1. Proxy Pattern
At startup, Spring Data JPA scans for interfaces extending `Repository`. For each interface, it uses the **JDK Dynamic Proxy** (or CGLIB) to create a proxy object.
- When you call a method on the repository, the call is intercepted by a `QueryExecutorMethodInterceptor`.

### 2. The Implementation Class: `SimpleJpaRepository`
Most standard methods (`save`, `findById`, `findAll`) are not actually implemented by the proxy. The proxy delegates these calls to a default implementation class provided by Spring Data: `org.springframework.data.jpa.repository.support.SimpleJpaRepository`.
This class contains the actual `EntityManager` calls (`em.persist()`, `em.find()`).

### 3. Query Derivation (The Parser)
When you call a custom method like `findByEmailAndStatus(String email, Status status)`, Spring Data doesn't have a pre-written implementation.
1. The **PartTree** parser breaks the method name into tokens: `findBy`, `Email`, `And`, `Status`.
2. It maps these tokens to the entity's fields (`user.email`, `user.status`).
3. It constructs a **JPQL** string (or a Criteria API query) behind the scenes.
4. It executes the query via the `EntityManager`.

## 🛑 The N+1 Problem
The most common performance pitfall in JPA is the N+1 problem.
If a `User` has many `Posts` (Lazy Loaded), and you run:
`List<User> users = repository.findAll();`
`for(User u : users) { u.getPosts().size(); }`

1. Spring runs 1 query to get all $N$ users.
2. For *each* user, it runs a separate query to get their posts.
3. Total queries = $1 + N$.

**The Solution: Entity Graphs**.
Spring Data JPA allows you to use `@EntityGraph` to force a `JOIN FETCH` at the database level, reducing $1+N$ queries down to exactly 1 query.