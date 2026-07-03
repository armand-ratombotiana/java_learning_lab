# Data Types — Exercises

## Exercise 1: All Primitive Sizes
Write a program that prints the size (in bits) and range of each primitive type using their wrapper classes.

## Exercise 2: Overflow Detection
Write a method `safeAdd(int a, int b)` that detects overflow and throws `ArithmeticException`.

## Exercise 3: Money Calculator
Calculate compound interest: `A = P(1 + r/n)^(nt)`. Use BigDecimal for precision.

## Exercise 4: Autoboxing Benchmark
Compare the speed of summing 10 million integers using `int` vs `Integer`.

## Exercise 5: Type Conversion Matrix
Write a program that shows which numeric types convert implicitly to which others.

## Exercise 6: Wrapper Utility Explorer
Use each wrapper class's utility methods: `parseXxx()`, `toString()`, `compare()`, `valueOf()`.

## Exercise 7: String to Primitive
Parse command-line args into different primitive types and perform calculations.

## Exercise 8: Number Base Converter
Convert between decimal, binary, hex, and octal using `Integer.toString(n, radix)` and `Integer.parseInt(s, radix)`.

## Exercise 9: IEEE 754 Exploration
Explore special double values: NaN, Infinity, -0.0. Show how they arise and how to test for them.

## Exercise 10: Numeric Literal Format
Write a program that uses all literal formats (decimal, hex, binary, octal, underscores, scientific notation).

## Exercise 11: Cache Range Exploration
Find the Integer cache upper limit by testing `==` equality on consecutive values.

## Exercise 12: Type Promotion Puzzle
Given `byte b = 5; short s = 10; char c = 'A';` show the result type of `b + s + c`.
