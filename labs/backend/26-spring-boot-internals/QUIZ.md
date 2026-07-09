# Quiz — Spring Boot Internals

## Question 1
What interface does `AutoConfigurationImportSelector` implement that makes it process after user `@Configuration` classes?

> `DeferredImportSelector`

## Question 2
Where are auto-configuration class names listed in Spring Boot 2.7+?

> `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

## Question 3
Which annotation prevents an auto-configuration class from loading if another bean of the same type already exists?

> `@ConditionalOnMissingBean`

## Question 4
What is the difference between `BeanFactoryPostProcessor` and `BeanPostProcessor`?

> `BeanFactoryPostProcessor` runs after bean definitions are loaded but before any beans are created. `BeanPostProcessor` runs during bean creation.

## Question 5
Which interface allows modifying the Spring `Environment` before beans are created?

> `EnvironmentPostProcessor`

## Question 6
What determines whether Spring Boot starts as SERVLET, REACTIVE, or NONE?

> `WebApplicationType.deduceFromClasspath()` — checks for `DispatcherServlet` (REACTIVE) or `jakarta.servlet.Servlet` (SERVLET)

## Question 7
Which method in `DispatcherServlet` is the central request handling method?

> `doDispatch()`

## Question 8
What is the default embedded server for Spring Boot MVC?

> Tomcat

## Question 9
Which Actuator annotation creates both JMX and HTTP endpoints?

> `@Endpoint`

## Question 10
How do you exclude auto-configuration classes from Spring Boot?

> `@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)` or `spring.autoconfigure.exclude=DataSourceAutoConfiguration`

## Question 11
What is the purpose of `ContentNegotiationManager`?

> Determines the response media type based on `Accept` header, URL parameter, or fixed configuration

## Question 12
Which class handles the `@ResponseBody` return value?

> `ViewNameMethodReturnValueHandler` if model + view name, but `RequestResponseBodyMethodProcessor` handles `@ResponseBody`