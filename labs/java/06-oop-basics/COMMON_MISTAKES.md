# Common Mistakes — OOP Basics

1. **Forgetting `this`**: Parameter shadows field: `name = name` assigns parameter to itself. Use `this.name = name`.
2. **No constructor defined but custom init needed**: Default constructor sets fields to defaults. Need explicit if custom init required.
3. **Overloading instead of overriding**: Changing parameter types accidentally when intending to override. Use `@Override`.
4. **Mutable objects from getters**: `return balance` returns reference to internal object. Return defensive copy.
5. **Static method accessing instance field**: `static void method() { this.x = 5; }` — compile error.
6. **Calling instance method from static context**: Need an object reference.
7. **Leaking `this` in constructor**: `new Thread(this).start()` in constructor publishes partially constructed object.
8. **Not calling super in constructor**: If superclass has no no-arg constructor, compile error.
9. **Mutable static fields**: Static fields shared across all instances — thread-unsafe.
10. **Excessive mutability**: Too many setters makes state management hard. Prefer immutability.
11. **Constructor doing too much**: Should only initialize. Complex logic belongs in factory methods.
12. **Protected field access from unrelated class**: Protected allows subclass/package access only.
