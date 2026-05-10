package com.learning.lab.module50;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import org.junit.jupiter.engine.*;
import org.junit.platform.suite.*;
import org.opentest4j.*;

import java.util.*;
import java.util.concurrent.*;
import java.time.*;
import java.util.stream.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 50: JUnit 5 Advanced Lab ===\n");

        System.out.println("1. Parameterized Tests:");
        parameterizedTestsDemo();

        System.out.println("\n2. Nested Tests:");
        nestedTestsDemo();

        System.out.println("\n3. Repeated Tests:");
        repeatedTestsDemo();

        System.out.println("\n4. Dynamic Tests:");
        dynamicTestsDemo();

        System.out.println("\n5. Test Interfaces:");
        testInterfacesDemo();

        System.out.println("\n6. Conditional Execution:");
        conditionalExecutionDemo();

        System.out.println("\n7. Custom Display Names:");
        displayNamesDemo();

        System.out.println("\n8. Assertions:");
        advancedAssertionsDemo();

        System.out.println("\n9. Timeouts and Threading:");
        timeoutDemo();

        System.out.println("\n=== JUnit 5 Advanced Lab Complete ===");
    }

    static void parameterizedTestsDemo() {
        System.out.println("   @ParameterizedTest:");
        System.out.println("   @ValueSource(ints = {1, 2, 3})");
        System.out.println("   @CsvSource({\"a,1\", \"b,2\"})");
        System.out.println("   @CsvFileSource(\"data.csv\")");
        System.out.println("   @EnumSource(Size.class)");
        System.out.println("   @MethodSource(\"providerMethod\")");

        System.out.println("\n   Argument Conversion:");
        System.out.println("   Automatic: String to int");
        System.out.println("   @ConvertWith(CustomConverter.class)");
    }

    static void nestedTestsDemo() {
        System.out.println("   @Nested Classes:");
        System.out.println("   - Group related tests");
        System.out.println("   - Share @BeforeAll/@AfterAll");
        System.out.println("   - Hierarchical display names");

        System.out.println("\n   Example:");
        System.out.println("   @Nested class CalculatorTests {");
        System.out.println("       @Nested class AdditionTests { }");
        System.out.println("       @Nested class SubtractionTests { }");
        System.out.println("   }");
    }

    static void repeatedTestsDemo() {
        System.out.println("   @RepeatedTest:");
        System.out.println("   @RepeatedTest(value = 10, name = \" repetition {currentRepetition}\")");
        System.out.println("   RepetitionInfo.getCurrentRepetition()");
        System.out.println("   RepetitionInfo.getTotalRepetitions()");
    }

    static void dynamicTestsDemo() {
        System.out.println("   DynamicTest Factory:");
        System.out.println("   @TestFactory");
        System.out.println("   Collection<DynamicTest> tests() {");
        System.out.println("       return DynamicTest.stream(");
        System.out.println("           input, name, test);");
        System.out.println("   }");

        System.out.println("\n   Dynamic Test Methods:");
        System.out.println("   DynamicTest.dynamicTest(\"name\", () -> { })");
    }

    static void testInterfacesDemo() {
        System.out.println("   Test Interfaces:");
        System.out.println("   interface TestLifecycle {");
        System.out.println("       @BeforeAll default void init() { }");
        System.out.println("       @AfterAll default void cleanup() { }");
        System.out.println("   }");
        System.out.println("   class MyTest implements TestLifecycle { }");
    }

    static void conditionalExecutionDemo() {
        System.out.println("   @EnabledOnOs:");
        System.out.println("   @EnabledOnOs(OS.WINDOWS)");
        System.out.println("   @DisabledOnOs(OS.LINUX)");

        System.out.println("\n   @EnabledOnJre:");
        System.out.println("   @EnabledOnJre(JRE.JAVA_17)");

        System.out.println("\n   @EnabledIfSystemProperty:");
        System.out.println("   @EnabledIfSystemProperty(named = \"os.name\", matches = \".*Windows.*\")");

        System.out.println("\n   @DisabledIfEnvironmentVariable:");
        System.out.println("   @DisabledIfEnvironmentVariable(named = \"CI\", matches = \"true\")");
    }

    static void displayNamesDemo() {
        System.out.println("   @DisplayName:");
        System.out.println("   @DisplayName(\"Addition Test\")");
        System.out.println("   @DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)");
        System.out.println("   @DisplayNameGeneration(DisplayNameGenerator.Simple.class)");
    }

    static void advancedAssertionsDemo() {
        System.out.println("   Grouped Assertions:");
        System.out.println("   assertAll(\"user\",");
        System.out.println("       () -> assertEquals(\"John\", user.getName()),");
        System.out.println("       () -> assertEquals(25, user.getAge())");
        System.out.println("   );");

        System.out.println("\n   Exception Assertions:");
        System.out.println("   assertThrows(ArithmeticException.class, () -> 1/0);");
        System.out.println("   assertDoesNotThrow(() -> compute());");

        System.out.println("\n   Timeout Assertions:");
        System.out.println("   assertTimeout(Duration.ofSeconds(1), () -> task());");
        System.out.println("   assertTimeoutPreemptively(Duration.ofSeconds(1), () -> task());");

        System.out.println("\n   Message Supplier:");
        System.out.println("   assertEquals(expected, actual, () -> \"Error: \" + expensiveOperation());");
    }

    static void timeoutDemo() {
        System.out.println("   @Timeout:");
        System.out.println("   @Timeout(5) @Test");
        System.out.println("   @Timeout(value = 10, unit = TimeUnit.SECONDS)");

        System.out.println("\n   Thread Safety:");
        System.out.println("   @Execution(SAME_THREAD)");
        System.out.println("   @Execution(CONCURRENT)");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void testWithParams(int value) {
        assertTrue(value > 0);
    }

    @RepeatedTest(3)
    void repeatedTest() {
        assertTrue(true);
    }

    @TestFactory
    Collection<DynamicTest> dynamicTests() {
        return List.of(
            DynamicTest.dynamicTest("Test 1", () -> assertEquals(1, 1)),
            DynamicTest.dynamicTest("Test 2", () -> assertEquals(2, 2))
        );
    }
}