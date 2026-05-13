# Spring Core Internals

## BeanFactory Hierarchy

### BeanFactory Interface Hierarchy

```
BeanFactory (root interface)
  │
  ├── ListableBeanFactory
  │     └── ApplicationContext (most common)
  │
  ├── HierarchicalBeanFactory
  │     └── ConfigurableBeanFactory
  │           └── DefaultSingletonBeanRegistry
  │
  └── AutowireCapableBeanFactory
        └── AbstractAutowireCapableBeanFactory
```

### Default BeanFactory Implementation

```
DefaultListableBeanFactory
  - Registry of bean definitions
  - Resolves bean dependencies
  - Creates bean instances
```

## Bean Definition Architecture

### BeanDefinition Interface

```java
public interface BeanDefinition extends AttributeAccessor, BeanMetadata {
    String getBeanClassName();
    ConstructorArgumentValues getConstructorArgumentValues();
    MutablePropertyValues getPropertyValues();
    String getScope();
    boolean isSingleton();
    boolean isPrototype();
    Class<?> resolveBeanClass(ClassLoader classLoader);
    // ... many more
}
```

### BeanDefinition Hierarchy

```
AbstractBeanDefinition
  ├── GenericBeanDefinition (XML, @Bean)
  ├── RootBeanDefinition (parent definitions)
  └── ChildBeanDefinition (bean inheritance)
```

### BeanDefinition parsing flow

```
@Configuration class
      ↓
@Bean method
      ↓
AnnotatedBeanDefinitionReader
      ↓
BeanDefinitionBuilder
      ↓
GenericBeanDefinition
      ↓
DefaultListableBeanFactory.registerBeanDefinition()
```

## Dependency Injection Mechanism

### Injection Points

```java
// Constructor injection
@Component
public class Service {
    private final Repository repository;
    
    public Service(Repository repository) {
        this.repository = repository;  // Constructor injection
    }
}

// Setter injection
@Component
public class Service {
    private Repository repository;
    
    @Autowired
    public void setRepository(Repository repository) {
        this.repository = repository;
    }
}

// Field injection (not recommended)
@Component
public class Service {
    @Autowired
    private Repository repository;
}
```

### Autowiring Algorithm

1. **byType**: Check for exactly one bean of the property type
2. **byName**: Match property name with bean name
3. **constructor**: Match constructor parameters by type

```java
// AutowireCapableBeanFactory.autowire()
public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
    if (autowireMode == AUTOWIRE_CONSTRUCTOR) {
        return autowireConstructor(beanClass, dependencyCheck);
    }
    // ...
}
```

## Bean Lifecycle

### Initialization Sequence

```
1. Instantiate bean (constructor)
       ↓
2. Populate bean properties (DI)
       ↓
3. BeanNameAware.setBeanName()
       ↓
4. BeanFactoryAware.setBeanFactory()
       ↓
5. BeanPostProcessor.postProcessBeforeInitialization()
       ↓
6. @PostConstruct method
       ↓
7. InitializingBean.afterPropertiesSet()
       ↓
8. @Bean(initMethod=)
       ↓
9. BeanPostProcessor.postProcessAfterInitialization()
       ↓
10. Bean ready for use
```

### Destruction Sequence

```
1. @PreDestroy method
       ↓
2. DisposableBean.destroy()
       ↓
3. @Bean(destroyMethod=)
       ↓
4. Bean destroyed
```

### BeanPostProcessor Implementation

```java
public interface BeanPostProcessor {
    @Nullable
    default Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }
    
    @Nullable
    default Object postProcessAfterInitialization(Object bean, String beanName) {
        return bean;
    }
}
```

Used for:
- AOP proxies (`AnnotationAwareAspectJAutoProxyCreator`)
- `@Required` annotation processing
- `@Autowired` injection (`AutowiredAnnotationBeanPostProcessor`)

## IoC Container Implementation

### ApplicationContext Implementation

```
DefaultListableBeanFactory
      ↑
      │ (BeanFactory delegation)
      │
AbstractApplicationContext
      ↑
      │
AnnotationConfigApplicationContext
      (most common for Spring Boot)
```

### Bean Creation: AbstractAutowireCapableBeanFactory

```java
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args)
        throws BeanCreationException {
    
    // 1. Resolve bean class
    Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
    
    // 2. Instantiate (using constructor or factory method)
    Object beanInstance = createBeanInstance(beanName, mbd, args);
    
    // 3. Populate bean (property injection)
    populateBean(beanName, mbd, instanceWrapper);
    
    // 4. Initialize
    beanInstance = initializeBean(beanName, beanInstance, mbd);
    
    return beanInstance;
}
```

### Constructor Resolution

```java
// Determining which constructor to use
private Constructor<?>[] determineConstructors(Class<?> beanClass, BeanDefinition bd) {
    // 1. Check @Autowired on constructor
    // 2. Check @Primary constructor
    // 3. Use default constructor
    // 4. Use longest constructor (autowire by type)
}
```

## Bean Scopes

### Scope Implementation

| Scope | Implementation |
|-------|----------------|
| singleton | Single instance per container |
| prototype | New instance per request |
| request | Per HTTP request (web context) |
| session | Per HTTP session |
| application | Per ServletContext |

```java
// Scope implementation in DefaultListableBeanFactory
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
private final Map<String, Object> prototypeObjects = new ConcurrentHashMap<>();

// For prototype, create new each time
if (mbd.isPrototype()) {
    Object beanInstance = createBean(beanName, mbd, args);
    return beanInstance;
}
```

### Request Scope Example

```java
// RequestScoped bean goes into RequestAttributes
public class RequestScopedBean implements Serializable {
    private static final long serialVersionUID = 1L;
}

// Stored in: RequestContextHolder.currentRequestAttributes()
```

## Circular Dependency Handling

### How Spring Handles It

```java
// AbstractBeanFactory.doGetBean()
if (isPrototypeCurrentlyInCreation(beanName)) {
    throw new BeanCurrentlyInCreationException(beanName);
}

// For singleton: allow circular references
// Creates early reference (ObjectFactory)
```

### Solution: Using ObjectFactory

```java
@Component
public class ServiceA {
    private final ObjectFactory<ServiceB> serviceB;
    
    public ServiceA(ObjectFactory<ServiceB> serviceB) {
        this.serviceB = serviceB;
    }
    
    public void doSomething() {
        ServiceB b = serviceB.getObject();  // Gets from container
    }
}
```

## AOP Proxy Mechanism

### JDK Dynamic Proxy vs CGLIB

```
JDK Dynamic Proxy:
┌─────────────────────────┐
│ Proxy (implements I)    │
│   → InvocationHandler   │
│       → Target          │
└─────────────────────────┘
- Requires interface
- Slower (reflection)

CGLIB Proxy:
┌─────────────────────────┐
│ Target subclass          │
│   (generated at runtime) │
└─────────────────────────┘
- No interface needed
- Faster (direct method calls)
- Cannot proxy final classes/methods
```

### Spring AOP Flow

```
@Aspect
public class LoggingAspect {
    @Before("execution(* com.example..*.save(..))")
    public void logBefore(JoinPoint jp) { ... }
}

      ↓
AnnotationAwareAspectJAutoProxyCreator (BeanPostProcessor)
      ↓
For each bean:
  - Scan for aspects
  - Create proxy wrapping original bean
  - Replace bean in registry with proxy
```

### Proxy Creation

```java
// DefaultAopProxyFactory.createAopProxy()
public AopProxy createAopProxy(AdvisedSupport config) {
    if (config.isProxyTargetClass() || 
        !hasNoInterfaces(config.getTargetClass())) {
        return new JdkDynamicAopProxy(config);
    }
    return new CglibAopProxy(config);
}
```

## BeanFactoryPostProcessor

### Order of Execution

```
1. BeanFactoryPostProcessor.postProcessBeanFactory()
      ↓
2. BeanDefinitionRegistryPostProcessor.registerBeanDefinitions()
      ↓
3. Bean instantiation
      ↓
4. BeanPostProcessor.postProcessBeforeInitialization()
```

### Property Placeholder Resolution

```java
// PropertySourcesPlaceholderConfigurer
@Value("${database.url}")
private String url;

// Resolves ${...} placeholders before bean creation
```

## Resource Loading

### Resource Interface

```java
public interface Resource extends InputStreamSource {
    boolean exists();
    boolean isOpen();
    URL getURL() throws IOException;
    File getFile() throws IOException;
    Resource createRelative(String relativePath);
    String getFilename();
    String getDescription();
}
```

### Resource Implementations

| Class | Description |
|-------|-------------|
| UrlResource | URL-based resources |
| ClassPathResource | Classpath resources |
| FileSystemResource | File system resources |
| ServletContextResource | Web application resources |
| InputStreamResource | Input stream wrapper |