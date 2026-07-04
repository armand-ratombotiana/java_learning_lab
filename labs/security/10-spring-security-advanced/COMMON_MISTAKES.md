# Common Mistakes in 10-spring-security-advanced

## Critical Mistakes

### 1. Storing Passwords in Plain Text

```java
// INCORRECT - Never store passwords in plain text
user.setPassword(request.getPassword());

// CORRECT - Always hash passwords
user.setPassword(passwordEncoder.encode(request.getPassword()));
```

### 2. Using Weak Password Encoders

```java
// INCORRECT - NoOpPasswordEncoder is for testing only
PasswordEncoder encoder = NoOpPasswordEncoder.getInstance();

// CORRECT - BCrypt is the recommended password encoder
PasswordEncoder encoder = new BCryptPasswordEncoder();
```

### 3. Disabling CSRF Without Understanding

```java
// INCORRECT - Disabling CSRF without understanding implications
http.csrf(csrf -> csrf.disable());

// CORRECT - Only disable for stateless REST APIs
http.csrf(csrf -> csrf.disable()); // Only when stateless
```

### 4. Hardcoding Credentials

```java
// INCORRECT - Hardcoded credentials in source code
UserDetails user = User.withDefaultPasswordEncoder()
    .username("admin")
    .password("password123")
    .roles("ADMIN")
    .build();

// CORRECT - Use environment variables or external config
UserDetails user = User.builder()
    .username(System.getenv("APP_USERNAME"))
    .password(passwordEncoder.encode(System.getenv("APP_PASSWORD")))
    .roles("ADMIN")
    .build();
```

### 5. Exposing Sensitive Endpoints

```java
// INCORRECT - Unrestricted access to all endpoints
http.authorizeHttpRequests(auth -> auth
    .anyRequest().permitAll()
);

// CORRECT - Explicit access rules
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/api/public/**").permitAll()
    .requestMatchers("/api/admin/**").hasRole("ADMIN")
    .anyRequest().authenticated()
);
```

### 6. Session Fixation Vulnerability

```java
// INCORRECT - No session fixation protection
http.sessionManagement(session -> session
    .sessionFixation().none()
);

// CORRECT - Always migrate session on authentication
http.sessionManagement(session -> session
    .sessionFixation().migrateSession()
);
```

### 7. Information Leakage in Error Messages

```java
// INCORRECT - Revealing whether username exists
throw new UsernameNotFoundException("User not found: " + username);

// CORRECT - Generic error message to prevent enumeration
throw new BadCredentialsException("Invalid username or password");
```

## Security Checklist

- [ ] Passwords hashed with BCrypt or stronger
- [ ] CSRF protection enabled for state-changing operations
- [ ] HTTPS enforced
- [ ] Session fixation protection enabled
- [ ] Security headers set (HSTS, X-Frame-Options, etc.)
- [ ] Input validation on all endpoints
- [ ] Error messages don't leak information
- [ ] Rate limiting on login endpoints
- [ ] Account lockout after failed attempts
- [ ] Audit logging for sensitive operations
