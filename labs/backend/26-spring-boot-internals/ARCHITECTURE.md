# Architecture

## Spring Boot Startup Architecture

```
SpringApplication
 ├── BootstrapContext (short-lived, before context)
 ├── ApplicationStartup (FlightRecorder / Micrometer)
 ├── SpringApplicationRunListeners
 │    ├── EventPublishingRunListener (spring.factories listeners)
 │    └── Custom RunListeners
 ├── Environment
 │    ├── PropertySources
 │    │    ├── commandLineArgs
 │    │    ├── servletConfigInitParams
 │    │    ├── servletContextInitParams
 │    │    ├── systemProperties
 │    │    ├── systemEnvironment
 │    │    ├── random (random.*)
 │    │    ├── application.properties (profile-specific)
 │    │    └── application-{profile}.properties
 │    └── ActiveProfiles
 └── ApplicationContext
      ├── BeanFactory (DefaultListableBeanFactory)
      ├── BeanDefinitionRegistry (maps names → BeanDefinition)
      ├── BeanPostProcessor chain
      └── BeanFactoryPostProcessor chain
```

## Auto-Configuration Metadata Architecture

```
AutoConfigurationImportSelector
  └─> AutoConfigurationImportSelector.getCandidateConfigurations()
       ├─> AutoConfigurationMetadataLoader.loadMetadata(classLoader)
       │    └─> Read AutoConfiguration.imports file
       └─> ConfigurationClassParser.processImports()
            └─> ConditionEvaluator.shouldSkip()
                 ├─> ConditionOutcome for each @Conditional
                 └─> Filtered by ConfigurationClassFilter
```

## Actuator Architecture

```
Actuator Endpoints
  ├─> @Endpoint (ReadOperation, WriteOperation, DeleteOperation)
  ├─> @WebEndpoint (web-specific extensions)
  ├─> @JmxEndpoint (JMX-specific)
  └─> HealthEndpoint
       ├─> HealthContributorRegistry
       └─> HealthEndpointGroups
            ├─> HealthEndpointGroup
            └─> HealthEndpoint

Metrics
  └─> MeterRegistry
       ├─> Counter
       ├─> Gauge
       ├─> Timer
       ├─> DistributionSummary
       └─> LongTaskTimer
```

## DispatcherServlet Architecture

```
DispatcherServlet (Front Controller)
  ├─> HandlerMapping chain (ordered)
  ├─> HandlerAdapter chain
  ├─> HandlerInterceptor chain
  ├─> ContentNegotiationManager
  │    └─> ContentNegotiationStrategy[] (Accept, Header, Parameter, Fixed)
  ├─> RequestMappingHandlerAdapter
  │    ├─> ArgumentResolver[]
  │    └─> ReturnValueHandler[]
  └─> HttpMessageConverter[]
       ├─> StringHttpMessageConverter
       ├─> MappingJackson2HttpMessageConverter
       ├─> MappingJackson2XmlHttpMessageConverter
       ├─> ByteArrayHttpMessageConverter
       └─> ResourceHttpMessageConverter
```

## Classloader Architecture in Fat JAR

```
LaunchedURLClassLoader
  ├─> BOOT-INF/classes/ (application classes)
  └─> BOOT-INF/lib/ (dependency JARs)
       ├─> spring-boot-autoconfigure-*.jar
       │    └─> META-INF/spring/
       │         └─> org.springframework.boot.autoconfigure.AutoConfiguration.imports
       ├─> spring-boot-starter-web-*.jar
       ├─> tomcat-embed-core-*.jar
       └─> jackson-databind-*.jar
```