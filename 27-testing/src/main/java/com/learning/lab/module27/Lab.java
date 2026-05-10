package com.learning.lab.module27;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class Lab {
    public static void main(String[] args) {
        System.out.println("=== Module 27: JUnit Tests, Mockito ===");

        basicAssertionsDemo();
        mockingDemo();
        stubbingDemo();
        argumentMatchersDemo();
        verifyDemo();
        spyDemo();
        exceptionTesting();
        parameterizedTestDemo();
    }

    static void basicAssertionsDemo() {
        System.out.println("\n--- JUnit Basic Assertions ---");
        assertEquals(10, 5 + 5);
        assertNotEquals(20, 5 + 5);
        assertTrue(10 > 5);
        assertFalse(5 > 10);
        assertNull(null);
        assertNotNull("value");
        assertSame("text", "text");
        assertArrayEquals(new int[]{1, 2}, new int[]{1, 2});
        System.out.println("All assertions passed");
    }

    static void mockingDemo() {
        System.out.println("\n--- Mockito Mocking ---");
        UserService mockService = mock(UserService.class);
        when(mockService.getUserById(1)).thenReturn(new User(1, "John"));

        User result = mockService.getUserById(1);
        assertEquals("John", result.getName());
        System.out.println("Mock created and tested");
    }

    static void stubbingDemo() {
        System.out.println("\n--- Mockito Stubbing ---");
        List<String> mockList = mock(List.class);

        when(mockList.get(0)).thenReturn("First");
        when(mockList.size()).thenReturn(100);

        assertEquals("First", mockList.get(0));
        assertEquals(100, mockList.size());

        when(mockList.get(anyInt())).thenThrow(new RuntimeException("Out of bounds"));
    }

    static void argumentMatchersDemo() {
        System.out.println("\n--- Argument Matchers ---");
        UserService mockService = mock(UserService.class);

        when(mockService.getUserById(anyInt())).thenReturn(new User(1, "Any"));
        when(mockService.saveUser(argThat(user -> user.getName().length() > 0))).thenReturn(true);

        assertNotNull(mockService.getUserById(999));
    }

    static void verifyDemo() {
        System.out.println("\n--- Mockito Verify ---");
        UserService mockService = mock(UserService.class);

        mockService.saveUser(new User(1, "John"));
        mockService.getUserById(1);

        verify(mockService).saveUser(any(User.class));
        verify(mockService, times(1)).getUserById(1);
        verify(mockService, never()).deleteUser(1);
        verify(mockService, atLeastOnce()).saveUser(any(User.class));

        System.out.println("Verification passed");
    }

    static void spyDemo() {
        System.out.println("\n--- Mockito Spy ---");
        List<String> list = new java.util.ArrayList<>();
        list.add("Original");
        List<String> spyList = spy(list);

        spyList.add("Added");
        when(spyList.size()).thenReturn(100);

        assertEquals(100, spyList.size());
        assertTrue(spyList.contains("Original"));
        System.out.println("Spy tested");
    }

    static void exceptionTesting() {
        System.out.println("\n--- Exception Testing ---");
        assertThrows(ArithmeticException.class, () -> {
            int x = 10 / 0;
        });

        Exception exception = assertThrows(RuntimeException.class, () -> {
            throw new RuntimeException("Test error");
        });
        assertEquals("Test error", exception.getMessage());
    }

    static void parameterizedTestDemo() {
        System.out.println("\n--- Parameterized Tests ---");
        System.out.println("Using @ParameterizedTest with @ValueSource");
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testWithParams(int number) {
        assertTrue(number > 0);
    }
}

class User {
    private int id;
    private String name;

    public User(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
}

interface UserService {
    User getUserById(int id);
    boolean saveUser(User user);
    boolean deleteUser(int id);
    List<User> getAllUsers();
}