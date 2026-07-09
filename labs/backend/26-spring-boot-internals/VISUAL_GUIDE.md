# Visual Guide — Spring Boot Internals

## Auto-Configuration Loading Sequence

```
           +-------------------------------+
           |   META-INF/spring/            |
           |   AutoConfiguration.imports   |
           +-------------------------------+
                        |
                        v
           +-------------------------------+
           | AutoConfigurationImportSelector|
           +-------------------------------+
                        |
           +------------+------------+
           |                         |
           v                        v
  +------------------+    +-------------------+
  | ConditionEvaluator|    | Exclusion Filter  |
  +------------------+    +-------------------+
           |                        |
           v                        v
     +-----------+          +------------+
     | Matched    |          | Excluded    |
     | (keep)     |          | (remove)    |
     +-----------+          +------------+
           |
           v
  +--------------------------+
  | ConfigurationClassParser |
  | (process @Configuration)  |
  +--------------------------+
           |
           v
  +--------------------------+
  | Bean Definitions          |
  | (DefaultListableBeanFactory)|
  +--------------------------+
```

## BeanFactoryPostProcessor Chain Order

```
Lowest Order Runner First
         |
         v
   BeanDefinitionRegistryPostProcessors
   (PriorityOrdered)
         |
         v
   BeanDefinitionRegistryPostProcessors
   (Ordered)
         |
         v
   BeanDefinitionRegistryPostProcessors
   (remaining)
         |
         v
   BeanFactoryPostProcessors
   (PriorityOrdered)
         |
         v
   BeanFactoryPostProcessors
   (Ordered)
         |
         v
   BeanFactoryPostProcessors
   (remaining)
```

## DispatcherServlet Request Flow

```
  Client
    |
    v
  Servlet Container (Tomcat)
    |
    v
  DispatcherServlet.doDispatch()
    |
    +----> 1. checkMultipart()
    |
    +----> 2. getHandler() via HandlerMapping[]
    |         |
    |         v
    |       HandlerExecutionChain
    |       (handler + interceptors)
    |
    +----> 3. getHandlerAdapter(handler)
    |         |
    |         v
    |       HandlerAdapter
    |
    +----> 4. applyPreHandle(interceptors)
    |
    +----> 5. ha.handle(...)
    |         |
    |         +--> ArgumentResolver[]
    |         +--> Method invoke
    |         +--> ReturnValueHandler[]
    |
    +----> 6. applyPostHandle(interceptors)
    |
    +----> 7. processDispatchResult()
    |         |
    |         +--> View resolution
    |         +--> Content negotiation
    |         +--> MessageConverter.write()
    |
    +----> 8. triggerAfterCompletion(interceptors)
    |
    v
  HTTP Response
```

## Actuator Architecture

```
  /actuator
    |
    +-- /health --> HealthEndpoint --> HealthIndicatorRegistry
    |                                      |
    |    ┌── DiskSpaceHealthIndicator──────+
    |    ├── DataSourceHealthIndicator ────+
    |    ├── MongoHealthIndicator ─────────+
    |    └── CustomHealthIndicator ────────+
    |
    +-- /metrics --> MetricsEndpoint --> MeterRegistry
    |                                      |
    |    ┌── JvmMemoryMetrics──────────────+
    |    ├── TomcatMetrics ────────────────+
    |    ├── CustomMetrics ───────────────+
    |    └── JdbcMetrics ────────────────+
    |
    +-- /info
    +-- /env
    +-- /beans
    +-- /mappings
```

## Embedded Server Selection

```
SpringApplication.run()
    |
    v
WebApplicationType.deduceFromClasspath()
    |
    +-- SERVLET (contains jakarta.servlet.Servlet)
    |       |
    |       v
    |   TomcatServletWebServerFactory (default)
    |   JettyServletWebServerFactory (opt-in)
    |   UndertowServletWebServerFactory (opt-in)
    |
    +-- REACTIVE (contains DispatcherHandler)
    |       |
    |       v
    |   NettyWebServer (default)
    |
    +-- NONE
            |
            v
        No embedded server
```