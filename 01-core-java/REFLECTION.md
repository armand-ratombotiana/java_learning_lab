# Reflection for Core Java

## What is Reflection?

Reflection is the ability to inspect and manipulate classes, fields, methods, and constructors at runtime without knowing their names at compile time.

## Common Use Cases

### 1. Framework Development
- Spring uses reflection for dependency injection
- Serialization frameworks inspect object fields
- Testing frameworks create instances and invoke methods

### 2. Dynamic Loading
- Plugins and modules loaded at runtime
- Configuration-driven class loading

### 3. Debugging and Tools
- IDE debuggers use reflection
- Logging frameworks inspect objects

## Key Classes

- `Class` - represents a class
- `Field` - represents a field
- `Method` - represents a method
- `Constructor` - represents a constructor

## Example Usage

```java
Class<?> clazz = MyClass.class;
Object instance = clazz.getDeclaredConstructor().newInstance();
Method method = clazz.getMethod("doSomething");
method.invoke(instance);
```

## Performance Considerations

- Reflection is slower than direct calls
- Cache reflectively accessed members
- Use setAccessible(true) carefully (security)
- Consider MethodHandle for better performance

## Alternatives

- Modern alternatives: lambdas, method references
- Code generation: Annotation Processors, APT
- Bytecode manipulation: ASM, Javassist, CGLIB