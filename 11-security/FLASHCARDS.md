# Security Flashcards

## Card 1: @EnableWebSecurity
**Question:** What does @EnableWebSecurity do?

**Answer:**
Enables Spring Security web security support. Configures SecurityFilterChain and default security settings.

---

## Card 2: Authentication vs Authorization
**Question:** What is the difference between authentication and authorization?

**Answer:**
- Authentication: Verifies WHO you are (login)
- Authorization: Verifies WHAT you can do (permissions)

---

## Card 3: JWT Structure
**Question:** What are the three parts of a JWT?

**Answer:**
Header (base64) . Payload (base64) . Signature (base64)

---

## Card 4: UserDetailsService
**Question:** What does UserDetailsService do?

**Answer:**
Loads user-specific data during authentication. Implement to integrate with custom user stores.

---

## Card 5: PasswordEncoder
**Question:** What is PasswordEncoder used for?

**Answer:**
Hashes passwords for storage. Use BCrypt with appropriate strength (10-12).

---

## Card 6: JWT Filter
**Question:** How does JWT authentication work?

**Answer:**
- Extract token from request header
- Validate token
- Set authentication in SecurityContext

---

## Card 7: OAuth2 Flow
**Question:** What is Authorization Code flow?

**Answer:**
User → Login → Authorization Code → Client → Token → Access Resource

---

## Card 8: @PreAuthorize
**Question:** What does @PreAuthorize do?

**Answer:**
Checks authorization before method execution. Use for method-level security.

---

## Card 9: CSRF Token
**Question:** What is CSRF token used for?

**Answer:**
Prevents Cross-Site Request Forgery attacks. Include in requests for state-changing operations.

---

## Card 10: CORS
**Question:** What is CORS and how to handle it?

**Answer:**
Cross-Origin Resource Sharing. Configure via CorsConfiguration or Spring Boot properties.

---

## Card 11: hasRole vs hasAuthority
**Question:** What's the difference between hasRole and hasAuthority?

**Answer:**
hasRole automatically adds "ROLE_" prefix. hasAuthority uses exact authority name.

---

## Card 12: SecurityFilterChain
**Question:** What does SecurityFilterChain define?

**Answer:**
Defines which requests require authentication, authentication mechanism, and authorization rules.

---

## Card 13: JWT Claims
**Question:** What common JWT claims exist?

**Answer:**
- sub: Subject (user ID)
- iat: Issued at
- exp: Expiration
- roles: User roles

---

## Card 14: BCrypt
**Question:** What is BCrypt?

**Answer:**
Password hashing algorithm with built-in salt and configurable work factor. Recommended for password storage.

---

## Card 15: OAuth2 Roles
**Question:** What are the OAuth2 roles?

**Answer:**
- Resource Owner: User
- Client: Application
- Authorization Server
- Resource Server

---

## Card 16: JWT Storage
**Question:** Where should JWT be stored in browser?

**Answer:**
HttpOnly, Secure cookie. Avoid LocalStorage (vulnerable to XSS).

---

## Card 17: @Secured
**Question:** How does @Secured work?

**Answer:**
Restricts access by specifying roles. Only users with listed roles can execute method.

---

## Card 18: Stateless Authentication
**Question:** What is stateless authentication?

**Answer:**
No session on server. Each request includes token. JWT enables this.

---

## Card 19: Filter Chain Order
**Question:** Why is filter chain order important?

**Answer:**
Filters execute in order. Authentication filters must run before authorization.

---

## Card 20: Method Security
**Question:** What annotations enable method security?

**Answer:**
- @PreAuthorize - expression-based
- @Secured - role-based
- @RolesAllowed - JSR-250