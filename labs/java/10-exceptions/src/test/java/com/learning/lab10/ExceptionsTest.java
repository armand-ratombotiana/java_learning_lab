package com.learning.lab10;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionsTest {

    @Test
    @DisplayName("Try-catch handles exception")
    void tryCatchHandles() {
        try {
            throw new RuntimeException("error");
        } catch (RuntimeException e) {
            assertEquals("error", e.getMessage());
        }
    }

    @Test
    @DisplayName("Multiple catch blocks")
    void multipleCatches() {
        try {
            int[] arr = new int[5];
            int x = arr[10];
        } catch (ArrayIndexOutOfBoundsException e) {
            assertTrue(e.getMessage().contains("Index 10"));
        } catch (RuntimeException e) {
            fail("Should catch ArrayIndexOutOfBoundsException");
        }
    }

    @Test
    @DisplayName("Finally block always executes")
    void finallyExecutes() {
        var result = new StringBuilder();
        try {
            result.append("try");
            throw new RuntimeException();
        } catch (RuntimeException e) {
            result.append("catch");
        } finally {
            result.append("finally");
        }
        assertEquals("trycatchfinally", result.toString());
    }

    @Test
    @DisplayName("Try-with-resources closes AutoCloseable")
    void tryWithResources() throws Exception {
        var resource = new TestResource();
        try (resource) {
            assertEquals("open", resource.getState());
        }
        assertEquals("closed", resource.getState());
    }

    @Test
    @DisplayName("Try-with-resources closes multiple resources")
    void tryWithMultipleResources() throws Exception {
        var r1 = new TestResource();
        var r2 = new TestResource();
        try (r1; r2) {
            assertEquals("open", r1.getState());
            assertEquals("open", r2.getState());
        }
        assertEquals("closed", r1.getState());
        assertEquals("closed", r2.getState());
    }

    @Test
    @DisplayName("Custom exception can be thrown and caught")
    void customException() {
        assertThrows(BusinessException.class, () -> {
            throw new BusinessException("business error", 400);
        });
    }

    @Test
    @DisplayName("Custom exception has error code")
    void customExceptionErrorCode() {
        var ex = new BusinessException("invalid", 422);
        assertEquals(422, ex.getErrorCode());
        assertEquals("invalid", ex.getMessage());
    }

    @Test
    @DisplayName("Chained exception preserves cause")
    void chainedException() {
        var cause = new IllegalArgumentException("root cause");
        var ex = new RuntimeException("wrapper", cause);
        assertEquals(cause, ex.getCause());
        assertEquals("root cause", ex.getCause().getMessage());
    }

    @Test
    @DisplayName("Nested try-catch")
    void nestedTryCatch() {
        try {
            try {
                throw new RuntimeException("inner");
            } catch (RuntimeException e) {
                assertEquals("inner", e.getMessage());
                throw new RuntimeException("outer", e);
            }
        } catch (RuntimeException e) {
            assertEquals("outer", e.getMessage());
        }
    }

    @Test
    @DisplayName("Exception message and toString")
    void exceptionMessage() {
        var ex = new BusinessException("msg", 500);
        assertTrue(ex.toString().contains("BusinessException"));
        assertTrue(ex.toString().contains("msg"));
    }
}

class TestResource implements AutoCloseable {
    private String state = "open";
    public String getState() { return state; }
    public void close() { state = "closed"; }
}

class BusinessException extends Exception {
    private final int errorCode;
    public BusinessException(String message, int errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    public int getErrorCode() { return errorCode; }
}
