# Deep Dive — Spring IoC Container Internals

## BeanDefinition Merging

When Spring processes `@Configuration` classes, `ConfigurationClassParser` creates `BeanDefinition` objects. For classes with `@Bean` methods, each method produces a `BeanDefinition`.

### Parent-Child BeanDefinition Merging

When a `@Configuration` class extends another `@Configuration` class with `@Bean` methods:

```java
@Configuration
public class ParentConfig {
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create().build();
    }
}

@Configuration
public class ChildConfig extends ParentConfig {
    @Override
    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
            .url("jdbc:h2:mem:testdb")
            .build();
    }
}
```

Spring uses `RootBeanDefinition` merging:
1. Parent bean definitions loaded first via `ConfigurationClassParser.processConfigurationClass()`
2. Child `@Bean` methods override parent definitions (standard Java method override)
3. `MergedBeanDefinitionPostProcessor` post-processors merge property overrides
4. `@Primary` annotations on child beans ensure they take precedence

### BeanDefinition Merging Internals

`DefaultListableBeanFactory` uses `MergedBeanDefinitionPostProcessor` at bean creation:
```java
// AbstractAutowireCapableBeanFactory.applyMergedBeanDefinitionPostProcessors()
for (MergedBeanDefinitionPostProcessor processor : getBeanPostProcessors()) {
    processor.postProcessMergedBeanDefinition(mergedBeanDef, beanType, beanName);
}
```

## Singleton vs Prototype Lifecycle

### Singleton Scope (Default)

```java
@Bean
@Scope("singleton")  // Default, can be omitted
public MyService myService() {
    return new MyService();
}
```

- One instance per Spring `ApplicationContext`
- Created eagerly by default (during `finishBeanFactoryInitialization()`)
- Lifecycle callbacks: `@PostConstruct`, `@PreDestroy`, `InitializingBean`, `DisposableBean`
- Thread-safe considerations: all threads share the same instance

### Prototype Scope

```java
@Bean
@Scope("prototype")
public MyPrototypeService prototypeService() {
    return new MyPrototypeService();
}
```

- New instance created on every `applicationContext.getBean()` or `@Inject`
- Created lazily on demand
- Lifecycle: `@PostConstruct` and `InitializingBean` are invoked, but `@PreDestroy` and `DisposableBean` are NOT
- Spring does not track prototype instances — garbage collection reclaims them

### Lazy Singleton

```java
@Component
@Lazy
public class LazyService { ... }
```

- Bean definition registered eagerly, but instance created on first request
- Uses CGLIB proxy if `@Lazy` with `proxyMode` is specified
- Internally: `LazyResolutionProxyFactoryBean` creates a JDK dynamic proxy or CGLIB proxy

## Bean Scopes

| Scope | Description | Created By | Proxy Mode |
|-------|-------------|------------|------------|
| `singleton` | One instance per context | Context refresh | No proxy needed |
| `prototype` | New instance per injection | Each getBean() call | No proxy needed |
| `request` | One instance per HTTP request | HTTP request | `TARGET_CLASS` or `INTERFACES` |
| `session` | One instance per HTTP session | HTTP session | `TARGET_CLASS` or `INTERFACES` |
| `application` | One instance per `ServletContext` | Servlet context | `TARGET_CLASS` or `INTERFACES` |
| `websocket` | One instance per WebSocket session | WebSocket session | `TARGET_CLASS` or `INTERFACES` |
| Custom | User-defined | User-defined `Scope` implementation | Configurable |

### Request Scope Example

```java
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestScopedBean { ... }
```

### Proxy Mode Resolution

When `proxyMode = ScopedProxyMode.TARGET_CLASS`:
1. Spring's `ScopedProxyFactoryBean` creates a CGLIB proxy
2. Proxy intercepts all method calls
3. Proxy resolves the real target from the appropriate scope container (e.g., `HttpSession` for session scope)
4. `SimpleTargetSource` or `ThreadPoolTargetSource` provides target resolution strategy

Internally:
```java
// ScopedProxyUtils.createScopedProxy()
ScopedProxyFactoryBean factory = new ScopedProxyFactoryBean();
factory.setTargetBeanName(beanName);
factory.setBeanFactory(beanFactory);
factory.setProxyTargetClass(proxyMode == ScopedProxyMode.TARGET_CLASS);
```

## Circular Dependencies

### Detection

Spring detects circular dependencies during `DefaultSingletonBeanRegistry.getSingleton()`:
- Uses `singletonsCurrentlyInCreation` set to track beans being created
- If a bean is already in the set, Spring detects the circular reference

```java
// AbstractAutowireCapableBeanFactory.doCreateBean()
if (isSingletonCurrentlyInCreation(beanName)) {
    throw new BeanCurrentlyInCreationException(beanName);
}
```

### Resolution Strategies

**1. Setter Injection (Recommended):**
Spring can resolve circular dependencies with setter injection because it can create the bean, then inject dependencies later:

```java
@Component
public class A {
    private B b;
    @Autowired
    public void setB(B b) { this.b = b; }
}

@Component
public class B {
    private A a;
    @Autowired
    public void setA(A a) { this.a = a; }
}
```

**2. Constructor Injection (Not Possible for Circular):**
Constructor injection creates beans before injection, so Spring immediately detects the deadlock and throws `BeanCurrentlyInCreationException`.

**3. @Lazy Proxy:**
```java
@Component
public class A {
    private final B b;
    public A(@Lazy B b) { this.b = b; }
}
```
Creates a CGLIB proxy for `B` that is resolved when first accessed.

**4. ApplicationContextAware:**
Retrieve beans lazily from the context after initialization:
```java
@Component
public class A implements ApplicationContextAware {
    private B b;
    @Override
    public void setApplicationContext(ApplicationContext ctx) {
        this.b = ctx.getBean(B.class);
    }
}
```

## @Lazy Proxy Internals

### How @Lazy Works with Constructor Injection

When `@Lazy` is used on a constructor parameter:
1. `AutowiredAnnotationBeanPostProcessor` sees `@Lazy` on the parameter
2. Instead of resolving the dependency immediately, creates a `LazyResolutionProxyFactory`
3. The proxy is a JDK dynamic proxy (if interface exists) or CGLIB proxy
4. On first method invocation, the proxy resolves the real bean
5. Resolution uses `DefaultSingletonBeanRegistry.getSingleton(beanName, false)` — returns null if singleton not yet created, which triggers early creation

### Lazy Annotation Processing

```java
// InjectionMetadata.InjectedElement.resolveDependency()
DependencyDescriptor descriptor = new DependencyDescriptor(methodParameter, true);
if (field.isAnnotationPresent(Lazy.class)) {
    descriptor.setLazy(true);
}
Object value = beanFactory.resolveDependency(descriptor, beanName, null, null);
```

When `descriptor.isLazy()` returns true:
- `DefaultListableBeanFactory.resolveDependency()` returns a proxy instead of the real bean
- `beans.factory.support.LazyResolutionProxyFactory` constructs the proxy
- The proxy intercepts all method calls and delegates to the resolved bean

## Bean Definition Metadata

Each bean definition in `DefaultListableBeanFactory` contains:
- `beanClassName` — fully qualified class name
- `scope` — singleton/prototype/request/session
- `abstract` — flag for template definitions
- `lazyInit` — should bean initialize lazily
- `autowireMode` — no/byName/byType/constructor
- `dependencyCheck` — none/simple/objects/all
- `autowireCandidate` — eligible for injection
- `primary` — primary candidate for injection
- `initMethodName` — custom init method
- `destroyMethodName` — custom destroy method
- `factoryBeanName` — if created by a factory bean
- `factoryMethodName` — if created by @Bean method

### BeanDefinition Merging Algorithm

```java
// AbstractBeanDefinition.mergeAttributes(AbstractBeanDefinition parent)
// Merging order of precedence:
// 1. Child definition values (highest priority)
// 2. Parent definition values (fallback)
// 3. Default values from DefaultListableBeanFactory

merged.setScope(firstNonNull(child.getScope(), parent.getScope()));
merged.setLazyInit(firstNonNull(child.getLazyInit(), parent.getLazyInit()));
merged.setAutowireCandidate(firstNonNull(child.getAutowireCandidate(), parent.getAutowireCandidate()));
```

## BeanPostProcessor Lifecycle

The `BeanPostProcessor` chain runs during `AbstractAutowireCapableBeanFactory.createBean()`:

1. `postProcessBeforeInitialization()` — before `@PostConstruct` / `InitializingBean.afterPropertiesSet()`
2. `@PostConstruct` — initialization callback
3. `InitializingBean.afterPropertiesSet()` — initialization callback
4. `init-method` — custom init method from `@Bean(initMethod="...")`
5. `postProcessAfterInitialization()` — after all initialization

### SmartInstantiationAwareBeanPostProcessor

Handles:
- `predictBeanType()` — predict bean type before creation
- `determineCandidateConstructors()` — determine which constructor to use
- `getEarlyBeanReference()` — expose early reference for circular dependency resolution

## FactoryBean Pattern

```java
@Component
public class MyFactoryBean implements FactoryBean<MyService> {
    @Override
    public MyService getObject() { return new MyService(); }

    @Override
    public Class<?> getObjectType() { return MyService.class; }

    @Override
    public boolean isSingleton() { return true; }
}
```

- Spring recognizes `FactoryBean` beans and creates the result of `getObject()` as the bean
- The factory bean is obtained via `getBean("&myFactory")` prefix
- Used internally for `ScopedProxyFactoryBean`, `ProxyFactoryBean`, `SqlSessionFactoryBean`

## AbstractAutowireCapableBeanFactory.createBean() Internals

```java
protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    RootBeanDefinition mbdToUse = mbd;
    // 1. Resolve bean class
    Class<?> resolvedClass = resolveBeanClass(mbd, beanName);

    // 2. Prepare method overrides (lookup-method and replace-method)
    mbdToUse.prepareMethodOverrides();

    // 3. Give BeanPostProcessors a chance to return a proxy
    Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
    if (bean != null) return bean;

    // 4. Create the bean
    return doCreateBean(beanName, mbdToUse, args);
}

protected Object doCreateBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
    // 1. Instantiate bean
    BeanWrapper instanceWrapper = createBeanInstance(beanName, mbd, args);

    // 2. Allow early references for circular dependency resolution
    addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));

    // 3. Populate bean (inject dependencies)
    populateBean(beanName, mbd, instanceWrapper);

    // 4. Apply BeanPostProcessors and init callbacks
    exposedObject = initializeBean(beanName, exposedObject, mbd);

    // 5. Register disposable bean
    registerDisposableBeanIfNecessary(beanName, bean, mbd);
}
```

## DefaultSingletonBeanRegistry Singleton Cache

```java
// Three-level singleton cache structure
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);       // Level 1: fully initialized singletons
private final Map<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(16); // Level 2: early references
private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);    // Level 3: early-exposed singletons

// For circular dependency resolution:
// Level 2 (singletonFactories) provides early bean references via ObjectFactory
// Bean identified as circular: moved to Level 3 (earlySingletonObjects)
// After full initialization: moved to Level 1 (singletonObjects)
```

## Spring Bean Lifecycle Summary

```
Bean Definition Loaded
  └─> Merge with parent definition (if applicable)
       └─> Bean factory post processors (modify definitions)
            └─> Instantiate bean (constructor resolved)
                 └─> Dependencies injected
                      ├─> BeanNameAware.setBeanName()
                      ├─> BeanClassLoaderAware.setBeanClassLoader()
                      ├─> BeanFactoryAware.setBeanFactory()
                      ├─> EnvironmentAware.setEnvironment()
                      ├─> EmbeddedValueResolverAware.setEmbeddedValueResolver()
                      ├─> ApplicationContextAware.setApplicationContext()
                      └─> BeanPostProcessor.postProcessBeforeInitialization()
                           └─> @PostConstruct / InitializingBean.afterPropertiesSet() / @Bean initMethod
                                └─> BeanPostProcessor.postProcessAfterInitialization()
                                     └─> Bean is ready for use
                                          └─> @PreDestroy / DisposableBean.destroy() / @Bean destroyMethod
                                               └─> Bean destroyed
```