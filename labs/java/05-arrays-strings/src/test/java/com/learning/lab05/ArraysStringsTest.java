package com.learning.lab05;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ArraysStringsTest {

    @Test
    @DisplayName("Array creation and initialization")
    void arrayCreation() {
        int[] numbers = new int[5];
        assertEquals(5, numbers.length);
        int[] initialized = {1, 2, 3};
        assertEquals(3, initialized.length);
    }

    @Test
    @DisplayName("Array iteration and sum")
    void arraySum() {
        int[] nums = {1, 2, 3, 4, 5};
        int sum = 0;
        for (int n : nums) sum += n;
        assertEquals(15, sum);
    }

    @Test
    @DisplayName("Array index out of bounds")
    void arrayIndexOutOfBounds() {
        int[] arr = {1, 2, 3};
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> { int x = arr[5]; });
    }

    @Test
    @DisplayName("Multidimensional array")
    void multidimensionalArray() {
        int[][] matrix = {{1, 2}, {3, 4}};
        assertEquals(1, matrix[0][0]);
        assertEquals(4, matrix[1][1]);
    }

    @Test
    @DisplayName("String length and charAt")
    void stringLengthAndCharAt() {
        String s = "Hello";
        assertEquals(5, s.length());
        assertEquals('H', s.charAt(0));
        assertEquals('o', s.charAt(4));
    }

    @Test
    @DisplayName("String comparison with equals")
    void stringEquals() {
        String a = "hello";
        String b = new String("hello");
        assertTrue(a.equals(b));
        assertFalse(a == b);
    }

    @Test
    @DisplayName("String substring")
    void stringSubstring() {
        String s = "Hello World";
        assertEquals("Hello", s.substring(0, 5));
        assertEquals("World", s.substring(6));
    }

    @Test
    @DisplayName("String split")
    void stringSplit() {
        String csv = "a,b,c";
        String[] parts = csv.split(",");
        assertArrayEquals(new String[]{"a", "b", "c"}, parts);
    }

    @Test
    @DisplayName("StringBuilder append chain")
    void stringBuilderAppend() {
        StringBuilder sb = new StringBuilder();
        sb.append("a").append("b").append("c");
        assertEquals("abc", sb.toString());
    }

    @Test
    @DisplayName("Array copy using System.arraycopy")
    void arrayCopy() {
        int[] src = {1, 2, 3, 4, 5};
        int[] dest = new int[3];
        System.arraycopy(src, 1, dest, 0, 3);
        assertArrayEquals(new int[]{2, 3, 4}, dest);
    }
}
