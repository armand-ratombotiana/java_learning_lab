# Deep Dive — Spring Security Internals: Filter Chain, Authentication, Context

## SecurityFilterChain Architecture

The `SecurityFilterChain` is the backbone of Spring Security. Every request passes through a chain of filters.

### FilterChainRegistration

```java
public class FilterChainProxy extends GenericFilterBean {
    private final List<SecurityFilterChain> filterChains;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        // For each SecurityFilterChain, check if it matches the request
        for (SecurityFilterChain filterChain : filterChains) {
            if (filterChain.matches(request)) {
                // Delegate to the Security filter chain
                filterChain.getSecurityFilterChain(request, response, chain);
                return;
            }
        }
        // No match, continue with the container's filter chain
        chain.doFilter(request, response);
    }
}
```

### SecurityFilterChain Interface

```java
public interface SecurityFilterChain {
    boolean matches(HttpServletRequest request);
    List<Filter> getFilters();
}
```

### Default Filter Order (FilterOrderRegistration)

Spring Security provides `FilterOrderRegistration` to manage relative ordering:

```java
// FilterOrderRegistration maintains a fixed order
public class FilterOrderRegistration {
    private static final int INITIAL_ORDER = 100;
    private static final int ORDER_STEP = 100;

    // Built-in filter order assignments:
    // 100  - DisableEncodeUrlFilter
    // 200  - SecurityContextHolderFilter
    // 300  - WebAsyncManagerIntegrationFilter
    // 400  - SecurityContextPersistenceFilter
    // 500  - HeaderWriterFilter
    // 600  - CorsFilter
    // 700  - CsrfFilter
    // 800  - LogoutFilter
    // 900  - OAuth2AuthorizationRequestRedirectFilter
    // 1000 - Saml2WebSsoAuthenticationRequestFilter
    // 1100 - X509AuthenticationFilter
    // 1200 - AbstractPreFlightHeadersFilter
    // 1300 - RequestCacheAwareFilter
    // 1400 - SecurityContextHolderAwareRequestFilter
    // 1500 - JaasApiIntegrationFilter
    // 1600 - RememberMeAuthenticationFilter
    // 1700 - AnonymousAuthenticationFilter
    // 1800 - OAuth2AuthorizationCodeGrantFilter
    // 1900 - SessionManagementFilter
    // 2000 - ExceptionTranslationFilter
    // 2100 - FilterSecurityInterceptor
    // 2200 - SwitchUserFilter
}
```

### Custom SecurityFilterChain Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")             // Matcher for this chain
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/web/**")
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/web/login", "/web/register").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .logout(logout -> logout.logoutSuccessUrl("/web/login"));

        return http.build();
    }
}
```

### How FilterChainProxy Selects a Chain

```java
// In FilterChainProxy.doFilter(), for each SecurityFilterChain:
for (SecurityFilterChain filterChain : this.filterChains) {
    if (filterChain.matches(request)) {
        VirtualFilterChain virtualFilterChain = new VirtualFilterChain(request, response, chain, filterChain.getFilters());
        virtualFilterChain.doFilter(request, response);
        return;
    }
}
chain.doFilter(request, response);
```

## ProviderManager and AuthenticationProvider Chain

### ProviderManager

`ProviderManager` implements `AuthenticationManager` and delegates to an ordered chain of `AuthenticationProvider` implementations:

```java
public class ProviderManager implements AuthenticationManager, MessageSourceAware, InitializingBean {
    private final List<AuthenticationProvider> providers;
    private AuthenticationManager parent;
    private final boolean eraseCredentialsAfterAuthentication;
    private final boolean eraseSecretAfterAuthentication;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Class of the authentication token
        Class<? extends Authentication> toTest = authentication.getClass();

        for (AuthenticationProvider provider : getProviders()) {
            // Check if this provider supports the token type
            if (!provider.supports(toTest)) {
                continue;
            }

            // Attempt authentication
            try {
                result = provider.authenticate(authentication);
                if (result != null) {
                    copyDetails(authentication, result);
                    break;
                }
            } catch (AccountStatusException | InternalAuthenticationServiceException ex) {
                prepareException(ex, authentication);
                // throw — authentication stopped
            } catch (AuthenticationException ex) {
                // Continue to next provider
                lastException = ex;
            }
        }

        // If no provider succeeded, try parent
        if (result == null && parent != null) {
            try {
                result = parent.authenticate(authentication);
            } catch (AuthenticationException ex) {
                lastException = ex;
            }
        }

        if (result != null) {
            if (this.eraseCredentialsAfterAuthentication) {
                result.eraseCredentials();
            }
            if (this.eraseSecretAfterAuthentication) {
                result.eraseSecrets();
            }
            this.eventPublisher.publishAuthenticationSuccess(result);
            return result;
        }

        // Authentication failed entirely
        if (lastException == null) {
            lastException = new ProviderNotFoundException("No provider found for " + authentication.getClass());
        }
        if (parent == null) {
            prepareException(lastException, authentication);
        }
        throw lastException;
    }
}
```

### AuthenticationProvider Implementations

| Provider | Supported Token | What It Does |
|----------|----------------|--------------|
| `DaoAuthenticationProvider` | `UsernamePasswordAuthenticationToken` | Loads user from `UserDetailsService`, validates password |
| `JwtAuthenticationProvider` | `BearerTokenAuthenticationToken` | Validates JWT tokens |
| `OAuth2LoginAuthenticationProvider` | `OAuth2LoginAuthenticationToken` | OAuth2 login flow |
| `RememberMeAuthenticationProvider` | `RememberMeAuthenticationToken` | Remember-me auto login |
| `AnonymousAuthenticationProvider` | `AnonymousAuthenticationToken` | Provides anonymous user |
| `LdapAuthenticationProvider` | `UsernamePasswordAuthenticationToken` | LDAP/ActiveDirectory auth |
| `CasAuthenticationProvider` | `CasAuthenticationToken` | CAS single sign-on |

### Ordered Provider Iteration

```java
@Bean
public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
    // Provider 1: Database
    DaoAuthenticationProvider dbProvider = new DaoAuthenticationProvider(passwordEncoder);
    dbProvider.setUserDetailsService(userDetailsService);

    // Provider 2: LDAP (fallback)
    LdapAuthenticationProvider ldapProvider = new LdapAuthenticationProvider(...);

    // Provider 3: API key
    ApiKeyAuthenticationProvider apiProvider = new ApiKeyAuthenticationProvider();

    // Provider manager with ordered chain
    return new ProviderManager(Arrays.asList(dbProvider, ldapProvider, apiProvider));
}
```

### Custom AuthenticationProvider

```java
@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getCredentials();
        Optional<String> userId = apiKeyService.findUserIdByKey(apiKey);

        if (userId.isPresent()) {
            return new ApiKeyAuthenticationToken(userId.get(), apiKey, List.of(new SimpleGrantedAuthority("ROLE_USER")));
        }

        return null; // Let next provider try
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

## Authentication Flow Through Filters

### Step-by-step authentication process

```
1. Request arrives at server
2. FilterChainProxy matches request to SecurityFilterChain
3. SecurityContextHolderFilter (or SecurityContextPersistenceFilter) loads SecurityContext
4. Authentication filters execute:
   a. UsernamePasswordAuthenticationFilter checks for login POST
   b. BasicAuthenticationFilter checks Authorization: Basic header
   c. BearerTokenAuthenticationFilter checks Authorization: Bearer header
   d. OAuth2LoginAuthenticationFilter handles OAuth2 callbacks
5. If authentication found:
   a. Authentication is passed to ProviderManager
   b. ProviderManager iterates AuthenticationProviders
   c. Matching provider authenticates and returns populated Authentication
   d. SecurityContextHolder.getContext().setAuthentication(authenticated)
6. ExceptionTranslationFilter wraps protected resources
7. FilterSecurityInterceptor checks authorization
8. If authorized: request proceeds to controller
9. If not authorized: AccessDeniedException → ExceptionTranslationFilter → 403

After request:
10. SecurityContextHolderFilter clears the SecurityContext
```

### UsernamePasswordAuthenticationFilter

```java
public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    // Default filter processes POST /login

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        // 1. Extract username and password from request
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 2. Create unauthenticated token
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username, password);

        // 3. Allow customization
        setDetails(request, authRequest);

        // 4. Delegate to AuthenticationManager
        return this.getAuthenticationManager().authenticate(authRequest);
    }
}
```

### BearerTokenAuthenticationFilter (JWT)

```java
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        // 1. Extract token from Authorization header
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.toLowerCase().startsWith("bearer ")) {
            chain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);

        // 2. Create authentication request
        BearerTokenAuthenticationToken authRequest = new BearerTokenAuthenticationToken(token);

        // 3. Resolve AuthenticationManager (per-request)
        AuthenticationManager manager = authenticationManagerResolver.resolve(request);

        // 4. Authenticate
        Authentication auth = manager.authenticate(authRequest);

        // 5. Set authenticated context
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 6. Continue filter chain
        chain.doFilter(request, response);
    }
}
```

## SecurityContextHolder Strategies

The `SecurityContextHolder` stores the `SecurityContext` (which contains the current `Authentication`).

### Strategy Modes

```java
// StrategyHolder strategy supported
public class SecurityContextHolder {
    public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
    public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
    public static final String MODE_GLOBAL = "MODE_GLOBAL";

    private static final String STRATEGY_CLASS_NAME = "spring.security.strategy";

    // Default: MODE_THREADLOCAL
    private static SecurityContextHolderStrategy strategy = new ThreadLocalSecurityContextHolderStrategy();
}
```

### MODE_THREADLOCAL (Default)

```java
// ThreadLocalSecurityContextHolderStrategy
public class ThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    private static final ThreadLocal<SecurityContext> contextHolder = new ThreadLocal<>();

    @Override
    public void clearContext() {
        contextHolder.remove();
    }

    @Override
    public SecurityContext getContext() {
        SecurityContext ctx = contextHolder.get();
        if (ctx == null) {
            ctx = createEmptyContext();
            contextHolder.set(ctx);
        }
        return ctx;
    }

    @Override
    public void setContext(SecurityContext context) {
        // Set for current thread only
        contextHolder.set(context);
    }
}
```

**Behavior:** SecurityContext is scoped to the current thread only. If a new thread is spawned (e.g., `@Async`), the SecurityContext is NOT automatically propagated.

**Use case:** Standard synchronous request handling where one request stays on one thread.

### MODE_INHERITABLETHREADLOCAL

```java
// InheritableThreadLocalSecurityContextHolderStrategy
public class InheritableThreadLocalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    private static final ThreadLocal<SecurityContext> contextHolder = new InheritableThreadLocal<>();

    @Override
    public void setContext(SecurityContext context) {
        contextHolder.set(context);
    }

    @Override
    public SecurityContext getContext() {
        // Child threads inherit parent's SecurityContext!
        return contextHolder.get();
    }

    @Override
    public void clearContext() {
        contextHolder.remove();
    }
}
```

**Behavior:** SecurityContext is inherited by child threads automatically via `InheritableThreadLocal`. When a parent thread spawns a child thread, the child's initial context matches the parent's context.

**Use case:** When using `@Async` or `TaskExecutor` and the child task needs the parent's authentication context.

**Warning:** Thread pools reuse threads. Task executors must be configured to clear or re-inherit context:

```java
@Bean
public Executor asyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setTaskDecorator(new SecurityContextHolderTaskDecorator());
    return executor;
}
```

### MODE_GLOBAL

```java
// GlobalSecurityContextHolderStrategy
public class GlobalSecurityContextHolderStrategy implements SecurityContextHolderStrategy {
    private static final SecurityContext context = new SecurityContextImpl(new HashMap<>());

    @Override
    public void setContext(SecurityContext context) {
        // This is very dangerous in multi-threaded environments
        // Used only for Swing/desktop applications
    }
}
```

**Behavior:** Single SecurityContext shared across all threads. Extremely dangerous in production web applications.

### Switching Strategies

```properties
# application.properties
spring.security.strategy=MODE_INHERITABLETHREADLOCAL
```

Or programmatically (before any requests):

```java
@PostConstruct
public void init() {
    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
}
```

## SecurityContextHolderFilter

In Spring Security 6+, `SecurityContextHolderFilter` replaces the legacy `SecurityContextPersistenceFilter`:

```java
public class SecurityContextHolderFilter extends OncePerRequestFilter {
    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {
        // 1. Load SecurityContext from repository (session, JWT token, etc.)
        SecurityContext context = this.securityContextHolderStrategy.loadSecurityContext(request);

        try {
            // 2. Set SecurityContext on current thread
            this.securityContextHolderStrategy.setDeferredContext(context);

            // 3. Continue filter chain (authentication happens during this call)
            chain.doFilter(request, response);

        } finally {
            // 4. Clear context after request completes
            this.securityContextHolderStrategy.clearContext();
        }
    }
}
```

## SecurityContextRepository

`SecurityContextRepository` manages loading and saving the `SecurityContext`:

### HttpSessionSecurityContextRepository (Default for Servlet Applications)

```java
public class HttpSessionSecurityContextRepository implements SecurityContextRepository {
    public static final String SPRING_SECURITY_CONTEXT_KEY = "SPRING_SECURITY_CONTEXT";

    private String securityContextKey = SPRING_SECURITY_CONTEXT_KEY;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpSession session = requestHolder.getSession(false);
        if (session == null) {
            return SecurityContextHolder.createEmptyContext();
        }

        Object obj = session.getAttribute(this.securityContextKey);
        if (obj instanceof SecurityContext sc) {
            return sc;
        }
        return SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpRequestResponseHolder requestResponseHolder) {
        SaveContextOnUpdateOrErrorResponseWrapper response = new SaveContextOnUpdateOrErrorResponseWrapper(requestResponseHolder.getResponse());

        // Authentication success or change → save to session
        SecurityContext currentContext = loadContext(requestResponseHolder);
        if (context != null && !context.equals(currentContext)) {
            HttpSession session = requestResponseHolder.getRequest().getSession(true);
            session.setAttribute(this.securityContextKey, context);
        }
    }
}
```

### RequestAttributeSecurityContextRepository (for OAuth2)

Used when tokens carry security context (no session needed):

```java
public class RequestAttributeSecurityContextRepository implements SecurityContextRepository {
    public static final String DEFAULT_REQUEST_ATTR_NAME = "org.springframework.security.web.context.SecurityContext";

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        HttpServletRequest request = requestResponseHolder.getRequest();
        SecurityContext context = (SecurityContext) request.getAttribute(DEFAULT_REQUEST_ATTR_NAME);
        return context != null ? context : SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpRequestResponseHolder requestResponseHolder) {
        requestResponseHolder.getRequest().setAttribute(DEFAULT_REQUEST_ATTR_NAME, context);
    }
}
```

### DelegatingSecurityContextRepository

Allows multiple repositories to work together:

```java
public class DelegatingSecurityContextRepository implements SecurityContextRepository {
    private final List<SecurityContextRepository> delegates;

    @Override
    public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
        for (SecurityContextRepository delegate : delegates) {
            SecurityContext context = delegate.loadContext(requestResponseHolder);
            if (context != null && context.getAuthentication() != null) {
                return context;
            }
        }
        return SecurityContextHolder.createEmptyContext();
    }

    @Override
    public void saveContext(SecurityContext context, HttpRequestResponseHolder requestResponseHolder) {
        for (SecurityContextRepository delegate : delegates) {
            delegate.saveContext(context, requestResponseHolder);
        }
    }
}
```

## RequestRejectedHandler

Spring Security can gracefully handle invalid requests:

```java
@Bean
public WebSecurityCustomizer webSecurityCustomizer() {
    return (web) -> web
        .httpFirewall(new StrictHttpFirewall())  // Default
        .requestRejectedHandler(new HttpStatusRequestRejectedHandler());
}
```

The `StrictHttpFirewall` rejects:
- Semicolons in URLs
- Double-encoded slashes
- Non-ASCII characters
- Line feeds and carriage returns
- `%2e%2e` directory traversal

## Method Security Internals

```java
@Configuration
@EnableMethodSecurity  // Replaces @EnableGlobalMethodSecurity in Spring Security 6+
public class MethodSecurityConfig {
    @Bean
    public PrePostMethodSecurityConfiguration prePostConfig() {
        return new PrePostMethodSecurityConfiguration();
    }
}
```

This enables:
- `@PreAuthorize` / `@PostAuthorize`
- `@PreFilter` / `@PostFilter`
- `@Secured`
- `@RolesAllowed` (JSR-250)

### PreAuthorize Evaluation

```java
// PrePostAnnotationSecurityMetadataSource extracts @PreAuthorize value
// MethodSecurityInterceptor.invoke() calls AccessDecisionManager

// Example:
@PreAuthorize("hasRole('ADMIN') or hasPermission(#user, 'WRITE')")

// Evaluation produces:
// - adminRoleVoter.check(authentication) → ADMIN role check
// - permissionEvaluator.hasPermission(authentication, user, 'WRITE')
```

## SecurityContext Propagation for Async

### Default: MODE_THREADLOCAL

```java
@Async
public CompletableFuture<User> loadUser(Long id) {
    // SecurityContext is NOT available here
    // SecurityContextHolder.getContext() returns empty context
}
```

### Solution: MODE_INHERITABLETHREADLOCAL or SecurityContextRunnable

```java
@Bean
public Executor taskExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setTaskDecorator(securityContextDecorator());
    return executor;
}

private TaskDecorator securityContextDecorator() {
    return (Runnable task) -> {
        SecurityContext context = SecurityContextHolder.getContext();
        return () -> {
            try {
                SecurityContextHolder.setContext(context);
                task.run();
            } finally {
                SecurityContextHolder.clearContext();
            }
        };
    };
}
```