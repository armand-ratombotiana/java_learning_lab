# Exercises — Spring Boot Internals

## Exercise 1: Custom Auto-Configuration

Create a custom auto-configuration for a fictional `GreetingService` that activates only when `greeting.enabled=true`.

1. Create `GreetingService` interface with method `String greet(String name)`
2. Create `GreetingProperties` class with `@ConfigurationProperties("greeting")`
3. Create `AutoConfiguration` class with `@ConditionalOnProperty`
4. Register in `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Exercise 2: BeanFactoryPostProcessor

Write a `BeanFactoryPostProcessor` that:
1. Logs all bean definition names
2. Modifies the scope of beans named `prototypeBean` to `singleton`

## Exercise 3: Custom FailureAnalyzer

Create a `FailureAnalyzer` for a custom exception `DatabaseTimeoutException`:
1. Message: "Database connection timed out after 30 seconds"
2. Action: "Check network connectivity and database server status"

## Exercise 4: Actuator Custom Endpoint

Create a `@Endpoint` called `app-info` with:
- `@ReadOperation` returning app name, version, and uptime
- `@WriteOperation` updating a configuration value

## Exercise 5: Custom Started

Create a custom starter `myapp-starter` with:
- `MyAppAutoConfiguration`
- `MyAppProperties`
- `MyAppService` bean created conditionally
- Documentation in `README` about the starter

## Exercise 6: DispatcherServlet Tracing

Write a trace that:
1. Logs all available `HandlerMapping` beans
2. Logs all `HandlerAdapter` beans
3. For a given request, logs which handler was selected

## Exercise 7: WebApplication Type Switch

Create an application that runs in both SERVLET and REACTIVE mode depending on classpath.

## Exercise 8: Content Negotiation

Configure content negotiation:
1. JSON when `Accept: application/json`
2. XML when `Accept: application/xml`
3. Fallback to JSON