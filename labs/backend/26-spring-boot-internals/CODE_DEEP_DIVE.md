# Code Deep Dive

## AutoConfigurationImportSelector Source

```java
// Spring Boot 3.x AutoConfigurationImportSelector
public class AutoConfigurationImportSelector
        implements DeferredImportSelector, BeanClassLoaderAware,
                   ResourceLoaderAware, BeanFactoryAware,
                   EnvironmentAware, Ordered {

    @Override
    public String[] selectImports(AnnotationMetadata annotationMetadata) {
        if (!isEnabled(annotationMetadata)) {
            return NO_IMPORTS;
        }
        AutoConfigurationMetadata autoConfigurationMetadata =
            AutoConfigurationMetadataLoader.loadMetadata(this.beanClassLoader);
        AutoConfigurationEntry autoConfigurationEntry =
            getAutoConfigurationEntry(autoConfigurationMetadata,
                                      annotationMetadata);
        return StringUtils.toStringArray(autoConfigurationEntry.getConfigurations());
    }

    protected AutoConfigurationEntry getAutoConfigurationEntry(
            AutoConfigurationMetadata autoConfigurationMetadata,
            AnnotationMetadata annotationMetadata) {

        AnnotationAttributes attributes = getAttributes(annotationMetadata);
        List<String> configurations = getCandidateConfigurations(annotationMetadata, attributes);
        // Remove duplicates
        configurations = removeDuplicates(configurations);
        // Remove excluded configurations
        Set<String> exclusions = getExclusions(annotationMetadata, attributes);
        configurations.removeAll(exclusions);
        // Filter by condition
        configurations = filter(configurations, autoConfigurationMetadata);
        // Fire event
        fireAutoConfigurationImportEvents(configurations, exclusions);
        return new AutoConfigurationEntry(configurations, exclusions);
    }

    protected List<String> getCandidateConfigurations(
            AnnotationMetadata metadata, AnnotationAttributes attributes) {
        List<String> configurations = SpringFactoriesLoader.loadFactoryNames(
            getSpringFactoriesLoaderFactoryClass(), getBeanClassLoader());
        Assert.notEmpty(configurations, "No auto-configuration classes found");
        return configurations;
    }
}
```

## SpringFactoriesLoader vs AutoConfigurationImportFilter

```java
// SpringFactoriesLoader (legacy)
List<String> factoryNames = SpringFactoriesLoader.loadFactoryNames(
    EnableAutoConfiguration.class, classLoader);

// Modern: AutoConfiguration.imports via AutoConfigurationImportFilter
// Loaded by AutoConfigurationImportSelector.metadataReaderFactory
```

## ConditionEvaluator.shouldSkip

```java
class ConditionEvaluator {
    public boolean shouldSkip(AnnotatedTypeMetadata metadata, ConfigurationPhase phase) {
        for (AnnotationAttributes attributes : getConditionAttributes(metadata)) {
            for (Condition condition : getConditions(attributes)) {
                ConfigurationPhase conditionPhase = getConditionPhase(condition);
                if (conditionPhase == null || conditionPhase == phase) {
                    if (!condition.matches(this.context, metadata)) {
                        return true; // Skip this configuration
                    }
                }
            }
        }
        return false;
    }
}
```

## DispatcherServlet.doDispatch()

```java
protected void doDispatch(HttpServletRequest request, HttpServletResponse response) {
    HttpServletRequest processedRequest = request;
    HandlerExecutionChain mappedHandler = null;
    boolean multipartRequestParsed = false;

    try {
        ModelAndView mv = null;
        Exception dispatchException = null;

        try {
            processedRequest = checkMultipart(request);
            multipartRequestParsed = (processedRequest != request);

            // 1. Determine handler
            mappedHandler = getHandler(processedRequest);

            // 2. Determine handler adapter
            HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());

            // 3. Apply preHandle interceptors
            if (!mappedHandler.applyPreHandle(processedRequest, response)) {
                return;
            }

            // 4. Actually invoke the handler
            mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

            // 5. Apply postHandle interceptors
            mappedHandler.applyPostHandle(processedRequest, response, mv);

        } catch (Exception ex) {
            dispatchException = ex;
        }

        // 6. Process dispatch result (view resolution, etc.)
        processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);

    } finally {
        // 7. Trigger afterCompletion
        if (mappedHandler != null) {
            mappedHandler.triggerAfterCompletion(request, response, ex);
        }
    }
}
```

## BeanFactoryPostProcessor Registration in Spring Boot

```java
// In AbstractApplicationContext.refresh()
protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
    // 1. Invoke BeanDefinitionRegistryPostProcessors first
    PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(
        beanFactory, getBeanFactoryPostProcessors());

    // 2. From BeanFactoryPostProcessor beans in the context
    String[] postProcessorNames = beanFactory.getBeanNamesForType(
        BeanFactoryPostProcessor.class, true, false);
    // Sort by PriorityOrdered, Ordered, then rest
    // Invoke ordered postProcessors...
}
```

## ApplicationContextInitializer Invocation

```java
// In SpringApplication.prepareContext()
private void prepareContext(DefaultBootstrapContext bootstrapContext,
                            ConfigurableApplicationContext context,
                            ConfigurableEnvironment environment,
                            SpringApplicationRunListeners listeners,
                            ApplicationArguments applicationArguments,
                            Banner printedBanner) {
    // Apply initializers after Environment is set
    applyInitializers(context);
    // Initializers call context.setEnvironment(), addPropertySources, etc.
}
```