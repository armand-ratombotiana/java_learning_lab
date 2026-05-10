package com.learning.lab.module14.solution;

import java.lang.reflect.Field;
import java.util.*;

public class Solution {

    // DI Container - Simple implementation
    public static class SimpleDIContainer {
        private final Map<Class<?>, Object> singletons = new HashMap<>();
        private final Map<Class<?>, Class<?>> bindings = new HashMap<>();

        public <T> void registerSingleton(Class<T> clazz, T instance) {
            singletons.put(clazz, instance);
        }

        public <T> void registerBinding(Class<T> interfaceClass, Class<? extends T> implementationClass) {
            bindings.put(interfaceClass, implementationClass);
        }

        @SuppressWarnings("unchecked")
        public <T> T getBean(Class<T> clazz) {
            if (singletons.containsKey(clazz)) {
                return (T) singletons.get(clazz);
            }
            if (bindings.containsKey(clazz)) {
                try {
                    Class<?> implClass = bindings.get(clazz);
                    T instance = (T) implClass.getDeclaredConstructor().newInstance();
                    injectDependencies(instance);
                    return instance;
                } catch (Exception e) {
                    throw new RuntimeException("Failed to create bean: " + clazz, e);
                }
            }
            throw new IllegalArgumentException("No binding found for: " + clazz);
        }

        private void injectDependencies(Object instance) throws Exception {
            Class<?> clazz = instance.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Autowired.class)) {
                    field.setAccessible(true);
                    Object dependency = getBean(field.getType());
                    field.set(instance, dependency);
                }
            }
        }
    }

    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Autowired {}

    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Component {
        String value() default "";
    }

    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Service {}

    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Repository {}

    // Bean Factory - Bean lifecycle management
    public static class BeanFactory {
        private final Map<String, Object> singletonBeans = new HashMap<>();
        private final Map<String, Class<?>> beanDefinitions = new HashMap<>();

        public void registerBeanDefinition(String name, Class<?> beanClass) {
            beanDefinitions.put(name, beanClass);
        }

        public Object getBean(String name) {
            if (!singletonBeans.containsKey(name)) {
                createBean(name);
            }
            return singletonBeans.get(name);
        }

        private void createBean(String name) {
            try {
                Class<?> beanClass = beanDefinitions.get(name);
                Object bean = beanClass.getDeclaredConstructor().newInstance();
                singletonBeans.put(name, bean);
            } catch (Exception e) {
                throw new RuntimeException("Failed to create bean: " + name, e);
            }
        }

        public boolean containsBean(String name) {
            return singletonBeans.containsKey(name) || beanDefinitions.containsKey(name);
        }
    }

    // AOP - Aspect-Oriented Programming
    public interface Aspect {
        void before(String method);
        void after(String method);
    }

    public static class LoggingAspect implements Aspect {
        @Override
        public void before(String method) {
            System.out.println("[LOG] Before executing: " + method);
        }

        @Override
        public void after(String method) {
            System.out.println("[LOG] After executing: " + method);
        }
    }

    public static class PerformanceAspect implements Aspect {
        @Override
        public void before(String method) {
            System.out.println("[PERF] Starting: " + method);
        }

        @Override
        public void after(String method) {
            System.out.println("[PERF] Completed: " + method);
        }
    }

    // AOP Proxy
    public static class AOPProxy {
        private final Object target;
        private final List<Aspect> aspects = new ArrayList<>();

        public AOPProxy(Object target) {
            this.target = target;
        }

        public void addAspect(Aspect aspect) {
            aspects.add(aspect);
        }

        public Object invoke(String methodName, Object... args) {
            for (Aspect aspect : aspects) {
                aspect.before(methodName);
            }

            try {
                return target.getClass()
                    .getMethod(methodName)
                    .invoke(target, args);
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                for (Aspect aspect : aspects) {
                    aspect.after(methodName);
                }
            }
        }
    }

    // IoC - Inversion of Control
    public static class IoCContainer {
        private final Map<Class<?>, Object> dependencies = new HashMap<>();

        public <T> void registerDependency(Class<T> clazz, T instance) {
            dependencies.put(clazz, instance);
        }

        public <T> T resolve(Class<T> clazz) {
            return (T) dependencies.get(clazz);
        }

        public void clear() {
            dependencies.clear();
        }
    }

    // Bean Scope types
    public enum BeanScope {
        SINGLETON,
        PROTOTYPE,
        REQUEST,
        SESSION
    }

    public static class ScopedBean {
        private final String name;
        private final BeanScope scope;

        public ScopedBean(String name, BeanScope scope) {
            this.name = name;
            this.scope = scope;
        }

        public String getName() { return name; }
        public BeanScope getScope() { return scope; }
    }

    // BeanPostProcessor
    public interface BeanPostProcessor {
        Object postProcessBeforeInitialization(Object bean, String beanName);
        Object postProcessAfterInitialization(Object bean, String beanName);
    }

    public static class ValidationBeanPostProcessor implements BeanPostProcessor {
        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) {
            System.out.println("Before initialization: " + beanName);
            return bean;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) {
            System.out.println("After initialization: " + beanName);
            return bean;
        }
    }

    // BeanFactoryPostProcessor
    public interface BeanFactoryPostProcessor {
        void postProcessBeanFactory(Map<String, Class<?>> beanDefinitions);
    }

    public static class PropertyPlaceholderBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
        @Override
        public void postProcessBeanFactory(Map<String, Class<?>> beanDefinitions) {
            System.out.println("Processing bean definitions: " + beanDefinitions.size());
        }
    }

    // ApplicationEvent and EventListener
    public static class ApplicationEvent {
        private final String source;
        private final long timestamp;

        public ApplicationEvent(String source) {
            this.source = source;
            this.timestamp = System.currentTimeMillis();
        }

        public String getSource() { return source; }
        public long getTimestamp() { return timestamp; }
    }

    public interface ApplicationListener {
        void handleEvent(ApplicationEvent event);
    }

    public static class EventPublisher {
        private final List<ApplicationListener> listeners = new ArrayList<>();

        public void addListener(ApplicationListener listener) {
            listeners.add(listener);
        }

        public void publishEvent(ApplicationEvent event) {
            for (ApplicationListener listener : listeners) {
                listener.handleEvent(event);
            }
        }
    }

    // PropertySource
    public static class PropertySource {
        private final Map<String, String> properties = new HashMap<>();

        public void setProperty(String key, String value) {
            properties.put(key, value);
        }

        public String getProperty(String key) {
            return properties.get(key);
        }

        public String getProperty(String key, String defaultValue) {
            return properties.getOrDefault(key, defaultValue);
        }
    }

    // Environment
    public static class Environment {
        private final PropertySource propertySource;
        private final List<String> activeProfiles = new ArrayList<>();

        public Environment(PropertySource propertySource) {
            this.propertySource = propertySource;
        }

        public void setActiveProfiles(String... profiles) {
            activeProfiles.addAll(Arrays.asList(profiles));
        }

        public String getProperty(String key) {
            return propertySource.getProperty(key);
        }

        public boolean containsProperty(String key) {
            return propertySource.getProperty(key) != null;
        }

        public String[] getActiveProfiles() {
            return activeProfiles.toArray(new String[0]);
        }
    }

    // ResourceLoader
    public interface Resource {
        String getFilename();
        boolean exists();
    }

    public static class FileResource implements Resource {
        private final String path;

        public FileResource(String path) {
            this.path = path;
        }

        @Override
        public String getFilename() {
            return path.substring(path.lastIndexOf('/') + 1);
        }

        @Override
        public boolean exists() {
            return path != null && !path.isEmpty();
        }
    }

    public static class ResourceLoader {
        public Resource getResource(String path) {
            return new FileResource(path);
        }
    }

    // SpEL - Spring Expression Language simplified
    public static class SpELParser {
        public String evaluate(String expression, Map<String, Object> variables) {
            String result = expression;
            for (Map.Entry<String, Object> entry : variables.entrySet()) {
                result = result.replace("${" + entry.getKey() + "}", String.valueOf(entry.getValue()));
            }
            return result;
        }
    }

    // Component Scan
    public static class ComponentScanner {
        private final Set<Class<?>> discoveredComponents = new HashSet<>();

        public void scan(String basePackage) {
            System.out.println("Scanning package: " + basePackage);
        }

        public Set<Class<?>> getDiscoveredComponents() {
            return discoveredComponents;
        }
    }

    // Qualifier
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Qualifier {
        String value() default "";
    }

    // Primary
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Primary {}

    // Order
    @Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    public @interface Order {
        int value() default 0;
    }

    public static class OrderedComparator implements java.util.Comparator<Class<?>> {
        @Override
        public int compare(Class<?> c1, Class<?> c2) {
            Order o1 = c1.getAnnotation(Order.class);
            Order o2 = c2.getAnnotation(Order.class);
            int order1 = o1 != null ? o1.value() : 0;
            int order2 = o2 != null ? o2.value() : 0;
            return Integer.compare(order1, order2);
        }
    }

    public static void demonstrateCoreConcepts() {
        System.out.println("=== DI Container ===");
        SimpleDIContainer container = new SimpleDIContainer();
        container.registerSingleton(String.class, "test");

        System.out.println("\n=== Bean Factory ===");
        BeanFactory factory = new BeanFactory();
        factory.registerBeanDefinition("bean", ScopedBean.class);

        System.out.println("\n=== AOP Proxy ===");
        LoggingAspect logging = new LoggingAspect();
        PerformanceAspect perf = new PerformanceAspect();
        AOPProxy aop = new AOPProxy(new ScopedBean("test", BeanScope.SINGLETON));
        aop.addAspect(logging);
        aop.addAspect(perf);

        System.out.println("\n=== IoC Container ===");
        IoCContainer ioc = new IoCContainer();
        ioc.registerDependency(String.class, "Hello");

        System.out.println("\n=== Event Publisher ===");
        EventPublisher publisher = new EventPublisher();
        publisher.addListener(event -> System.out.println("Event received: " + event.getSource()));
        publisher.publishEvent(new ApplicationEvent("test event"));

        System.out.println("\n=== Environment ===");
        PropertySource props = new PropertySource();
        props.setProperty("app.name", "MyApp");
        Environment env = new Environment(props);
        env.setActiveProfiles("dev");
        System.out.println("App name: " + env.getProperty("app.name"));

        System.out.println("\n=== SpEL Parser ===");
        SpELParser parser = new SpELParser();
        Map<String, Object> vars = Map.of("name", "World", "version", "1.0");
        String result = parser.evaluate("Hello ${name}, version ${version}", vars);
        System.out.println(result);

        System.out.println("\n=== Resource Loader ===");
        ResourceLoader loader = new ResourceLoader();
        Resource resource = loader.getResource("/config/app.properties");
        System.out.println("Resource: " + resource.getFilename());
    }

    public static void main(String[] args) {
        demonstrateCoreConcepts();
    }
}