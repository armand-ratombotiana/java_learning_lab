package com.learning.lab05;

import java.util.Arrays;

/**
 * Demonstrates multi-dimensional and jagged arrays.
 */
public class MultiDimensionalArrayExample {

    public static void showMultiDim() {
        System.out.println("=== Multi-Dimensional Arrays ===");

        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };

        System.out.println("Matrix:");
        for (int[] row : matrix) {
            System.out.println(Arrays.toString(row));
        }

        int[][] jagged = new int[3][];
        jagged[0] = new int[]{1, 2};
        jagged[1] = new int[]{3, 4, 5};
        jagged[2] = new int[]{6};

        System.out.println("Jagged array:");
        for (int[] row : jagged) {
            System.out.println(Arrays.toString(row));
        }

        int[][][] threeD = {{{1, 2}, {3, 4}}, {{5, 6}, {7, 8}}};
        System.out.println("3D array element [0][1][0]: " + threeD[0][1][0]);
    }
}
