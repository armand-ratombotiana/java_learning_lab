# Debugging Polymorphism

## Unexpected Method Called

When polymorphic dispatch surprises you:
1. Check the object's actual runtime type (use debugger or `getClass()`)
2. Check if method is `static` — static methods are not polymorphic
3. Check if method is `final` — not overridable
4. Check overload vs override confusion — different parameter types = overload
5. Verify the method signature matches exactly (use `@Override`)

## ClassCastException in Polymorphic Code

1. Always check `instanceof` before downcasting
2. Use pattern matching (Java 16+) to avoid separate cast
3. Check if collection contains mixed types
4. Raw types bypass generic type checking at runtime

## Null in Polymorphic Collections

When a polymorphic collection contains null:
1. Add null check before calling methods
2. Use `Objects.requireNonNull()` to fail fast
3. Filter nulls: `list.stream().filter(Objects::nonNull)...`

## Debugging Overload Resolution

When the wrong overload is called:
1. Check parameter types (widening, autoboxing, varargs priority)
2. Temporarily add `System.out.println("called: " + param)` in each overload
3. Use IDE — Ctrl+Click on method call shows which overload is resolved
4. Check for ambiguity — `method(null)` with two applicable overloads
