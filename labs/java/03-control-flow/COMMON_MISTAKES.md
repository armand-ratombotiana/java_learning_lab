# Common Mistakes — Control Flow

1. **Assignment in condition**: `if (x = 5)` — compile error in Java (unlike C/C++). Must be `==`.
2. **Off-by-one**: `for (int i = 0; i <= arr.length; i++)` — accesses past end.
3. **Missing break in switch**: Falls through to next case. Use `break` or switch expression (Java 14+).
4. **Empty loop body**: `for (int i = 0; i < 10; i++);` — semicolon ends the loop. Body runs once.
5. **Infinite loop**: `while (true)` or `for (;;)` without exit condition.
6. **Using = instead of ==**: Already mentioned, but the most common bug.
7. **Loop variable modification**: Changing loop variable inside body causes confusing behavior.
8. **Comparing strings with ==**: `if (s == "hello")` compares references. Use `.equals()`.
9. **Floating-point in loop condition**: `for (double d = 0; d != 1.0; d += 0.1)` — may never end due to precision.
10. **Dangling else**: Without braces, `else` matches the nearest `if`.
11. **Missing braces**: `if (x > 0) doThis(); doThat();` — only doThis is conditional.
12. **continue in switch fallthrough**: `continue` inside switch inside loop continues the loop, not the switch.
