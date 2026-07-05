package com.learning.lab03;

/**
 * Demonstrates for, for-each, while, do-while loops with break and continue.
 */
public class LoopExample {

    public static void showLoops() {
        System.out.println("=== Loops ===");

        System.out.print("Standard for loop: ");
        for (int i = 0; i < 5; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        System.out.print("For-each loop: ");
        int[] numbers = {10, 20, 30, 40, 50};
        for (int n : numbers) {
            System.out.print(n + " ");
        }
        System.out.println();

        System.out.print("While loop with break: ");
        int i = 0;
        while (true) {
            if (i >= 4) break;
            System.out.print(i + " ");
            i++;
        }
        System.out.println();

        System.out.print("Do-while: ");
        int j = 0;
        do {
            System.out.print(j + " ");
            j++;
        } while (j < 3);
        System.out.println();

        System.out.print("Continue (skip even): ");
        for (int k = 1; k <= 10; k++) {
            if (k % 2 == 0) continue;
            System.out.print(k + " ");
        }
        System.out.println();

        System.out.print("Nested loops (multiplication): ");
        outer:
        for (int row = 1; row <= 3; row++) {
            for (int col = 1; col <= 3; col++) {
                if (row == 2 && col == 2) break outer;
                System.out.print((row * col) + " ");
            }
        }
        System.out.println("(break outer)");
    }
}
