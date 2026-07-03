# Module 28: Spring Security - Edge Cases & Pitfalls

---

## Pitfall 1: Leaving CSRF Enabled on Stateless APIs

### ❌ Wrong
Leaving Cross-Site Request Forgery (CSRF) protection enabled on a completely stateless REST API (e.g., one that uses JWTs instead of session cookies). If CSRF is active, POST/PUT/DELETE requests will be blocked with a 403 Forbidden because the client isn't sending a CSRF token.

### ✅ Correct
Disable CSRF explicitly if your application relies entirely on stateless tokens (like JWT) stored in Local Storage or memory, as CSRF relies on browser cookie mechanics.
```java
http.csrf(csrf -> csrf.disable());
```

---

## Pitfall 2: Storing Passwords in Plaintext

### ❌ Wrong
Saving user passwords exactly as typed into the database.
```java
// Vulnerable to database breaches
user.setPassword(rawPassword);
userRepository.save(user);
```

### ✅ Correct
Always use a strong password hashing algorithm. Spring Security provides `PasswordEncoder` (specifically `BCryptPasswordEncoder`) for this exact purpose.
```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Upon registration:
user.setPassword(passwordEncoder.encode(rawPassword));
```

---

## Pitfall 3: Modifying SecurityContext from Background Threads

### ❌ Wrong
Trying to access the authenticated user from an asynchronous thread (e.g., using `@Async` or inside a custom `CompletableFuture`). The `SecurityContextHolder` is bound to a `ThreadLocal` by default, meaning spawned threads do not inherit the security context.
```java
@Async
public void processTask() {
    // ❌ NullPointerException or Anonymous user!
    String username = SecurityContextHolder.getContext().getAuthentication().getName(); 
}
```

### ✅ Correct
Configure Spring Security to use `MODE_INHERITABLETHREADLOCAL` or use `DelegatingSecurityContextExecutor` to manually pass the context into the new thread.