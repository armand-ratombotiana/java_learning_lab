# Security Quiz

## Section 1: Spring Security Basics

**Question 1:** What annotation enables Spring Security?

A) @EnableSecurity
B) @EnableWebSecurity
C) @SecurityEnabled
D) @SpringSecurity

**Answer:** B) @EnableWebSecurity

---

**Question 2:** What is the default authentication mechanism in Spring Security?

A) Basic Auth
B) Form Login
C) OAuth2
D) JWT

**Answer:** B) Form Login

---

**Question 3:** Which interface is used to load user details?

A) UserLoader
B) UserDetailsLoader
C) UserDetailsService
D) AuthenticationService

**Answer:** C) UserDetailsService

---

**Question 4:** What does the SecurityFilterChain manage?

A) Database security
B) Request filtering based on security rules
C) Password encoding
D) Session management

**Answer:** B) Request filtering based on security rules

---

**Question 5:** What is the purpose of PasswordEncoder?

A) Encrypt passwords for storage
B) Hash passwords for security
C) Validate password strength
D) Generate random passwords

**Answer:** B) Hash passwords for security

---

## Section 2: JWT

**Question 6:** What are the three parts of a JWT?

A) Header, Payload, Signature
B) Body, Footer, Certificate
C) Token, Secret, Algorithm
D) Subject, Object, Action

**Answer:** A) Header, Payload, Signature

---

**Question 7:** What algorithm is commonly used for JWT signing?

A) MD5
B) SHA-256
C) HS256
D) RSA-512

**Answer:** C) HS256

---

**Question 8:** Which claim indicates JWT expiration?

A) iat
B) sub
C) exp
D) aud

**Answer:** C) exp

---

**Question 9:** What is the purpose of the "sub" claim in JWT?

A) Subject (user identifier)
B) Secret key
C) Signature
D) Timestamp

**Answer:** A) Subject (user identifier)

---

**Question 10:** How should JWT be stored on client side?

A) LocalStorage
B) Cookie with HttpOnly
C) SessionStorage
D) URL parameter

**Answer:** B) Cookie with HttpOnly (for security)

---

## Section 3: OAuth2

**Question 11:** What is the recommended OAuth2 flow for web applications?

A) Implicit Flow
B) Authorization Code Flow
C) Resource Owner Password Credentials
D) Client Credentials

**Answer:** B) Authorization Code Flow

---

**Question 12:** What does the Authorization Server do?

A) Hosts protected resources
B) Authenticates users and issues tokens
C) Makes API requests
D) Stores user data

**Answer:** B) Authenticates users and issues tokens

---

**Question 13:** What is the purpose of the client_id in OAuth2?

A) Identifies the user
B) Identifies the client application
C) Encrypts tokens
D) Stores secrets

**Answer:** B) Identifies the client application

---

**Question 14:** Which Spring Security module enables OAuth2 login?

A) spring-security-oauth
B) spring-security-oauth2-client
C) spring-security-oauth2
D) spring-boot-starter-oauth

**Answer:** B) spring-security-oauth2-client

---

**Question 15:** What is PKCE used for in OAuth2?

A) Password encryption
B) Proof Key for Code Exchange
C) Session management
D) Token refresh

**Answer:** B) Proof Key for Code Exchange

---

## Section 4: Authorization

**Question 16:** What does @PreAuthorize do?

A) Authenticates before request
B) Checks authorization before method execution
C) Logs authorization attempts
D) Caches authorization decisions

**Answer:** B) Checks authorization before method execution

---

**Question 17:** Which annotation checks roles?

A) @PreAuthorize
B) @Secured
C) @RolesAllowed
D) All of the above

**Answer:** D) All of the above

---

**Question 18:** What is the difference between hasRole and hasAuthority?

A) hasRole adds "ROLE_" prefix automatically
B) hasAuthority is more granular
C) They are identical
D) hasAuthority is deprecated

**Answer:** A) hasRole adds "ROLE_" prefix automatically

---

**Question 19:** What does method-level security provide?

A) URL-level authorization only
B) Authorization at method level
C) No authorization
D) Database-level security

**Answer:** B) Authorization at method level

---

**Question 20:** Which is more secure - role-based or permission-based?

A) Role-based
B) Permission-based (more granular)
C) They are the same
D) Neither is secure

**Answer:** B) Permission-based (more granular)

---

## Section 5: CORS and CSRF

**Question 21:** What does CORS protect against?

A) SQL Injection
B) Cross-Origin Resource Sharing issues
C) Cross-site scripting
D) Session hijacking

**Answer:** B) Cross-Origin Resource Sharing issues

---

**Question 22:** What does CSRF protect against?

A) XSS attacks
B) Cross-Site Request Forgery
C) SQL Injection
D) Man-in-the-middle

**Answer:** B) Cross-Site Request Forgery

---

**Question 23:** How is CSRF token typically stored?

A) URL parameter
B) Request header
C) Hidden form field or cookie
D) Local storage

**Answer:** C) Hidden form field or cookie

---

**Question 24:** Which header controls CORS?

A) X-Frame-Options
B) Access-Control-Allow-Origin
C) X-Content-Type-Options
D) Authorization

**Answer:** B) Access-Control-Allow-Origin

---

**Question 25:** What happens if CORS is misconfigured?

A) Better security
B) May allow unauthorized cross-origin access
C) Improved performance
D) No effect

**Answer:** B) May allow unauthorized cross-origin access

---

## Section 6: Password Security

**Question 26:** What is BCrypt?

A) Encryption algorithm
B) Password hashing function
C) Network protocol
D) Authentication framework

**Answer:** B) Password hashing function

---

**Question 27:** What makes a password hash secure?

A) It's reversible
B) It's slow and uses salt
C) It's short
D) It's encrypted

**Answer:** B) It's slow and uses salt

---

**Question 28:** What is the purpose of a salt in password hashing?

A) Speeds up hashing
B) Makes each hash unique even for same password
C) Reduces storage space
D) Adds to password length

**Answer:** B) Makes each hash unique even for same password

---

**Question 29:** Which is the safest password storage method?

A) Plain text
B) MD5
C) SHA-256
D) BCrypt or Argon2

**Answer:** D) BCrypt or Argon2

---

**Question 30:** What is the recommended work factor for BCrypt?

A) 1-5
B) 10-15
C) 20-30
D) 50+

**Answer:** B) 10-15

---

## Answer Key

| Question | Answer |
|----------|--------|
| 1 | B |
| 2 | B |
| 3 | C |
| 4 | B |
| 5 | B |
| 6 | A |
| 7 | C |
| 8 | C |
| 9 | A |
| 10 | B |
| 11 | B |
| 12 | B |
| 13 | B |
| 14 | B |
| 15 | B |
| 16 | B |
| 17 | D |
| 18 | A |
| 19 | B |
| 20 | B |
| 21 | B |
| 22 | B |
| 23 | C |
| 24 | B |
| 25 | B |
| 26 | B |
| 27 | B |
| 28 | B |
| 29 | D |
| 30 | B |