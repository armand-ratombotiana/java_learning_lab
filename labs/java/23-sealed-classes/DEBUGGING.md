# Debugging Sealed Classes

## Common Compiler Errors

### "Sealed class must have at least one permitted subclass"
A sealed class or interface with an empty `permits` clause is invalid:

```java
// ERROR: Empty permits
sealed interface Empty permits {}  // Must list at least one subtype
```

**Fix**: Remove the `permits` clause entirely is valid only if all subtypes are in the same file.

### "The type which is not a subtype of the sealed class cannot be listed in its permits clause"
A type in the `permits` clause that doesn't extend/implement the sealed type:

```java
sealed interface A permits B {}
final class B {}  // Doesn't implement A — ERROR
```

**Fix**: Ensure `B` extends or implements `A`.

### "A subclass of a sealed class must be declared sealed, final, or non-sealed"
A permitted subtype without a qualifying modifier:

```java
sealed interface Shape permits Circle {}
class Circle implements Shape {}  // Missing modifier — ERROR
```

**Fix**: Add `final`, `sealed`, or `non-sealed` to `Circle`.

### "Switch expression does not cover all possible values"
When switch over a sealed type misses one or more permitted subtypes:

```java
sealed interface A permits B, C {}
final class B implements A {}
final class C implements A {}

String test(A a) {
    return switch (a) {
        case B b -> "B";
        // Missing case for C!
    };
}
```

**Fix**: Add the missing case or a `default` clause.

## Runtime Errors

### VerifyError at Class Load Time
If bytecode manipulation creates a subclass of a sealed class without modifying the permits clause:

```
Exception in thread "main" java.lang.VerifyError: 
  (class: EvilExtendsShape, method: <init> Signature: ()V) 
  Subclass of sealed class 'Shape' is not permitted
```

This error occurs at class loading when the JVM detects an unpermitted subclass.

## Debugging Strategies

### Check the Permitted Subclass List
```java
public static void inspectSealedClass(Class<?> clazz) {
    if (clazz.isSealed()) {
        System.out.println(clazz.getName() + " is sealed");
        System.out.println("Permitted subclasses:");
        for (Class<?> permitted : clazz.getPermittedSubclasses()) {
            System.out.println("  - " + permitted.getName());
            System.out.println("    Is final: " + java.lang.reflect.Modifier.isFinal(permitted.getModifiers()));
            System.out.println("    Is sealed: " + permitted.isSealed());
        }
    }
}
```

### Verify Module Exports
```java
// Check if a sealed class is accessible from the current module
Module module = Shape.class.getModule();
System.out.println("Module: " + module.getName());
System.out.println("Is exported: " + module.isExported("com.example.shapes"));
```

### IDE-Specific Debugging

**IntelliJ IDEA**:
- Structure tool: Shows sealed hierarchy with all permitted subtypes
- Go to Implementations (Ctrl+Alt+B): Shows all subtypes
- Error highlighting: Immediately shows exhaustiveness errors

**Eclipse**:
- Type Hierarchy view: Shows the sealed hierarchy
- Quick Fix (Ctrl+1): Add missing cases on exhaustiveness errors

**VS Code with Java Extension**:
- Code Actions: Add missing cases in switch expressions
- Hover information: Shows that a class is sealed and its permitted subtypes

## Common Scenarios

### Scenario: Adding a New Subtype Causes Many Compilation Errors
This is expected behavior — it's the feature working as designed. Each compilation error is a location where the new subtype must be handled. Systematically fix each one:
1. Add the subtype to the `permits` clause
2. Create the subtype class
3. Fix each exhaustiveness error (one per switch expression)

### Scenario: Third-Party Code Extends a Sealed Class (Impossible)
If someone claims a library is extending a sealed class, they must be using bytecode manipulation. Verify with:
```bash
javap -p -c MyClass.class
```
Look for the `ACC_SEALED` flag or `PermittedSubclasses` attribute in the parent class. If it's sealed and they've loaded a subclass not in the permits list, the JVM throws `VerifyError`.

### Scenario: Sealed Class in a Library Causes Upgrade Issues
When updating a library where a sealed class adds a new permitted subtype, all code that switches exhaustively over that type must be updated. This is intentional and documented in the library's migration guide.
