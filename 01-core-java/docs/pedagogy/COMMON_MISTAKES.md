# Common Mistakes in Core Java

## 1. Confusing == with .equals()

- `==` compares references (memory addresses)
- `.equals()` compares object values
- Use `.equals()` for String comparison
- Use `==` for enum comparison and interned strings

## 2. Ignoring NullPointerException

- Always check for null before method calls
- Use Optional for return types
- Initialize collections as empty rather than null

## 3. Mutable Public Fields

- Always encapsulate fields with private access
- Provide getters and setters when needed
- Prefer immutable objects where possible

## 4. Not Understanding autoboxing

- Boxing/unboxing has performance cost
- Can cause subtle bugs with == comparison
- Be careful in loops and collections

## 5. Ignoring Resource Leaks

- Always close streams, connections, readers
- Use try-with-resources for automatic cleanup
- Don't suppress exceptions that indicate failures