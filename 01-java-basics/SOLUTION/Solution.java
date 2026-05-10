package com.learning.lab.module01.solution;

public class Solution {
    public static void main(String[] args) {
        System.out.println("=== Module 01: Java Basics - Complete Solution ===\n");

        DataTypesAndVariables solution = new DataTypesAndVariables();
        solution.demonstrateAll();

        Operators solution2 = new Operators();
        solution2.demonstrateAll();

        ControlFlow solution3 = new ControlFlow();
        solution3.demonstrateAll();

        Arrays solution4 = new Arrays();
        solution4.demonstrateAll();

        Strings solution5 = new Strings();
        solution5.demonstrateAll();

        Methods solution6 = new Methods();
        solution6.demonstrateAll();
    }
}

class DataTypesAndVariables {
    public void demonstrateAll() {
        System.out.println("=== Data Types & Variables ===\n");

        demonstratePrimitiveTypes();
        demonstrateReferenceTypes();
        demonstrateTypeInference();
        demonstrateConstants();
    }

    private void demonstratePrimitiveTypes() {
        System.out.println("--- Primitive Types ---");

        byte byteMin = Byte.MIN_VALUE;
        byte byteMax = Byte.MAX_VALUE;
        System.out.println("byte: " + byteMin + " to " + byteMax);

        short shortMin = Short.MIN_VALUE;
        short shortMax = Short.MAX_VALUE;
        System.out.println("short: " + shortMin + " to " + shortMax);

        int intMin = Integer.MIN_VALUE;
        int intMax = Integer.MAX_VALUE;
        System.out.println("int: " + intMin + " to " + intMax);

        long longMin = Long.MIN_VALUE;
        long longMax = Long.MAX_VALUE;
        System.out.println("long: " + longMin + " to " + longMax);

        float floatMin = Float.MIN_VALUE;
        float floatMax = Float.MAX_VALUE;
        System.out.println("float: " + floatMin + " to " + floatMax);

        double doubleMin = Double.MIN_VALUE;
        double doubleMax = Double.MAX_VALUE;
        System.out.println("double: " + doubleMin + " to " + doubleMax);

        char charMin = Character.MIN_VALUE;
        char charMax = Character.MAX_VALUE;
        System.out.println("char: " + (int)charMin + " to " + (int)charMax);

        System.out.println("boolean: true or false\n");
    }

    private void demonstrateReferenceTypes() {
        System.out.println("--- Reference Types ---");

        String greeting = "Hello, Java!";
        System.out.println("String: " + greeting);

        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("Array: length = " + numbers.length);

        Object obj = new Object();
        System.out.println("Object: " + obj.toString() + "\n");
    }

    private void demonstrateTypeInference() {
        System.out.println("--- Type Inference (var keyword) ---");

        var number = 42;
        var text = "Inferred String";
        var decimal = 3.14;
        var array = new int[]{1, 2, 3};

        System.out.println("var number = " + number + " (type: Integer)");
        System.out.println("var text = " + text + " (type: String)");
        System.out.println("var decimal = " + decimal + " (type: Double)");
        System.out.println("var array = array of length " + array.length + "\n");
    }

    private void demonstrateConstants() {
        System.out.println("--- Constants (final keyword) ---");

        final double PI = 3.141592653589793;
        final int MAX_SIZE = 100;
        final String APP_NAME = "Java Learning Lab";

        System.out.println("final double PI = " + PI);
        System.out.println("final int MAX_SIZE = " + MAX_SIZE);
        System.out.println("final String APP_NAME = " + APP_NAME + "\n");
    }
}

class Operators {
    public void demonstrateAll() {
        System.out.println("=== Operators ===\n");

        demonstrateArithmeticOperators();
        demonstrateComparisonOperators();
        demonstrateLogicalOperators();
        demonstrateBitwiseOperators();
        demonstrateAssignmentOperators();
        demonstrateTernaryOperator();
    }

    private void demonstrateArithmeticOperators() {
        System.out.println("--- Arithmetic Operators ---");

        int a = 17, b = 4;
        System.out.println("a = " + a + ", b = " + b);
        System.out.println("a + b = " + (a + b));
        System.out.println("a - b = " + (a - b));
        System.out.println("a * b = " + (a * b));
        System.out.println("a / b = " + (a / b) + " (integer division)");
        System.out.println("a % b = " + (a % b) + " (remainder)\n");
    }

    private void demonstrateComparisonOperators() {
        System.out.println("--- Comparison Operators ---");

        int x = 10, y = 20;
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("x == y: " + (x == y));
        System.out.println("x != y: " + (x != y));
        System.out.println("x > y: " + (x > y));
        System.out.println("x < y: " + (x < y));
        System.out.println("x >= y: " + (x >= y));
        System.out.println("x <= y: " + (x <= y) + "\n");
    }

    private void demonstrateLogicalOperators() {
        System.out.println("--- Logical Operators ---");

        boolean p = true, q = false;
        System.out.println("p = " + p + ", q = " + q);
        System.out.println("p && q: " + (p && q));
        System.out.println("p || q: " + (p || q));
        System.out.println("!p: " + (!p) + "\n");
    }

    private void demonstrateBitwiseOperators() {
        System.out.println("--- Bitwise Operators ---");

        int m = 5, n = 3;
        System.out.println("m = " + m + " (binary: " + Integer.toBinaryString(m) + ")");
        System.out.println("n = " + n + " (binary: " + Integer.toBinaryString(n) + ")");
        System.out.println("m & n = " + (m & n) + " (AND)");
        System.out.println("m | n = " + (m | n) + " (OR)");
        System.out.println("m ^ n = " + (m ^ n) + " (XOR)");
        System.out.println("~m = " + (~m) + " (NOT)");
        System.out.println("m << 1 = " + (m << 1) + " (left shift)");
        System.out.println("m >> 1 = " + (m >> 1) + " (right shift)\n");
    }

    private void demonstrateAssignmentOperators() {
        System.out.println("--- Assignment Operators ---");

        int num = 10;
        System.out.println("Initial: num = " + num);

        num += 5;
        System.out.println("num += 5: " + num);

        num -= 3;
        System.out.println("num -= 3: " + num);

        num *= 2;
        System.out.println("num *= 2: " + num);

        num /= 4;
        System.out.println("num /= 4: " + num);

        num %= 3;
        System.out.println("num %= 3: " + num + "\n");
    }

    private void demonstrateTernaryOperator() {
        System.out.println("--- Ternary Operator ---");

        int age = 20;
        String status = age >= 18 ? "Adult" : "Minor";
        System.out.println("Age: " + age + ", Status: " + status + "\n");
    }
}

class ControlFlow {
    public void demonstrateAll() {
        System.out.println("=== Control Flow ===\n");

        demonstrateIfElse();
        demonstrateSwitch();
        demonstrateForLoop();
        demonstrateWhileLoop();
        demonstrateDoWhile();
        demonstrateBreakContinue();
    }

    private void demonstrateIfElse() {
        System.out.println("--- If-Else Statements ---");

        int number = -5;

        if (number > 0) {
            System.out.println(number + " is positive");
        } else if (number < 0) {
            System.out.println(number + " is negative");
        } else {
            System.out.println(number + " is zero");
        }

        boolean isEven = number % 2 == 0;
        if (isEven && number != 0) {
            System.out.println(number + " is even");
        }
        System.out.println();
    }

    private void demonstrateSwitch() {
        System.out.println("--- Switch Statement ---");

        String day = "Wednesday";
        int dayNumber;

        switch (day) {
            case "Monday":
                dayNumber = 1;
                break;
            case "Tuesday":
                dayNumber = 2;
                break;
            case "Wednesday":
                dayNumber = 3;
                break;
            case "Thursday":
                dayNumber = 4;
                break;
            case "Friday":
                dayNumber = 5;
                break;
            default:
                dayNumber = 0;
        }
        System.out.println("Day: " + day + " -> Number: " + dayNumber);

        String result = switch (day) {
            case "Monday", "Tuesday", "Wednesday", "Thursday", "Friday" -> "Weekday";
            case "Saturday", "Sunday" -> "Weekend";
            default -> "Unknown";
        };
        System.out.println("Type: " + result + "\n");
    }

    private void demonstrateForLoop() {
        System.out.println("--- For Loop ---");

        System.out.print("Count 1-5: ");
        for (int i = 1; i <= 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("Even numbers 2-10: ");
        for (int i = 2; i <= 10; i += 2) {
            System.out.print(i + " ");
        }
        System.out.println();

        int[] arr = {10, 20, 30};
        System.out.print("Array elements: ");
        for (int i = 0; i < arr.length; i++) {
            System.out.print("arr[" + i + "]=" + arr[i] + " ");
        }
        System.out.println("\n");
    }

    private void demonstrateWhileLoop() {
        System.out.println("--- While Loop ---");

        int count = 0;
        while (count < 3) {
            System.out.println("Count: " + count);
            count++;
        }
        System.out.println();

        int[] numbers = {1, 2, 3};
        int index = 0;
        while (index < numbers.length) {
            System.out.println("numbers[" + index + "] = " + numbers[index]);
            index++;
        }
        System.out.println();
    }

    private void demonstrateDoWhile() {
        System.out.println("--- Do-While Loop ---");

        int i = 0;
        do {
            System.out.println("Value: " + i);
            i++;
        } while (i < 3);
        System.out.println();

        boolean flag = false;
        do {
            System.out.println("Executes at least once");
        } while (flag);
        System.out.println();
    }

    private void demonstrateBreakContinue() {
        System.out.println("--- Break & Continue ---");

        System.out.print("Break at 5: ");
        for (int i = 1; i <= 10; i++) {
            if (i == 5) break;
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("Skip even numbers: ");
        for (int i = 1; i <= 10; i++) {
            if (i % 2 == 0) continue;
            System.out.print(i + " ");
        }
        System.out.println();
    }
}

class Arrays {
    public void demonstrateAll() {
        System.out.println("=== Arrays ===\n");

        demonstrateArrayCreation();
        demonstrateArrayManipulation();
        demonstrateMultiDimensionalArrays();
        demonstrateArrayUtilities();
    }

    private void demonstrateArrayCreation() {
        System.out.println("--- Array Creation ---");

        int[] numbers = {1, 2, 3, 4, 5};
        System.out.println("Initialized: " + java.util.Arrays.toString(numbers));

        String[] names = new String[3];
        names[0] = "Alice";
        names[1] = "Bob";
        names[2] = "Charlie";
        System.out.println("String array: " + java.util.Arrays.toString(names));

        double[] prices = new double[]{1.99, 2.99, 3.99};
        System.out.println("Double array: " + java.util.Arrays.toString(prices) + "\n");
    }

    private void demonstrateArrayManipulation() {
        System.out.println("--- Array Manipulation ---");

        int[] arr = {5, 2, 8, 1, 9};

        System.out.println("Original: " + java.util.Arrays.toString(arr));

        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) max = arr[i];
        }
        System.out.println("Max value: " + max);

        int sum = 0;
        for (int num : arr) {
            sum += num;
        }
        System.out.println("Sum: " + sum);
        System.out.println("Average: " + (double) sum / arr.length);

        java.util.Arrays.sort(arr);
        System.out.println("Sorted: " + java.util.Arrays.toString(arr) + "\n");
    }

    private void demonstrateMultiDimensionalArrays() {
        System.out.println("--- Multi-Dimensional Arrays ---");

        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        System.out.println("3x3 Matrix:");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }

        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1, 2};
        jagged[1] = new int[]{3, 4, 5};
        jagged[2] = new int[]{6};

        System.out.println("\nJagged Array:");
        for (int[] row : jagged) {
            System.out.println(java.util.Arrays.toString(row));
        }
        System.out.println();
    }

    private void demonstrateArrayUtilities() {
        System.out.println("--- Array Utilities ---");

        int[] arr1 = {1, 2, 3};
        int[] arr2 = {1, 2, 3};

        System.out.println("Arrays equal: " + java.util.Arrays.equals(arr1, arr2));

        int[] copy = java.util.Arrays.copyOf(arr1, 5);
        System.out.println("Copy with extra space: " + java.util.Arrays.toString(copy));

        int[] filled = new int[5];
        java.util.Arrays.fill(filled, 42);
        System.out.println("Filled array: " + java.util.Arrays.toString(filled));

        String searchResult = java.util.Arrays.toString(arr1);
        System.out.println("Binary search for 2: index = " + java.util.Arrays.binarySearch(arr1, 2) + "\n");
    }
}

class Strings {
    public void demonstrateAll() {
        System.out.println("=== Strings ===\n");

        demonstrateStringCreation();
        demonstrateStringOperations();
        demonstrateStringMethods();
        demonstrateStringBuilder();
        demonstrateStringBuffer();
    }

    private void demonstrateStringCreation() {
        System.out.println("--- String Creation ---");

        String literal = "Hello";
        String constructor = new String("World");
        char[] chars = {'J', 'a', 'v', 'a'};
        String fromChars = new String(chars);

        System.out.println("Literal: " + literal);
        System.out.println("Constructor: " + constructor);
        System.out.println("From char array: " + fromChars);
        System.out.println("String pool example: " + "Hello" + "\n");
    }

    private void demonstrateStringOperations() {
        System.out.println("--- String Operations ---");

        String str = "Java Programming";

        System.out.println("Original: " + str);
        System.out.println("Length: " + str.length());
        System.out.println("Char at index 5: " + str.charAt(5));
        System.out.println("Substring(5): " + str.substring(5));
        System.out.println("Substring(5, 15): " + str.substring(5, 15));
        System.out.println("toUpperCase: " + str.toUpperCase());
        System.out.println("toLowerCase: " + str.toLowerCase());
        System.out.println("contains 'gram': " + str.contains("gram"));
        System.out.println("indexOf 'a': " + str.indexOf('a'));
        System.out.println("lastIndexOf 'a': " + str.lastIndexOf('a'));
        System.out.println("startsWith 'Java': " + str.startsWith("Java"));
        System.out.println("endsWith 'ing': " + str.endsWith("ing") + "\n");
    }

    private void demonstrateStringMethods() {
        System.out.println("--- String Methods ---");

        String text = "  Hello, Java!  ";

        System.out.println("Original: '" + text + "'");
        System.out.println("trim(): '" + text.trim() + "'");
        System.out.println("replace: " + text.replace("Java", "World"));
        System.out.println("replaceAll: " + text.replaceAll("\\s+", "-"));

        String[] parts = "apple,banana,cherry".split(",");
        System.out.println("Split: " + java.util.Arrays.toString(parts));

        String joined = String.join(" - ", "One", "Two", "Three");
        System.out.println("Join: " + joined);

        String.format("Formatted: %d, %.2f, %s", 42, 3.14, "test");
        System.out.printf("printf style: %d, %.2f, %s%n", 42, 3.14, "test");

        String s1 = "Hello";
        String s2 = "hello";
        System.out.println("equals: " + s1.equals(s2));
        System.out.println("equalsIgnoreCase: " + s1.equalsIgnoreCase(s2));
        System.out.println("compareTo: " + s1.compareTo(s2) + "\n");
    }

    private void demonstrateStringBuilder() {
        System.out.println("--- StringBuilder ---");

        StringBuilder sb = new StringBuilder("Hello");
        sb.append(" World");
        sb.append("!");
        System.out.println("After append: " + sb.toString());

        sb.insert(6, "Beautiful ");
        System.out.println("After insert: " + sb.toString());

        sb.delete(6, 16);
        System.out.println("After delete: " + sb.toString());

        sb.reverse();
        System.out.println("After reverse: " + sb.toString());

        sb.reverse();
        sb.replace(0, 5, "Hi");
        System.out.println("After replace: " + sb.toString() + "\n");
    }

    private void demonstrateStringBuffer() {
        System.out.println("--- StringBuffer (Thread-Safe) ---");

        StringBuffer sf = new StringBuffer("Start");
        sf.append(" - Middle");
        sf.append(" - End");
        System.out.println("Appended: " + sf.toString());

        sf.insert(6, "[Insert] ");
        System.out.println("Inserted: " + sf.toString());

        System.out.println("Capacity: " + sf.capacity());
        System.out.println("Length: " + sf.length() + "\n");
    }
}

class Methods {
    public void demonstrateAll() {
        System.out.println("=== Methods ===\n");

        demonstrateMethodTypes();
        demonstrateMethodOverloading();
        demonstrateVarargs();
        demonstrateRecursion();
    }

    private void demonstrateMethodTypes() {
        System.out.println("--- Method Types ---");

        Methods demo = new Methods();

        demo.noReturnNoParam();

        int result = demo.withReturnNoParam();
        System.out.println("Returned: " + result);

        demo.noReturnWithParam(42, "Test");

        String greet = demo.withReturnWithParam("Alice");
        System.out.println("Greeting: " + greet);

        demo.staticMethod();

        System.out.println("Final variable: " + demo.FINAL_VALUE + "\n");
    }

    private void demonstrateMethodOverloading() {
        System.out.println("--- Method Overloading ---");

        Methods demo = new Methods();

        System.out.println(demo.add(5, 3));
        System.out.println(demo.add(5.0, 3.0));
        System.out.println(demo.add(5, 3, 2));
        System.out.println(demo.add("Hello", "World") + "\n");
    }

    private void demonstrateVarargs() {
        System.out.println("--- Variable Arguments (Varargs) ---");

        Methods demo = new Methods();

        demo.printValues(1, 2, 3);
        demo.printValues("a", "b", "c", "d");
        demo.calculateSum(1, 2, 3, 4, 5);
    }

    private void demonstrateRecursion() {
        System.out.println("--- Recursion ---");

        Methods demo = new Methods();

        System.out.println("Factorial of 5: " + demo.factorial(5));
        System.out.println("Fibonacci(10): " + demo.fibonacci(10));
        System.out.println("Power(2, 10): " + demo.power(2, 10));
        System.out.println("Sum of 1-10: " + demo.sumRange(1, 10));
    }

    void noReturnNoParam() {
        System.out.println("Method: noReturnNoParam");
    }

    int withReturnNoParam() {
        return 42;
    }

    void noReturnWithParam(int num, String text) {
        System.out.println("Method: noReturnWithParam - " + num + ", " + text);
    }

    String withReturnWithParam(String name) {
        return "Hello, " + name + "!";
    }

    static void staticMethod() {
        System.out.println("Static method called");
    }

    final int FINAL_VALUE = 100;

    int add(int a, int b) {
        return a + b;
    }

    double add(double a, double b) {
        return a + b;
    }

    int add(int a, int b, int c) {
        return a + b + c;
    }

    String add(String a, String b) {
        return a + " " + b;
    }

    void printValues(Object... values) {
        System.out.print("Varargs: ");
        for (Object val : values) {
            System.out.print(val + " ");
        }
        System.out.println();
    }

    void calculateSum(int... numbers) {
        int sum = 0;
        for (int num : numbers) {
            sum += num;
        }
        System.out.println("Sum: " + sum);
    }

    int factorial(int n) {
        if (n <= 1) return 1;
        return n * factorial(n - 1);
    }

    long fibonacci(int n) {
        if (n <= 1) return n;
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    double power(double base, int exp) {
        if (exp == 0) return 1;
        if (exp > 0) return base * power(base, exp - 1);
        return 1 / power(base, -exp);
    }

    int sumRange(int start, int end) {
        if (start > end) return 0;
        return start + sumRange(start + 1, end);
    }
}