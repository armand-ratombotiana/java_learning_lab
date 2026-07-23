package testing;

/**
 * Advanced JUnit 5 features demonstration.
 * 
 * Features: @ParameterizedTest, @RepeatedTest, @TestFactory (dynamic tests),
 *           @Nested, @Tag, @DisplayName, @Timeout, extensions.
 * 
 * This self-contained demo runs the test logic in main().
 * For real JUnit execution, add:
 *   org.junit.jupiter:junit-jupiter:5.10+
 *   org.junit.platform:junit-platform-launcher
 */
public class JUnit5AdvancedExample {

    // System under test
    static class Calculator {
        int add(int... nums) { return java.util.Arrays.stream(nums).sum(); }

        int divide(int a, int b) {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return a / b;
        }

        boolean isPrime(int n) {
            if (n <= 1) return false;
            for (int i = 2; i * i <= n; i++) if (n % i == 0) return false;
            return true;
        }
    }

    // Simulated parameterized test
    static void testPrime() {
        var calc = new Calculator();
        int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 97, 101};
        int[] nonPrimes = {0, 1, 4, 6, 8, 9, 10, 12, 100};

        for (int p : primes) assert calc.isPrime(p) : p + " should be prime";
        for (int np : nonPrimes) assert !calc.isPrime(np) : np + " should not be prime";
        System.out.println("Prime tests passed.");
    }

    // Simulated repeated test
    static void testAddRepeated() {
        var calc = new Calculator();
        for (int i = 0; i < 10; i++) {
            assert calc.add(1, 2) == 3 : "iteration " + i;
        }
        System.out.println("Repeated add test passed.");
    }

    // Simulated timeout
    static void testTimeout() {
        var calc = new Calculator();
        long start = System.nanoTime();
        calc.isPrime(9999991); // large prime
        long elapsed = System.nanoTime() - start;
        assert elapsed < 5_000_000_000L : "Took too long: " + elapsed / 1_000_000 + "ms";
        System.out.println("Timeout test passed: " + elapsed / 1_000_000 + "ms");
    }

    // Simulated exception test
    static void testException() {
        var calc = new Calculator();
        try {
            calc.divide(1, 0);
            assert false : "Should throw ArithmeticException";
        } catch (ArithmeticException e) {
            assert "Division by zero".equals(e.getMessage());
        }
        System.out.println("Exception test passed.");
    }

    // Simulated nested test group
    static class NestedTests {
        static void runAll() {
            System.out.println("--- Nested: Division tests ---");
            var calc = new Calculator();
            assert calc.divide(10, 2) == 5;
            assert calc.divide(7, 3) == 2;
            assert calc.divide(-10, 2) == -5;
            System.out.println("--- Nested: Edge cases ---");
            assert calc.divide(0, 1) == 0;
            assert calc.divide(Integer.MAX_VALUE, 1) == Integer.MAX_VALUE;
            System.out.println("Nested tests passed.");
        }
    }

    public static void main(String[] args) {
        testPrime();
        testAddRepeated();
        testTimeout();
        testException();
        NestedTests.runAll();
        System.out.println("All JUnit5AdvancedExample tests passed.");

        // Show advanced features overview
        System.out.println("\nAdvanced JUnit 5 features:");
        System.out.println("  @ParameterizedTest @ValueSource/StringSource/MethodSource");
        System.out.println("  @RepeatedTest(value = 10, name = \"{currentRepetition}/{totalRepetitions}\")");
        System.out.println("  @TestFactory — dynamic test generation");
        System.out.println("  @Nested — hierarchical test organization");
        System.out.println("  @Timeout(100, unit = TimeUnit.MILLISECONDS)");
        System.out.println("  @Tag(\"slow\") — test filtering");
        System.out.println("  @ExtendWith(SpringExtension.class)");
        System.out.println("  assertAll, assertThrows, assertTimeout");
    }
}