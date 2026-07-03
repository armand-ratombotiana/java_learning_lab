# Java Syntax — Flashcards

## Card 1
**Q:** What are the three types of comments in Java?
**A:** Single-line (`//`), multi-line (`/* */`), and Javadoc (`/** */`).

## Card 2
**Q:** What is the correct signature of the `main` method?
**A:** `public static void main(String[] args)` or `public static void main(String... args)`.

## Card 3
**Q:** What naming convention is used for Java class names?
**A:** PascalCase (e.g., `ArrayList`, `HttpServlet`).

## Card 4
**Q:** What naming convention is used for Java constants?
**A:** UPPER_SNAKE_CASE (e.g., `MAX_VALUE`, `PI`).

## Card 5
**Q:** What is the difference between `=` and `==` in Java?
**A:** `=` is assignment; `==` is equality comparison.

## Card 6
**Q:** Can a Java source file have more than one public class?
**A:** No — at most one public class per `.java` file.

## Card 7
**Q:** What does the `++` operator do? What is the difference between prefix and postfix?
**A:** Increments by 1. `++x` increments then returns; `x++` returns then increments.

## Card 8
**Q:** What is operator precedence and why does it matter?
**A:** It determines evaluation order. `*` and `/` before `+` and `-`. Use `()` to override.

## Card 9
**Q:** What are the valid bases for integer literals?
**A:** Decimal (none), Hex (`0x`), Octal (`0`), Binary (`0b`).

## Card 10
**Q:** What is the purpose of `import` statements?
**A:** They bring in classes from other packages so you can use short names instead of fully qualified names.

## Card 11
**Q:** What does `var` do in Java?
**A:** Local variable type inference — compiler infers the type from the initializer.

## Card 12
**Q:** What is a Javadoc comment used for?
**A:** Generating HTML API documentation from source code.

## Card 13
**Q:** What is the default access modifier if none is specified?
**A:** Package-private (accessible only within the same package).

## Card 14
**Q:** What is the difference between `&&` and `&`?
**A:** `&&` is short-circuit AND (stops if left is false); `&` is non-short-circuit (evaluates both sides).

## Card 15
**Q:** Can a method be declared inside another method in Java?
**A:** No — methods can only be declared inside classes (but can contain local classes or lambdas).

## Card 16
**Q:** What does `>>>` do differently from `>>`?
**A:** `>>>` is unsigned right shift (fills with 0); `>>` is signed right shift (fills with sign bit).

## Card 17
**Q:** What is the ternary operator and how is it used?
**A:** `condition ? valueIfTrue : valueIfFalse`. A compact if-else expression.

## Card 18
**Q:** What is a text block in Java?
**A:** A multi-line string literal enclosed in `""" """` (Java 15+). Preserves formatting.

## Card 19
**Q:** What is a reserved keyword?
**A:** A word that has special meaning in Java and cannot be used as an identifier. There are 49 reserved keywords (plus `goto` and `const` which are reserved but unused).

## Card 20
**Q:** What is the short-circuit evaluation?
**A:** For `&&`, if left operand is `false`, right is not evaluated. For `||`, if left is `true`, right is not evaluated. Prevents unnecessary computation and NullPointerException.

## Card 21
**Q:** What is the result of `System.out.println(10 / 3)`?
**A:** `3` — integer division truncates the fractional part.

## Card 22
**Q:** How do you write the value 1 million as an underscore literal?
**A:** `1_000_000` (underscores between digits for readability).

## Card 23
**Q:** What is a compound assignment operator?
**A:** Operators like `+=`, `-=`, `*=`, `/=`, `%=` that combine operation with assignment. `x += 5` is equivalent to `x = x + 5`.

## Card 24
**Q:** Can a Java file have no package declaration?
**A:** Yes — the class goes in the default (unnamed) package. This is fine for small examples but bad practice for real projects.
