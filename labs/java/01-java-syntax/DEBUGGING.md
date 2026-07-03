# Debugging Java Syntax Errors

## Understanding Compiler Error Messages

Java compiler errors follow a consistent pattern:

```
<filename>:<line>: <error type>: <message>
<code line>
<caret position>
```

Example:
```
Test.java:5: error: ';' expected
        int x = 5
                  ^
```

The caret (`^`) points to the exact position the compiler detected the problem. However, the *actual* mistake might be earlier — the compiler reports where it *realized* something was wrong.

## Common Error Types and Strategies

### "cannot find symbol"

```
Test.java:3: error: cannot find symbol
  symbol:   variable age
  location: class Test
```

**Strategies:**
1. Check spelling (Java is case-sensitive)
2. Check if the variable is in scope
3. Check if the class/method is imported
4. Check if you're accessing a static member via instance (or vice versa)

### "expected"

```
Test.java:2: error: ';' expected
```

**Strategies:**
1. Check the line before the caret — missing `;` is most common
2. Check for unclosed braces, parentheses, or brackets
3. Check for missing `{` after class/method/if/loop declarations

### "incompatible types"

```
Test.java:3: error: incompatible types: possible lossy conversion from double to int
```

**Strategies:**
1. Add explicit cast: `int x = (int) 5.5;`
2. Change the variable type to match the expression
3. Check for integer division when float/double expected

### "illegal start of expression"

```
Test.java:3: error: illegal start of expression
```

This often means the compiler encountered something unexpected. Check for:
- Misplaced operators (`==` used where `=` belongs, or vice versa)
- Extra/missing parentheses
- Method declaration inside another method

## Using IDE Tools

### IntelliJ IDEA
- **Red squiggly** = compilation error (hover for explanation)
- **Yellow squiggly** = warning (hover for suggestion)
- **Alt+Enter** = Show quick fix options
- **Ctrl+Shift+F7** = Highlight usages of symbol in file

### VS Code
- **Red squiggly** = error (hover for details)
- **Lightbulb** = Quick fix suggestions
- **Problems panel** (Ctrl+Shift+M) = All errors and warnings

### Eclipse
- **Red mark** in gutter = error
- **Ctrl+1** = Quick fix

## Debugging with `javap`

When you need to understand how the compiler interpreted your code:

```bash
javap -c -p ClassName.class
```

Shows the bytecode, revealing how syntax was translated. Useful for:
- Verifying that switch expressions are correctly compiled
- Checking that enhanced for-loops use iterators
- Seeing how lambdas are invoked via `invokedynamic`

## Debugging with `-Xlint`

Enable more detailed compiler warnings:

```bash
javac -Xlint:all Test.java
```

Flags include:
- `-Xlint:fallthrough` — Warns about switch fall-through
- `-Xlint:unchecked` — Warns about unchecked generic operations
- `-Xlint:deprecation` — Warns about deprecated API usage
- `-Xlint:all` — All warnings

## Systematic Debugging Approach

1. **Read the error message** — understand the error type and location
2. **Look at the line indicated** — but also the lines before
3. **Check for missing brackets/parentheses** — most syntax errors are structural
4. **Isolate the problem** — comment out sections until the error disappears
5. **Use IDE formatting** — auto-formatting often reveals brace mismatches
6. **Check imports** — missing imports are a common source of "cannot find symbol"
7. **Verify case sensitivity** — especially `String` vs `string`, `true` vs `True`
8. **Check semicolons** — verify every statement ends with `;`
