# Control Flow — Interview Questions

1. **Q: What is short-circuit evaluation?** A: `&&` and `||` evaluate right operand only when needed. Prevents unnecessary computation and NullPointerException: `obj != null && obj.isValid()`.

2. **Q: How does a switch on String work internally?** A: Compiler generates two-level switch: hash code switch, then equals verification. Avoids O(n) string comparison.

3. **Q: What is the difference between for and for-each?** A: for-each is cleaner for arrays/Iterables. For arrays, both compile to same bytecode. For Iterables, for-each uses Iterator.

4. **Q: Can you use break/continue with labels?** A: Yes: `outer: for (...) { for (...) { if (...) break outer; } }`. Exits/continues the labeled loop.

5. **Q: What is the difference between while and do-while?** A: while checks condition before body (0+ iterations). do-while checks after body (1+ iterations).

6. **Q: What is a switch expression and how is it different?** A: Java 14+: `int x = switch(v) { case 1 -> 10; default -> 0; };`. No fall-through, must be exhaustive, can be assigned.

7. **Q: How does the compiler detect unreachable code?** A: Flow analysis: after break/continue/return/throw, next statement is unreachable. `if (false) { }` body is also unreachable.

8. **Q: What is the enhanced for-loop limitation?** A: Cannot modify the underlying collection during iteration (ConcurrentModificationException). Cannot access index.

9. **Q: What is loop invariant code motion?** A: Compiler moves loop-invariant computations outside the loop. `for (int i = 0; i < list.size(); i++)` — size() can be hoisted.

10. **Q: Which is faster: if-else or switch?** A: switch with dense int values uses tableswitch (O(1)). if-else is O(n) in worst case. For small numbers, difference is negligible.

11. **Q: What is the ternary operator?** A: `condition ? valueIfTrue : valueIfFalse`. Compact if-else expression. Overuse hurts readability.

12. **Q: Can you use a switch on a long?** A: No. Switch supports byte, short, char, int, String, enum. Not long, float, double, boolean.
