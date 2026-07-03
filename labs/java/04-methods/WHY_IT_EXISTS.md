# Why Methods Exist

## The Problem Methods Solve

Without methods, all code for a program must be written in a single block — no reuse, no decomposition, no abstraction. A calculator program would be one massive `main` method. Methods enable:
- **Decomposition**: Break complex problems into smaller subproblems
- **Reuse**: Write once, call many times
- **Abstraction**: Call a method without knowing its implementation
- **Testing**: Test small units independently

## Historical Context

Subroutines existed in early assembly languages. FORTRAN introduced `SUBROUTINE` and `FUNCTION`. C distinguished functions (return a value) from procedures (void). Java unified these as methods.

Java's method design emphasized:
- **Type safety**: Return types and parameter types are declared and checked
- **Access control**: Methods can be private, protected, public — controlling who can call them
- **Overloading**: Same name for related operations with different parameters
- **Recursion**: Methods can call themselves (stack-supported)

The `main` method as entry point followed C's convention but used a cleaner signature. Method overloading provided a solution to the "what if I need multiple constructors/operations" problem that C handled with differently-named functions.
