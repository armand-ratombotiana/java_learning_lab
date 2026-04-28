package com.learning;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.*;

/**
 * Comprehensive test suite for DataTypes class.
 * Tests all primitive types, reference types, and type conversions.
 * 
 * @author Java Learning Team
 * @version 1.0
 */
@DisplayName("Data Types Tests")
class DataTypesTest {
    
    @Test
    @DisplayName("Should demonstrate data types without errors")
    void testDemonstrateDataTypes() {
        assertThatCode(() -> DataTypes.demonstrateDataTypes())
            .doesNotThrowAnyException();
    }
    
    @ParameterizedTest
    @CsvSource({
        "byte, 1",
        "short, 2",
        "char, 2",
        "int, 4",
        "float, 4",
        "long, 8",
        "double, 8",
        "boolean, 1"
    })
    @DisplayName("Should return correct size in bytes for primitive types")
    void testGetSizeInBytes(String type, int expectedSize) {
        assertThat(DataTypes.getSizeInBytes(type)).isEqualTo(expectedSize);
    }
    
    @Test
    @DisplayName("Should return -1 for invalid type")
    void testGetSizeInBytesInvalidType() {
        assertThat(DataTypes.getSizeInBytes("invalid")).isEqualTo(-1);
    }
    
    @Test
    @DisplayName("Should handle case-insensitive type names")
    void testGetSizeInBytesCaseInsensitive() {
        assertThat(DataTypes.getSizeInBytes("INT")).isEqualTo(4);
        assertThat(DataTypes.getSizeInBytes("Int")).isEqualTo(4);
        assertThat(DataTypes.getSizeInBytes("DOUBLE")).isEqualTo(8);
    }
    
    @Test
    @DisplayName("Should validate byte range")
    void testIsWithinRangeByte() {
        assertThat(DataTypes.isWithinRange(127, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(-128, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(128, "byte")).isFalse();
        assertThat(DataTypes.isWithinRange(-129, "byte")).isFalse();
    }
    
    @Test
    @DisplayName("Should validate short range")
    void testIsWithinRangeShort() {
        assertThat(DataTypes.isWithinRange(32767, "short")).isTrue();
        assertThat(DataTypes.isWithinRange(-32768, "short")).isTrue();
        assertThat(DataTypes.isWithinRange(32768, "short")).isFalse();
        assertThat(DataTypes.isWithinRange(-32769, "short")).isFalse();
    }
    
    @Test
    @DisplayName("Should validate int range")
    void testIsWithinRangeInt() {
        assertThat(DataTypes.isWithinRange(Integer.MAX_VALUE, "int")).isTrue();
        assertThat(DataTypes.isWithinRange(Integer.MIN_VALUE, "int")).isTrue();
        assertThat(DataTypes.isWithinRange(0, "int")).isTrue();
    }
    
    @Test
    @DisplayName("Should validate long range")
    void testIsWithinRangeLong() {
        assertThat(DataTypes.isWithinRange(Long.MAX_VALUE, "long")).isTrue();
        assertThat(DataTypes.isWithinRange(Long.MIN_VALUE, "long")).isTrue();
        assertThat(DataTypes.isWithinRange(0, "long")).isTrue();
    }
    
    @Test
    @DisplayName("Should return false for invalid type in range check")
    void testIsWithinRangeInvalidType() {
        assertThat(DataTypes.isWithinRange(100, "invalid")).isFalse();
    }
    
    @Test
    @DisplayName("Should handle case-insensitive type names in range check")
    void testIsWithinRangeCaseInsensitive() {
        assertThat(DataTypes.isWithinRange(100, "BYTE")).isTrue();
        assertThat(DataTypes.isWithinRange(100, "Byte")).isTrue();
    }
    
    @Test
    @DisplayName("Should validate boundary values for byte")
    void testByteBoundaries() {
        assertThat(DataTypes.isWithinRange(Byte.MAX_VALUE, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(Byte.MIN_VALUE, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(Byte.MAX_VALUE + 1, "byte")).isFalse();
        assertThat(DataTypes.isWithinRange(Byte.MIN_VALUE - 1, "byte")).isFalse();
    }
    
    @Test
    @DisplayName("Should validate boundary values for short")
    void testShortBoundaries() {
        assertThat(DataTypes.isWithinRange(Short.MAX_VALUE, "short")).isTrue();
        assertThat(DataTypes.isWithinRange(Short.MIN_VALUE, "short")).isTrue();
        assertThat(DataTypes.isWithinRange(Short.MAX_VALUE + 1, "short")).isFalse();
        assertThat(DataTypes.isWithinRange(Short.MIN_VALUE - 1, "short")).isFalse();
    }
    
    @Test
    @DisplayName("Should validate zero for all types")
    void testZeroForAllTypes() {
        assertThat(DataTypes.isWithinRange(0, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(0, "short")).isTrue();
        assertThat(DataTypes.isWithinRange(0, "int")).isTrue();
        assertThat(DataTypes.isWithinRange(0, "long")).isTrue();
    }
    
    @Test
    @DisplayName("Should handle positive and negative values")
    void testPositiveAndNegativeValues() {
        assertThat(DataTypes.isWithinRange(100, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(-100, "byte")).isTrue();
        assertThat(DataTypes.isWithinRange(1000, "short")).isTrue();
        assertThat(DataTypes.isWithinRange(-1000, "short")).isTrue();
    }
}