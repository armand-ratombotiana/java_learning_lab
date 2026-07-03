# Interview Questions: Records

## Beginner Questions

### Q1: What are records in Java and why were they introduced?
Records are a special kind of class introduced in Java 16 that serve as transparent carriers for immutable data. They were introduced to eliminate the boilerplate code required for simple data classes — constructors, getters, equals, hashCode, and toString. A record declaration like `record Point(int x, int y) {}` replaces ~30 lines of hand-written code. Records also improve security through guaranteed deserialization through the canonical constructor.

### Q2: How do you create a record with validation?
Validation is added via a compact constructor: `record Range(int start, int end) { Range { if (start > end) throw new IllegalArgumentException(); } }`. The compact constructor body runs before the implicit field assignments, ensuring validation happens on all construction paths, including deserialization.

### Q3: Can records inherit from other classes?
No, records implicitly extend `java.lang.Record` and cannot extend any other class. Java does not support multiple inheritance of state, and records must be `final`. However, records can implement interfaces.

### Q4: What is the difference between a record and a class?
Records are a concise syntactic form for immutable data carriers. A record's state is entirely defined by its components, and the API (constructor, accessors, equals, hashCode, toString) is derived automatically. Records are shallowly immutable and value-based (equality based on component values). Classes can be mutable or immutable, can encapsulate state behind an API, and typically use identity-based equality unless explicitly overridden.

## Intermediate Questions

### Q5: How do records handle serialization differently from regular classes?
Records have special serialization rules. Deserialization always invokes the canonical constructor with the serialized component values. This means validation logic in compact constructors is automatically enforced during deserialization. Custom `readObject()`, `writeObject()`, `readResolve()`, and `writeReplace()` methods are ignored. This design prevents serialization attacks that exploit object state corruption.

### Q6: How would you create a builder pattern for a record with many fields?
Since records cannot have mutable state, the builder must be a separate (static nested) class. The builder accumulates state in mutable fields and calls the record's canonical constructor in its `build()` method. This pattern provides both the immutability guarantee of records and the convenience of a builder for records with many fields or optional components.

### Q7: How do records work with pattern matching in Java 21?
Record patterns allow destructuring records directly in pattern matching: `if (obj instanceof Point(int x, int y))`. This combines type checking, type casting, and component extraction into a single step. Record patterns can be nested: `instanceof Line(Point(int x1, int y1), Point(int x2, int y2))`. This enables algebraic data type style programming when combined with sealed classes.

### Q8: What are the limitations of records regarding reflection-based frameworks?
Records don't follow the JavaBeans convention (they have no no-arg constructor, no setters, and accessors are named `component()` not `getComponent()`). Older versions of frameworks like Jackson, Spring Data, and MyBatis that rely on JavaBeans introspection may not work with records out of the box. Jackson 2.12+ and Spring Data 2021.0+ have added explicit record support.

## Advanced Questions

### Q9: Explain how the JIT compiler optimizes records differently from regular classes.
The JIT optimizes records similarly to hand-written classes with final fields. However, records' simplicity enables more aggressive optimizations: (1) Accessor methods are trivially inlined since they're simple field reads. (2) Escape analysis can scalarize records more aggressively because they cannot have subclasses and their state is fully visible. (3) The JIT can reason about record components being effectively final, enabling constant folding when records are created with constant values.

### Q10: How would you design a type-safe heterogeneous container using records?
A type-safe heterogeneous container uses type tokens as keys. Records can serve as composite keys: `record Key<T>(Class<T> type, String name) {}`. The container stores values typed by their key: `Key<String> nameKey = new Key<>(String.class, "name"); container.put(nameKey, "Alice"); String name = container.get(nameKey);`. This pattern combines records' value semantics with generics for type safety.

### Q11: How do records interact with inheritance in the context of ORMs?
Records are incompatible with JPA-style ORMs because: (1) Records are final and cannot be proxied for lazy loading. (2) Records have no no-arg constructor, which JPA requires. (3) Records have no setters, so JPA cannot populate fields during hydration. (4) Records' shallow immutability conflicts with JPA's dirty checking mechanism. The recommended pattern is to use records for DTOs (query results) and regular classes for entities.

### Q12: Compare records with Kotlin's data classes and Scala's case classes.
All three provide concise syntax for immutable data carriers with automatically derived methods. Differences: (1) Java records cannot inherit from other classes (Scala and Kotlin allow this). (2) Java records are shallowly immutable with no option for mutable components (Kotlin allows `var` components). (3) Kotlin data classes provide `copy()` method; Java records require explicit `with*` methods. (4) Scala case classes have pattern matching support built into the language; Java records achieve this through record patterns. (5) Java records' serialization security (mandatory canonical constructor) is unique.
