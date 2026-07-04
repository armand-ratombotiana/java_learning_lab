# Architecture: 02-oauth2

## System Architecture

### High-Level Architecture

```
+--------------------------------------------------+
|                    DMZ / Perimeter                 |
|  +----------+  +----------+  +----------+        |
|  |  WAF     |  |  LB      |  |  API GW  |        |
|  +----------+  +----------+  +----------+        |
+------------------------+-------------------------+
                         |
+------------------------v-------------------------+
|              Spring Security Filter Chain          |
|  +------+ +------+ +------+ +------+ +---------+ |
|  |CORS  | |CSRF  | |Auth  | |Async | |FilterSec| |
|  |Filter| |Filter| |Filter| |Mgt   | |Intercept| |
|  +------+ +------+ +------+ +------+ +---------+ |
+--------------------------------------------------+
                         |
+--------------------------------------------------+
|              Authentication Layer                   |
|  +----------------+  +------------------------+   |
|  | Auth Manager   |  | Provider Manager       |   |
|  +----------------+  +------------------------+   |
+--------------------------------------------------+
                         |
+--------------------------------------------------+
|              Authorization Layer                    |
|  +----------------+  +------------------------+   |
|  |Access Decision |  |  Method Security       |   |
|  |Manager         |  |  (@PreAuthorize)       |   |
|  +----------------+  +------------------------+   |
+--------------------------------------------------+
```

## Component Architecture

### Core Security Components

| Component | Responsibility |
|-----------|---------------|
| SecurityFilterChain | Ordered list of security filters |
| AuthenticationManager | Processes authentication requests |
| ProviderManager | Delegates to AuthenticationProviders |
| AuthenticationProvider | Validates credentials |
| UserDetailsService | Loads user data |
| SecurityContextHolder | Stores security context |
| AccessDecisionManager | Makes authorization decisions |

### Integration Points

- **Database** - User storage via UserDetailsService
- **LDAP / AD** - Enterprise directory authentication
- **OAuth2 Provider** - External identity providers
- **Redis** - Distributed session storage

## Design Patterns

### Chain of Responsibility
The filter chain pattern allows each filter to process or skip the request independently.

### Strategy Pattern
Authentication providers are pluggable strategies for different auth mechanisms.

### Template Method
AbstractAuthenticationProcessingFilter defines the skeleton of authentication processing.

### Factory Pattern
SecurityFilterChain bean factory creates the appropriate filter chain.
