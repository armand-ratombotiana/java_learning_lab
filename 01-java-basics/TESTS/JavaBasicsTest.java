package com.learning.lab.module01.tests;

import static org.junit.Assert.*;
import org.junit.Test;

public class JavaBasicsTest {
    
    @Test
    public void testVariables() {
        int age = 25;
        assertEquals(25, age);
        
        double salary = 50000.0;
        assertEquals(50000.0, salary, 0.01);
        
        String name = "John";
        assertEquals("John", name);
    }
    
    @Test
    public void testOperators() {
        assertEquals(8, 5 + 3);
        assertEquals(2, 5 - 3);
        assertEquals(15, 5 * 3);
        assertEquals(2, 7 / 3);
        assertEquals(1, 7 % 3);
    }
    
    @Test
    public void testControlFlow() {
        int age = 20;
        String result = (age >= 18) ? "Adult" : "Minor";
        assertEquals("Adult", result);
    }
    
    @Test
    public void testArrays() {
        int[] numbers = {1, 2, 3, 4, 5};
        assertEquals(5, numbers.length);
        assertEquals(1, numbers[0]);
        assertEquals(5, numbers[4]);
    }
    
    @Test
    public void testMethod() {
        assertEquals(8, add(5, 3));
    }
    
    private int add(int a, int b) {
        return a + b;
    }
}