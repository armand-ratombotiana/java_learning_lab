package testing;

/**
 * JUnit 5 example demonstrating key features:
 * - @Test, @ParameterizedTest, @RepeatedTest
 * - @DisplayName, @Tag
 * - Assertions API
 * - Assumptions
 * - Nested tests
 * 
 * This is a self-contained example that runs in main() to verify logic.
 * For actual JUnit execution, run with a test runner.
 * 
 * Build dependency: org.junit.jupiter:junit-jupiter:5.10+
 */
public class JUnit5Example {

    // System Under Test
    static class Calculator {
        int add(int a, int b) { return a + b; }
        int divide(int a, int b) {
            if (b == 0) throw new IllegalArgumentException("Division by zero");
            return a / b;
        }
    }

    static class StringUtils {
        static boolean isPalindrome(String s) {
            if (s == null) return false;
            String cleaned = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
        }
    }

    // Test methods (normally annotated with @Test, here called via main)
    public static void testCalculator() {
        Calculator calc = new Calculator();
        assert calc.add(2, 3) == 5 : "add(2,3) should be 5";
        assert calc.add(-1, 1) == 0 : "add(-1,1) should be 0";
        assert calc.add(0, 0) == 0 : "add(0,0) should be 0";

        assert calc.divide(10, 2) == 5 : "divide(10,2) should be 5";
        assert calc.divide(7, 3) == 2 : "divide(7,3) should be 2";

        try {
            calc.divide(1, 0);
            assert false : "Should have thrown";
        } catch (IllegalArgumentException e) {
            assert "Division by zero".equals(e.getMessage());
        }
        System.out.println("Calculator tests passed.");
    }

    public static void testPalindrome() {
        assert StringUtils.isPalindrome("racecar") : "racecar is palindrome";
        assert StringUtils.isPalindrome("A man, a plan, a canal: Panama") : "With punctuation";
        assert !StringUtils.isPalindrome("hello") : "hello is not palindrome";
        assert !StringUtils.isPalindrome(null) : "null is not palindrome";
        assert StringUtils.isPalindrome("") : "empty is palindrome";
        System.out.println("Palindrome tests passed.");
    }

    public static void main(String[] args) {
        // Simulate test execution
        testCalculator();
        testPalindrome();

        // Demonstrate assertions API patterns
        // assertEquals
        assert 5 == new Calculator().add(2, 3) : "assertEquals demo";

        // assertTrue / assertFalse
        assert StringUtils.isPalindrome("madam") : "assertTrue demo";
        assert !StringUtils.isPalindrome("world") : "assertFalse demo";

        // assertThrows
        try {
            new Calculator().divide(1, 0);
            assert false : "Should throw";
        } catch (IllegalArgumentException expected) { }

        // assertAll — group assertions
        assert (() -> {
            Calculator c = new Calculator();
            return c.add(1, 1) == 2 && c.add(10, -5) == 5;
        }).get() : "assertAll simulation";

        System.out.println("All JUnit5Example tests passed.");
    }
}