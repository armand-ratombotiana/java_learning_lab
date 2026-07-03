# Common Mistakes — Data Types

1. **Integer overflow**: `int max = 2_147_483_647; max + 1;` wraps to negative. Use `long` or `Math.addExact()`.
2. **Integer division**: `double d = 5 / 2;` yields 2.0. Use `5.0 / 2`.
3. **Floating-point precision**: `0.1 + 0.2 != 0.3`. Use `BigDecimal` for money.
4. **Comparing wrapper objects with `==`**: `Integer a = 200; Integer b = 200; a == b` is false. Use `.equals()`.
5. **Null wrapper unboxing**: `Integer x = null; int y = x;` throws NullPointerException.
6. **Forgetting L/f suffix**: `long x = 1234567890123;` (compile error without L). `float f = 3.14;` (needs f).
7. **Char vs int confusion**: `'A'` is 65. Mixing char in arithmetic: `'A' + 1` yields 66.
8. **Underscore placement**: `int x = _1000;` (error). Underscores must be between digits: `1_000`.
9. **String concatenation instead of addition**: `"Sum: " + a + b` yields "Sum: ab", not "Sum: a+b".
10. **Narrowing without cast**: `int i = 3.14;` — compile error. Must cast: `(int) 3.14`.
11. **Boolean vs int**: `if (1)` doesn't compile in Java. Must use `if (true)`.
12. **Autoboxing in loops**: `Integer sum = 0; for (int i = 0; i < 100000; i++) sum += i;` — creates 100k Integer objects.
