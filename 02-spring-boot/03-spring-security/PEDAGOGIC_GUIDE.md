# Spring Security - Pedagogic Guide

## Learning Path

### Phase 1: Security Fundamentals (Day 1)
1. **Authentication** - Who is the user?
2. **Authorization** - What can the user do?
3. **Spring Security Filter Chain**
4. **Default Security Behavior**

### Phase 2: Configuration (Day 2)
1. **SecurityFilterChain** bean
2. **UserDetailsService** - User data loading
3. **PasswordEncoder** - BCrypt encoding
4. **AuthenticationEntryPoint** - 401 handling

### Phase 3: Authorization Rules (Day 3)
1. **authorizeHttpRequests()** - URL-based security
2. **hasRole()** / **hasAuthority()** - Role checks
3. **permitAll()** / **denyAll()** - Access control
4. **Method Security** - @Secured, @PreAuthorize

## Key Concepts

### Security Flow
1. Request arrives
2. Filter chain processes
3. Authentication manager validates
4. Security context set
5. Authorization checks
6. Access granted/denied

### Authentication Providers
- InMemoryUserDetailsManager (dev)
- JdbcDaoImpl (database)
- Custom UserDetailsService

### Password Encoding
- **Never store plain passwords**
- Use BCrypt (recommended)
- Encoding strength: 10+
- Matches like raw vs encoded

## Common Configurations

### Form Login
```java
.formLogin(form -> form.defaultSuccessUrl("/"))
```

### Basic Auth
```java
.httpBasic(Customizer.withDefaults())
```

### JWT (Advanced)
- Token-based stateless auth
- Filter validates each request
- No session management

## Best Practices
- Always use password encoding
- HTTPS in production
- Implement account lockout
- CSRF protection enabled
- Secure session cookies