# Math Foundation for Java Syntax

No specific math foundation is required to understand Java syntax. The syntactic rules of Java are based on formal language theory concepts that are a prerequisite for computer science, but the practical usage requires only basic arithmetic.

## Rudimentary Concepts That Help

### Operator Precedence (Arithmetic)

Understanding standard arithmetic precedence (`*` and `/` before `+` and `-`) is necessary. Java follows these conventions exactly:

```java
int result = 5 + 3 * 2;   // 11, not 16
int result2 = (5 + 3) * 2; // 16 — parentheses override
```

### Number Systems

Java supports integer literals in four bases:
- Decimal (base 10): `42`
- Hexadecimal (base 16): `0x2A`
- Octal (base 8): `052` (prefix 0)
- Binary (base 2): `0b101010`

Understanding these number systems helps when working with bitwise operators (`&`, `|`, `^`, `<<`, `>>`).

### Boolean Algebra

Logical operators (`&&`, `||`, `!`) follow Boolean algebra. The truth tables are:

```
A     B     A && B    A || B    !A
true  true  true      true      false
true  false false     true      false
false true  false     true      true
false false false     false     true
```

Short-circuit evaluation (`&&` and `||` evaluate right side only if needed) is based on these logical identities.

### No Advanced Math Required

Java syntax does not require calculus, linear algebra, statistics, or discrete mathematics beyond basic Boolean logic. If you can perform elementary arithmetic and understand true/false logic, you have sufficient math foundation for this lab.
