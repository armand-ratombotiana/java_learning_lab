package com.learning.testing;

import java.util.*;
import java.util.function.*;

public class TestingStrategiesLab {

    public static void main(String[] args) {
        System.out.println("=== Testing Strategies Lab ===\n");

        System.out.println("1. Unit Testing:");
        System.out.println("   @Test");
        System.out.println("   void testAddition() {");
        System.out.println("       Calculator calc = new Calculator();");
        System.out.println("       assertEquals(5, calc.add(2, 3));");
        System.out.println("   }");

        System.out.println("\n2. Test Pyramid:");
        System.out.println("   ┌──────────┐");
        System.out.println("   │   E2E    │  Few - Slow, comprehensive");
        System.out.println("   ├──────────┤");
        System.out.println("   │Integratio│  Some - Medium speed");
        System.out.println("   ├──────────┤");
        System.out.println("   │  Unit    │  Many - Fast, granular");
        System.out.println("   └──────────┘");

        System.out.println("\n3. Assertions Demo:");
        Calculator calc = new Calculator();
        assertEqual(5, calc.add(2, 3), "add(2, 3)");
        assertEqual(1, calc.subtract(3, 2), "subtract(3, 2)");
        assertEqual(6, calc.multiply(2, 3), "multiply(2, 3)");
        assertEqual(2.0, calc.divide(6, 3), "divide(6, 3)");

        try {
            calc.divide(1, 0);
            System.out.println("   FAIL: divide(1,0) should throw");
        } catch (ArithmeticException e) {
            System.out.println("   PASS: divide(1,0) throws ArithmeticException");
        }

        System.out.println("\n4. Mockito Example:");
        System.out.println("   @Mock UserRepository userRepo;");
        System.out.println("   @InjectMocks UserService userService;");
        System.out.println("   when(userRepo.findById(1L)).thenReturn(Optional.of(user));");
        System.out.println("   User result = userService.getUser(1L);");
        System.out.println("   verify(userRepo).findById(1L);");

        System.out.println("\n5. Parameterized Tests:");
        System.out.println("   @ParameterizedTest");
        System.out.println("   @CsvSource({\"2,3,5\", \"0,0,0\", \"-1,1,0\"})");
        System.out.println("   void testAdd(int a, int b, int expected) {");
        System.out.println("       assertEquals(expected, calc.add(a, b));");
        System.out.println("   }");

        System.out.println("\n6. Integration Test:");
        System.out.println("   @SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)");
        System.out.println("   @Testcontainers");
        System.out.println("   class UserRepositoryTest {");
        System.out.println("       @Container static PostgreSQLContainer<?> postgres =");
        System.out.println("           new PostgreSQLContainer<>(\"postgres:15\");");
        System.out.println("       @Autowired UserRepository repository;");
        System.out.println("       @Test void testSaveUser() {");
        System.out.println("           User saved = repository.save(new User(\"Alice\"));");
        System.out.println("           assertNotNull(saved.getId());");
        System.out.println("       }");
        System.out.println("   }");

        System.out.println("\n7. Behavior-Driven Testing:");
        System.out.println("   given(userRepo.findById(1L)).willReturn(Optional.of(user));");
        System.out.println("   when(userService.getUser(1L));");
        System.out.println("   then(result).isEqualTo(user);");

        System.out.println("\n8. TDD Cycle:");
        System.out.println("   Red (failing test) -> Green (make it pass) -> Refactor (clean up)");
        System.out.println("   1. Write a failing test");
        System.out.println("   2. Write minimal code to pass");
        System.out.println("   3. Refactor and repeat");

        System.out.println("\n=== Testing Strategies Lab Complete ===");
    }

    static class Calculator {
        int add(int a, int b) { return a + b; }
        int subtract(int a, int b) { return a - b; }
        int multiply(int a, int b) { return a * b; }
        double divide(int a, int b) {
            if (b == 0) throw new ArithmeticException("Division by zero");
            return (double) a / b;
        }
    }

    static void assertEqual(Object expected, Object actual, String test) {
        boolean pass = Objects.equals(expected, actual);
        System.out.println("   " + (pass ? "PASS" : "FAIL") + ": " + test +
            " (expected=" + expected + ", actual=" + actual + ")");
    }
}