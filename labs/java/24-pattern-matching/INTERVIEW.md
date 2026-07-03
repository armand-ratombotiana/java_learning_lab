# Interview Questions: Pattern Matching

## Beginner Questions

### Q1: What is pattern matching in Java and how does it improve code?
Pattern matching allows testing whether a value has a certain shape or type and extracting data from it in one step. Examples include `instanceof` pattern matching (`if (obj instanceof String s)`) and switch type patterns (`case Integer i -> ...`). It improves code by eliminating manual casts (reducing ClassCastException risk), reducing boilerplate, and enabling exhaustive compile-time checking when combined with sealed classes.

### Q2: What is the difference between traditional switch and pattern matching switch?
Traditional switch matches on constant values (int, String, enum) using equality. Pattern matching switch matches on types, record structures, and conditions. Pattern matching switch also requires arrow syntax (`->`), supports guarded patterns with `when`, and can be exhaustive for sealed types.

### Q3: How do you handle null in a pattern matching switch?
Null must be handled with an explicit `case null`. Without it, the switch throws NullPointerException at runtime. The `default` case does NOT catch null in pattern matching — only `case null` does.

## Intermediate Questions

### Q4: Explain how record patterns work with nested data structures.
Record patterns deconstruct records into their components. Nesting allows matching on the entire structure at once. For example, `Line(Point(int x1, int y1), Point(int x2, int y2))` matches a Line containing two Points, extracting all four coordinates. The compiler generates accessor calls for each level.

### Q5: How does the compiler verify exhaustiveness of a switch over a sealed type?
The compiler collects all leaf subtypes of the sealed hierarchy (final or non-sealed types that aren't further restricted). It then checks that each leaf type has a corresponding case in the switch. If a subtype is missing, the switch is marked as non-exhaustive. For non-sealed types within the hierarchy, the compiler falls back to requiring a default case or a pattern covering the non-sealed type.

### Q6: What happens when a pattern is dominated by a previous pattern?
The compiler rejects it with an error. For example, if `case Object o` appears before `case String s`, the String case is unreachable because Object matches everything String matches and more. Patterns must be ordered from most specific (with guards) to most general.

## Advanced Questions

### Q7: How does the JIT compiler optimize pattern matching for sealed types?
The JIT can use sealed type information to generate efficient dispatch code. Instead of a chain of instanceof checks, the JIT can assign an internal type ID to each sealed subtype and use a tableswitch/lookupswitch instruction. This makes dispatch O(1). The JIT can also devirtualize method calls within sealed hierarchies more aggressively.

### Q8: Design a JSON processing library using pattern matching. What are the key design decisions?
The key design is a sealed interface `Json` with record implementations for each JSON type (Null, Bool, Number, String, Array, Object). Pattern matching enables exhaustive processing: serialization, deserialization, querying, and transformation are all implemented via switch expressions. Defensive copying is needed for mutable components (arrays, objects). The sealed hierarchy ensures that adding a new JSON type requires updating all processors.

### Q9: Compare Java's pattern matching with Scala's pattern matching. What are the tradeoffs?
Java's pattern matching is more limited but safer. Java doesn't support custom extractors (unapply in Scala) or sequence patterns. Java requires records and sealed classes for deconstruction; Scala works with case classes. Java's pattern variables have well-defined scope rules; Scala's bindings are more flexible. Java exhaustiveness is verified via sealed types; Scala's sealed trait works similarly. Java's approach is more verbose but integrates with existing Java idioms.

### Q10: How would you refactor a large codebase using the Visitor pattern to use pattern matching?
Identify each Visitor interface and its implementations. Replace the visited types with a sealed hierarchy (using records for the data types). Replace the accept/visit boilerplate with a single switch expression over the sealed type. Each visit method becomes a case in the switch. If the visitor has multiple methods, create separate switch expressions. Add `default` cases only if the hierarchy uses `non-sealed` types. The result is less code, no boilerplate accept methods, and compile-time exhaustiveness.

### Q11: What are the performance implications of deeply nested record patterns?
Each nesting level adds an instanceof check and accessor call. These are JIT-inlined after warmup, so the cost is minimal for typical use. For extremely deep nesting (e.g., 10+ levels), consider extracting intermediate variables to improve readability and potentially aid JIT optimization. Microbenchmarks show record pattern deconstruction is as fast as manual accessor chains.

### Q12: How does Java handle type erasure with generic records in pattern matching?
Generic records like `record Box<T>(T value) {}` require the type parameter to be matched with a type pattern or var: `case Box(var v) -> ...` or `case Box(String s) -> ...`. The compiler uses erasure for the type check and inserts appropriate casts. For example, `case Box(String s)` generates `obj instanceof Box` check then casts the value to String. This works correctly but cannot distinguish between `Box<String>` and `Box<Integer>` at runtime due to erasure.
