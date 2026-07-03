# Refactoring — Reflection

## Direct Invocation → Reflection (when needed)
```java
// Before (hardcoded)
UserService service = new UserService();
service.process(user);

// After (configurable via name)
Class<?> clazz = Class.forName(config.getServiceClass());
Object service = clazz.getDeclaredConstructor().newInstance();
clazz.getMethod("process", User.class).invoke(service, user);
```

## Reflection → MethodHandle (performance)
```java
// Before (reflection)
Method m = clazz.getMethod("process");
m.invoke(instance);

// After (method handle — faster)
MethodHandle mh = MethodHandles.lookup()
    .findVirtual(clazz, "process", MethodType.methodType(void.class));
mh.invoke(instance);
```

## Reflection → Lambda (when possible)
```java
// Before
Method m = button.getClass().getMethod("addActionListener", ActionListener.class);
m.invoke(button, (ActionListener) e -> {});

// After (lambda with typed reference)
button.addActionListener(e -> {});
```

## Manual Proxy → Dynamic Proxy
```java
// Before — manual decorator per interface
class LoggingService implements Service {
    private final Service delegate;
    public void process() { log("process"); delegate.process(); }
}

// After — one dynamic proxy for any interface
Service s = loggingProxy(realService, Service.class);
```
