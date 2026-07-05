package com.learning.lab28;

/**
 * Demonstrates enums with abstract methods — each constant providing its own implementation.
 */
public class EnumAbstractMethodExample {

    public static void showEnumAbstract() {
        System.out.println("=== Enum with Abstract Methods ===");

        for (Operation op : Operation.values()) {
            System.out.println("  " + op + ": 10 " + op.getSymbol() + " 5 = " + op.apply(10, 5));
        }
    }
}

enum Operation {
    ADD("+") {
        @Override public int apply(int a, int b) { return a + b; }
    },
    SUBTRACT("-") {
        @Override public int apply(int a, int b) { return a - b; }
    },
    MULTIPLY("*") {
        @Override public int apply(int a, int b) { return a * b; }
    },
    DIVIDE("/") {
        @Override public int apply(int a, int b) { return a / b; }
    };

    private final String symbol;

    Operation(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return symbol; }

    public abstract int apply(int a, int b);
}
