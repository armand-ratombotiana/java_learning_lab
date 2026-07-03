# Common Mistakes — Inheritance

1. **Not calling super constructor**: If super has no no-arg constructor, compilation error.
2. **Calling overridable method from constructor**: Subclass override may use uninitialized state.
3. **Forgetting `@Override`**: Typo like `equals(Object o)` instead `equals(MyType o)` — overloads instead of overrides.
4. **Breaking equals contract**: Overriding equals without hashCode violates contract.
5. **Deep inheritance hierarchies**: 5+ levels become fragile and hard to understand.
6. **Using inheritance for code reuse only**: Prefer composition over inheritance. Inheritance implies IS-A.
7. **Final class misuse**: Making everything final prevents testing (mocking), but making nothing final allows fragile subclasses.
8. **Downcasting unnecessarily**: `((Dog) animal).bark()` instead of using polymorphic method.
9. **Protected field overuse**: Exposes internal state to subclasses, breaking encapsulation.
10. **Constructor calling super at wrong time**: `super()` must be first statement.
11. **Overriding private methods**: Private methods are not inherited — they can't be overridden.
12. **Exception in override**: Override can't throw broader checked exceptions than parent.
