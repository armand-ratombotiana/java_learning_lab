# Common Mistakes — Polymorphism

1. **Confusing overloading with overriding**: Overloading = same name, different params (compile-time). Overriding = same sig (runtime).
2. **Casting unnecessarily**: `((Dog) animal).bark()` when animal might be Cat → ClassCastException.
3. **Not using `@Override`**: Misses compiler check — signature mismatch creates overload, not override.
4. **Static method "overriding"**: Static methods are hidden, not overridden. Parent's version called if reference type is parent.
5. **Calling overloaded method with null**: `method(null)` when overloaded with `Object` and `String` — ambiguous.
6. **Forgetting covariant return**: Could simplify code by returning subtype.
7. **instanceof without pattern matching**: Verbose cast after instanceof check (pre-Java 16).
8. **Breaking Liskov Substitution**: Subclass weakens parent's guarantees (e.g., throws new exceptions).
9. **Mutable objects in polymorphic collections**: All references share same mutable state.
10. **Overriding equals with wrong type**: `equals(MyClass other)` doesn't override Object.equals(Object).
11. **Instance variables and overriding**: Instance variables don't override — they hide. Methods are polymorphic, fields are not.
12. **Final method thinking it's virtual**: Final methods can't be overridden at all.
