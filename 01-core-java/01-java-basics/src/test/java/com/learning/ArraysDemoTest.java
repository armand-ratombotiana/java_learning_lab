package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for ArraysDemo class.
 * Tests all array operations and utility methods.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Arrays Demo Tests")
class ArraysDemoTest {
    
    @Test
    @DisplayName("Should demonstrate arrays without errors")
    void testDemonstrateArrays() {
        assertThatCode(() -> ArraysDemo.demonstrateArrays())
            .doesNotThrowAnyException();
    }
    
    @Test
    @DisplayName("Should find minimum value in array")
    void testFindMin() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.findMin(array)).isEqualTo(1);
    }
    
    @Test
    @DisplayName("Should find minimum in single element array")
    void testFindMinSingleElement() {
        int[] array = {42};
        assertThat(ArraysDemo.findMin(array)).isEqualTo(42);
    }
    
    @Test
    @DisplayName("Should find minimum with negative numbers")
    void testFindMinNegative() {
        int[] array = {-5, -2, -8, -1, -9};
        assertThat(ArraysDemo.findMin(array)).isEqualTo(-9);
    }
    
    @Test
    @DisplayName("Should throw exception for null array in findMin")
    void testFindMinNullArray() {
        assertThatThrownBy(() -> ArraysDemo.findMin(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Array cannot be null or empty");
    }
    
    @Test
    @DisplayName("Should throw exception for empty array in findMin")
    void testFindMinEmptyArray() {
        assertThatThrownBy(() -> ArraysDemo.findMin(new int[0]))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Array cannot be null or empty");
    }
    
    @Test
    @DisplayName("Should find maximum value in array")
    void testFindMax() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.findMax(array)).isEqualTo(9);
    }
    
    @Test
    @DisplayName("Should find maximum in single element array")
    void testFindMaxSingleElement() {
        int[] array = {42};
        assertThat(ArraysDemo.findMax(array)).isEqualTo(42);
    }
    
    @Test
    @DisplayName("Should find maximum with negative numbers")
    void testFindMaxNegative() {
        int[] array = {-5, -2, -8, -1, -9};
        assertThat(ArraysDemo.findMax(array)).isEqualTo(-1);
    }
    
    @Test
    @DisplayName("Should throw exception for null array in findMax")
    void testFindMaxNullArray() {
        assertThatThrownBy(() -> ArraysDemo.findMax(null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Array cannot be null or empty");
    }
    
    @Test
    @DisplayName("Should throw exception for empty array in findMax")
    void testFindMaxEmptyArray() {
        assertThatThrownBy(() -> ArraysDemo.findMax(new int[0]))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Array cannot be null or empty");
    }
    
    @Test
    @DisplayName("Should calculate sum correctly")
    void testCalculateSum() {
        int[] array = {1, 2, 3, 4, 5};
        assertThat(ArraysDemo.calculateSum(array)).isEqualTo(15);
    }
    
    @Test
    @DisplayName("Should calculate sum with negative numbers")
    void testCalculateSumNegative() {
        int[] array = {-1, -2, -3, -4, -5};
        assertThat(ArraysDemo.calculateSum(array)).isEqualTo(-15);
    }
    
    @Test
    @DisplayName("Should calculate sum with mixed numbers")
    void testCalculateSumMixed() {
        int[] array = {-5, 10, -3, 8};
        assertThat(ArraysDemo.calculateSum(array)).isEqualTo(10);
    }
    
    @Test
    @DisplayName("Should return zero for empty array sum")
    void testCalculateSumEmpty() {
        int[] array = {};
        assertThat(ArraysDemo.calculateSum(array)).isEqualTo(0);
    }
    
    @Test
    @DisplayName("Should calculate average correctly")
    void testCalculateAverage() {
        int[] array = {1, 2, 3, 4, 5};
        assertThat(ArraysDemo.calculateAverage(array)).isEqualTo(3.0);
    }
    
    @Test
    @DisplayName("Should calculate average with decimals")
    void testCalculateAverageDecimals() {
        int[] array = {1, 2, 3, 4};
        assertThat(ArraysDemo.calculateAverage(array)).isEqualTo(2.5);
    }
    
    @Test
    @DisplayName("Should return 0.0 for null array average")
    void testCalculateAverageNull() {
        assertThat(ArraysDemo.calculateAverage(null)).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("Should return 0.0 for empty array average")
    void testCalculateAverageEmpty() {
        assertThat(ArraysDemo.calculateAverage(new int[0])).isEqualTo(0.0);
    }
    
    @Test
    @DisplayName("Should perform linear search successfully")
    void testLinearSearch() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.linearSearch(array, 8)).isEqualTo(2);
        assertThat(ArraysDemo.linearSearch(array, 5)).isEqualTo(0);
        assertThat(ArraysDemo.linearSearch(array, 9)).isEqualTo(4);
    }
    
    @Test
    @DisplayName("Should return -1 when element not found")
    void testLinearSearchNotFound() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.linearSearch(array, 100)).isEqualTo(-1);
    }
    
    @Test
    @DisplayName("Should reverse array correctly")
    void testReverseArray() {
        int[] array = {1, 2, 3, 4, 5};
        int[] reversed = ArraysDemo.reverseArray(array);
        assertThat(reversed).containsExactly(5, 4, 3, 2, 1);
    }
    
    @Test
    @DisplayName("Should reverse single element array")
    void testReverseArraySingleElement() {
        int[] array = {42};
        int[] reversed = ArraysDemo.reverseArray(array);
        assertThat(reversed).containsExactly(42);
    }
    
    @Test
    @DisplayName("Should not modify original array when reversing")
    void testReverseArrayOriginalUnchanged() {
        int[] array = {1, 2, 3, 4, 5};
        int[] reversed = ArraysDemo.reverseArray(array);
        assertThat(array).containsExactly(1, 2, 3, 4, 5);
        assertThat(reversed).containsExactly(5, 4, 3, 2, 1);
    }
    
    @Test
    @DisplayName("Should check if array contains value")
    void testContains() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.contains(array, 8)).isTrue();
        assertThat(ArraysDemo.contains(array, 100)).isFalse();
    }
    
    @Test
    @DisplayName("Should handle contains with first element")
    void testContainsFirstElement() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.contains(array, 5)).isTrue();
    }
    
    @Test
    @DisplayName("Should handle contains with last element")
    void testContainsLastElement() {
        int[] array = {5, 2, 8, 1, 9};
        assertThat(ArraysDemo.contains(array, 9)).isTrue();
    }
    
    @Test
    @DisplayName("Should handle array with duplicate values")
    void testArrayWithDuplicates() {
        int[] array = {1, 2, 2, 3, 3, 3};
        assertThat(ArraysDemo.findMin(array)).isEqualTo(1);
        assertThat(ArraysDemo.findMax(array)).isEqualTo(3);
        assertThat(ArraysDemo.linearSearch(array, 2)).isEqualTo(1); // First occurrence
    }
}