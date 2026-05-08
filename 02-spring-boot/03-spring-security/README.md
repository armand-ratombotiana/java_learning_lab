# Spring Security

Authentication and authorization module using Spring Security.

## Overview

- Authentication (who you are)
- Authorization (what you can do)
- Role-based access control
- Password encoding

## Key Concepts

- **SecurityFilterChain** - HTTP security configuration
- **UserDetailsService** - Load user data
- **PasswordEncoder** - BCrypt encoding
- **AuthenticationProvider** - Authentication logic

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-boot-starter-security

## Default Behavior

- All endpoints require authentication
- Default user: `user` (password generated in console)
- CSRF protection enabled

## Authorization Roles

| Role | Description |
|------|-------------|
| ROLE_USER | Basic user access |
| ROLE_ADMIN | Administrative access |

## Configuration

Custom security via SecurityFilterChain bean:
- URL-based authorization
- Form login / Basic auth
- Custom user details

## Version

Spring Boot 3.3.0