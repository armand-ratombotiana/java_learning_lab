# Debugging Data Types

## Type Mismatch Errors

When you see `incompatible types: possible lossy conversion from double to int`:
1. Check if you need an explicit cast: `(int) value`
2. Check variable types — maybe the variable should be `double` not `int`
3. Check expression types — `5 / 2` is `int`, `5.0 / 2` is `double`

## NullPointerException from Unboxing

Stack trace: `NullPointerException at Integer.intValue(...)`
- Look for autoboxing/unboxing with a null wrapper
- Solution: Check for null before unboxing, or use `OptionalInt`

## Integer Cache Confusion

If `a == b` returns unexpected results for `Integer`:
- Values -128 to 127 are cached (same reference)
- Values outside this range are different objects
- Always use `.equals()` for wrapper comparison

## Debug Tools

- Use `javap -c -p` to see bytecode — reveals autoboxing
- IDE debugger: watch variables, set breakpoints on field access
- Add `-ea` VM argument to enable assertions for type checking
- Use `System.identityHashCode(obj)` to check object identity
