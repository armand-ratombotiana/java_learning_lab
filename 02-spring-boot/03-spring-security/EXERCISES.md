# Spring Security - Exercises

## Lab Exercises

### Exercise 1: Run Application
```bash
mvn spring-boot:run
```
- Note default security is enabled
- All endpoints require authentication

### Exercise 2: Explore Security Configuration
- Identify security configuration class
- Note `@EnableWebSecurity` annotation
- Understand filter chain configuration

### Exercise 3: Authentication
- Find form login configuration
- Understand in-memory / JDBC / LDAP auth
- Try default credentials (if configured)

### Exercise 4: Authorization
- Add role-based access control
- Configure `/admin/**` for ADMIN role
- Configure `/user/**` for USER role

### Exercise 5: Custom Security
- Add custom `UserDetailsService`
- Implement database-backed authentication
- Add password encoding (BCrypt)

### Exercise 6: CSRF & CORS
- Understand CSRF protection
- Configure CORS for REST API
- Test withPostman (disable CSRF for testing)

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

## H2 Console (if enabled)
Requires CSRF exemption or proper headers.