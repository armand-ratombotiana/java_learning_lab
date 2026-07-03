# Debugging Inheritance

## Wrong Method Called

When the wrong version of an overridden method executes:
1. Check the object's actual runtime type (not reference type)
2. Use debugger: "Evaluate Expression" with `obj.getClass().getName()`
3. Check if the method is `static` — static methods don't override
4. Check if the method is `final` — cannot override
5. Check if `@Override` is present — missing it means accidental overload

## Super Not Called

When superclass behavior is missing:
1. Check if overridden method calls `super.methodName()`
2. Some patterns require super call (e.g., `Activity.onCreate()` in Android)
3. Constructor must call `super()` (implicitly or explicitly)

## ClassCastException

When casting fails:
1. Check inheritance hierarchy with `instanceof` before casting
2. Use pattern matching: `if (obj instanceof Dog dog) { dog.bark(); }`
3. Check if the object was created as a different type
4. Collections: verify generic type parameters

## equals/hashCode Issues

When object comparison fails in collections:
1. Check that both equals and hashCode are overridden (contract)
2. Check that equals handles null and different types
3. Use `Objects.equals()` and `Objects.hash()` for safe implementation
4. Verify that equals is consistent: `a.equals(b) == b.equals(a)`
