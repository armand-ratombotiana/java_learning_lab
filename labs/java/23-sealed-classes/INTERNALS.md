# Internal Implementation of Sealed Classes

## Class File Attributes

Sealed classes are represented in the class file using the `PermittedSubclasses` attribute:

```
PermittedSubclasses_attribute {
    u2 attribute_name_index;
    u4 attribute_length;
    u2 number_of_classes;
    u2 classes[number_of_classes];
}
```

Each entry in the `classes` array is an index into the constant pool pointing to a `CONSTANT_Class_info` entry representing a permitted subclass.

The JVM uses this attribute to:
1. Verify that all permitted subclasses directly extend the sealed class
2. Verify that no other class extends the sealed class
3. Provide runtime reflection via `Class.getPermittedSubclasses()`

## Sealed Flag in Class Access Flags

The class file format includes an `ACC_SEALED` flag (0x0400) in the `access_flags` field. The JVM specification requires:

```
If ACC_SEALED is set for a class:
  - The class file must have a PermittedSubclasses attribute
  - Each class named in PermittedSubclasses must directly extend this class
  - No other class may directly extend this class
```

## Verification During Class Loading

When the JVM loads a class that extends a sealed class:

1. The class loader checks if the parent class has `ACC_SEALED` set
2. If yes, the loader verifies that the current class is listed in the parent's `PermittedSubclasses` attribute
3. If not found, the JVM throws a `VerifyError`
4. If found, loading proceeds normally

This verification happens at class loading time, not compile time, which means:
- Bytecode manipulation tools (e.g., byte-buddy, cglib) cannot bypass sealed restrictions
- Runtime code generation (e.g., proxies) respects sealed hierarchies
- Different class loaders enforce sealed separately (but within a module, there's typically one class loader)

## Compiler Implementation

### Type Checking
The compiler maintains a set of permitted subtypes for each sealed type. During type checking:

1. When a class `C` extends a sealed type `S`, the compiler checks if `C` is in `S`'s permitted list
2. If not, it reports a compilation error
3. The compiler also checks that `C` is `final`, `sealed`, or `non-sealed`

### Exhaustiveness Analysis
For pattern matching exhaustiveness:

1. The compiler collects all permitted subtypes of the sealed type
2. For each permitted subtype, it recursively collects its permitted subtypes (if the subtype is `sealed`)
3. This creates a complete set of "leaf" subtypes (all subtypes that are `final` or `non-sealed`)
4. The switch expression's cases are checked against this leaf set
5. If any leaf type is missing, the switch is marked as non-exhaustive

```java
// Leaf set for sealed type:
// A permits B, C
// B is sealed → permits D, E
// C is final
// D is final
// E is final
// Leaf set: {C, D, E}

// A switch over A must cover C, D, and E (or have a default)
```

## Runtime Optimization

The JIT compiler can use sealed class information to optimize virtual dispatch:

- **Monomorphic dispatch**: If a sealed type has a single concrete subclass at runtime, the JIT can inline and devirtualize method calls
- **Bimorphic/megamorphic dispatch**: For sealed types with few subtypes, the JIT can generate inline caches or type-check cascades
- **Shape analysis**: The closed type set enables better escape analysis and scalar replacement

## Interaction with the Module System

In the module system, sealed classes interact with modules:

1. All permitted subtypes must be in the same module
2. If a sealed class is exported from a module, external code can see the class but cannot extend it
3. The `exports` directive in `module-info.java` controls visibility of the sealed type, while the `permits` clause controls extensibility

## Bytecode Example

A sealed class like:
```java
public sealed class Shape permits Circle, Rectangle {}
```

Generates bytecode containing:
```
Class: Shape
  flags: ACC_PUBLIC, ACC_ABSTRACT, ACC_SEALED
  
  PermittedSubclasses_attribute:
    Circle
    Rectangle
```

The `Circle` class:
```java
public final class Circle extends Shape {}
```
Has no special flags related to sealing, but the JVM verifies it's in `Shape`'s permitted list during loading.
