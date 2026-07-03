# Common Mistakes — Methods

1. **Return type mismatch**: Declaring method returns `int` but returning `String`.
2. **Missing return**: Not all code paths return a value. Compiler catches this.
3. **Modifying parameters**: `void method(int x) { x = 10; }` — caller's variable unchanged. Java is pass-by-value.
4. **Varargs as first parameter**: `void method(int... nums, String s)` — error. Varargs must be last.
5. **Multiple varargs**: `void method(String... a, int... b)` — error. Only one varargs allowed.
6. **Name shadowing**: Parameter name same as field: `name = name` — doesn't work. Use `this.name = name`.
7. **Relying on return from finally**: `return` in finally overrides try/catch return.
8. **Swallowing exceptions**: Empty catch block that ignores the exception.
9. **Deep recursion**: No base case or too many recursive calls → StackOverflowError.
10. **Confusing overloading with overriding**: Same name, different params = overload (compile-time). Same signature = override (runtime).
11. **Too many parameters**: 7+ parameters indicates poor design. Use a parameter object.
12. **Side effects in assertions**: `assert list.remove(x)` — assertions can be disabled.
