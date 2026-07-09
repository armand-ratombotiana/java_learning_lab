# Flashcards — Spring Boot Internals

## Front: What annotation triggers auto-configuration?
**Back:** `@EnableAutoConfiguration` (included in `@SpringBootApplication`)

## Front: Where are auto-configuration classes listed in Spring Boot 3.x?
**Back:** `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Front: What interface does AutoConfigurationImportSelector implement to defer processing?
**Back:** `DeferredImportSelector`

## Front: Which annotation prevents config when bean already exists?
**Back:** `@ConditionalOnMissingBean`

## Front: Which annotation checks a property value?
**Back:** `@ConditionalOnProperty`

## Front: What's the difference between BeanFactoryPostProcessor and BeanPostProcessor?
**Back:** BFPP runs before beans created, modifies definitions. BPP runs during bean creation, modifies objects.

## Front: How does EnvironmentPostProcessor get registered?
**Back:** Via `META-INF/spring.factories` key `org.springframework.boot.env.EnvironmentPostProcessor`

## Front: What does WebApplicationType.deduceFromClasspath() check?
**Back:** REACTIVE → `DispatcherServlet` in WebFlux, SERVLET → `jakarta.servlet.Servlet`, otherwise NONE

## Front: Name 3 embedded servers in Spring Boot.
**Back:** Tomcat (default), Jetty, Undertow

## Front: What is the central method in DispatcherServlet?
**Back:** `doDispatch()`

## Front: What HandlerMapping handles @RequestMapping methods?
**Back:** `RequestMappingHandlerMapping`

## Front: What returns 400 for health?
**Back:** UP status

## Front: What Actuator annotation creates HTTP + JMX endpoint?
**Back:** `@Endpoint`

## Front: How does Micrometer MeterRegistry work?
**Back:** Base `MeterRegistry` abstraction for counters, timers, gauges

## Front: What is the default BeanFactory implementation?
**Back:** `DefaultListableBeanFactory`

## Front: What runs before BeanFactoryPostProcessor?
**Back:** `BeanDefinitionRegistryPostProcessor`