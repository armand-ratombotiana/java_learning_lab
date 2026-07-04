# Exercises

## Exercise 1: Create a Spring Boot Application
Create a Spring Boot app with:
- A `/greeting` endpoint that accepts a `name` query parameter
- Custom banner
- Actuator health endpoint
- Run on port 9090

## Exercise 2: Externalized Configuration
Create a `@ConfigurationProperties` class for mail configuration (host, port, username, password).
Read from `application.yml` with profile-specific overrides.

## Exercise 3: Custom Starter
Build a simple "greeting" starter that auto-configures a `GreetingService` when a `greeting.message` property is set. Package it as a separate Maven module.

## Exercise 4: Conditionals
Write auto-configuration that:
- Creates bean A if `feature.x.enabled=true`
- Creates bean B ONLY if bean A exists and class `SomeClass` is on classpath

## Exercise 5: Actuator Customization
Add a custom actuator endpoint that returns application build info (version, timestamp, build number).
