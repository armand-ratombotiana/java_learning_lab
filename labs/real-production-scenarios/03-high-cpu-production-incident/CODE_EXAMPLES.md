# Lab 03 — High CPU / ReDoS: Code Examples

## Reproducing the Bug

```java
import java.util.regex.*;
import java.util.concurrent.*;

public class ReDoSDemo {

    // Vulnerable pattern with catastrophic backtracking
    // (a|aa|aaa|aaaa)+b on "aaaaaaaaac" causes exponential backtracking
    private static final String VULNERABLE_PATTERN = "(a|aa|aaa|aaaa)+b";
    private static final Pattern vulnerablePattern = Pattern.compile(VULNERABLE_PATTERN);

    public static void main(String[] args) {
        String attackPayload = "aaaaaaaaac"; // Just 10 chars!

        System.out.println("ReDoS Demo - Catastrophic Backtracking");
        System.out.println("Pattern: " + VULNERABLE_PATTERN);
        System.out.println("Input: " + attackPayload + " (length: " + attackPayload.length() + ")");

        long start = System.nanoTime();
        Matcher matcher = vulnerablePattern.matcher(attackPayload);
        boolean matches = matcher.matches();
        long duration = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Matches: " + matches);
        System.out.println("Duration: " + duration + " ms");

        if (duration > 1000) {
            System.out.println("\nCATASTROPHIC BACKTRACKING DETECTED! Input=" + attackPayload.length() + " chars took " + duration + "ms");
        }
    }
}
```

## Fixing the Bug

### Fix 1: Atomic Groups

```java
import java.util.regex.*;

public class ReDoSFixAtomicGroup {

    private static final String FIXED_PATTERN = "(?>a|aa|aaa|aaaa)+b";
    private static final Pattern fixedPattern = Pattern.compile(FIXED_PATTERN);

    public static void main(String[] args) {
        String attackPayload = "aaaaaaaaac";

        long start = System.nanoTime();
        boolean matches = fixedPattern.matcher(attackPayload).matches();
        long duration = (System.nanoTime() - start) / 1_000_000;

        System.out.println("Fixed with atomic group: " + FIXED_PATTERN);
        System.out.println("Input: " + attackPayload);
        System.out.println("Matches: " + matches);
        System.out.println("Duration: " + duration + " ms");

        if (duration < 100) {
            System.out.println("SUCCESS: Catastrophic backtracking prevented!");
        }
    }
}
```

### Fix 2: Regex Timeout Wrapper

```java
import java.util.concurrent.*;
import java.util.regex.*;

public class TimeoutPattern {
    private final Pattern pattern;
    private final long timeoutMs;
    private final ExecutorService executor = Executors.newCachedThreadPool();

    public TimeoutPattern(String regex, long timeoutMs) {
        this.pattern = Pattern.compile(regex);
        this.timeoutMs = timeoutMs;
    }

    public boolean matches(String input) throws TimeoutException {
        Future<Boolean> future = executor.submit(() -> pattern.matcher(input).matches());
        try {
            return future.get(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true); // Interrupt the worker thread
            throw new TimeoutException("Regex evaluation timed out after " + timeoutMs + "ms");
        } catch (Exception e) {
            throw new RuntimeException("Regex evaluation failed", e);
        }
    }

    public void shutdown() {
        executor.shutdown();
    }

    public static void main(String[] args) {
        TimeoutPattern safePattern = new TimeoutPattern("(a|aa|aaa|aaaa)+b", 100);
        String attackPayload = "aaaaaaaaac";

        try {
            boolean matches = safePattern.matches(attackPayload);
            System.out.println("Result: " + matches);
        } catch (TimeoutException e) {
            System.out.println("SAFE: Regex timed out after 100ms — potential ReDoS prevented!");
        } finally {
            safePattern.shutdown();
        }
    }
}
```

### Fix 3: Input Validation

```java
public class InputValidator {
    private static final int MAX_INPUT_LENGTH = 256;
    private static final int MAX_REPEATED_CHARS = 10;

    public static boolean isValidInput(String input) {
        if (input == null || input.length() > MAX_INPUT_LENGTH) {
            System.out.println("REJECTED: Input exceeds " + MAX_INPUT_LENGTH + " characters");
            return false;
        }

        // Detect long repeated character sequences (common ReDoS payloads)
        int maxRepeat = 1;
        int currentRepeat = 1;
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == input.charAt(i-1)) {
                currentRepeat++;
                maxRepeat = Math.max(maxRepeat, currentRepeat);
            } else {
                currentRepeat = 1;
            }
            if (maxRepeat > MAX_REPEATED_CHARS) {
                System.out.println("REJECTED: Input has " + maxRepeat + " repeated chars (max " + MAX_REPEATED_CHARS + ")");
                return false;
            }
        }

        return true;
    }
}
```

### Fix 4: ReDoS Scanner for CI/CD

```java
import java.util.regex.*;
import java.util.*;

public class ReDoSScanner {
    private static final long TIMEOUT_MS = 100;
    private static final String[] TEST_PAYLOADS = {
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
        "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaab",
        "test test test test test test test test test test x",
    };

    public static void scanPattern(String regex) {
        System.out.println("Scanning pattern: " + regex);
        Pattern pattern = Pattern.compile(regex);
        boolean vulnerable = false;

        for (String payload : TEST_PAYLOADS) {
            long start = System.nanoTime();
            try {
                pattern.matcher(payload).matches();
            } catch (Exception e) {
                // Ignore
            }
            long duration = (System.nanoTime() - start) / 1_000_000;

            if (duration > 100) {
                System.out.println("  VULNERABLE: Input '" + payload.substring(0, Math.min(20, payload.length())) + "...' took " + duration + "ms");
                vulnerable = true;
            }
        }

        if (!vulnerable) {
            System.out.println("  SAFE: No catastrophic backtracking detected");
        }
    }

    public static void main(String[] args) {
        scanPattern("(a|aa|aaa|aaaa)+b");
        scanPattern("(?>a|aa|aaa|aaaa)+b");
        scanPattern("^[a-zA-Z0-9]+$");
    }
}
```

### Unit Tests

```java
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;

class ReDoSTest {

    @Test
    @Timeout(5)
    void testVulnerablePatternIsSlow() {
        Pattern p = Pattern.compile("(a|aa|aaa|aaaa)+b");
        long start = System.nanoTime();
        p.matcher("aaaaaaaaac").matches();
        long duration = (System.nanoTime() - start) / 1_000_000;
        assertTrue(duration > 100, "Vulnerable pattern should be slow: " + duration + "ms");
    }

    @Test
    @Timeout(1)
    void testFixedPatternIsFast() {
        Pattern p = Pattern.compile("(?>a|aa|aaa|aaaa)+b");
        long start = System.nanoTime();
        p.matcher("aaaaaaaaac").matches();
        long duration = (System.nanoTime() - start) / 1_000_000;
        assertTrue(duration < 50, "Fixed pattern should be fast: " + duration + "ms");
    }

    @Test
    @Timeout(2)
    void testTimeoutPreventsHang() throws Exception {
        TimeoutPattern tp = new TimeoutPattern("(a|aa|aaa|aaaa)+b", 100);
        try {
            tp.matches("aaaaaaaaac");
            fail("Should have thrown TimeoutException");
        } catch (TimeoutException e) {
            // Expected — timeout prevented hang
        } finally {
            tp.shutdown();
        }
    }
}
```
