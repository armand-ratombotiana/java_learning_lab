# Spring Bean Lifecycle Internals

## 🔬 BeanPostProcessors: The Secret Sauce
The `BeanPostProcessor` (BPP) interface is the most powerful extension point in the Spring Framework. BPPs operate on bean instances *after* they are created by the container.

### 1. `postProcessBeforeInitialization`
- Called after properties are set but *before* any custom init methods (like `@PostConstruct`).
- Spring uses this to handle `@Value`, `@Resource`, and `@Autowired` (via `AutowiredAnnotationBeanPostProcessor`).

### 2. `postProcessAfterInitialization`
- Called after the bean is fully initialized.
- **Proxy Creation**: This is where Spring's AOP (Aspect-Oriented Programming) magic happens. If a bean is annotated with `@Transactional`, `@Async`, or `@Cacheable`, the BPP wraps the original bean in a **Dynamic Proxy** (JDK or CGLIB).
- When you call a method on an autowired bean, you are often calling the Proxy, which handles the transaction/async logic before delegating to your actual bean.

## 🏗️ FactoryBean vs BeanFactory
- **`BeanFactory`**: The engine that manages beans.
- **`FactoryBean<T>`**: A special type of bean that acts as a factory for *other* beans. If a bean implements `FactoryBean`, Spring doesn't expose the factory itself as a bean, but rather the object returned by `factory.getObject()`. This is used heavily for complex third-party integrations (e.g., Hibernate `SessionFactory`).

## 🔄 Circular Dependencies
How does Spring handle two beans that require each other?
- **Constructor Injection**: Spring fails immediately with a `BeanCurrentlyInCreationException`. This is the recommended behavior as it forces better design.
- **Setter/Field Injection**: Spring uses a **Three-Level Cache** to resolve this. It creates a "partially initialized" bean, puts it in a cache, and allows the other bean to reference it before it's fully configured.