# Exercises: 04-spring-security

## Beginner Level

### Exercise 1: Basic Authentication Setup
Configure Spring Security with form-based login for a simple web application.

**Requirements:**
- Create a Spring Boot application with security
- Configure in-memory users with roles USER and ADMIN
- Set up form login with a custom login page
- Protect certain endpoints while leaving others public

### Exercise 2: Password Encoding
Implement secure password storage using BCrypt.

**Requirements:**
- Replace any plain text password storage
- Configure BCryptPasswordEncoder
- Verify password matching works correctly

### Exercise 3: Custom UserDetailsService
Create a database-backed UserDetailsService.

**Requirements:**
- Create a User entity with JPA annotations
- Implement UserDetailsService interface
- Connect to a database (H2 for simplicity)

## Intermediate Level

### Exercise 4: Multi-Factor Authentication
Add an extra verification step after password authentication.

**Requirements:**
- After password validation, prompt for a one-time code
- Implement a simple TOTP generator
- Store and verify the OTP code

### Exercise 5: Remember-Me Authentication
Implement persistent remember-me functionality.

**Requirements:**
- Configure remember-me with a secure key
- Store tokens in a database
- Set appropriate token expiration

### Exercise 6: Session Management
Implement comprehensive session management.

**Requirements:**
- Configure concurrent session control
- Implement session fixation protection
- Handle session expiration gracefully

## Advanced Level

### Exercise 7: Custom Authentication Provider
Create a custom provider that supports multiple authentication methods.

**Requirements:**
- Support username/password AND API key authentication
- Implement AuthenticationProvider interface
- Chain multiple authentication mechanisms

### Exercise 8: Security Event Auditing
Implement a comprehensive audit system.

**Requirements:**
- Log all authentication attempts
- Track authorization decisions
- Store audit events in a database

### Exercise 9: Distributed Security
Implement security for a microservices architecture.

**Requirements:**
- Configure token relay between services
- Implement service-to-service authentication
- Handle token propagation across service boundaries

## Challenge Exercises

### Challenge 1: Complete Auth System
Build a production-ready authentication system including:
- User registration with email verification
- Password reset flow
- Account lockout after failed attempts
- Session management
- Audit logging

### Challenge 2: Security Audit
Review a provided application and fix all security issues:
- Identify all security vulnerabilities
- Document each vulnerability
- Implement fixes
- Write tests to verify fixes
