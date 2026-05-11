# Spring DI Lifecycle Diagram

Visual representation of Spring Dependency Injection lifecycle.

## Bean Lifecycle Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    SPRING BEAN LIFECYCLE                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │                     CONTAINER STARTUP                    │   │
│   └─────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              ▼                                  │
│   ┌───────────────┐  ┌────────────────┐  ┌──────────────────┐   │
│   │ Instantiate  │→ │ Populate Beans │→ │ BeanNameAware     │   │
│   │ Bean         │  │ (DI)           │  │ (setBeanName)     │   │
│   └───────────────┘  └────────────────┘  └──────────────────┘   │
│                                                  │               │
│                              ┌───────────────────┘               │
│                              ▼                                  │
│   ┌────────────────────────────────────────────────────────┐   │
│   │                  INITIALIZINGBEAN                       │   │
│   │  ┌──────────────┐ ┌──────────────┐ ┌────────────────┐  │   │
│   │  │BeanFactory  │→│ApplicationCon │→│@PostConstruct  │  │   │
│   │  │Aware         │→│textAware     │→│(recommended)   │  │   │
│   │  └──────────────┘ └──────────────┘ └────────────────┘  │   │
│   └────────────────────────────────────────────────────────┘   │
│                              │                                  │
│                              ▼                                  │
│   ┌────────────────────────────────────────────────────────┐    │
│   │                    READY TO USE                         │    │
│   │  ┌──────────────────────────────────────────────────┐  │    │
│   │  │  Bean is fully initialized and ready for use      │  │    │
│   │  └──────────────────────────────────────────────────┘  │    │
│   └────────────────────────────────────────────────────────┘    │
│                              │                                  │
│                              ▼                                  │
│   ┌────────────────────────────────────────────────────────┐    │
│   │                CONTAINER SHUTDOWN                       │    │
│   │  ┌──────────────┐ ┌──────────────┐ ┌────────────────┐  │    │
│   │  │@PreDestroy   │→│DisposableBean│→│ Custom destroy  │  │    │
│   │  │(recommended) │→│ destroy()    │→│ methods        │  │    │
│   │  └──────────────┘ └──────────────┘ └────────────────┘  │    │
│   └────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Dependency Injection Types

```
┌─────────────────────────────────────────────────────────────────┐
│                   DEPENDENCY INJECTION TYPES                     │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  1. CONSTRUCTOR INJECTION (Recommended)                         │
│     ────────────────────────────────────────                     │
│     ┌─────────────────────────────────────────────┐              │
│     │  @Component                                   │              │
│     │  public class Service {                      │              │
│     │      private final Repository repo;          │              │
│     │                                               │              │
│     │      @Autowired                               │              │
│     │      public Service(Repository repo) {      │              │
│     │          this.repo = repo;                   │              │
│     │      }                                       │              │
│     │  }                                           │              │
│     └─────────────────────────────────────────────┘              │
│     ✅ Required dependencies, immutable objects                  │
│                                                                  │
│  2. SETTER INJECTION (Optional)                                 │
│     ──────────────────────────────                              │
│     ┌─────────────────────────────────────────────┐              │
│     │  @Component                                   │              │
│     │  public class Service {                      │              │
│     │      private Repository repo;                │              │
│     │                                               │              │
│     │      @Autowired                               │              │
│     │      public void setRepo(Repository repo) {  │              │
│     │          this.repo = repo;                   │              │
│     │      }                                       │              │
│     │  }                                           │              │
│     └─────────────────────────────────────────────┘              │
│     ⚠️ Optional dependencies, can be changed                      │
│                                                                  │
│  3. FIELD INJECTION (Not Recommended)                           │
│     ────────────────────────────────────────                     │
│     ┌─────────────────────────────────────────────┐              │
│     │  @Component                                   │              │
│     │  public class Service {                      │              │
│     │      @Autowired                              │              │
│     │      private Repository repo;                │              │
│     │  }                                           │              │
│     └─────────────────────────────────────────────┘              │
│     ❌ Hard to test, violates encapsulation                      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Component Scanning

```
┌─────────────────────────────────────────────────────────────────┐
│                   COMPONENT SCANNING                             │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   Default: @ComponentScan (scans same package)                 │
│                                                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │  @Configuration                                          │   │
│   │  @ComponentScan(basePackages = "com.example")            │   │
│   │  public class AppConfig { }                              │   │
│   └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│   Annotations & Stereotypes:                                     │
│   ┌────────────────────────────────────────────────────────┐    │
│   │  @Repository   │  Data access layer (persistence)       │    │
│   │  @Service      │  Business logic layer                  │    │
│   │  @Controller   │  Web layer (MVC)                      │    │
│   │  @Configuration│  Configuration classes                │    │
│   │  @RestController│ Web REST endpoints                   │    │
│   │  @Component    │  Generic component (catch-all)        │    │
│   └────────────────────────────────────────────────────────┘    │
│                                                                  │
│   Filters:                                                       │
│   ┌────────────────────────────────────────────────────────┐    │
│   │  @ComponentScan(                                         │    │
│   │    includeFilters = @Filter(type = ANNOTATION,          │    │
│   │                             pattern = @MyAnnotation),  │    │
│   │    excludeFilters = @Filter(type = REGEX,              │    │
│   │                             pattern = ".*Test")         │    │
│   │  )                                                       │    │
│   └────────────────────────────────────────────────────────┘    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Bean Scopes

```
┌─────────────────────────────────────────────────────────────────┐
│                       BEAN SCOPES                               │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  SCOPE        │  DESCRIPTION        │  DEFAULT FOR        │   │
│  ├───────────────┼─────────────────────┼────────────────────┤   │
│  │  singleton    │  One per container  │  @Component         │   │
│  │  prototype    │  New instance each  │  -                  │   │
│  │  request      │  One per HTTP req   │  @Controller        │   │
│  │  session      │  One per HTTP sess  │  @Controller        │   │
│  │  application  │  One per servlet ctx│  @Controller        │   │
│  │  websocket    │  One per websocket  │  @Controller        │   │
│  └───────────────┴─────────────────────┴────────────────────┘   │
│                                                                  │
│  ⚠️ Prototype beans: container doesn't manage destruction      │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Aware Interfaces

```
┌─────────────────────────────────────────────────────────────────┐
│                    AWARENESS INTERFACES                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │  Interface              │  Injected Method               │   │
│   ├─────────────────────────┼─────────────────────────────────┤   │
│   │  BeanNameAware          │  setBeanName(String)            │   │
│   │  BeanFactoryAware       │  setBeanFactory(BeanFactory)    │   │
│   │  ApplicationContextAware│  setApplicationContext(AppCtx)  │   │
│   │  ResourceLoaderAware    │  setResourceLoader(ResourceLdr) │   │
│   │  MessageSourceAware     │  setMessageSource(MessageSrc)   │   │
│   │  ApplicationEventPub... │  setApplicationEventPub(... )   │   │
│   └─────────────────────────┴─────────────────────────────────┘   │
│                                                                  │
│   Alternative: Implement EnvironmentAware, BeanClassLoaderAware │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

## Init/Destroy Best Practices

```
┌─────────────────────────────────────────────────────────────────┐
│              INITIALIZATION/DESTRUCTION                         │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│   PREFERRED ORDER (Spring 2.5+):                                │
│   ─────────────────────────────────                             │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │                                                         │   │
│   │  1. @PostConstruct / @PreDestroy (JSR-250)              │   │
│   │     ✅ Standard, clear intent, framework agnostic      │   │
│   │                                                         │   │
│   └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│   ALTERNATIVES:                                                  │
│   ─────────────                                                  │
│   ┌─────────────────────────────────────────────────────────┐   │
│   │  • InitializingBean.afterPropertiesSet()               │   │
│   │  • DisposableBean.destroy()                             │   │
│   │  • @Bean(initMethod="method", destroyMethod="method")   │   │
│   └─────────────────────────────────────────────────────────┘   │
│                                                                  │
│   ⚠️ Mixing styles is confusing - pick one and be consistent    │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```
