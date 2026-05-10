# Advanced Java Topics Projects - Module 15

This module covers advanced Java concepts including reflection, annotations, dynamic proxies, classloaders, bytecode manipulation, and advanced JVM topics.

## Mini-Project: Custom Annotations and Reflection (2-4 hours)

### Overview
Build a custom annotation framework with reflection-based processing for runtime validation and dependency injection.

### Project Structure
```
advanced-java-demo/
├── pom.xml
├── src/main/java/com/learning/advanced/
│   ├── AdvancedJavaApplication.java
│   ├── annotation/
│   │   ├── Valid.java
│   │   ├── Validate.java
│   │   ├── Inject.java
│   │   └── PerformanceLog.java
│   ├── processor/
│   │   ├── ValidationProcessor.java
│   │   ├── InjectionProcessor.java
│   │   └── PerformanceProcessor.java
│   ├── model/
│   │   └── User.java
│   └── service/
│       └── UserService.java
└── src/main/java/com/learning/framework/
    └── CustomContainer.java
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>advanced-java-demo</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
        <dependency>
            <groupId>jakarta.annotation</groupId>
            <artifactId>jakarta.annotation-api</artifactId>
            <version>2.1.1</version>
        </dependency>
    </dependencies>
</project>
```

#### Custom Annotations
```java
package com.learning.advanced.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Valid {
    String message() default "Validation failed";
    
    Class<?>[] groups() default {};
    
    Class<? extends jakarta.validation.Payload>[] payload() default {};
}

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validate {
    String value();
    
    boolean required() default true;
    
    int minLength() default 0;
    
    int maxLength() default Integer.MAX_VALUE;
    
    String pattern() default "";
}

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inject {
    String name() default "";
    
    Class<?> implementation() default Object.class;
}

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceLog {
    String value() default "";
    
    long threshold() default 100;
    
    boolean logArguments() default false;
    
    boolean logResult() default false;
}
```

#### Validation Processor
```java
package com.learning.advanced.processor;

import com.learning.advanced.annotation.*;
import java.lang.reflect.*;
import java.util.*;

public class ValidationProcessor {
    
    public void validate(Object instance) throws ValidationException {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(Validate.class)) {
                Validate validate = field.getAnnotation(Validate.class);
                Object value = getFieldValue(field, instance);
                
                validateField(field.getName(), value, validate);
            }
            
            if (field.isAnnotationPresent(Valid.class)) {
                Valid valid = field.getAnnotation(Valid.class);
                Object value = getFieldValue(field, instance);
                
                if (value == null) {
                    throw new ValidationException(field.getName() + " cannot be null");
                }
                
                if (value instanceof String str) {
                    if (str.isEmpty() && valid.message().isEmpty()) {
                        throw new ValidationException(field.getName() + " cannot be empty");
                    }
                }
            }
        }
    }
    
    private void validateField(String fieldName, Object value, Validate validate) 
            throws ValidationException {
        
        if (validate.required() && (value == null || (value instanceof String s && s.isEmpty()))) {
            throw new ValidationException(fieldName + " is required");
        }
        
        if (value instanceof String str) {
            if (validate.minLength() > 0 && str.length() < validate.minLength()) {
                throw new ValidationException(fieldName + " must be at least " + 
                    validate.minLength() + " characters");
            }
            
            if (validate.maxLength() < Integer.MAX_VALUE && str.length() > validate.maxLength()) {
                throw new ValidationException(fieldName + " must be at most " + 
                    validate.maxLength() + " characters");
            }
            
            if (!validate.pattern().isEmpty()) {
                if (!str.matches(validate.pattern())) {
                    throw new ValidationException(fieldName + " does not match required pattern");
                }
            }
        }
    }
    
    private Object getFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access field: " + field.getName(), e);
        }
    }
    
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
```

#### Injection Processor
```java
package com.learning.advanced.processor;

import com.learning.advanced.annotation.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class InjectionProcessor {
    
    private final Map<Class<?>, Object> singletons = new ConcurrentHashMap<>();
    private final Map<Class<?>, Class<?>> implementations = new ConcurrentHashMap<>();
    
    public void registerImplementation(Class<?> interfaceClass, Class<?> implementationClass) {
        implementations.put(interfaceClass, implementationClass);
    }
    
    public void injectDependencies(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(Inject.class)) {
                Inject inject = field.getAnnotation(Inject.class);
                
                Class<?> targetType = field.getType();
                Object dependency = resolveDependency(targetType, inject);
                
                setFieldValue(field, instance, dependency);
            }
        }
        
        processMethods(instance);
    }
    
    private void processMethods(Object instance) {
        Class<?> clazz = instance.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        
        for (Method method : methods) {
            if (method.isAnnotationPresent(Inject.class)) {
                method.setAccessible(true);
                
                try {
                    Object[] args = Arrays.stream(method.getParameters())
                        .map(param -> resolveDependency(param.getType(), null))
                        .toArray();
                    method.invoke(instance, args);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to inject: " + method.getName(), e);
                }
            }
        }
    }
    
    private Object resolveDependency(Class<?> type, Inject inject) {
        if (singletons.containsKey(type)) {
            return singletons.get(type);
        }
        
        Class<?> implementationClass = implementations.get(type);
        if (implementationClass == null) {
            implementationClass = type;
        }
        
        try {
            Object instance = createInstance(implementationClass);
            
            injectDependencies(instance);
            
            singletons.put(type, instance);
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Cannot create instance: " + implementationClass, e);
        }
    }
    
    private Object createInstance(Class<?> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        
        for (Constructor<?> constructor : constructors) {
            if (constructor.getParameterCount() == 0) {
                constructor.setAccessible(true);
                return constructor.newInstance();
            }
        }
        
        Constructor<?> constructor = constructors[0];
        constructor.setAccessible(true);
        
        Object[] args = Arrays.stream(constructor.getParameterTypes())
            .map(this::resolveDependency)
            .toArray();
        
        return constructor.newInstance(args);
    }
    
    private void setFieldValue(Field field, Object instance, Object value) {
        try {
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot set field: " + field.getName(), e);
        }
    }
}
```

#### Performance Processor
```java
package com.learning.advanced.processor;

import com.learning.advanced.annotation.PerformanceLog;
import java.lang.reflect.*;
import java.util.*;

public class PerformanceProcessor {
    
    public Object executeWithLogging(Object instance, Method method, Object... args) 
            throws Exception {
        
        if (!method.isAnnotationPresent(PerformanceLog.class)) {
            return method.invoke(instance, args);
        }
        
        PerformanceLog annotation = method.getAnnotation(PerformanceLog.class);
        
        long startTime = System.nanoTime();
        
        Object result = null;
        Exception thrown = null;
        
        try {
            result = method.invoke(instance, args);
            return result;
        } catch (Exception e) {
            thrown = e;
            throw e;
        } finally {
            long endTime = System.nanoTime();
            long durationMs = (endTime - startTime) / 1_000_000;
            
            if (durationMs > annotation.threshold() || annotation.logArguments()) {
                StringBuilder log = new StringBuilder();
                log.append("Method: ").append(method.getName()).append("\n");
                log.append("Duration: ").append(durationMs).append("ms\n");
                
                if (annotation.logArguments() && args != null) {
                    log.append("Arguments: ").append(Arrays.toString(args)).append("\n");
                }
                
                if (annotation.logResult() && result != null) {
                    log.append("Result: ").append(result).append("\n");
                }
                
                if (durationMs > annotation.threshold()) {
                    log.append("WARNING: Exceeded threshold of ").append(annotation.threshold()).append("ms");
                }
                
                System.out.println(log.toString());
            }
        }
    }
    
    public Object createProxy(Object instance, Class<?> interfaceClass) {
        return java.lang.reflect.Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            (proxy, method, args) -> {
                if (method.isAnnotationPresent(PerformanceLog.class)) {
                    return executeWithLogging(instance, method, args);
                }
                return method.invoke(instance, args);
            }
        );
    }
}
```

#### User Model with Annotations
```java
package com.learning.advanced.model;

import com.learning.advanced.annotation.*;

public class User {
    
    @Validate(value = "username", required = true, minLength = 3, maxLength = 50)
    private String username;
    
    @Validate(value = "email", required = true, pattern = "^[A-Za-z0-9+_.-]+@(.+)$")
    private String email;
    
    @Validate(value = "password", required = true, minLength = 8)
    private String password;
    
    @Validate(value = "age", required = false)
    private Integer age;
    
    @Valid
    private String address;
    
    private String phoneNumber;
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
```

#### Custom Service Container
```java
package com.learning.framework;

import com.learning.advanced.annotation.*;
import com.learning.advanced.processor.*;
import java.lang.reflect.*;
import java.util.*;

public class CustomContainer {
    
    private final Map<Class<?>, Object> singletons = new HashMap<>();
    private final Map<Class<?>, Class<?>> implementations = new HashMap<>();
    private final ValidationProcessor validationProcessor = new ValidationProcessor();
    private final InjectionProcessor injectionProcessor = new InjectionProcessor();
    
    public void registerImplementation(Class<?> interfaceClass, Class<?> implementationClass) {
        implementations.put(interfaceClass, implementationClass);
        injectionProcessor.registerImplementation(interfaceClass, implementationClass);
    }
    
    public <T> T getInstance(Class<T> clazz) {
        if (singletons.containsKey(clazz)) {
            return clazz.cast(singletons.get(clazz));
        }
        
        try {
            T instance = createInstance(clazz);
            
            injectionProcessor.injectDependencies(instance);
            
            processLifecycleMethods(instance);
            
            singletons.put(clazz, instance);
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance: " + clazz.getName(), e);
        }
    }
    
    private <T> T createInstance(Class<T> clazz) throws Exception {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(Inject.class)) {
                constructor.setAccessible(true);
                Object[] args = Arrays.stream(constructor.getParameterTypes())
                    .map(this::getInstance)
                    .toArray();
                return clazz.cast(constructor.newInstance(args));
            }
        }
        
        Constructor<T> defaultConstructor = (Constructor<T>) clazz.getDeclaredConstructor();
        defaultConstructor.setAccessible(true);
        return defaultConstructor.newInstance();
    }
    
    private void processLifecycleMethods(Object instance) {
        Class<?> clazz = instance.getClass();
        
        for (Method method : clazz.getDeclaredMethods()) {
            method.setAccessible(true);
            
            if (method.isAnnotationPresent(PerformanceLog.class)) {
                processPerformanceMethod(instance, method);
            }
        }
    }
    
    private void processPerformanceMethod(Object instance, Method method) {
        PerformanceProcessor processor = new PerformanceProcessor();
        
        Object proxy = processor.createProxy(instance, method.getDeclaringClass());
        singletons.put(method.getDeclaringClass(), proxy);
    }
    
    public void validate(Object instance) throws ValidationProcessor.ValidationException {
        validationProcessor.validate(instance);
    }
}
```

### Build and Run Instructions
```bash
# Build the project
cd advanced-java-demo
mvn clean compile

# Run the main class
java -cp target/classes com.learning.advanced.AdvancedJavaApplication
```

---

## Real-World Project: Advanced Framework Implementation (8+ hours)

### Overview
Build a comprehensive framework similar to Spring using reflection, dynamic proxies, AOP, and bytecode manipulation.

### Project Structure
```
advanced-framework/
├── pom.xml
├── src/main/java/com/learning/framework/
│   ├── CoreFrameworkApplication.java
│   ├── context/
│   │   ├── ApplicationContext.java
│   │   ├── BeanFactory.java
│   │   └── BeanDefinition.java
│   ├── aop/
│   │   ├── Aspect.java
│   │   ├── AspectAdvisor.java
│   │   ├── AopProxy.java
│   │   └── CglibAopProxy.java
│   ├── beans/
│   │   ├── annotation/
│   │   │   ├── Component.java
│   │   │   ├── Service.java
│   │   │   ├── Repository.java
│   │   │   ├── Controller.java
│   │   │   └── Autowired.java
│   │   └── processor/
│   │       └── BeanPostProcessor.java
│   ├── di/
│   │   └── DependencyInjector.java
│   ├── transaction/
│   │   ├── TransactionManager.java
│   │   ├── Transactional.java
│   │   └── TransactionInterceptor.java
│   └── mvc/
│       ├── RequestMapping.java
│       ├── GetMapping.java
│       ├── PostMapping.java
│       └── DispatcherServlet.java
```

### Implementation

#### Bean Definition
```java
package com.learning.framework.context;

import java.lang.reflect.Method;

public class BeanDefinition {
    
    private String beanName;
    private Class<?> beanClass;
    private Object instance;
    private String scope;
    private boolean lazyInit;
    private Method initMethod;
    private Method destroyMethod;
    
    public BeanDefinition(Class<?> beanClass) {
        this.beanClass = beanClass;
        this.scope = "singleton";
        this.lazyInit = false;
    }
    
    public String getBeanName() { return beanName; }
    public void setBeanName(String beanName) { this.beanName = beanName; }
    public Class<?> getBeanClass() { return beanClass; }
    public void setBeanClass(Class<?> beanClass) { this.beanClass = beanClass; }
    public Object getInstance() { return instance; }
    public void setInstance(Object instance) { this.instance = instance; }
    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
    public boolean isLazyInit() { return lazyInit; }
    public void setLazyInit(boolean lazyInit) { this.lazyInit = lazyInit; }
    public Method getInitMethod() { return initMethod; }
    public void setInitMethod(Method initMethod) { this.initMethod = initMethod; }
    public Method getDestroyMethod() { return destroyMethod; }
    public void setDestroyMethod(Method destroyMethod) { this.destroyMethod = destroyMethod; }
}
```

#### Application Context
```java
package com.learning.framework.context;

import com.learning.framework.beans.annotation.*;
import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext implements BeanFactory {
    
    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<>();
    private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>();
    private final Map<Class<?>, List<BeanPostProcessor>> beanPostProcessors = new HashMap<>();
    
    public ApplicationContext(String basePackage) {
        scanComponents(basePackage);
        createBeans();
    }
    
    private void scanComponents(String basePackage) {
        try {
            String path = basePackage.replace('.', '/');
            ClassLoader classLoader = getClass().getClassLoader();
            
            File directory = new File(classLoader.getResource(path).getFile());
            
            if (directory.exists()) {
                scanDirectory(directory, basePackage);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to scan components", e);
        }
    }
    
    private void scanDirectory(File directory, String packageName) {
        File[] files = directory.listFiles();
        
        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName());
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().replace(".class", "");
                try {
                    Class<?> clazz = Class.forName(className);
                    
                    if (isComponent(clazz)) {
                        registerBean(clazz);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    private boolean isComponent(Class<?> clazz) {
        return clazz.isAnnotationPresent(Component.class) ||
               clazz.isAnnotationPresent(Service.class) ||
               clazz.isAnnotationPresent(Repository.class) ||
               clazz.isAnnotationPresent(Controller.class);
    }
    
    private void registerBean(Class<?> clazz) {
        Component annotation = clazz.getAnnotation(Component.class);
        String beanName = annotation != null && !annotation.value().isEmpty() 
            ? annotation.value() 
            : generateBeanName(clazz);
        
        BeanDefinition definition = new BeanDefinition(clazz);
        definition.setBeanName(beanName);
        
        if (clazz.isAnnotationPresent(Scope.class)) {
            definition.setScope(clazz.getAnnotation(Scope.class).value());
        }
        
        if (clazz.isAnnotationPresent(Lazy.class)) {
            definition.setLazyInit(true);
        }
        
        beanDefinitions.put(beanName, definition);
    }
    
    private String generateBeanName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    
    private void createBeans() {
        for (BeanDefinition definition : beanDefinitions.values()) {
            if ("singleton".equals(definition.getScope())) {
                createBean(definition);
            }
        }
    }
    
    private Object createBean(BeanDefinition definition) {
        try {
            Class<?> clazz = definition.getBeanClass();
            
            Constructor<?> constructor = findAutowiredConstructor(clazz);
            Object instance;
            
            if (constructor != null) {
                Object[] args = resolveConstructorArguments(constructor);
                constructor.setAccessible(true);
                instance = constructor.newInstance(args);
            } else {
                constructor = clazz.getDeclaredConstructor();
                constructor.setAccessible(true);
                instance = constructor.newInstance();
            }
            
            populateBean(instance);
            
            invokeInitMethod(instance, definition);
            
            definition.setInstance(instance);
            singletonObjects.put(definition.getBeanName(), instance);
            
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create bean: " + definition.getBeanName(), e);
        }
    }
    
    private Constructor<?> findAutowiredConstructor(Class<?> clazz) {
        for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return constructor;
            }
        }
        return null;
    }
    
    private Object[] resolveConstructorArguments(Constructor<?> constructor) {
        Class<?>[] paramTypes = constructor.getParameterTypes();
        Object[] args = new Object[paramTypes.length];
        
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = getBean(paramTypes[i]);
        }
        
        return args;
    }
    
    private void populateBean(Object instance) {
        Class<?> clazz = instance.getClass();
        Field[] fields = clazz.getDeclaredFields();
        
        for (Field field : fields) {
            if (field.isAnnotationPresent(Autowired.class)) {
                field.setAccessible(true);
                
                Object dependency = getBean(field.getType());
                
                try {
                    field.set(instance, dependency);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot inject dependency", e);
                }
            }
        }
    }
    
    private void invokeInitMethod(Object instance, BeanDefinition definition) {
        if (definition.getInitMethod() != null) {
            try {
                definition.getInitMethod().invoke(instance);
            } catch (Exception e) {
                throw new RuntimeException("Init method failed", e);
            }
        }
    }
    
    @Override
    public Object getBean(String name) {
        BeanDefinition definition = beanDefinitions.get(name);
        
        if (definition == null) {
            throw new NoSuchBeanDefinitionException("Bean not found: " + name);
        }
        
        if ("singleton".equals(definition.getScope())) {
            return singletonObjects.get(name);
        }
        
        return createBean(definition);
    }
    
    @Override
    public <T> T getBean(Class<T> clazz) {
        for (BeanDefinition definition : beanDefinitions.values()) {
            if (clazz.isAssignableFrom(definition.getBeanClass())) {
                return clazz.cast(getBean(definition.getBeanName()));
            }
        }
        throw new RuntimeException("Bean not found for class: " + clazz.getName());
    }
    
    public void registerBeanPostProcessor(BeanPostProcessor processor) {
        beanPostProcessors.computeIfAbsent(BeanPostProcessor.class, k -> new ArrayList<>())
            .add(processor);
    }
}
```

#### AOP Framework
```java
package com.learning.framework.aop;

import java.lang.reflect.*;
import java.util.*;

public class CglibAopProxy implements AopProxy {
    
    private final Object target;
    private final List<AspectAdvisor> advisors;
    
    public CglibAopProxy(Object target, List<AspectAdvisor> advisors) {
        this.target = target;
        this.advisors = advisors;
    }
    
    @Override
    public Object getProxy() {
        return Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            (proxy, method, args) -> {
                List<AspectAdvisor> matchingAdvisors = findMatchingAdvisors(method);
                
                if (matchingAdvisors.isEmpty()) {
                    return method.invoke(target, args);
                }
                
                return new CglibMethodInvocation(target, method, args, matchingAdvisors).proceed();
            }
        );
    }
    
    private List<AspectAdvisor> findMatchingAdvisors(Method method) {
        List<AspectAdvisor> matching = new ArrayList<>();
        
        for (AspectAdvisor advisor : advisors) {
            if (advisor.matches(method, target.getClass())) {
                matching.add(advisor);
            }
        }
        
        return matching;
    }
    
    static class CglibMethodInvocation {
        
        private final Object target;
        private final Method method;
        private final Object[] args;
        private final List<AspectAdvisor> advisors;
        private int currentIndex = -1;
        
        CglibMethodInvocation(Object target, Method method, Object[] args, 
                              List<AspectAdvisor> advisors) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.advisors = advisors;
        }
        
        public Object proceed() throws Throwable {
            if (currentIndex >= advisors.size() - 1) {
                return method.invoke(target, args);
            }
            
            AspectAdvisor advisor = advisors.get(++currentIndex);
            return advisor.getAdvice().invoke(this);
        }
    }
}

interface AopProxy {
    Object getProxy();
}
```

#### Transaction Management
```java
package com.learning.framework.transaction;

import java.lang.reflect.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class TransactionManager {
    
    private final ThreadLocal<TransactionStatus> currentTransaction = new ThreadLocal<>();
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    
    public void beginTransaction() {
        if (currentTransaction.get() != null) {
            throw new RuntimeException("Transaction already in progress");
        }
        
        TransactionStatus status = new TransactionStatus();
        status.setActive(true);
        status.setStartTime(System.currentTimeMillis());
        
        currentTransaction.set(status);
    }
    
    public void commit() {
        TransactionStatus status = currentTransaction.get();
        
        if (status == null) {
            throw new RuntimeException("No transaction in progress");
        }
        
        try {
            Connection connection = status.getConnection();
            if (connection != null) {
                connection.commit();
            }
            status.setCommitted(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to commit transaction", e);
        } finally {
            currentTransaction.remove();
        }
    }
    
    public void rollback() {
        TransactionStatus status = currentTransaction.get();
        
        if (status == null) {
            throw new RuntimeException("No transaction in progress");
        }
        
        try {
            Connection connection = status.getConnection();
            if (connection != null) {
                connection.rollback();
            }
            status.setRolledBack(true);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to rollback transaction", e);
        } finally {
            currentTransaction.remove();
        }
    }
    
    public TransactionStatus getCurrentTransaction() {
        return currentTransaction.get();
    }
    
    public boolean isTransactionActive() {
        return currentTransaction.get() != null;
    }
    
    static class TransactionStatus {
        private boolean active;
        private boolean committed;
        private boolean rolledBack;
        private long startTime;
        private Connection connection;
        
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public boolean isCommitted() { return committed; }
        public void setCommitted(boolean committed) { this.committed = committed; }
        public boolean isRolledBack() { return rolledBack; }
        public void setRolledBack(boolean rolledBack) { this.rolledBack = rolledBack; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public Connection getConnection() { return connection; }
        public void setConnection(Connection connection) { this.connection = connection; }
    }
}
```

#### Transaction Interceptor
```java
package com.learning.framework.transaction;

import java.lang.reflect.*;

public class TransactionInterceptor {
    
    private final TransactionManager transactionManager;
    
    public TransactionInterceptor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }
    
    public Object invoke(Method method, Object target, Object... args) throws Throwable {
        if (!method.isAnnotationPresent(Transactional.class)) {
            return method.invoke(target, args);
        }
        
        Transactional annotation = method.getAnnotation(Transactional.class);
        
        boolean requiresNew = annotation.requiresNew();
        
        if (requiresNew || !transactionManager.isTransactionActive()) {
            transactionManager.beginTransaction();
            
            try {
                Object result = method.invoke(target, args);
                transactionManager.commit();
                return result;
            } catch (Throwable e) {
                transactionManager.rollback();
                throw e;
            }
        }
        
        return method.invoke(target, args);
    }
}
```

#### Dispatcher Servlet
```java
package com.learning.framework.mvc;

import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DispatcherServlet {
    
    private final Map<String, HandlerMethod> handlers = new ConcurrentHashMap<>();
    private final Map<String, Object> controllers = new ConcurrentHashMap<>();
    
    public void registerController(Object controller) {
        controllers.put(controller.getClass().getSimpleName(), controller);
        
        for (Method method : controller.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping mapping = method.getAnnotation(RequestMapping.class);
                String url = mapping.value();
                handlers.put(url, new HandlerMethod(controller, method));
            }
            
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                String url = mapping.value();
                handlers.put(url, new HandlerMethod(controller, method));
            }
            
            if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                String url = mapping.value();
                handlers.put(url, new HandlerMethod(controller, method));
            }
        }
    }
    
    public void handleRequest(HttpRequest request, HttpResponse response) {
        String path = request.getPath();
        
        HandlerMethod handler = handlers.get(path);
        
        if (handler == null) {
            response.setStatus(404);
            response.setBody("Not Found");
            return;
        }
        
        try {
            Object result = handler.getMethod().invoke(handler.getController());
            response.setStatus(200);
            response.setBody(serialize(result));
        } catch (Exception e) {
            response.setStatus(500);
            response.setBody("Internal Server Error: " + e.getMessage());
        }
    }
    
    private String serialize(Object obj) {
        if (obj instanceof String) {
            return (String) obj;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object value = field.get(obj);
                sb.append("\"").append(field.getName()).append("\":\"").append(value).append("\",");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }
        sb.append("}");
        
        return sb.toString();
    }
    
    static class HandlerMethod {
        private final Object controller;
        private final Method method;
        
        HandlerMethod(Object controller, Method method) {
            this.controller = controller;
            this.method = method;
        }
        
        Object getController() { return controller; }
        Method getMethod() { return method; }
    }
}

class HttpRequest {
    private String path;
    
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}

class HttpResponse {
    private int status;
    private String body;
    
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
}
```

#### Custom Annotations
```java
package com.learning.framework.beans.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {
    String value() default "";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Service {
    String value() default "";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Repository {
    String value() default "";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {
    String value() default "";
}

@Target({ElementType.FIELD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
public @interface Autowired {
    boolean required() default true;
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Scope {
    String value() default "singleton";
}

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Lazy {
}
```

#### MVC Annotations
```java
package com.learning.framework.mvc;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    String value() default "";
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String value() default "";
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {
    String value() default "";
}
```

#### Transaction Annotation
```java
package com.learning.framework.transaction;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Transactional {
    boolean requiresNew() default false;
}
```

### Build and Run Instructions
```bash
# Build the framework
cd advanced-framework
mvn clean compile

# Run sample application
java -cp target/classes com.learning.framework.CoreFrameworkApplication
```

### Learning Outcomes
- Implement custom annotation processors
- Build dependency injection container
- Create AOP framework with proxies
- Implement transaction management
- Build MVC framework with dispatcher servlet
- Use reflection for runtime processing
- Create bean post processors