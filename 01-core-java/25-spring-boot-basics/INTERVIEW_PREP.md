# Module 25: Spring Boot Basics - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is Dependency Injection (DI) and Inversion of Control (IoC) in Spring?
**Answer**:
- **Inversion of Control (IoC)** is a design principle where the control of object creation and lifecycle management is transferred from the programmer to a container (the Spring IoC Container).
- **Dependency Injection (DI)** is a specific implementation of IoC. Instead of a class instantiating its own dependencies (e.g., `UserService userService = new UserService()`), the Spring framework creates the dependency and "injects" it into the class via its constructor, a setter, or field injection. This makes the code loosely coupled and highly testable (since dependencies can easily be swapped with mocks).

### Q2: What exactly does `@SpringBootApplication` do?
**Answer**:
It is a convenience annotation that encapsulates three core Spring annotations:
1. **`@Configuration`**: Tags the class as a source of bean definitions for the application context.
2. **`@EnableAutoConfiguration`**: Tells Spring Boot to start adding beans based on classpath settings, other beans, and various property settings (e.g., if `spring-webmvc` is on the classpath, it configures a Tomcat server and Spring MVC).
3. **`@ComponentScan`**: Tells Spring to look for other components, configurations, and services in the *current package and its sub-packages*, allowing it to find and register your `@Controller` and `@Service` classes automatically.

### Q3: Constructor Injection vs Field Injection (`@Autowired`)?
**Answer**:
Field injection (using `@Autowired` directly on a private field) is heavily discouraged in modern Spring development because:
1. It prevents the class from being instantiated without the Spring container (making plain unit tests difficult).
2. It allows for circular dependencies to go unnoticed until runtime.
3. It violates the immutability principle because the field cannot be declared `final`.

**Constructor Injection** is the best practice. It ensures the class cannot be instantiated without its required dependencies, allows fields to be `final`, and makes unit testing simple by passing mocks directly into the constructor via the `new` keyword. (Note: Since Spring 4.3, if a class has only one constructor, the `@Autowired` annotation can be omitted).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Resolving Bean Conflicts
**Problem**: You have an interface `PaymentService` and two implementations: `CreditCardPaymentService` and `PayPalPaymentService`, both annotated with `@Service`. In your controller, you try to inject `PaymentService`. The application crashes on startup with a `NoUniqueBeanDefinitionException`. How do you fix this?

**Solution**:
Spring doesn't know which implementation to inject. You can resolve this in several ways:
1. **`@Primary`**: Annotate one of the implementations (e.g., `CreditCardPaymentService`) with `@Primary`. Spring will use this one by default when multiple beans qualify.
2. **`@Qualifier`**: At the injection point (the constructor), use `@Qualifier("payPalPaymentService")` to explicitly state which bean ID you want to inject.
3. **Property Injection**: If you want to decide at runtime, you can inject a `Map<String, PaymentService>` or `List<PaymentService>` and use a factory or switch statement based on application properties.

### Scenario 2: Overriding Auto-Configuration
**Problem**: Spring Boot auto-configures a default `DataSource` because you have H2 in your classpath. However, for a specific test, you want to manually configure a custom `DataSource` and stop Spring Boot from creating the default one.

**Solution**:
You can exclude specific auto-configuration classes on the main application class.
```java
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class MyApplication { ... }
```
Then, you manually define your own `@Bean` for the `DataSource` in a `@Configuration` class.