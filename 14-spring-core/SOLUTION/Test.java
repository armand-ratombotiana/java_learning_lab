package com.learning.lab.module14.solution;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

public class Test {

    // DI Container Tests
    @Test
    void testDIContainerSingletonRegistration() {
        Solution.SimpleDIContainer container = new Solution.SimpleDIContainer();
        container.registerSingleton(String.class, "test");
        assertEquals("test", container.getBean(String.class));
    }

    @Test
    void testDIContainerReturnsSameInstance() {
        Solution.SimpleDIContainer container = new Solution.SimpleDIContainer();
        container.registerSingleton(String.class, "test");
        String bean1 = container.getBean(String.class);
        String bean2 = container.getBean(String.class);
        assertSame(bean1, bean2);
    }

    @Test
    void testDIContainerBinding() {
        Solution.SimpleDIContainer container = new Solution.SimpleDIContainer();
        container.registerBinding(Solution.BeanFactory.class, Solution.BeanFactory.class);
        assertNotNull(container.getBean(Solution.BeanFactory.class));
    }

    @Test
    void testDIContainerThrowsForUnregistered() {
        Solution.SimpleDIContainer container = new Solution.SimpleDIContainer();
        assertThrows(IllegalArgumentException.class, () -> container.getBean(String.class));
    }

    // Bean Factory Tests
    @Test
    void testBeanFactoryRegisterDefinition() {
        Solution.BeanFactory factory = new Solution.BeanFactory();
        factory.registerBeanDefinition("myBean", Solution.ScopedBean.class);
        assertTrue(factory.containsBean("myBean"));
    }

    @Test
    void testBeanFactoryGetBean() {
        Solution.BeanFactory factory = new Solution.BeanFactory();
        factory.registerBeanDefinition("bean", Solution.ScopedBean.class);
        Object bean = factory.getBean("bean");
        assertNotNull(bean);
    }

    @Test
    void testBeanFactoryContainsBean() {
        Solution.BeanFactory factory = new Solution.BeanFactory();
        assertFalse(factory.containsBean("unknown"));
    }

    // AOP Tests
    @Test
    void testAOPProxyCreation() {
        Solution.ScopedBean target = new Solution.ScopedBean("test", Solution.BeanScope.SINGLETON);
        Solution.AOPProxy proxy = new Solution.AOPProxy(target);
        assertNotNull(proxy);
    }

    @Test
    void testAOPProxyAddAspect() {
        Solution.AOPProxy proxy = new Solution.AOPProxy(new Solution.ScopedBean("test", Solution.BeanScope.SINGLETON));
        Solution.LoggingAspect aspect = new Solution.LoggingAspect();
        proxy.addAspect(aspect);
    }

    // IoC Container Tests
    @Test
    void testIoCRegisterDependency() {
        Solution.IoCContainer ioc = new Solution.IoCContainer();
        ioc.registerDependency(String.class, "Hello");
        assertEquals("Hello", ioc.resolve(String.class));
    }

    @Test
    void testIoCClear() {
        Solution.IoCContainer ioc = new Solution.IoCContainer();
        ioc.registerDependency(String.class, "Hello");
        ioc.clear();
        assertNull(ioc.resolve(String.class));
    }

    // Bean Scope Tests
    @Test
    void testBeanScopeSingleton() {
        Solution.ScopedBean bean = new Solution.ScopedBean("test", Solution.BeanScope.SINGLETON);
        assertEquals(Solution.BeanScope.SINGLETON, bean.getScope());
    }

    @Test
    void testBeanScopePrototype() {
        Solution.ScopedBean bean = new Solution.ScopedBean("test", Solution.BeanScope.PROTOTYPE);
        assertEquals(Solution.BeanScope.PROTOTYPE, bean.getScope());
    }

    @Test
    void testBeanScopeRequest() {
        Solution.ScopedBean bean = new Solution.ScopedBean("test", Solution.BeanScope.REQUEST);
        assertEquals(Solution.BeanScope.REQUEST, bean.getScope());
    }

    @Test
    void testBeanScopeSession() {
        Solution.ScopedBean bean = new Solution.ScopedBean("test", Solution.BeanScope.SESSION);
        assertEquals(Solution.BeanScope.SESSION, bean.getScope());
    }

    // BeanPostProcessor Tests
    @Test
    void testValidationBeanPostProcessor() {
        Solution.BeanPostProcessor processor = new Solution.ValidationBeanPostProcessor();
        Object result = processor.postProcessBeforeInitialization(new Object(), "testBean");
        assertNotNull(result);
    }

    @Test
    void testValidationBeanPostProcessorAfter() {
        Solution.BeanPostProcessor processor = new Solution.ValidationBeanPostProcessor();
        Object result = processor.postProcessAfterInitialization(new Object(), "testBean");
        assertNotNull(result);
    }

    // BeanFactoryPostProcessor Tests
    @Test
    void testPropertyPlaceholderProcessor() {
        Solution.BeanFactoryPostProcessor processor = new Solution.PropertyPlaceholderBeanFactoryPostProcessor();
        Map<String, Class<?>> definitions = new HashMap<>();
        processor.postProcessBeanFactory(definitions);
    }

    // Event Tests
    @Test
    void testApplicationEventCreation() {
        Solution.ApplicationEvent event = new Solution.ApplicationEvent("test");
        assertEquals("test", event.getSource());
    }

    @Test
    void testApplicationEventTimestamp() {
        Solution.ApplicationEvent event = new Solution.ApplicationEvent("test");
        assertTrue(event.getTimestamp() > 0);
    }

    @Test
    void testEventPublisherPublish() {
        Solution.EventPublisher publisher = new Solution.EventPublisher();
        publisher.addListener(e -> assertEquals("test", e.getSource()));
        publisher.publishEvent(new Solution.ApplicationEvent("test"));
    }

    // PropertySource Tests
    @Test
    void testPropertySourceSetAndGet() {
        Solution.PropertySource props = new Solution.PropertySource();
        props.setProperty("key", "value");
        assertEquals("value", props.getProperty("key"));
    }

    @Test
    void testPropertySourceDefaultValue() {
        Solution.PropertySource props = new Solution.PropertySource();
        assertEquals("default", props.getProperty("unknown", "default"));
    }

    @Test
    void testPropertySourceGetNullForUnknown() {
        Solution.PropertySource props = new Solution.PropertySource();
        assertNull(props.getProperty("unknown"));
    }

    // Environment Tests
    @Test
    void testEnvironmentGetProperty() {
        Solution.PropertySource props = new Solution.PropertySource();
        props.setProperty("test", "value");
        Solution.Environment env = new Solution.Environment(props);
        assertEquals("value", env.getProperty("test"));
    }

    @Test
    void testEnvironmentContainsProperty() {
        Solution.PropertySource props = new Solution.PropertySource();
        props.setProperty("test", "value");
        Solution.Environment env = new Solution.Environment(props);
        assertTrue(env.containsProperty("test"));
        assertFalse(env.containsProperty("unknown"));
    }

    @Test
    void testEnvironmentActiveProfiles() {
        Solution.PropertySource props = new Solution.PropertySource();
        Solution.Environment env = new Solution.Environment(props);
        env.setActiveProfiles("dev", "test");
        String[] profiles = env.getActiveProfiles();
        assertEquals(2, profiles.length);
    }

    // Resource Tests
    @Test
    void testFileResourceFilename() {
        Solution.Resource resource = new Solution.FileResource("/path/to/file.txt");
        assertEquals("file.txt", resource.getFilename());
    }

    @Test
    void testFileResourceExists() {
        Solution.Resource resource = new Solution.FileResource("/path/to/file.txt");
        assertTrue(resource.exists());
    }

    @Test
    void testFileResourceEmptyPath() {
        Solution.Resource resource = new Solution.FileResource("");
        assertFalse(resource.exists());
    }

    // ResourceLoader Tests
    @Test
    void testResourceLoaderGetResource() {
        Solution.ResourceLoader loader = new Solution.ResourceLoader();
        Solution.Resource resource = loader.getResource("/test.txt");
        assertNotNull(resource);
    }

    // SpEL Tests
    @Test
    void testSpELParserEvaluate() {
        Solution.SpELParser parser = new Solution.SpELParser();
        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "World");
        String result = parser.evaluate("Hello ${name}", vars);
        assertEquals("Hello World", result);
    }

    @Test
    void testSpELParserMultipleVariables() {
        Solution.SpELParser parser = new Solution.SpELParser();
        Map<String, Object> vars = Map.of("name", "John", "age", "30");
        String result = parser.evaluate("${name} is ${age}", vars);
        assertEquals("John is 30", result);
    }

    // Component Scanner Tests
    @Test
    void testComponentScannerCreation() {
        Solution.ComponentScanner scanner = new Solution.ComponentScanner();
        assertNotNull(scanner);
    }

    @Test
    void testComponentScannerScan() {
        Solution.ComponentScanner scanner = new Solution.ComponentScanner();
        scanner.scan("com.example");
    }

    @Test
    void testComponentScannerGetDiscovered() {
        Solution.ComponentScanner scanner = new Solution.ComponentScanner();
        assertNotNull(scanner.getDiscoveredComponents());
    }

    // OrderedComparator Tests
    @Test
    void testOrderedComparatorWithOrder() {
        @Solution.Order(1)
        class TestClass1 {}
        @Solution.Order(2)
        class TestClass2 {}

        Solution.OrderedComparator comparator = new Solution.OrderedComparator();
        int result = comparator.compare(TestClass1.class, TestClass2.class);
        assertTrue(result < 0);
    }

    @Test
    void testOrderedComparatorWithoutOrder() {
        class TestClass1 {}
        class TestClass2 {}

        Solution.OrderedComparator comparator = new Solution.OrderedComparator();
        int result = comparator.compare(TestClass1.class, TestClass2.class);
        assertEquals(0, result);
    }

    @Test
    void testOrderedComparatorSameOrder() {
        @Solution.Order(1)
        class TestClass1 {}
        @Solution.Order(1)
        class TestClass2 {}

        Solution.OrderedComparator comparator = new Solution.OrderedComparator();
        int result = comparator.compare(TestClass1.class, TestClass2.class);
        assertEquals(0, result);
    }

    // Integration Tests
    @Test
    void testContainerIntegration() {
        Solution.SimpleDIContainer container = new Solution.SimpleDIContainer();
        container.registerSingleton(String.class, "integration");
        container.registerBinding(Solution.BeanFactory.class, Solution.BeanFactory.class);

        assertEquals("integration", container.getBean(String.class));
        assertNotNull(container.getBean(Solution.BeanFactory.class));
    }

    @Test
    void testEnvironmentIntegration() {
        Solution.PropertySource props = new Solution.PropertySource();
        props.setProperty("db.url", "jdbc:mysql://localhost:3306/test");
        props.setProperty("db.username", "root");

        Solution.Environment env = new Solution.Environment(props);
        env.setActiveProfiles("prod");

        assertEquals("jdbc:mysql://localhost:3306/test", env.getProperty("db.url"));
        assertEquals("root", env.getProperty("db.username"));
    }
}