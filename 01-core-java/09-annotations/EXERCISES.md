# Exercises: Annotations

<div align="center">

![Module](https://img.shields.io/badge/Module-09-blue?style=for-the-badge)
![Exercises](https://img.shields.io/badge/Exercises-20-green?style=for-the-badge)
![Difficulty](https://img.shields.io/badge/Difficulty-Easy%20to%20Hard-orange?style=for-the-badge)

**20 comprehensive exercises for Annotations module**

</div>

---

## 📚 Table of Contents

1. [Easy Exercises (1-7)](#easy-exercises-1-7)
2. [Medium Exercises (8-14)](#medium-exercises-8-14)
3. [Hard Exercises (15-18)](#hard-exercises-15-18)
4. [Interview Exercises (19-20)](#interview-exercises-19-20)

---

## 🟢 Easy Exercises (1-7)

### Exercise 1: Built-in Annotations
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** @Override, @Deprecated, @SuppressWarnings

**Pedagogic Objective:**
Understand using built-in annotations.

**Problem:**
Use @Override, @Deprecated, and @SuppressWarnings annotations.

**Complete Solution:**
```java
public class BuiltInAnnotations {
    @Deprecated
    public void oldMethod() {
        System.out.println("This method is deprecated");
    }
    
    public void newMethod() {
        System.out.println("This is the new method");
    }
}

public class BuiltInAnnotationsExample {
    public static void main(String[] args) {
        BuiltInAnnotations obj = new BuiltInAnnotations();
        
        @SuppressWarnings("deprecation")
        BuiltInAnnotations deprecated = new BuiltInAnnotations();
        deprecated.oldMethod();
        
        obj.newMethod();
    }
}
```

**Key Concepts:**
- @Override marks method overriding
- @Deprecated marks obsolete code
- @SuppressWarnings suppresses compiler warnings
- Built-in annotations provide metadata

---

### Exercise 2: Custom Annotations
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Custom annotations, @interface, annotation elements

**Pedagogic Objective:**
Understand creating custom annotations.

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MyAnnotation {
    String value() default "default value";
    int count() default 0;
}

public class CustomAnnotationsExample {
    @MyAnnotation(value = "test", count = 5)
    public void annotatedMethod() {
        System.out.println("This method is annotated");
    }
    
    @MyAnnotation
    public void defaultAnnotation() {
        System.out.println("Using default values");
    }
    
    public static void main(String[] args) {
        annotatedMethod();
        defaultAnnotation();
    }
}
```

**Key Concepts:**
- @interface defines annotation
- @Target specifies where annotation applies
- @Retention specifies retention policy
- Elements define annotation parameters

---

### Exercise 3: Annotation Retention Policies
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** Retention policies, SOURCE, CLASS, RUNTIME

**Pedagogic Objective:**
Understand annotation retention policies.

**Complete Solution:**
```java
import java.lang.annotation.*;

@Retention(RetentionPolicy.SOURCE)
public @interface SourceAnnotation {
}

@Retention(RetentionPolicy.CLASS)
public @interface ClassAnnotation {
}

@Retention(RetentionPolicy.RUNTIME)
public @interface RuntimeAnnotation {
}

public class RetentionPoliciesExample {
    @SourceAnnotation
    public void sourceOnly() {
        System.out.println("Source annotation");
    }
    
    @ClassAnnotation
    public void classLevel() {
        System.out.println("Class annotation");
    }
    
    @RuntimeAnnotation
    public void runtimeLevel() {
        System.out.println("Runtime annotation");
    }
    
    public static void main(String[] args) {
        sourceOnly();
        classLevel();
        runtimeLevel();
    }
}
```

**Key Concepts:**
- SOURCE: Available only in source code
- CLASS: Available in compiled class
- RUNTIME: Available at runtime via reflection
- Choose based on use case

---

### Exercise 4: Annotation Targets
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** @Target, ElementType, annotation scope

**Pedagogic Objective:**
Understand annotation target types.

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.TYPE)
public @interface TypeAnnotation {
}

@Target(ElementType.METHOD)
public @interface MethodAnnotation {
}

@Target(ElementType.FIELD)
public @interface FieldAnnotation {
}

@Target({ElementType.TYPE, ElementType.METHOD})
public @interface MultiTargetAnnotation {
}

@TypeAnnotation
public class AnnotationTargetsExample {
    @FieldAnnotation
    private String field;
    
    @MethodAnnotation
    public void method() {
        System.out.println("Annotated method");
    }
    
    @MultiTargetAnnotation
    public void multiTarget() {
        System.out.println("Multi-target annotation");
    }
}
```

**Key Concepts:**
- TYPE: Classes, interfaces, enums
- METHOD: Methods
- FIELD: Fields
- PARAMETER: Parameters
- CONSTRUCTOR: Constructors
- Multiple targets possible

---

### Exercise 5: Annotation with Parameters
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** Annotation elements, parameters, default values

**Pedagogic Objective:**
Understand annotation parameters.

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestCase {
    String name();
    String description() default "No description";
    int priority() default 1;
    boolean enabled() default true;
}

public class AnnotationParametersExample {
    @TestCase(name = "test1", description = "First test", priority = 1)
    public void test1() {
        System.out.println("Test 1");
    }
    
    @TestCase(name = "test2")
    public void test2() {
        System.out.println("Test 2");
    }
    
    public static void main(String[] args) {
        test1();
        test2();
    }
}
```

**Key Concepts:**
- Elements define annotation parameters
- Required elements have no default
- Optional elements have defaults
- Primitive types and String allowed

---

### Exercise 6: Repeating Annotations
**Difficulty:** Easy  
**Time:** 20 minutes  
**Topics:** @Repeatable, repeating annotations, Java 8+

**Pedagogic Objective:**
Understand repeating annotations.

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Schedules.class)
public @interface Schedule {
    String day();
    String time();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Schedules {
    Schedule[] value();
}

public class RepeatingAnnotationsExample {
    @Schedule(day = "Monday", time = "10:00")
    @Schedule(day = "Wednesday", time = "14:00")
    @Schedule(day = "Friday", time = "16:00")
    public void meetingSchedule() {
        System.out.println("Meeting scheduled");
    }
    
    public static void main(String[] args) {
        meetingSchedule();
    }
}
```

**Key Concepts:**
- @Repeatable allows multiple annotations
- Container annotation holds repeated values
- Java 8+ feature
- Cleaner syntax for multiple annotations

---

### Exercise 7: Annotation Inheritance
**Difficulty:** Easy  
**Time:** 15 minutes  
**Topics:** @Inherited, annotation inheritance

**Pedagogic Objective:**
Understand annotation inheritance.

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface InheritedAnnotation {
    String value() default "";
}

@InheritedAnnotation("Parent")
public class Parent {
}

public class Child extends Parent {
}

public class AnnotationInheritanceExample {
    public static void main(String[] args) {
        InheritedAnnotation parentAnnotation = Parent.class.getAnnotation(InheritedAnnotation.class);
        System.out.println("Parent: " + parentAnnotation.value());
        
        InheritedAnnotation childAnnotation = Child.class.getAnnotation(InheritedAnnotation.class);
        System.out.println("Child: " + (childAnnotation != null ? childAnnotation.value() : "Not inherited"));
    }
}
```

**Key Concepts:**
- @Inherited allows annotation inheritance
- Only applies to class annotations
- Subclasses inherit parent annotations
- Useful for framework annotations

---

## 🟡 Medium Exercises (8-14)

### Exercise 8: Annotation Processing
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Reflection, annotation processing, runtime inspection

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Validate {
    String pattern() default "";
}

public class AnnotationProcessingExample {
    @Validate(pattern = "\\d+")
    public void validateNumber(String input) {
        System.out.println("Validating: " + input);
    }
    
    public static void main(String[] args) throws Exception {
        Method method = AnnotationProcessingExample.class.getMethod("validateNumber", String.class);
        
        if (method.isAnnotationPresent(Validate.class)) {
            Validate validate = method.getAnnotation(Validate.class);
            System.out.println("Pattern: " + validate.pattern());
        }
    }
}
```

---

### Exercise 9: Custom Validator Annotation
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Custom annotations, validation, reflection

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {
    String message() default "Field cannot be null";
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MinLength {
    int value();
    String message() default "String too short";
}

public class User {
    @NotNull
    private String name;
    
    @MinLength(value = 8)
    private String password;
    
    public User(String name, String password) {
        this.name = name;
        this.password = password;
    }
}

public class CustomValidatorAnnotation {
    public static boolean validate(Object obj) {
        Class<?> clazz = obj.getClass();
        
        for (java.lang.reflect.Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            if (field.isAnnotationPresent(NotNull.class)) {
                try {
                    if (field.get(obj) == null) {
                        System.out.println("Validation failed: " + field.getName());
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            
            if (field.isAnnotationPresent(MinLength.class)) {
                MinLength minLength = field.getAnnotation(MinLength.class);
                try {
                    String value = (String) field.get(obj);
                    if (value.length() < minLength.value()) {
                        System.out.println("Validation failed: " + minLength.message());
                        return false;
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return true;
    }
    
    public static void main(String[] args) {
        User user1 = new User("Alice", "password123");
        System.out.println("User1 valid: " + validate(user1));
        
        User user2 = new User("Bob", "short");
        System.out.println("User2 valid: " + validate(user2));
    }
}
```

---

### Exercise 10: Annotation-Based Configuration
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Configuration, annotations, metadata

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.CLASS)
@Retention(RetentionPolicy.RUNTIME)
public @interface Configuration {
    String name();
    String version() default "1.0";
    String[] authors() default {};
}

@Configuration(
    name = "MyApp",
    version = "2.0",
    authors = {"Alice", "Bob"}
)
public class MyApplication {
    public static void main(String[] args) {
        Configuration config = MyApplication.class.getAnnotation(Configuration.class);
        
        System.out.println("Name: " + config.name());
        System.out.println("Version: " + config.version());
        System.out.println("Authors: " + java.util.Arrays.toString(config.authors()));
    }
}
```

---

### Exercise 11: Annotation-Based Logging
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Logging, annotations, AOP-like behavior

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    String value() default "Method called";
}

public class AnnotationBasedLogging {
    @Loggable("User login")
    public void login(String username) {
        System.out.println("Logging in: " + username);
    }
    
    @Loggable("User logout")
    public void logout() {
        System.out.println("Logging out");
    }
    
    public static void executeWithLogging(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);
        
        if (method.isAnnotationPresent(Loggable.class)) {
            Loggable loggable = method.getAnnotation(Loggable.class);
            System.out.println("[LOG] " + loggable.value());
        }
        
        method.invoke(obj);
    }
    
    public static void main(String[] args) throws Exception {
        AnnotationBasedLogging app = new AnnotationBasedLogging();
        executeWithLogging(app, "login");
        executeWithLogging(app, "logout");
    }
}
```

---

### Exercise 12: Annotation-Based Caching
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Caching, annotations, reflection

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.util.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    String value() default "";
}

public class CacheManager {
    private static Map<String, Object> cache = new HashMap<>();
    
    public static Object getFromCache(String key) {
        return cache.get(key);
    }
    
    public static void putInCache(String key, Object value) {
        cache.put(key, value);
    }
}

public class AnnotationBasedCaching {
    @Cacheable("fibonacci")
    public int fibonacci(int n) {
        String key = "fib_" + n;
        Object cached = CacheManager.getFromCache(key);
        
        if (cached != null) {
            System.out.println("Cache hit for " + n);
            return (int) cached;
        }
        
        int result = n <= 1 ? n : fibonacci(n - 1) + fibonacci(n - 2);
        CacheManager.putInCache(key, result);
        return result;
    }
    
    public static void main(String[] args) {
        AnnotationBasedCaching app = new AnnotationBasedCaching();
        System.out.println("fib(5) = " + app.fibonacci(5));
        System.out.println("fib(5) = " + app.fibonacci(5));
    }
}
```

---

### Exercise 13: Meta-Annotations
**Difficulty:** Medium  
**Time:** 25 minutes  
**Topics:** Meta-annotations, @Target, @Retention, @Documented

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface API {
    String version() default "1.0";
    String since() default "";
}

@API(version = "2.0", since = "2020-01-01")
public class MetaAnnotationsExample {
    @API(version = "1.5")
    public void method1() {
        System.out.println("Method 1");
    }
    
    public static void main(String[] args) {
        API classAnnotation = MetaAnnotationsExample.class.getAnnotation(API.class);
        System.out.println("Class version: " + classAnnotation.version());
    }
}
```

---

### Exercise 14: Annotation Composition
**Difficulty:** Medium  
**Time:** 30 minutes  
**Topics:** Annotation composition, combining annotations

**Complete Solution:**
```java
import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TestMethod {
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {
    long value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@TestMethod
@Timeout(5000)
public @interface FastTest {
}

public class AnnotationComposition {
    @FastTest
    public void quickTest() {
        System.out.println("Quick test");
    }
    
    public static void main(String[] args) {
        java.lang.reflect.Method method = null;
        try {
            method = AnnotationComposition.class.getMethod("quickTest");
            
            if (method.isAnnotationPresent(FastTest.class)) {
                System.out.println("Has @FastTest");
            }
            
            if (method.isAnnotationPresent(TestMethod.class)) {
                System.out.println("Has @TestMethod (via composition)");
            }
            
            if (method.isAnnotationPresent(Timeout.class)) {
                Timeout timeout = method.getAnnotation(Timeout.class);
                System.out.println("Timeout: " + timeout.value() + "ms");
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
```

---

## 🔴 Hard Exercises (15-18)

### Exercise 15: Custom Annotation Framework
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Annotation framework, reflection, advanced processing

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String name();
    String description() default "";
}

public class CommandFramework {
    private Map<String, Method> commands = new HashMap<>();
    
    public void register(Object obj) {
        Class<?> clazz = obj.getClass();
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Command.class)) {
                Command command = method.getAnnotation(Command.class);
                commands.put(command.name(), method);
                System.out.println("Registered command: " + command.name());
            }
        }
    }
    
    public void execute(String commandName, Object obj) throws Exception {
        Method method = commands.get(commandName);
        if (method != null) {
            method.invoke(obj);
        } else {
            System.out.println("Command not found: " + commandName);
        }
    }
}

public class CustomAnnotationFramework {
    @Command(name = "start", description = "Start the application")
    public void start() {
        System.out.println("Application started");
    }
    
    @Command(name = "stop", description = "Stop the application")
    public void stop() {
        System.out.println("Application stopped");
    }
    
    public static void main(String[] args) throws Exception {
        CommandFramework framework = new CommandFramework();
        CustomAnnotationFramework app = new CustomAnnotationFramework();
        
        framework.register(app);
        framework.execute("start", app);
        framework.execute("stop", app);
    }
}
```

---

### Exercise 16: Annotation-Based Dependency Injection
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Dependency injection, annotations, reflection

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Inject {
}

public class DIContainer {
    private Map<Class<?>, Object> instances = new HashMap<>();
    
    public <T> void register(Class<T> type, T instance) {
        instances.put(type, instance);
    }
    
    public void inject(Object obj) throws IllegalAccessException {
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Inject.class)) {
                field.setAccessible(true);
                Object dependency = instances.get(field.getType());
                
                if (dependency != null) {
                    field.set(obj, dependency);
                    System.out.println("Injected: " + field.getName());
                }
            }
        }
    }
}

public class AnnotationBasedDependencyInjection {
    interface Logger {
        void log(String message);
    }
    
    static class ConsoleLogger implements Logger {
        @Override
        public void log(String message) {
            System.out.println("[LOG] " + message);
        }
    }
    
    static class Service {
        @Inject
        private Logger logger;
        
        public void doWork() {
            logger.log("Doing work...");
        }
    }
    
    public static void main(String[] args) throws IllegalAccessException {
        DIContainer container = new DIContainer();
        container.register(Logger.class, new ConsoleLogger());
        
        Service service = new Service();
        container.inject(service);
        service.doWork();
    }
}
```

---

### Exercise 17: Annotation-Based ORM
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** ORM, annotations, reflection, database mapping

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    String tableName();
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    boolean primaryKey() default false;
}

@Entity(tableName = "users")
public class User {
    @Column(name = "id", primaryKey = true)
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "email")
    private String email;
    
    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
}

public class AnnotationBasedORM {
    public static String generateSQL(Class<?> clazz) {
        Entity entity = clazz.getAnnotation(Entity.class);
        if (entity == null) return null;
        
        StringBuilder sql = new StringBuilder("CREATE TABLE " + entity.tableName() + " (");
        
        Field[] fields = clazz.getDeclaredFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            Column column = field.getAnnotation(Column.class);
            
            if (column != null) {
                sql.append(column.name()).append(" ");
                sql.append(field.getType().getSimpleName().toUpperCase());
                
                if (column.primaryKey()) {
                    sql.append(" PRIMARY KEY");
                }
                
                if (i < fields.length - 1) {
                    sql.append(", ");
                }
            }
        }
        
        sql.append(")");
        return sql.toString();
    }
    
    public static void main(String[] args) {
        String sql = generateSQL(User.class);
        System.out.println(sql);
    }
}
```

---

### Exercise 18: Annotation Validator Framework
**Difficulty:** Hard  
**Time:** 40 minutes  
**Topics:** Validation framework, annotations, reflection

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Required {
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    int min() default 0;
    int max() default Integer.MAX_VALUE;
}

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Email {
}

public class ValidatorFramework {
    public static List<String> validate(Object obj) {
        List<String> errors = new ArrayList<>();
        Class<?> clazz = obj.getClass();
        
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            
            try {
                Object value = field.get(obj);
                
                if (field.isAnnotationPresent(Required.class)) {
                    if (value == null || (value instanceof String && ((String) value).isEmpty())) {
                        errors.add(field.getName() + " is required");
                    }
                }
                
                if (field.isAnnotationPresent(Range.class) && value instanceof Integer) {
                    Range range = field.getAnnotation(Range.class);
                    int intValue = (Integer) value;
                    if (intValue < range.min() || intValue > range.max()) {
                        errors.add(field.getName() + " must be between " + range.min() + " and " + range.max());
                    }
                }
                
                if (field.isAnnotationPresent(Email.class) && value instanceof String) {
                    String email = (String) value;
                    if (!email.contains("@")) {
                        errors.add(field.getName() + " must be a valid email");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        
        return errors;
    }
}

public class AnnotationValidatorFramework {
    static class User {
        @Required
        private String name;
        
        @Range(min = 18, max = 100)
        private int age;
        
        @Email
        private String email;
        
        public User(String name, int age, String email) {
            this.name = name;
            this.age = age;
            this.email = email;
        }
    }
    
    public static void main(String[] args) {
        User user1 = new User("Alice", 25, "alice@example.com");
        List<String> errors1 = ValidatorFramework.validate(user1);
        System.out.println("User1 errors: " + errors1);
        
        User user2 = new User("", 150, "invalid-email");
        List<String> errors2 = ValidatorFramework.validate(user2);
        System.out.println("User2 errors: " + errors2);
    }
}
```

---

## 🎯 Interview Exercises (19-20)

### Exercise 19: Annotation-Based Test Framework
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Test {
    String name() default "";
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Before {
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface After {
}

public class TestRunner {
    public static void run(Class<?> testClass) throws Exception {
        Object instance = testClass.getDeclaredConstructor().newInstance();
        
        List<Method> beforeMethods = new ArrayList<>();
        List<Method> testMethods = new ArrayList<>();
        List<Method> afterMethods = new ArrayList<>();
        
        for (Method method : testClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Before.class)) {
                beforeMethods.add(method);
            } else if (method.isAnnotationPresent(Test.class)) {
                testMethods.add(method);
            } else if (method.isAnnotationPresent(After.class)) {
                afterMethods.add(method);
            }
        }
        
        for (Method testMethod : testMethods) {
            for (Method before : beforeMethods) {
                before.invoke(instance);
            }
            
            testMethod.invoke(instance);
            System.out.println("✓ " + testMethod.getName());
            
            for (Method after : afterMethods) {
                after.invoke(instance);
            }
        }
    }
}

public class AnnotationBasedTestFramework {
    @Before
    public void setUp() {
        System.out.println("  Setting up...");
    }
    
    @Test(name = "test1")
    public void test1() {
        System.out.println("  Running test1");
    }
    
    @Test(name = "test2")
    public void test2() {
        System.out.println("  Running test2");
    }
    
    @After
    public void tearDown() {
        System.out.println("  Tearing down...");
    }
    
    public static void main(String[] args) throws Exception {
        TestRunner.run(AnnotationBasedTestFramework.class);
    }
}
```

---

### Exercise 20: Annotation-Based REST API
**Difficulty:** Interview  
**Time:** 40 minutes

**Complete Solution:**
```java
import java.lang.annotation.*;
import java.lang.reflect.*;
import java.util.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GetMapping {
    String value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PostMapping {
    String value();
}

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PathVariable {
    String value();
}

public class APIRouter {
    private Map<String, Method> routes = new HashMap<>();
    private Object controller;
    
    public APIRouter(Object controller) {
        this.controller = controller;
        registerRoutes();
    }
    
    private void registerRoutes() {
        for (Method method : controller.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(GetMapping.class)) {
                GetMapping mapping = method.getAnnotation(GetMapping.class);
                routes.put("GET:" + mapping.value(), method);
            } else if (method.isAnnotationPresent(PostMapping.class)) {
                PostMapping mapping = method.getAnnotation(PostMapping.class);
                routes.put("POST:" + mapping.value(), method);
            }
        }
    }
    
    public void handleRequest(String httpMethod, String path) throws Exception {
        String key = httpMethod + ":" + path;
        Method method = routes.get(key);
        
        if (method != null) {
            method.invoke(controller);
        } else {
            System.out.println("404 Not Found");
        }
    }
}

public class AnnotationBasedRESTAPI {
    @GetMapping("/users")
    public void getUsers() {
        System.out.println("GET /users - Returning all users");
    }
    
    @GetMapping("/users/{id}")
    public void getUser(@PathVariable("id") String id) {
        System.out.println("GET /users/{id} - Returning user " + id);
    }
    
    @PostMapping("/users")
    public void createUser() {
        System.out.println("POST /users - Creating new user");
    }
    
    public static void main(String[] args) throws Exception {
        APIRouter router = new APIRouter(new AnnotationBasedRESTAPI());
        
        router.handleRequest("GET", "/users");
        router.handleRequest("POST", "/users");
        router.handleRequest("GET", "/users/{id}");
    }
}
```

---

## 📊 Solutions Summary

| Exercise | Title | Difficulty | Time | Topics |
|----------|-------|-----------|------|--------|
| 1 | Built-in | Easy | 15 min | Built-in |
| 2 | Custom | Easy | 20 min | Custom |
| 3 | Retention | Easy | 15 min | Retention |
| 4 | Targets | Easy | 20 min | Targets |
| 5 | Parameters | Easy | 20 min | Parameters |
| 6 | Repeating | Easy | 20 min | Repeating |
| 7 | Inheritance | Easy | 15 min | Inheritance |
| 8 | Processing | Medium | 25 min | Processing |
| 9 | Validator | Medium | 30 min | Validation |
| 10 | Configuration | Medium | 30 min | Config |
| 11 | Logging | Medium | 30 min | Logging |
| 12 | Caching | Medium | 30 min | Caching |
| 13 | Meta | Medium | 25 min | Meta |
| 14 | Composition | Medium | 30 min | Composition |
| 15 | Framework | Hard | 40 min | Framework |
| 16 | DI | Hard | 40 min | DI |
| 17 | ORM | Hard | 40 min | ORM |
| 18 | Validator | Hard | 40 min | Validation |
| 19 | Test | Interview | 40 min | Testing |
| 20 | REST | Interview | 40 min | REST |

---

<div align="center">

## Exercises: Annotations

**20 Comprehensive Exercises**

**Easy (7) | Medium (7) | Hard (4) | Interview (2)**

**Total Time: 6-8 hours**

---

[Back to Module →](./README.md)

[View Pedagogic Guide →](./PEDAGOGIC_GUIDE.md)

[Take Quizzes →](./QUIZZES.md)

</div>

(ending readme)