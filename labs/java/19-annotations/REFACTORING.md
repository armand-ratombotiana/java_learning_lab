# Refactoring — Annotations

## XML Configuration → Annotations
```java
// Before (Spring XML)
<bean id="userService" class="com.example.UserService">
    <property name="repository" ref="userRepository"/>
</bean>

// After (Spring Annotations)
@Component
public class UserService {
    @Autowired
    private UserRepository repository;
}
```

## Marker Interface → Annotation
```java
// Before
public interface Auditable {}
public class DeleteUserAction implements Auditable { ... }

// After
@Auditable(action = "DELETE_USER")
public class DeleteUserAction { ... }
```

## Javadoc Tags → Annotations
```java
// Before
/**
 * @deprecated Use {@link #newMethod()} instead
 */
public void oldMethod() {}

// After
@Deprecated(since = "3.0", forRemoval = true)
public void oldMethod() {}
```

## Inheritance → Composition with Annotations
Use annotations to add behaviour rather than creating deep class hierarchies.
