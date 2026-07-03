# Module 09: Annotations - Mini Project

**Project Name**: Custom Dependency Injection Framework  
**Difficulty Level**: Advanced  
**Estimated Time**: 3 hours

---

## 🎯 Objective
Create a lightweight, custom Dependency Injection (DI) and Configuration framework using Java Custom Annotations and the Java Reflection API, mimicking the core behavior of Spring Boot.

## 📝 Requirements

### Core Features
1. **Custom Annotations**:
   - `@Service`: A class-level annotation indicating a class is a service component.
   - `@Inject`: A field-level annotation indicating a dependency that needs to be injected.
   - `@RunOnInit`: A method-level annotation indicating the method should be executed automatically after the class is instantiated and dependencies are injected.

2. **The Dependency Injector (IoC Container)**:
   - Create a class `ApplicationContext`.
   - Write a method `public void scanAndInitialize(String packageName)`:
     - Scan the given package for all classes (for simplicity, you can hardcode an array of `Class<?>` objects to simulate a classpath scan, or use a library like Reflections, but standard reflection via `ClassLoader` is preferred if possible).
     - Instantiate any class annotated with `@Service` and store it in a `Map<Class<?>, Object>`.

3. **Field Injection**:
   - Iterate over the created service instances.
   - For each instance, reflect on its declared fields.
   - If a field is annotated with `@Inject`, look up the required type in the `Map`.
   - Make the field accessible (`setAccessible(true)`) and inject the dependent object.

4. **Method Execution**:
   - After all dependencies are injected, iterate over the service instances again.
   - Reflect on their declared methods.
   - If a method is annotated with `@RunOnInit`, invoke it via reflection (`method.invoke(instance)`).

---

## 💡 Solution Blueprint

1. **Annotations**:
   ```java
   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.TYPE)
   public @interface Service {}

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.FIELD)
   public @interface Inject {}

   @Retention(RetentionPolicy.RUNTIME)
   @Target(ElementType.METHOD)
   public @interface RunOnInit {}
   ```

2. **Dummy Services**:
   ```java
   @Service
   public class DatabaseService {
       public void connect() { System.out.println("Connected to DB"); }
   }

   @Service
   public class UserService {
       @Inject
       private DatabaseService db;

       @RunOnInit
       public void start() {
           System.out.println("UserService starting...");
           db.connect();
       }
   }
   ```

3. **The Container**:
   - Hardcode classes to bypass classpath scanning complexity for the mini-project: 
     `Class<?>[] classes = {DatabaseService.class, UserService.class};`
   - **Step 1 (Instantiate)**: Find `@Service`, call `clazz.getDeclaredConstructor().newInstance()`, store in `Map<Class, Object> container`.
   - **Step 2 (Inject)**: For each object in `container`, loop over `obj.getClass().getDeclaredFields()`. If `field.isAnnotationPresent(Inject.class)`, do `field.setAccessible(true); field.set(obj, container.get(field.getType()));`.
   - **Step 3 (Init)**: Loop over methods, find `@RunOnInit`, call `method.invoke(obj)`.