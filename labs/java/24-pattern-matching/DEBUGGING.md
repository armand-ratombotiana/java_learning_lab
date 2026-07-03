# Debugging Pattern Matching

## Compiler Errors

### "Pattern type is not a subtype of the selector expression type"
```java
Object obj = "hello";
switch (obj) {
    case StringBuilder sb -> ...  // COMPILER ERROR (but why?)
    // String and StringBuilder are unrelated types
    // The check is valid but the compiler warns if the pattern can never match
}
```

### "This pattern is dominated by a previous pattern"
The pattern ordering is wrong. More specific patterns (guarded, subtypes) must come before more general ones.

### "Switch expression does not cover all possible values"
A sealed type or enum has unhandled cases. Add the missing cases or a default.

## Runtime Errors

### NullPointerException from Switch
If `null` is passed to a switch expression without a `case null`, the JVM throws `NullPointerException`. The stack trace points to the switch expression line.

**Fix**: Add `case null ->` or handle null before the switch.

### MatchException
If a switch expression has no `default` and no case matches (possible with non-sealed types or corrupted bytecode), a `MatchException` is thrown.

## Debugging Scenarios

### Scenario: Pattern Variable Not in Scope
```java
if (obj instanceof String s && s.length() > 5) {
    // s is in scope here
}
if (!(obj instanceof String s)) {
    // s is NOT in scope here (it might not be matched)
} else {
    // s IS in scope here
}
```

If you see unexpected "cannot find symbol" errors for pattern variables, check the scope rules.

### Scenario: Guard Evaluation Order
Guards are evaluated after the pattern matches. If a guard has side effects or throws, the execution continues to the next case:

```java
switch (obj) {
    case String s when logAndCheck(s) -> process(s);
    case String s -> fallback(s);
}
```

If `logAndCheck` returns false, execution falls through to the second case. If it throws, the exception propagates.

### Scenario: Default Behavior with null
Some developers expect `default` to catch null:

```java
switch (obj) {
    case String s -> "string";
    default -> "other";  // Does NOT catch null!
}
```

If `obj` is null, this throws NPE. Always use explicit `case null`.

## IDE Debugging

### IntelliJ IDEA
- Place breakpoints in switch cases. Step through to see which pattern matches.
- The debugger shows pattern variable values when the matching case is hit.
- Use "Evaluate Expression" to test pattern matching on different values.

### Visual Studio Code
- Debugger stops at the switch expression line
- Step over goes to the matching case body
- Hover over pattern variables to see their values

## Testing Pattern Matching

```java
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PatternMatchingTest {
    
    @Test
    void testInstanceOfPattern() {
        Object obj = "hello";
        if (obj instanceof String s) {
            assertEquals(5, s.length());
        } else {
            fail("Should have matched String");
        }
    }
    
    @Test
    void testNullHandling() {
        assertThrows(NullPointerException.class, () -> {
            Object obj = null;
            String result = switch (obj) {
                case String s -> "string";
                default -> "other";
            };
        });
    }
    
    @Test
    void testExhaustiveness() {
        sealed interface A permits B, C {}
        final class B implements A {}
        final class C implements A {}
        
        // This compiles because all subtypes are covered
        String test(A a) {
            return switch (a) {
                case B b -> "B";
                case C c -> "C";
            };
        }
    }
}
```

## Common Debugging Techniques

1. **Print the type**: Before the switch, print `obj.getClass().getName()` to see the actual runtime type
2. **Check null**: Verify if null is expected and handled
3. **Reduce nesting**: Flatten deeply nested record patterns into variable assignments
4. **Test with JUnit**: Write parameterized tests with different input types
5. **Use breakpoints**: Step through the switch to see which case matches
6. **Enable preview features**: Ensure your run configuration has `--enable-preview` if using preview APIs
