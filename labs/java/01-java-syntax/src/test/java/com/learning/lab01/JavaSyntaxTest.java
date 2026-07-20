package com.learning.lab01;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class JavaSyntaxTest {

    @Test
    @DisplayName("Variable declarations and initialization")
    void variableDeclarations() {
        int number = 42;
        double pi = 3.14159;
        String greeting = "Hello";
        assertEquals(42, number);
        assertEquals(3.14159, pi, 1e-9);
        assertEquals("Hello", greeting);
    }

    @Test
    @DisplayName("Type inference with var")
    void typeInferenceWithVar() {
        var message = "Inferred String";
        var count = 100;
        assertInstanceOf(String.class, message);
        assertInstanceOf(Integer.class, count);
    }

    @Test
    @DisplayName("Basic arithmetic operations")
    void arithmeticOperations() {
        assertEquals(5, 2 + 3);
        assertEquals(6, 2 * 3);
        assertEquals(1, 3 - 2);
        assertEquals(2, 4 / 2);
        assertEquals(1, 7 % 2);
    }

    @Test
    @DisplayName("String concatenation")
    void stringConcatenation() {
        String result = "Hello" + " " + "World";
        assertEquals("Hello World", result);
    }

    @Test
    @DisplayName("If-else control flow")
    void ifElseControlFlow() {
        int x = 10;
        String result;
        if (x > 5) {
            result = "greater";
        } else {
            result = "lesser";
        }
        assertEquals("greater", result);
    }

    @Test
    @DisplayName("Ternary operator")
    void ternaryOperator() {
        int x = 10;
        String result = x > 5 ? "yes" : "no";
        assertEquals("yes", result);
    }

    @Test
    @DisplayName("For loop iteration")
    void forLoop() {
        int sum = 0;
        for (int i = 1; i <= 5; i++) {
            sum += i;
        }
        assertEquals(15, sum);
    }

    @Test
    @DisplayName("While loop")
    void whileLoop() {
        int count = 0;
        int i = 0;
        while (i < 5) {
            count++;
            i++;
        }
        assertEquals(5, count);
    }

    @Test
    @DisplayName("Array creation and access")
    void arrayCreation() {
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length);
        assertEquals(1, numbers[0]);
        assertEquals(5, numbers[4]);
    }

    @Test
    @DisplayName("Enhanced for loop")
    void enhancedForLoop() {
        String[] names = {"Alice", "Bob", "Charlie"};
        var result = new StringBuilder();
        for (String name : names) {
            result.append(name);
        }
        assertEquals("AliceBobCharlie", result.toString());
    }
}
