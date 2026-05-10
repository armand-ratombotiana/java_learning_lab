package com.learning.lab.module01.solution;

import java.util.Arrays;

public class Test {
    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("=== Module 01: Java Basics - Comprehensive Tests ===\n");

        testDataTypes();
        testVariables();
        testOperators();
        testControlFlow();
        testArrays();
        testStrings();
        testMethods();

        printSummary();
    }

    private static void testDataTypes() {
        System.out.println("--- Testing Data Types ---");

        test("byte range", () -> {
            byte min = Byte.MIN_VALUE;
            byte max = Byte.MAX_VALUE;
            assert min == -128 : "Byte min failed";
            assert max == 127 : "Byte max failed";
        });

        test("short range", () -> {
            short min = Short.MIN_VALUE;
            short max = Short.MAX_VALUE;
            assert min == -32768 : "Short min failed";
            assert max == 32767 : "Short max failed";
        });

        test("int range", () -> {
            int min = Integer.MIN_VALUE;
            int max = Integer.MAX_VALUE;
            assert min == -2147483648 : "Int min failed";
            assert max == 2147483647 : "Int max failed";
        });

        test("long range", () -> {
            long min = Long.MIN_VALUE;
            long max = Long.MAX_VALUE;
            assert min == -9223372036854775808L : "Long min failed";
            assert max == 9223372036854775807L : "Long max failed";
        });

        test("float range", () -> {
            float min = Float.MIN_VALUE;
            float max = Float.MAX_VALUE;
            assert min > 0 : "Float min failed";
            assert max > 0 : "Float max failed";
        });

        test("double range", () -> {
            double min = Double.MIN_VALUE;
            double max = Double.MAX_VALUE;
            assert min > 0 : "Double min failed";
            assert max > 0 : "Double max failed";
        });

        test("char range", () -> {
            char min = Character.MIN_VALUE;
            char max = Character.MAX_VALUE;
            assert (int)min == 0 : "Char min failed";
            assert (int)max == 65535 : "Char max failed";
        });

        test("boolean values", () -> {
            boolean t = true;
            boolean f = false;
            assert t == true : "Boolean true failed";
            assert f == false : "Boolean false failed";
        });

        System.out.println();
    }

    private static void testVariables() {
        System.out.println("--- Testing Variables ---");

        test("final constant", () -> {
            final int CONST = 100;
            assert CONST == 100 : "Final constant failed";
        });

        test("var type inference", () -> {
            var number = 42;
            var text = "Hello";
            assert number instanceof Integer : "Var inference failed for number";
            assert text instanceof String : "Var inference failed for text";
        });

        test("instance variable initialization", () -> {
            VariablesTest vt = new VariablesTest();
            assert vt.defaultInt == 0 : "Default int failed";
            assert vt.defaultString == null : "Default string failed";
        });

        test("static variable", () -> {
            int count1 = VariablesTest.getCount();
            VariablesTest vt = new VariablesTest();
            int count2 = VariablesTest.getCount();
            assert count2 > count1 : "Static variable failed";
        });

        System.out.println();
    }

    private static void testOperators() {
        System.out.println("--- Testing Operators ---");

        test("arithmetic operators", () -> {
            int a = 10, b = 3;
            assert a + b == 13 : "Addition failed";
            assert a - b == 7 : "Subtraction failed";
            assert a * b == 30 : "Multiplication failed";
            assert a / b == 3 : "Division failed";
            assert a % b == 1 : "Modulo failed";
        });

        test("comparison operators", () -> {
            int x = 5, y = 10;
            assert (x == y) == false : "Equals failed";
            assert (x != y) == true : "Not equals failed";
            assert (x > y) == false : "Greater than failed";
            assert (x < y) == true : "Less than failed";
            assert (x >= y) == false : "Greater or equal failed";
            assert (x <= y) == true : "Less or equal failed";
        });

        test("logical operators", () -> {
            boolean p = true, q = false;
            assert (p && q) == false : "AND failed";
            assert (p || q) == true : "OR failed";
            assert !p == false : "NOT failed";
        });

        test("bitwise operators", () -> {
            int m = 6, n = 3;
            assert (m & n) == 2 : "AND bitwise failed";
            assert (m | n) == 7 : "OR bitwise failed";
            assert (m ^ n) == 5 : "XOR bitwise failed";
            assert (~m) == -7 : "NOT bitwise failed";
        });

        test("assignment operators", () -> {
            int num = 10;
            num += 5; assert num == 15 : "+= failed";
            num -= 3; assert num == 12 : "-= failed";
            num *= 2; assert num == 24 : "*= failed";
            num /= 4; assert num == 6 : "/= failed";
            num %= 5; assert num == 1 : "%= failed";
        });

        test("ternary operator", () -> {
            int age = 20;
            String status = age >= 18 ? "Adult" : "Minor";
            assert status.equals("Adult") : "Ternary failed";
        });

        test("increment/decrement", () -> {
            int a = 5;
            assert a++ == 5 : "Post increment failed";
            assert a == 6 : "Post increment result failed";
            assert ++a == 7 : "Pre increment failed";
        });

        System.out.println();
    }

    private static void testControlFlow() {
        System.out.println("--- Testing Control Flow ---");

        test("if-else positive", () -> {
            String result = ControlFlowTest.checkSign(5);
            assert result.equals("positive") : "Positive number failed";
        });

        test("if-else negative", () -> {
            String result = ControlFlowTest.checkSign(-3);
            assert result.equals("negative") : "Negative number failed";
        });

        test("if-else zero", () -> {
            String result = ControlFlowTest.checkSign(0);
            assert result.equals("zero") : "Zero failed";
        });

        test("switch statement", () -> {
            String result = ControlFlowTest.getDayType("Saturday");
            assert result.equals("Weekend") : "Switch weekend failed";
        });

        test("for loop", () -> {
            int sum = ControlFlowTest.sumRange(1, 5);
            assert sum == 15 : "For loop sum failed";
        });

        test("while loop", () -> {
            int count = ControlFlowTest.countDigits(12345);
            assert count == 5 : "While loop count failed";
        });

        test("do-while", () -> {
            int result = ControlFlowTest.multiplyUntil(3, 10);
            assert result == 6 : "Do-while failed";
        });

        test("break statement", () -> {
            int result = ControlFlowTest.findFirstGreaterThan(new int[]{1, 2, 5, 3, 4}, 4);
            assert result == 5 : "Break statement failed";
        });

        test("continue statement", () -> {
            int result = ControlFlowTest.sumOddNumbers(1, 10);
            assert result == 25 : "Continue statement failed";
        });

        System.out.println();
    }

    private static void testArrays() {
        System.out.println("--- Testing Arrays ---");

        test("array creation and access", () -> {
            int[] arr = new int[]{1, 2, 3, 4, 5};
            assert arr.length == 5 : "Array length failed";
            assert arr[0] == 1 : "Array access failed";
            assert arr[4] == 5 : "Array last element failed";
        });

        test("array sort", () -> {
            int[] arr = {5, 3, 1, 4, 2};
            ArraysTest.sort(arr);
            assert Arrays.equals(arr, new int[]{1, 2, 3, 4, 5}) : "Array sort failed";
        });

        test("array search", () -> {
            int[] arr = {1, 2, 3, 4, 5};
            int index = ArraysTest.binarySearch(arr, 3);
            assert index == 2 : "Binary search failed";
        });

        test("array copy", () -> {
            int[] original = {1, 2, 3};
            int[] copy = ArraysTest.copyOf(original, 5);
            assert copy.length == 5 : "Copy length failed";
            assert copy[0] == 1 : "Copy elements failed";
        });

        test("2D array", () -> {
            int[][] matrix = {{1, 2}, {3, 4}};
            assert matrix[0][0] == 1 : "2D array access failed";
            assert matrix[1][1] == 4 : "2D array access failed";
        });

        test("jagged array", () -> {
            int[][] jagged = new int[3][];
            jagged[0] = new int[]{1, 2};
            jagged[1] = new int[]{3, 4, 5};
            assert jagged[0].length == 2 : "Jagged array row 1 failed";
            assert jagged[1].length == 3 : "Jagged array row 2 failed";
        });

        System.out.println();
    }

    private static void testStrings() {
        System.out.println("--- Testing Strings ---");

        test("string creation", () -> {
            String s1 = "Hello";
            String s2 = new String("Hello");
            assert s1.equals(s2) : "String creation failed";
        });

        test("string length", () -> {
            String s = "Java";
            assert s.length() == 4 : "String length failed";
        });

        test("string charAt", () -> {
            String s = "Hello";
            assert s.charAt(0) == 'H' : "charAt failed";
            assert s.charAt(4) == 'o' : "charAt last failed";
        });

        test("string substring", () -> {
            String s = "Hello World";
            assert s.substring(0, 5).equals("Hello") : "substring failed";
            assert s.substring(6).equals("World") : "substring from index failed";
        });

        test("string toUpperCase/toLowerCase", () -> {
            String s = "Hello";
            assert s.toUpperCase().equals("HELLO") : "toUpperCase failed";
            assert s.toLowerCase().equals("hello") : "toLowerCase failed";
        });

        test("string contains", () -> {
            String s = "Hello World";
            assert s.contains("World") : "contains failed";
            assert !s.contains("Java") : "contains not found failed";
        });

        test("string indexOf", () -> {
            String s = "Hello";
            assert s.indexOf('l') == 2 : "indexOf first failed";
            assert s.lastIndexOf('l') == 3 : "lastIndexOf failed";
        });

        test("string replace", () -> {
            String s = "Hello World";
            assert s.replace("World", "Java").equals("Hello Java") : "replace failed";
        });

        test("string split", () -> {
            String s = "a,b,c";
            String[] parts = s.split(",");
            assert parts.length == 3 : "split length failed";
            assert parts[0].equals("a") : "split element failed";
        });

        test("StringBuilder operations", () -> {
            StringBuilder sb = new StringBuilder("Hello");
            sb.append(" World");
            assert sb.toString().equals("Hello World") : "StringBuilder append failed";
            sb.insert(5, ",");
            assert sb.toString().equals("Hello, World") : "StringBuilder insert failed";
            sb.reverse();
            assert sb.toString().equals("dlroW ,olleH") : "StringBuilder reverse failed";
        });

        test("StringBuffer operations", () -> {
            StringBuffer sb = new StringBuffer("Start");
            sb.append(" End");
            assert sb.toString().equals("Start End") : "StringBuffer append failed";
        });

        System.out.println();
    }

    private static void testMethods() {
        System.out.println("--- Testing Methods ---");

        test("method with return", () -> {
            MethodsTest mt = new MethodsTest();
            assert mt.add(2, 3) == 5 : "Method return failed";
        });

        test("method overloading", () -> {
            MethodsTest mt = new MethodsTest();
            assert mt.add(2, 3) == 5 : "Int add failed";
            assert mt.add(2.0, 3.0) == 5.0 : "Double add failed";
            assert mt.add(1, 2, 3) == 6 : "Triple int add failed";
        });

        test("varargs", () -> {
            MethodsTest mt = new MethodsTest();
            int sum = mt.sum(new int[]{1, 2, 3, 4, 5});
            assert sum == 15 : "Varargs sum failed";
        });

        test("recursion factorial", () -> {
            MethodsTest mt = new MethodsTest();
            assert mt.factorial(5) == 120 : "Factorial failed";
        });

        test("recursion fibonacci", () -> {
            MethodsTest mt = new MethodsTest();
            assert mt.fibonacci(10) == 55 : "Fibonacci failed";
        });

        test("static method", () -> {
            MethodsTest.staticMethod();
        });

        System.out.println();
    }

    private static void test(String name, Runnable test) {
        try {
            test.run();
            System.out.println("  PASS: " + name);
            passed++;
        } catch (AssertionError | Exception e) {
            System.out.println("  FAIL: " + name + " - " + e.getMessage());
            failed++;
        }
    }

    private static void printSummary() {
        System.out.println("=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total: " + (passed + failed));
        System.out.println("===================");
    }
}

class VariablesTest {
    int defaultInt;
    String defaultString;
    static int count = 0;

    VariablesTest() {
        count++;
    }

    static int getCount() {
        return count;
    }
}

class ControlFlowTest {
    static String checkSign(int num) {
        if (num > 0) return "positive";
        else if (num < 0) return "negative";
        else return "zero";
    }

    static String getDayType(String day) {
        return switch (day) {
            case "Saturday", "Sunday" -> "Weekend";
            default -> "Weekday";
        };
    }

    static int sumRange(int start, int end) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            sum += i;
        }
        return sum;
    }

    static int countDigits(int n) {
        int count = 0;
        while (n > 0) {
            count++;
            n /= 10;
        }
        return count;
    }

    static int multiplyUntil(int start, int limit) {
        int result = start;
        do {
            result *= 2;
        } while (result < limit);
        return result;
    }

    static int findFirstGreaterThan(int[] arr, int threshold) {
        for (int num : arr) {
            if (num > threshold) {
                return num;
            }
        }
        return -1;
    }

    static int sumOddNumbers(int start, int end) {
        int sum = 0;
        for (int i = start; i <= end; i++) {
            if (i % 2 == 0) continue;
            sum += i;
        }
        return sum;
    }
}

class ArraysTest {
    static void sort(int[] arr) {
        Arrays.sort(arr);
    }

    static int binarySearch(int[] arr, int key) {
        return Arrays.binarySearch(arr, key);
    }

    static int[] copyOf(int[] original, int newLength) {
        return Arrays.copyOf(original, newLength);
    }
}

class MethodsTest {
    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }

    int sum(int... numbers) {
        int total = 0;
        for (int n : numbers) {
            total += n;
        }
        return total;
    }

    int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    static void staticMethod() {
        System.out.println("  PASS: static method called");
    }
}