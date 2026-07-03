# Debugging Control Flow

## Infinite Loops

If a program hangs:
1. Check loop condition — does it ever become false?
2. Check loop variable update — does it change toward exit condition?
3. Check for `continue` that skips the update statement
4. Use IDE debugger: pause execution to see current line and variable values

## Logic Errors in Conditions

When if-else produces wrong branch:
1. Print or watch the condition expression value
2. Break complex conditions into simpler ones
3. Check precedence: `a || b && c` is `a || (b && c)` not `(a || b) && c`
4. Verify short-circuit behavior: `obj != null && obj.isValid()`

## Switch Fall-Through

Unexpected behavior in switch:
1. Check for missing `break` statements
2. Consider using switch expressions (Java 14+) which don't fall through
3. Use `-Xlint:fallthrough` compiler flag to warn about fall-through

## Debugging Techniques

- Set breakpoints at loop entry and condition
- Use conditional breakpoints: `i == 50` triggers when i reaches 50
- Add `System.out.println("Reached: " + var)` for quick diagnosis
- Use `assert` to validate loop invariants: `assert sum == expected : "Sum mismatch"`
