# Why Control Flow Exists

## The Problem Control Flow Solves

Without control flow, programs execute top-to-bottom — every statement runs exactly once, in order. This is insufficient for any nontrivial program. Decision-making and repetition are fundamental to computation.

## Historical Context

The GOTO statement dominated early programming (FORTRAN, COBOL, early BASIC). Unrestricted GOTOs created "spaghetti code" — programs whose control flow was impossible to follow. Edsger Dijkstra's 1968 paper "Go To Statement Considered Harmful" sparked a revolution toward structured programming.

Java omits GOTO entirely (though `goto` is a reserved keyword). Instead it provides structured control flow:
- **Sequential**: Default execution order
- **Selection**: `if-else`, `switch` — choose among paths
- **Iteration**: `for`, `while`, `do-while` — repeat blocks

This design follows the structured programming theorem: any computable function can be expressed using sequence, selection, and iteration alone. Java's `break` and `continue` provide limited, structured exit from these constructs — controlled exceptions to pure structure.
