# Visual Guide to 05-keycloak

## Mermaid Sequence Diagram

```mermaid
sequenceDiagram
    participant C as Client
    participant F as SecurityFilter
    participant AM as AuthenticationManager
    participant PM as ProviderManager
    participant AP as AuthenticationProvider
    participant UD as UserDetailsService
    participant SC as SecurityContext

    C->>F: HTTP Request
    F->>AM: authenticate(request)
    AM->>PM: authenticate(auth)
    PM->>AP: supports(auth)
    AP-->>PM: true
    PM->>AP: authenticate(auth)
    AP->>UD: loadUserByUsername(username)
    UD-->>AP: UserDetails
    AP-->>PM: Authentication (success)
    PM-->>AM: Authentication
    AM-->>F: Authentication
    F->>SC: setContext(auth)
    F->>C: HTTP Response
```

## Request Flow Diagram

```
Client -> FilterChain -> AuthenticationManager -> ProviderManager -> UserDetailsService
   |           |                |                      |
   +-----------+----------------+----------------------+

1. HTTP Request arrives
2. Security filters intercept
3. Authentication extracted
4. Credentials validated
5. Security context established
6. Authorization check
7. Resource served or denied
```

## Security Architecture Diagram

```
+------------------------------------------------+
|              Spring Security Filter Chain        |
|  +------+ +------+ +------+ +------+ +-------+ |
|  |CORS  | |CSRF  | |Auth  | |Async | |Filter | |
|  |Filter| |Filter| |Filter| |Mgt   | |Security| |
|  +------+ +------+ +------+ +------+ +-------+ |
+------------------------------------------------+
                        |
+------------------------------------------------+
|           Application Controllers                |
|  @RequestMapping annotated methods               |
+------------------------------------------------+
                        |
+------------------------------------------------+
|          Service Layer (Business Logic)          |
|  @PreAuthorize, @Secured, @RolesAllowed         |
+------------------------------------------------+
```
