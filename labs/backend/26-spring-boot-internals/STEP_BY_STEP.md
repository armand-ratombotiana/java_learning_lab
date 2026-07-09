# Step by Step — Spring Boot Internals

## Step 1: SpringApplication.run()

1. `StopWatch` starts timing the application
2. `DefaultBootstrapContext` is created (short-lived context for early startup)
3. `SpringApplicationRunListeners` loaded from `META-INF/spring.factories`:
   - `EventPublishingRunListener` publishes events to `ApplicationListener`s
4. `configureHeadlessProperty()` sets `java.awt.headless = true`
5. `getRunListeners()` discovers all `SpringApplicationRunListener` implementations via `SpringFactoriesLoader`

## Step 2: Environment Preparation

1. `prepareEnvironment()` creates `ConfigurableEnvironment`
2. Determines `WebApplicationType` (SERVLET, REACTIVE, NONE)
3. Loads property sources in order of priority
4. `EnvironmentPostProcessor`s modify environment
5. Active profiles determined from `spring.profiles.active`
6. Binders configure `SpringApplication` from environment properties

## Step 3: Application Context Creation

1. `createApplicationContext()` instantiates the right context type
2. Based on `WebApplicationType`:
   - SERVLET → `AnnotationConfigServletWebServerApplicationContext`
   - REACTIVE → `AnnotationConfigReactiveWebServerApplicationContext`
   - NONE → `AnnotationConfigApplicationContext`
3. Context is set with `ApplicationStartup` for metrics recording

## Step 4: Context Preparation

1. `prepareContext()`:
   - Sets `Environment` on context
   - Applies `ApplicationContextInitializer`s (post-process context)
   - Registers `ApplicationArguments` bean
   - Registers `Banner` printer
   - Publishes `ApplicationContextInitializedEvent`

## Step 5: Context Refresh (Core Logic)

1. `refreshContext()` calls `AbstractApplicationContext.refresh()`:
   - `prepareRefresh()` — set startup date, activate flags
   - `obtainFreshBeanFactory()` — parse `@Configuration` classes
   - **`invokeBeanFactoryPostProcessors()`** — processes `BeanFactoryPostProcessor` beans
     - First processes `BeanDefinitionRegistryPostProcessor` beans
     - Then processes all remaining `BeanFactoryPostProcessor` beans
     - This is where `AutoConfigurationImportSelector` runs
   - `registerBeanPostProcessors()` — registers `BeanPostProcessor` beans
   - `initMessageSource()` — sets up `MessageSource` for i18n
   - `initApplicationEventMulticaster()` — sets up event multicasting
   - `onRefresh()` — template method, used by web contexts to create web server
   - `registerListeners()` — registers all `ApplicationListener` beans
   - `finishBeanFactoryInitialization()` — creates all singleton beans
   - `finishRefresh()` — publishes `ContextRefreshedEvent`

## Step 6: Auto-Configuration Execution During Refresh

During `invokeBeanFactoryPostProcessors()`:
1. `ConfigurationClassPostProcessor` (a `BeanDefinitionRegistryPostProcessor`) processes `@Configuration` classes
2. Processes `@PropertySources`, `@ComponentScan`, `@Import` (including `AutoConfigurationImportSelector`)
3. `AutoConfigurationImportSelector.selectImports()` runs:
   - Loads auto-configuration class names from `AutoConfiguration.imports`
   - Applies `@Conditional` filtering
   - Applies exclusion list
   - Returns valid configuration class names
4. Each returned class becomes a new `@Configuration` class processed recursively

## Step 7: Web Server Startup

1. During `onRefresh()`, `ServletWebServerApplicationContext` creates the embedded container
2. `TomcatServletWebServerFactory` (or Jetty, Undertow) builds the server
3. `DispatcherServlet` is created and registered as a servlet
4. Server starts on configured port

## Step 8: Post-Refresh

1. `afterRefresh()` — runs callbacks
2. `callRunners()` invokes `ApplicationRunner` and `CommandLineRunner` beans
3. Publishes `ApplicationStartedEvent`
4. Publishes `ApplicationReadyEvent`
5. `Application` returns

## Request Processing

When a request arrives:

1. Servlet container receives request
2. `DispatcherServlet.service()` (via `doGet`, `doPost`)
3. `doDispatch()`:
   a. `checkMultipart()` — handle multipart requests
   b. `getHandler()` — iterate `HandlerMapping`s
   c. `getHandlerAdapter()` — find matching adapter
   d. Apply `preHandle` interceptors
   e. Invoke handler method with argument resolution
   f. Handle return value
   g. Apply `postHandle` interceptors
   h. `processDispatchResult()` — resolve view or write response
   i. `triggerAfterCompletion()` — cleanup interceptors