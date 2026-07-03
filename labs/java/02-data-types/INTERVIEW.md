# Data Types — Interview Questions

1. **Q: How are primitive types stored in memory?** A: Primitive local variables are stored on the stack. Primitive fields are stored inline in the object on the heap. Primitives are values, not references.

2. **Q: What is autoboxing and what are its pitfalls?** A: Automatic conversion between primitives and wrappers. Pitfalls: null unboxing (NPE), == comparison (cached range), performance overhead in loops.

3. **Q: How does the Integer cache work?** A: Integer caches -128 to 127. `valueOf()` returns cached instances in this range. Configure upper bound with `-XX:AutoBoxCacheMax=size`.

4. **Q: Why is 0.1 + 0.2 != 0.3 in Java?** A: Floating-point IEEE 754 representation. 0.1 cannot be represented exactly in binary. Use BigDecimal for exact decimal arithmetic.

5. **Q: What is the difference between float and double?** A: float = 32 bits, ~7 decimal digits precision. double = 64 bits, ~15 decimal digits precision. double is default for floating-point literals.

6. **Q: What is NaN and how do you check for it?** A: Not-a-Number (0.0/0.0). Use `Double.isNaN(x)`. NaN != NaN (always false).

7. **Q: What happens with integer overflow?** A: Wraps around silently (unlike C/C++ which has undefined behavior). Use `Math.addExact()`, `Math.multiplyExact()` to detect overflow.

8. **Q: What is the difference between `int` and `Integer`?** A: int = primitive (value). Integer = wrapper object (reference). Integer can be null, needed for generics. int is more memory-efficient.

9. **Q: Why use BigDecimal instead of double for money?** A: double has rounding errors (0.1 + 0.2 != 0.3). BigDecimal provides exact decimal arithmetic with configurable rounding.

10. **Q: What is `var` and when should you use it?** A: Local variable type inference (Java 10+). Use when type is obvious: `var list = new ArrayList<String>();`. Not for method params, fields, or return types.

11. **Q: Can you explain Java's type promotion rules?** A: Binary numeric promotion: if either operand is double → double, else float → float, else long → long, else int.

12. **Q: What is a widening vs narrowing conversion?** A: Widening = smaller type to larger (implicit, safe). Narrowing = larger to smaller (explicit cast, possible data loss).
