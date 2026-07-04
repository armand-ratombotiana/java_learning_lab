# Theory: Security Basics

## Spring Security Architecture

### Security Filter Chain
Spring Security uses a chain of servlet filters:
1. SecurityContextPersistenceFilter
2. LogoutFilter
3. UsernamePasswordAuthenticationFilter
4. ExceptionTranslationFilter
5. FilterSecurityInterceptor

### Authentication
- **AuthenticationManager**: Core authentication strategy
- **ProviderManager**: Delegates to AuthenticationProvider(s)
- **AuthenticationProvider**: Validates credentials
- **UserDetailsService**: Loads user details from database
- **PasswordEncoder**: Encodes and verifies passwords

### Authorization
- **FilterSecurityInterceptor**: Authorizes HTTP requests
- **@PreAuthorize**: Method-level access control
- **@Secured**: Simple role-based access
- **GrantedAuthority**: Represents permission/role

### Core Annotations
- @EnableWebSecurity
- @EnableGlobalMethodSecurity
- @PreFilter / @PostFilter
- @PreAuthorize("hasRole('ADMIN')")
