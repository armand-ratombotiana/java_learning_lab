# Flashcards: 03-jwt

## Card 1
**Q:** What is authentication?
**A:** The process of verifying the identity of a user or system.

## Card 2
**Q:** What is authorization?
**A:** The process of determining what an authenticated user is allowed to do.

## Card 3
**Q:** What is BCrypt?
**A:** A password hashing function designed for secure credential storage.

## Card 4
**Q:** What is Spring Security's SecurityContextHolder?
**A:** A thread-local holder that stores the current security context.

## Card 5
**Q:** What is the DelegatingFilterProxy?
**A:** A servlet filter that delegates requests to Spring-managed filter beans.

## Card 6
**Q:** What is CSRF?
**A:** Cross-Site Request Forgery - an attack that tricks users into performing unwanted actions.

## Card 7
**Q:** What is session fixation?
**A:** An attack where an attacker forces a user to use a known session ID.

## Card 8
**Q:** What is the AuthenticationManager?
**A:** The core interface for processing authentication requests in Spring Security.

## Card 9
**Q:** What does @PreAuthorize do?
**A:** A method-level security annotation that checks authorization before method execution.

## Card 10
**Q:** What is the PasswordEncoder interface?
**A:** Defines methods for encoding and verifying passwords.

## Card 11
**Q:** What is a GrantedAuthority?
**A:** Represents a permission or role granted to an authenticated principal.

## Card 12
**Q:** What is UserDetailsService?
**A:** Loads user-specific data during authentication.

## Card 13
**Q:** What is SecurityFilterChain?
**A:** An ordered list of security filters that process HTTP requests.

## Card 14
**Q:** Difference between hasRole() and hasAuthority()?
**A:** hasRole() checks for ROLE_ prefixed authorities; hasAuthority() checks exact names.

## Card 15
**Q:** What is the Authentication interface?
**A:** Represents the token for an authentication request or the authenticated principal.

## Card 16
**Q:** What is AccessDecisionManager?
**A:** Manages access control decisions by consulting voter implementations.

## Card 17
**Q:** What is ExceptionTranslationFilter?
**A:** Translates security exceptions into appropriate HTTP responses.

## Card 18
**Q:** What is RememberMeAuthenticationFilter?
**A:** Processes remember-me authentication tokens from cookies.

## Card 19
**Q:** What is AnonymousAuthenticationFilter?
**A:** Provides anonymous authentication for unauthenticated users.

## Card 20
**Q:** What is FilterSecurityInterceptor?
**A:** The last filter in the chain that makes authorization decisions.
