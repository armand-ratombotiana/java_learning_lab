# Quiz: 10-spring-security-advanced

## Multiple Choice Questions

### Question 1
What is the default password encoder in Spring Security 5+?

- A) MD5PasswordEncoder
- B) BCryptPasswordEncoder
- C) NoOpPasswordEncoder
- D) SCryptPasswordEncoder

### Question 2
Which interface must be implemented to load user-specific data?

- A) UserService
- B) UserDetailsService
- C) UserRepository
- D) LoginService

### Question 3
What is the purpose of the SecurityContextHolder?

- A) To store application configuration
- B) To hold the current security context
- C) To manage HTTP sessions
- D) To encrypt passwords

### Question 4
Which session fixation protection strategy should be used?

- A) migrateSession()
- B) newSession()
- C) changeSession()
- D) none()

### Question 5
What does @EnableWebSecurity do?

- A) Enables web MVC
- B) Enables Spring Security web security support
- C) Enables database connectivity
- D) Enables CORS support

### Question 6
Which method validates a password against its hash?

- A) passwordEncoder.compare()
- B) passwordEncoder.matches()
- C) passwordEncoder.validate()
- D) passwordEncoder.verify()

### Question 7
How to permit all requests to a public URL pattern?

- A) .requestMatchers("/public/**").hasRole("USER")
- B) .requestMatchers("/public/**").permitAll()
- C) .requestMatchers("/public/**").anonymous()
- D) .requestMatchers("/public/**").denyAll()

### Question 8
Which filter processes form-based logins?

- A) BasicAuthenticationFilter
- B) UsernamePasswordAuthenticationFilter
- C) DigestAuthenticationFilter
- D) RememberMeAuthenticationFilter

## True or False

### Question 9
CSRF protection should always be disabled for REST APIs.

- A) True
- B) False

### Question 10
BCryptPasswordEncoder uses a random salt for each password.

- A) True
- B) False

## Short Answer

### Question 11
Explain the difference between authentication and authorization.

### Question 12
What is the DelegatingFilterProxy and why is it needed?

## Scoring Guide
- 0-5 correct: Needs review
- 6-10 correct: Good understanding
- 11-12 correct: Excellent - ready for advanced topics
