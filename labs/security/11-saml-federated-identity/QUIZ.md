# QUIZ: 11-saml-federated-identity

## Knowledge Assessment

### Section 1: Fundamentals (5 questions)

**Q1**: What is the primary security mechanism described in this lab?
- A) Encryption algorithm
- B) Authentication protocol
- C) Access control pattern
- D) Security testing methodology

**Q2**: Which Java security API is most relevant to this lab's topic?
- A) javax.crypto
- B) java.security
- C) org.springframework.security
- D) java.net.http

**Q3**: What is the most common vulnerability related to this topic?
- A) Injection attacks
- B) Broken authentication
- C) Security misconfiguration
- D) Sensitive data exposure

**Q4**: Which design pattern is commonly used in this security context?
- A) Singleton
- B) Chain of Responsibility
- C) Factory
- D) Observer

**Q5**: What is the recommended approach for handling failures in this domain?
- A) Fail open
- B) Fail secure
- C) Fail fast
- D) Fail silently

### Section 2: Implementation (4 questions)

**Q6**: Which method is used to validate security tokens in the implementation?
- A) validate()
- B) verify()
- C) check()
- D) authenticate()

**Q7**: What is the correct order of operations in the security flow?
- A) Authenticate ? Authorize ? Audit
- B) Authorize ? Authenticate ? Audit
- C) Audit ? Authenticate ? Authorize
- D) Authenticate ? Audit ? Authorize

**Q8**: Which exception should be thrown for unauthorized access?
- A) IllegalArgumentException
- B) AccessDeniedException
- C) SecurityException
- D) AuthenticationException

**Q9**: What configuration property controls the timeout setting?
- A) security.timeout
- B) app.security.timeout
- C) security.session.timeout
- D) timeout.seconds

### Section 3: Best Practices (3 questions)

**Q10**: Which of the following is NOT a security best practice for this topic?
- A) Validate all inputs
- B) Log security events
- C) Store secrets in code
- D) Use HTTPS

**Q11**: What is the recommended minimum key size for the cryptographic operations used?
- A) 128 bits
- B) 256 bits
- C) 512 bits
- D) 1024 bits

**Q12**: Which tool is best suited for testing this security implementation?
- A) JUnit
- B) OWASP ZAP
- C) Postman
- D) All of the above

### Answer Key
Q1=B, Q2=C, Q3=B, Q4=B, Q5=B, Q6=B, Q7=A, Q8=B, Q9=C, Q10=C, Q11=B, Q12=D

### Section 4: Scenario-Based Questions

**Q13**: A user reports being logged out frequently. What is the likely cause?
- A) Session timeout too short
- B) Concurrent session limit reached
- C) Token refresh failure
- D) All of the above

**Q14**: During a penetration test, the tester bypasses authentication. What is the most likely vulnerability?
- A) Weak password policy
- B) Missing authentication check on endpoint
- C) CSRF token not validated
- D) CORS misconfiguration

**Q15**: An API returns detailed error messages. What is the security risk?
- A) Information disclosure
- B) Denial of service
- C) Man-in-the-middle
- D) Session hijacking

**Q16**: Performance degrades under load. Which security component is most likely the bottleneck?
- A) Password hashing
- B) TLS handshake
- C) Database queries
- D) Audit logging

### Section 5: Practical Application

**Q17**: You need to implement token refresh. What is the correct approach?
- A) Issue long-lived tokens
- B) Use refresh tokens with short-lived access tokens
- C) Re-authenticate for each request
- D) Store tokens in database

**Q18**: How should you store session tokens on the client side?
- A) localStorage
- B) sessionStorage
- C) HttpOnly secure cookie
- D) URL parameter

**Q19**: What is the correct way to handle password reset?
- A) Email the new password
- B) Send a time-limited reset link
- C) Have support reset it
- D) Allow user to set new password without verification

### Section 6: Advanced Topics

**Q20**: What is the purpose of a Content Security Policy (CSP)?
- A) Prevent SQL injection
- B) Mitigate XSS attacks
- C) Control CORS
- D) Manage sessions

**Q21**: When should you use symmetric vs asymmetric encryption?
- A) Symmetric for storage, asymmetric for transit
- B) Asymmetric for storage, symmetric for transit
- C) Always asymmetric
- D) Always symmetric

**Q22**: What is the main advantage of JWTs over opaque tokens?
- A) Better security
- B) Self-contained validation
- C) Smaller size
- D) Easier to revoke

### Answer Key (continued)
Q13=D, Q14=B, Q15=A, Q16=A, Q17=B, Q18=C, Q19=B, Q20=B, Q21=A, Q22=B
