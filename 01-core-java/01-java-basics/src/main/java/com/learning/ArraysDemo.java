package com.learning;

import java.util.Arrays;

/**
 * Demonstrates Java arrays including single-dimensional, multi-dimensional,
 * array operations, and common array methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
public class ArraysDemo {
    
    /**
     * Demonstrates all array concepts in Java.
     */
    public static void demonstrateArrays() {
        demonstrateSingleDimensionalArrays();
        demonstrateMultiDimensionalArrays();
        demonstrateArrayOperations();
        demonstrateArrayMethods();
        demonstrateArrayCopying();
    }
    
    /**
     * Demonstrates single-dimensional arrays.
     */
    private static void demonstrateSingleDimensionalArrays() {
        System.out.println("Single-Dimensional Arrays:");
        
        // Declaration and initialization
        int[] numbers1 = new int[5]; // Array of size 5, initialized to 0
        int[] numbers2 = {1, 2, 3, 4, 5}; // Array literal
        int[] numbers3 = new int[]{10, 20, 30, 40, 50}; // Explicit initialization
        
        // Accessing elements
        System.out.println("First element of numbers2: " + numbers2[0]);
        System.out.println("Last element of numbers2: " + numbers2[numbers2.length - 1]);
        
        // Modifying elements
        numbers1[0] = 100;
        numbers1[1] = 200;
        System.out.println("Modified numbers1[0]: " + numbers1[0]);
        
        // Array length
        System.out.println("Length of numbers2: " + numbers2.length);
        
        // Iterating through array
        System.out.print("numbers2 elements: ");
        for (int i = 0; i < numbers2.length; i++) {
            System.out.print(numbers2[i] + " ");
        }
        System.out.println();
        
        // Enhanced for loop
        System.out.print("numbers3 elements: ");
        for (int num : numbers3) {
            System.out.print(num + " ");
        }
        System.out.println();
        
        // String array
        String[] fruits = {"Apple", "Banana", "Orange", "Mango"};
        System.out.println("Fruits: " + Arrays.toString(fruits));
    }
    
    /**
     * Demonstrates multi-dimensional arrays.
     */
    private static void demonstrateMultiDimensionalArrays() {
        System.out.println("\nMulti-Dimensional Arrays:");
        
        // 2D array (matrix)
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        
        System.out.println("2D Array (Matrix):");
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + " ");
            }
            System.out.println();
        }
        
        // Jagged array (array of arrays with different lengths)
        int[][] jagged = {
            {1, 2},
            {3, 4, 5},
            {6, 7, 8, 9}
        };
        
        System.out.println("\nJagged Array:");
        for (int i = 0; i < jagged.length; i++) {
            System.out.println("Row " + i + ": " + Arrays.toString(jagged[i]));
        }
        
        // 3D array
        int[][][] cube = {
            {{1, 2}, {3, 4}},
            {{5, 6}, {7, 8}}
        };
        
        System.out.println("\n3D Array element [0][1][0]: " + cube[0][1][0]);
    }
    
    /**
     * Demonstrates common array operations.
     */
    private static void demonstrateArrayOperations() {
        System.out.println("\nArray Operations:");
        
        int[] numbers = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        
        // Finding minimum and maximum
        int min = findMin(numbers);
        int max = findMax(numbers);
        System.out.println("Original array: " + Arrays.toString(numbers));
        System.out.println("Minimum: " + min);
        System.out.println("Maximum: " + max);
        
        // Calculating sum and average
        int sum = calculateSum(numbers);
        double average = calculateAverage(numbers);
        System.out.println("Sum: " + sum);
        System.out.println("Average: " + average);
        
        // Searching for element
        int searchValue = 7;
        int index = linearSearch(numbers, searchValue);
        System.out.println("Index of " + searchValue + ": " + index);
        
        // Reversing array
        int[] reversed = reverseArray(numbers);
        System.out.println("Reversed array: " + Arrays.toString(reversed));
    }
    
    /**
     * Demonstrates Arrays utility class methods.
     */
    private static void demonstrateArrayMethods() {
        System.out.println("\nArrays Utility Methods:");
        
        int[] numbers = {5, 2, 8, 1, 9, 3, 7, 4, 6};
        
        // Sorting
        int[] sorted = numbers.clone();
        Arrays.sort(sorted);
        System.out.println("Original: " + Arrays.toString(numbers));
        System.out.println("Sorted: " + Arrays.toString(sorted));
        
        // Binary search (requires sorted array)
        int searchValue = 7;
        int index = Arrays.binarySearch(sorted, searchValue);
        System.out.println("Binary search for " + searchValue + ": index " + index);
        
        // Filling array
        int[] filled = new int[5];
        Arrays.fill(filled, 42);
        System.out.println("Filled array: " + Arrays.toString(filled));
        
        // Comparing arrays
        int[] array1 = {1, 2, 3};
        int[] array2 = {1, 2, 3};
        int[] array3 = {1, 2, 4};
        System.out.println("array1 equals array2: " + Arrays.equals(array1, array2));
        System.out.println("array1 equals array3: " + Arrays.equals(array1, array3));
        
        // Converting to string
        System.out.println("Array as string: " + Arrays.toString(numbers));
    }
    
    /**
     * Demonstrates array copying techniques.
     */
    private static void demonstrateArrayCopying() {
        System.out.println("\nArray Copying:");
        
        int[] original = {1, 2, 3, 4, 5};
        
        // Method 1: clone()
        int[] copy1 = original.clone();
        System.out.println("Clone: " + Arrays.toString(copy1));
        
        // Method 2: Arrays.copyOf()
        int[] copy2 = Arrays.copyOf(original, original.length);
        System.out.println("copyOf: " + Arrays.toString(copy2));
        
        // Method 3: Arrays.copyOfRange()
        int[] copy3 = Arrays.copyOfRange(original, 1, 4); // From index 1 to 3
        System.out.println("copyOfRange(1, 4): " + Arrays.toString(copy3));
        
        // Method 4: System.arraycopy()
        int[] copy4 = new int[original.length];
        System.arraycopy(original, 0, copy4, 0, original.length);
        System.out.println("System.arraycopy: " + Arrays.toString(copy4));
        
        // Shallow vs Deep copy demonstration
        System.out.println("\nShallow vs Deep Copy:");
        String[] strings1 = {"Hello", "World"};
        String[] strings2 = strings1.clone(); // Shallow copy
        strings2[0] = "Hi";
        System.out.println("Original: " + Arrays.toString(strings1));
        System.out.println("Modified copy: " + Arrays.toString(strings2));
    }
    
    /**
     * Finds the minimum value in an array.
     * 
     * @param array the array to search
     * @return the minimum value
     */
    public static int findMin(int[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        
        int min = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
            }
        }
        return min;
    }
    
    /**
     * Finds the maximum value in an array.
     * 
     * @param array the array to search
     * @return the maximum value
     */
    public static int findMax(int[] array) {
        if (array == null || array.length == 0) {
            throw new IllegalArgumentException("Array cannot be null or empty");
        }
        
        int max = array[0];
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
            }
        }
        return max;
    }
    
    /**
     * Calculates the sum of all elements in an array.
     * 
     * @param array the array
     * @return the sum
     */
    public static int calculateSum(int[] array) {
        int sum = 0;
        for (int num : array) {
            sum += num;
        }
        return sum;
    }
    
    /**
     * Calculates the average of all elements in an array.
     * 
     * @param array the array
     * @return the average
     */
    public static double calculateAverage(int[] array) {
        if (array == null || array.length == 0) {
            return 0.0;
        }
        return (double) calculateSum(array) / array.length;
    }
    
    /**
     * Performs linear search on an array.
     * 
     * @param array the array to search
     * @param target the value to find
     * @return the index of the target, or -1 if not found
     */
    public static int linearSearch(int[] array, int target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == target) {
                return i;
            }
        }
        return -1;
    }
    
    /**
     * Reverses an array.
     * 
     * @param array the array to reverse
     * @return a new reversed array
     */
    public static int[] reverseArray(int[] array) {
        int[] reversed = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            reversed[i] = array[array.length - 1 - i];
        }
        return reversed;
    }
    
    /**
     * Checks if an array contains a specific value.
     * 
     * @param array the array to search
     * @param value the value to find
     * @return true if found, false otherwise
     */
    public static boolean contains(int[] array, int value) {
        return linearSearch(array, value) != -1;
    }
}