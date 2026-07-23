package testing;

import java.util.*;

/**
 * AssertJ — fluent assertion library for Java.
 * 
 * AssertJ provides rich, type-specific assertions with a fluent API.
 * 
 * Maven dependency:
 *   org.assertj:assertj-core:3.24.2
 * 
 * This class demonstrates AssertJ-style assertions using standard Java
 * assertions with the same expressive patterns.
 */
public class AssertJExample {

    // Data classes
    record Person(String name, int age, String city) {}
    record Order(String id, double amount, boolean paid) {}

    static class StringUtils {
        static boolean isPalindrome(String s) {
            if (s == null) return false;
            String cleaned = s.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
            return cleaned.equals(new StringBuilder(cleaned).reverse().toString());
        }

        static String reverse(String s) {
            return s == null ? null : new StringBuilder(s).reverse().toString();
        }
    }

    public static void main(String[] args) {
        // String assertions
        String name = "Alice";
        assert name.equals("Alice") : "Expected Alice";
        assert name.length() == 5;
        assert name.startsWith("A");
        assert name.contains("lic");
        assert !name.isEmpty();

        // Numeric assertions
        int age = 30;
        assert age > 0;
        assert age < 150;
        assert age == 30;

        // Collection assertions
        List<String> names = List.of("Alice", "Bob", "Charlie");
        assert names.size() == 3;
        assert names.contains("Alice");
        assert names.get(1).equals("Bob");
        assert !names.isEmpty();

        // Map assertions
        Map<String, Integer> ages = Map.of("Alice", 30, "Bob", 25);
        assert ages.containsKey("Alice");
        assert ages.get("Bob") == 25;
        assert ages.size() == 2;

        // Object assertions
        Person p = new Person("Alice", 30, "NYC");
        assert p != null;
        assert p.name().equals("Alice");
        assert p.age() == 30;
        assert p.city().equals("NYC");

        // Exception assertions
        try {
            List.of().get(0);
            assert false : "Should throw IndexOutOfBoundsException";
        } catch (IndexOutOfBoundsException e) {
            // Expected
        }

        // Boolean assertions
        assert StringUtils.isPalindrome("racecar");
        assert !StringUtils.isPalindrome("hello");
        assert StringUtils.isPalindrome("A man, a plan, a canal: Panama");

        // fluent chaining (simulated — AssertJ allows real chaining)
        // In AssertJ:
        // assertThat(name).isEqualTo("Alice").startsWith("A").hasSize(5);

        // Soft assertions (multiple assertions, report all failures)
        // In AssertJ:
        // SoftAssertions softly = new SoftAssertions();
        // softly.assertThat(name).isEqualTo("Alice");
        // softly.assertThat(age).isEqualTo(30);
        // softly.assertAll();

        // Custom assertion
        assert Order.class.isRecord() : "Order should be a record";

        System.out.println("All AssertJExample tests passed.");

        // Show AssertJ features
        System.out.println("\nAssertJ features:");
        System.out.println("  assertThat(actual).isEqualTo(expected)");
        System.out.println("  assertThat(collection).hasSize(3).contains(\"Alice\")");
        System.out.println("  assertThatThrownBy(() -> {}).isInstanceOf(IllegalArgumentException.class)");
        System.out.println("  assertThat(map).containsKey(\"key\").doesNotContainValue(null)");
        System.out.println("  assertThat(optional).isPresent().get().isEqualTo(\"value\")");
        System.out.println("  SoftAssertions — collect all failures");
        System.out.println("  Conditions, as(), withFailMessage()");
    }
}