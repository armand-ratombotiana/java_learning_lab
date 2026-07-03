# Common Mistakes — Abstraction & Interfaces

1. **Choosing abstract class over interface**: Use interface for contracts/capabilities. Use abstract class for shared state + behavior.
2. **Default method diamond conflict**: Class implementing two interfaces with same default method must override.
3. **Forgetting `@Override` on interface method implementation**: Optional but recommended.
4. **Abstract class with all abstract methods**: Should be an interface.
5. **Functional interface with multiple abstract methods**: Won't compile with `@FunctionalInterface`.
6. **Not using default methods for API evolution**: Adding abstract method to existing interface breaks all implementations.
7. **Interface with no abstract methods**: Marker interfaces (Serializable) — consider annotations instead.
8. **Overusing default methods**: Complex default method logic defeats purpose of abstraction.
9. **Calling static interface method on instance**: `myImpl.staticMethod()` — error. Use `Interface.staticMethod()`.
10. **Private interface methods not accessible to default methods**: Actually they are — that's their purpose.
11. **Returning concrete types from interface methods**: Defeats abstraction. Return interface types.
12. **Throwing RuntimeExceptions from interface methods**: Not declared in contract — implementer should document.
