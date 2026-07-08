# INTERNALS: 12-webauthn-passkeys



### Core Classes

**SecurityService** (com.security.XX.SecurityService)
- Package: com.security.XX
- Extends: AbstractSecurityService
- Implements: SecurityOperations
- Dependencies: AuthProvider, TokenManager, AuditLogger

**AuthProvider** (com.security.XX.AuthProvider)
- Package: com.security.XX
- Implements: AuthenticationProvider
- Methods: authenticate(), supports()
- Strategies: JWT, OAuth2, SAML (configurable)

**TokenManager** (com.security.XX.TokenManager)
- Package: com.security.XX
- Key storage: In-memory cache + DB
- Validation: Signature, expiry, issuer, audience
- Rotation: Automatic on security events

### Internal Data Flow

1. **Request Reception**: HttpServletRequest enters filter chain
2. **Authentication**: SecurityContextHolder populated with principal
3. **Authorization**: AccessDecisionManager checks permissions
4. **Response**: Security headers added, audit logged

### State Management

- Authentication state: SecurityContextHolder (ThreadLocal)
- Session state: SessionRegistry (ConcurrentHashMap)
- Token cache: Caffeine cache with TTL
- Audit queue: BlockingQueue for async logging

### Configuration Loading

1. application.yml loaded
2. SecurityProperties bound
3. Bean definitions created
4. Filter chain assembled
5. Provider manager configured
6. Endpoint security mapped

### Thread Safety

- SecurityContextHolder: InheritableThreadLocal for child threads
- TokenCache: ConcurrentHashMap + Caffeine
- AuditLogger: ConcurrentLinkedQueue for batch processing
- SessionManager: ReentrantReadWriteLock for session operations

### Error Handling Flow

1. Exception thrown in filter
2. ExceptionTranslationFilter catches
3. AuthenticationEntryPoint sends 401/403
4. Error details logged (not exposed)
5. Security metrics updated

### Extension Points

- Custom AuthenticationProvider
- Custom AccessDecisionVoter
- Custom SecurityFilter
- Custom AuthenticationEntryPoint
- Custom AccessDeniedHandler

### Initialization Sequence

`
Application starts
  +-? SecurityConfig loaded
        +-? FilterChainProxy created
              +-? SecurityFilterChain beans collected
                    +-? Filters initialized
                          +-? AuthenticationManager configured
                                +-? ProviderManager with providers
                                      +-? UserDetailsService(s) configured
                                            +-? Security properties bound
                                                  +-? Ready to accept requests
`

### Request Processing Pipeline

`
HTTP Request
  +-? FilterChainProxy.doFilter()
        +-? VirtualFilterChain (iterates filters)
              +-? SecurityContextPersistenceFilter (load/create context)
                    +-? LogoutFilter (check logout)
                          +-? UsernamePasswordAuthenticationFilter (form login)
                                +-? RequestCacheAwareFilter (saved request)
                                      +-? SecurityContextHolderAwareRequestFilter
                                            +-? AnonymousAuthenticationFilter
                                                  +-? SessionManagementFilter
                                                        +-? ExceptionTranslationFilter
                                                              +-? FilterSecurityInterceptor
                                                                    +-? Resource (controller)
`

### Memory Layout

| Object | Approximate Size | Scope | Lifetime |
|--------|-----------------|-------|----------|
| SecurityContext | 100-200 bytes | Per request | Request |
| Authentication | 200-500 bytes | Per session | Session |
| UserDetails | 500-2000 bytes | Per user | Session |
| GrantedAuthority | 50-100 bytes | Per role | Session |
| Session | 200-500 bytes | Per session | Session timeout |
| Token | 300-1000 bytes | Per token | Token expiry |

### Bootstrap Sequence

The application bootstrap follows this sequence:
1. Spring ApplicationContext initialization
2. Security auto-configuration detection
3. @EnableWebSecurity processing
4. SecurityFilterChain bean registration
5. AuthenticationManagerBuilder configuration
6. Provider manager assembly with all providers
7. Filter chain ordering and registration
8. CORS and CSRF configuration
9. Session management setup
10. Exception handling configuration
11. Method security (@EnableGlobalMethodSecurity)
12. Custom security beans initialization
