package com.learning.lab03;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ControlFlowTest {

    @Test
    @DisplayName("If-else ladder")
    void ifElseLadder() {
        int score = 85;
        String grade;
        if (score >= 90) grade = "A";
        else if (score >= 80) grade = "B";
        else if (score >= 70) grade = "C";
        else grade = "F";
        assertEquals("B", grade);
    }

    @Test
    @DisplayName("Switch with traditional syntax")
    void traditionalSwitch() {
        int day = 3;
        String dayName;
        switch (day) {
            case 1: dayName = "Monday"; break;
            case 2: dayName = "Tuesday"; break;
            case 3: dayName = "Wednesday"; break;
            default: dayName = "Unknown";
        }
        assertEquals("Wednesday", dayName);
    }

    @Test
    @DisplayName("Switch expression with arrow syntax")
    void switchExpression() {
        int day = 6;
        String type = switch (day) {
            case 1, 2, 3, 4, 5 -> "weekday";
            case 6, 7 -> "weekend";
            default -> "invalid";
        };
        assertEquals("weekend", type);
    }

    @Test
    @DisplayName("Switch expression returning value with yield")
    void switchExpressionYield() {
        int x = 2;
        String result = switch (x) {
            case 1 -> "one";
            case 2 -> "two";
            default -> {
                yield "many";
            }
        };
        assertEquals("two", result);
    }

    @Test
    @DisplayName("For loop computes factorial")
    void forLoopFactorial() {
        int n = 5;
        int factorial = 1;
        for (int i = 2; i <= n; i++) {
            factorial *= i;
        }
        assertEquals(120, factorial);
    }

    @Test
    @DisplayName("While loop counts down")
    void whileLoopCountdown() {
        int count = 5;
        var result = new StringBuilder();
        while (count > 0) {
            result.append(count--);
        }
        assertEquals("54321", result.toString());
    }

    @Test
    @DisplayName("Do-while executes at least once")
    void doWhileLoop() {
        int x = 10;
        int count = 0;
        do {
            count++;
        } while (x < 5);
        assertEquals(1, count);
    }

    @Test
    @DisplayName("Break exits loop")
    void breakStatement() {
        int sum = 0;
        for (int i = 1; i <= 10; i++) {
            if (i == 5) break;
            sum += i;
        }
        assertEquals(10, sum);
    }

    @Test
    @DisplayName("Continue skips iteration")
    void continueStatement() {
        var evens = new java.util.ArrayList<Integer>();
        for (int i = 1; i <= 10; i++) {
            if (i % 2 != 0) continue;
            evens.add(i);
        }
        assertEquals(java.util.List.of(2, 4, 6, 8, 10), evens);
    }

    @Test
    @DisplayName("Nested loops execute correctly")
    void nestedLoops() {
        int count = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                count++;
            }
        }
        assertEquals(12, count);
    }
}
