# Internals of 06-encryption

## Core Components Deep Dive

### SecurityContextHolder

The SecurityContextHolder is the cornerstone of Spring Security's authentication state management:

```java
// ThreadLocal storage by default
SecurityContext context = SecurityContextHolder.createEmptyContext();
context.setAuthentication(authentication);
SecurityContextHolder.setContext(context);

// For asynchronous operations
SecurityContextHolder.setStrategyName(
    SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
```

### SecurityContext

Holds the Authentication object and potentially session information:

```java
public interface SecurityContext extends Serializable {
    Authentication getAuthentication();
    void setAuthentication(Authentication authentication);
}
```

### Authentication

The Authentication interface represents the authenticated principal:

```java
public interface Authentication extends Principal, Serializable {
    Collection<? extends GrantedAuthority> getAuthorities();
    Object getCredentials();
    Object getDetails();
    Object getPrincipal();
    boolean isAuthenticated();
    void setAuthenticated(boolean isAuthenticated);
}
```

### AuthenticationManager

The core authentication interface:

```java
public interface AuthenticationManager {
    Authentication authenticate(Authentication authentication)
        throws AuthenticationException;
}
```

### ProviderManager

The default implementation delegates to a chain of AuthenticationProviders:

```java
public class ProviderManager implements AuthenticationManager {
    private List<AuthenticationProvider> providers;

    public Authentication authenticate(Authentication auth) {
        for (AuthenticationProvider provider : providers) {
            if (provider.supports(auth.getClass())) {
                return provider.authenticate(auth);
            }
        }
        throw new ProviderNotFoundException(...);
    }
}
```

### SecurityFilterChain

The filter chain that processes every HTTP request:

```java
public interface SecurityFilterChain {
    boolean matches(HttpServletRequest request);
    List<Filter> getFilters();
}
```

## Threading Model

Spring Security uses ThreadLocal to store the SecurityContext:
- Each thread has its own security context
- Child threads do not inherit the parent's context by default
- Use MODE_INHERITABLETHREADLOCAL for async scenarios
- Reactive programming uses Reactor's Context instead
