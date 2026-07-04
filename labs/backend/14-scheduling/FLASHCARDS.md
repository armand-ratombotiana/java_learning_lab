# Flashcards: 14 Scheduling

## Q: What is the main annotation for enabling this feature?
A: @Enable14Scheduling

## Q: How do you configure properties?
A: Via application.properties with a specific prefix.

## Q: What is the key interface for implementation?
A: The service interface that defines the contract.

## Q: How do you handle errors?
A: Through exception classes and @ControllerAdvice.

## Q: How do you test?
A: Using @SpringBootTest and Mockito.

## Q: How do you monitor?
A: Via Spring Boot Actuator endpoints.

## Q: What is the lifecycle?
A: Initialize -> Configure -> Active -> Destroy.

## Q: How does Spring Boot auto-configure it?
A: By detecting classpath dependencies and applying @Conditional checks.

## Q: Best practice for configuration?
A: Use @ConfigurationProperties for type-safe configuration.

## Q: Security consideration?
A: Validate inputs, authenticate requests, encrypt sensitive data.

