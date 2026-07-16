# Spring Bean Lifecycle Theory & Intuition

## 💡 What is a Bean?
In Spring, a **Bean** is simply an object that is managed by the Spring IoC (Inversion of Control) Container. Instead of you creating the object with `new MyService()`, Spring creates it, configures it, and wires it together with its dependencies.

## 🔄 The Lifecycle Phases
A Spring Bean goes through a very specific set of steps. Understanding these is crucial for debugging complex dependency injection issues.

### 1. Instantiation
Spring finds the bean definition (via `@Component`, `@Bean`, or XML) and creates the raw Java object using its constructor.

### 2. Populate Properties (Dependency Injection)
Spring looks at the `@Autowired` fields or setter methods and injects the required dependencies.

### 3. Aware Interfaces
Spring checks if the bean implements any "Aware" interfaces. This allows the bean to "know" about its environment.
- `BeanNameAware`: Gives the bean its own name.
- `ApplicationContextAware`: Gives the bean access to the entire Spring context.

### 4. Bean Post-Processor (Before Initialization)
Spring calls the `postProcessBeforeInitialization` method on all registered `BeanPostProcessors`. This is where Spring handles annotations like `@Value`.

### 5. Initialization
Spring calls the initialization callbacks:
1. Method annotated with `@PostConstruct`.
2. `afterPropertiesSet()` method (if `InitializingBean` is implemented).
3. Custom `init-method` defined in configuration.

### 6. Bean Post-Processor (After Initialization)
Spring calls the `postProcessAfterInitialization` method. **This is the most important step for AOP**. This is where Spring wraps your bean in a **Proxy** if you use features like `@Transactional` or `@Async`.

### 7. Ready for Use
The bean is now fully configured and exists in the Application Context, ready to be injected into other beans.

### 8. Destruction
When the context is closed (e.g., the app stops):
1. Method annotated with `@PreDestroy`.
2. `destroy()` method (if `DisposableBean` is implemented).