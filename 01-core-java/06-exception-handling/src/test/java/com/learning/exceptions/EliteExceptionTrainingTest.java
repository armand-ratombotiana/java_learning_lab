package com.learning.exceptions;

import org.junit.jupiter.api.*;
import java.io.*;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for Elite Exception Handling Training
 *
 * Tests all 12 exception handling patterns with:
 * - Normal case scenarios
 * - Exception throwing scenarios
 * - Edge cases
 * - Recovery mechanisms
 *
 * Total: 36 test methods
 *
 * @author Elite Java Learning Platform
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Elite Exception Handling Training Tests")
class EliteExceptionTrainingTest {

    // ========================================================================
    // PROBLEM 1: Custom Exception Hierarchy Tests
    // ========================================================================

    @Test
    @Order(1)
    @DisplayName("Problem 1: InsufficientFundsException - Properties")
    void testInsufficientFundsException() {
        EliteExceptionTraining.InsufficientFundsException ex =
            new EliteExceptionTraining.InsufficientFundsException(100.0, 150.0);

        assertEquals(100.0, ex.getAvailable());
        assertEquals(150.0, ex.getRequested());
        assertEquals("INS_FUNDS_001", ex.getErrorCode());
        assertTrue(ex.getMessage().contains("100.00"));
        assertTrue(ex.getMessage().contains("150.00"));
        assertTrue(ex.getTimestamp() > 0);
    }

    @Test
    @Order(2)
    @DisplayName("Problem 1: InvalidAccountException - Properties")
    void testInvalidAccountException() {
        EliteExceptionTraining.InvalidAccountException ex =
            new EliteExceptionTraining.InvalidAccountException("ACC123");

        assertEquals("ACC123", ex.getAccountId());
        assertEquals("INV_ACCT_002", ex.getErrorCode());
        assertTrue(ex.getMessage().contains("ACC123"));
    }

    @Test
    @Order(3)
    @DisplayName("Problem 1: TransactionLimitExceededException - Properties")
    void testTransactionLimitExceededException() {
        EliteExceptionTraining.TransactionLimitExceededException ex =
            new EliteExceptionTraining.TransactionLimitExceededException(1000.0, 1500.0);

        assertEquals(1000.0, ex.getLimit());
        assertEquals(1500.0, ex.getAttempted());
        assertEquals("TXN_LIMIT_003", ex.getErrorCode());
    }

    @Test
    @Order(4)
    @DisplayName("Problem 1: Exception Hierarchy - Inheritance")
    void testExceptionHierarchy() {
        EliteExceptionTraining.BankingException baseEx =
            new EliteExceptionTraining.InsufficientFundsException(50.0, 100.0);

        assertInstanceOf(EliteExceptionTraining.BankingException.class, baseEx);
        assertInstanceOf(Exception.class, baseEx);
    }

    // ========================================================================
    // PROBLEM 2: Try-With-Resources Tests
    // ========================================================================

    @Test
    @Order(5)
    @DisplayName("Problem 2: Try-With-Resources - Successful Processing")
    void testTryWithResources_Success() throws IOException {
        List<String> results =
            EliteExceptionTraining.processMultipleResources(false);

        assertEquals(3, results.size());
        assertTrue(results.get(0).contains("Database"));
        assertTrue(results.get(1).contains("FileSystem"));
        assertTrue(results.get(2).contains("Network"));
    }

    @Test
    @Order(6)
    @DisplayName("Problem 2: CustomResource - Auto Close")
    void testCustomResource_AutoClose() throws IOException {
        EliteExceptionTraining.CustomResource resource =
            new EliteExceptionTraining.CustomResource("TestResource", false);

        try (resource) {
            resource.use();
            assertFalse(resource.isClosed());
        }

        assertTrue(resource.isClosed());
    }

    @Test
    @Order(7)
    @DisplayName("Problem 2: CustomResource - Close Exception")
    void testCustomResource_CloseException() {
        assertThrows(IOException.class, () -> {
            try (EliteExceptionTraining.CustomResource r =
                     new EliteExceptionTraining.CustomResource("TestResource", true)) {
                r.use();
            }
        });
    }

    @Test
    @Order(8)
    @DisplayName("Problem 2: CustomResource - Already Closed")
    void testCustomResource_AlreadyClosed() throws IOException {
        EliteExceptionTraining.CustomResource resource =
            new EliteExceptionTraining.CustomResource("TestResource", false);

        resource.close();
        assertTrue(resource.isClosed());

        // Second close should be idempotent
        assertDoesNotThrow(() -> resource.close());
        assertTrue(resource.isClosed());
    }

    // ========================================================================
    // PROBLEM 3: Exception Chaining Tests
    // ========================================================================

    @Test
    @Order(9)
    @DisplayName("Problem 3: Exception Chaining - Success")
    void testExceptionChaining_Success() {
        assertDoesNotThrow(() ->
            EliteExceptionTraining.performDatabaseOperation(false)
        );
    }

    @Test
    @Order(10)
    @DisplayName("Problem 3: Exception Chaining - Failure with Chain")
    void testExceptionChaining_FailureWithChain() {
        EliteExceptionTraining.ServiceException ex = assertThrows(
            EliteExceptionTraining.ServiceException.class,
            () -> EliteExceptionTraining.performDatabaseOperation(true)
        );

        assertNotNull(ex.getCause());
        assertInstanceOf(EliteExceptionTraining.DataAccessException.class, ex.getCause());

        Throwable dataAccessEx = ex.getCause();
        assertNotNull(dataAccessEx.getCause());
        assertInstanceOf(SQLException.class, dataAccessEx.getCause());
    }

    @Test
    @Order(11)
    @DisplayName("Problem 3: Get Root Cause")
    void testGetRootCause() {
        try {
            EliteExceptionTraining.performDatabaseOperation(true);
        } catch (EliteExceptionTraining.ServiceException e) {
            Throwable root = EliteExceptionTraining.getRootCause(e);

            assertInstanceOf(SQLException.class, root);
            assertTrue(root.getMessage().contains("Connection timeout"));
        }
    }

    @Test
    @Order(12)
    @DisplayName("Problem 3: Root Cause - Single Exception")
    void testGetRootCause_SingleException() {
        Exception ex = new Exception("Test exception");
        Throwable root = EliteExceptionTraining.getRootCause(ex);

        assertSame(ex, root);
    }

    // ========================================================================
    // PROBLEM 4: Multi-Catch and File Processing Tests
    // ========================================================================

    @Test
    @Order(13)
    @DisplayName("Problem 4: File Processing - File Not Found")
    void testFileProcessing_FileNotFound() {
        EliteExceptionTraining.FileProcessingResult result =
            EliteExceptionTraining.processFile("nonexistent_file_12345.txt");

        assertFalse(result.isSuccess());
        assertNotNull(result.getError());
        assertTrue(result.getError().contains("not found") ||
                   result.getError().contains("I/O error"));
        assertNull(result.getContent());
    }

    @Test
    @Order(14)
    @DisplayName("Problem 4: FileProcessingResult - Success Creation")
    void testFileProcessingResult_Success() {
        EliteExceptionTraining.FileProcessingResult result =
            EliteExceptionTraining.FileProcessingResult.success("Test content");

        assertTrue(result.isSuccess());
        assertEquals("Test content", result.getContent());
        assertNull(result.getError());
    }

    @Test
    @Order(15)
    @DisplayName("Problem 4: FileProcessingResult - Failure Creation")
    void testFileProcessingResult_Failure() {
        EliteExceptionTraining.FileProcessingResult result =
            EliteExceptionTraining.FileProcessingResult.failure("Test error");

        assertFalse(result.isSuccess());
        assertEquals("Test error", result.getError());
        assertNull(result.getContent());
    }

    // ========================================================================
    // PROBLEM 5: Exception in Constructor Tests
    // ========================================================================

    @Test
    @Order(16)
    @DisplayName("Problem 5: Constructor Exception - Failure")
    void testConstructorException_Failure() {
        SQLException ex = assertThrows(SQLException.class,
            () -> new EliteExceptionTraining.DatabaseConnection("jdbc:test", true)
        );

        assertTrue(ex.getMessage().contains("Failed to create database connection"));
        assertNotNull(ex.getCause());
    }

    @Test
    @Order(17)
    @DisplayName("Problem 5: DatabaseConnection - URL Property")
    void testDatabaseConnection_URLProperty() throws SQLException {
        EliteExceptionTraining.DatabaseConnection conn =
            new EliteExceptionTraining.DatabaseConnection("jdbc:test", false);

        assertEquals("jdbc:test", conn.getUrl());
    }

    // ========================================================================
    // PROBLEM 6: Retry Pattern Tests
    // ========================================================================

    @Test
    @Order(18)
    @DisplayName("Problem 6: Retry Pattern - Success on First Try")
    void testRetryPattern_SuccessFirstTry() throws Exception {
        EliteExceptionTraining.RetryableOperation<String> retry =
            new EliteExceptionTraining.RetryableOperation<>(3, 10, 2.0);

        String result = retry.execute(() -> "Success");

        assertEquals("Success", result);
    }

    @Test
    @Order(19)
    @DisplayName("Problem 6: Retry Pattern - Success After Retries")
    void testRetryPattern_SuccessAfterRetries() throws Exception {
        EliteExceptionTraining.RetryableOperation<Integer> retry =
            new EliteExceptionTraining.RetryableOperation<>(3, 10, 2.0);

        int[] attempts = {0};
        Integer result = retry.execute(() -> {
            attempts[0]++;
            if (attempts[0] < 3) {
                throw new IOException("Transient failure");
            }
            return attempts[0];
        });

        assertEquals(3, result);
        assertEquals(3, attempts[0]);
    }

    @Test
    @Order(20)
    @DisplayName("Problem 6: Retry Pattern - Non-Retryable Exception")
    void testRetryPattern_NonRetryableException() {
        EliteExceptionTraining.RetryableOperation<String> retry =
            new EliteExceptionTraining.RetryableOperation<>(3, 10, 2.0);

        // IllegalArgumentException is not retryable by default
        assertThrows(IllegalArgumentException.class, () ->
            retry.execute(() -> {
                throw new IllegalArgumentException("Not retryable");
            })
        );
    }

    @Test
    @Order(21)
    @DisplayName("Problem 6: Retry Pattern - Max Retries Exceeded")
    void testRetryPattern_MaxRetriesExceeded() {
        EliteExceptionTraining.RetryableOperation<String> retry =
            new EliteExceptionTraining.RetryableOperation<>(2, 10, 2.0);

        assertThrows(IOException.class, () ->
            retry.execute(() -> {
                throw new IOException("Always fails");
            })
        );
    }

    // ========================================================================
    // PROBLEM 7: Circuit Breaker Tests
    // ========================================================================

    @Test
    @Order(22)
    @DisplayName("Problem 7: Circuit Breaker - Initial State Closed")
    void testCircuitBreaker_InitialStateClosed() {
        EliteExceptionTraining.CircuitBreaker breaker =
            new EliteExceptionTraining.CircuitBreaker(3, 1000);

        assertEquals(EliteExceptionTraining.CircuitBreaker.State.CLOSED,
                     breaker.getState());
        assertEquals(0, breaker.getFailureCount());
    }

    @Test
    @Order(23)
    @DisplayName("Problem 7: Circuit Breaker - Success in Closed State")
    void testCircuitBreaker_SuccessInClosedState() throws Exception {
        EliteExceptionTraining.CircuitBreaker breaker =
            new EliteExceptionTraining.CircuitBreaker(3, 1000);

        String result = breaker.execute(() -> "Success");

        assertEquals("Success", result);
        assertEquals(EliteExceptionTraining.CircuitBreaker.State.CLOSED,
                     breaker.getState());
    }

    @Test
    @Order(24)
    @DisplayName("Problem 7: Circuit Breaker - Opens After Threshold")
    void testCircuitBreaker_OpensAfterThreshold() {
        EliteExceptionTraining.CircuitBreaker breaker =
            new EliteExceptionTraining.CircuitBreaker(3, 1000);

        // Cause 3 failures to open circuit
        for (int i = 0; i < 3; i++) {
            try {
                breaker.execute(() -> {
                    throw new Exception("Failure");
                });
            } catch (Exception ignored) {}
        }

        assertEquals(EliteExceptionTraining.CircuitBreaker.State.OPEN,
                     breaker.getState());
        assertEquals(3, breaker.getFailureCount());
    }

    @Test
    @Order(25)
    @DisplayName("Problem 7: Circuit Breaker - Fails Fast When Open")
    void testCircuitBreaker_FailsFastWhenOpen() {
        EliteExceptionTraining.CircuitBreaker breaker =
            new EliteExceptionTraining.CircuitBreaker(2, 1000);

        // Open the circuit
        for (int i = 0; i < 2; i++) {
            try {
                breaker.execute(() -> { throw new Exception("Failure"); });
            } catch (Exception ignored) {}
        }

        // Should fail fast without executing operation
        assertThrows(EliteExceptionTraining.CircuitBreakerOpenException.class,
            () -> breaker.execute(() -> "Should not execute")
        );
    }

    @Test
    @Order(26)
    @DisplayName("Problem 7: Circuit Breaker - Transitions to Half-Open")
    void testCircuitBreaker_TransitionsToHalfOpen() throws Exception {
        EliteExceptionTraining.CircuitBreaker breaker =
            new EliteExceptionTraining.CircuitBreaker(2, 100); // 100ms timeout

        // Open the circuit
        for (int i = 0; i < 2; i++) {
            try {
                breaker.execute(() -> { throw new Exception("Failure"); });
            } catch (Exception ignored) {}
        }

        assertEquals(EliteExceptionTraining.CircuitBreaker.State.OPEN,
                     breaker.getState());

        // Wait for timeout
        Thread.sleep(150);

        // Next call should transition to HALF_OPEN and succeed
        String result = breaker.execute(() -> "Success");

        assertEquals("Success", result);
        assertEquals(EliteExceptionTraining.CircuitBreaker.State.CLOSED,
                     breaker.getState());
    }

    // ========================================================================
    // PROBLEM 8: Exception Translation Tests
    // ========================================================================

    @Test
    @Order(27)
    @DisplayName("Problem 8: Exception Translation - User Not Found")
    void testExceptionTranslation_UserNotFound() {
        EliteExceptionTraining.UserService service =
            new EliteExceptionTraining.UserService();

        EliteExceptionTraining.UserNotFoundException ex = assertThrows(
            EliteExceptionTraining.UserNotFoundException.class,
            () -> service.getUser("notfound")
        );

        assertEquals(404, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("notfound"));
    }

    @Test
    @Order(28)
    @DisplayName("Problem 8: Exception Translation - Service Unavailable")
    void testExceptionTranslation_ServiceUnavailable() {
        EliteExceptionTraining.UserService service =
            new EliteExceptionTraining.UserService();

        EliteExceptionTraining.ServiceUnavailableException ex = assertThrows(
            EliteExceptionTraining.ServiceUnavailableException.class,
            () -> service.getUser("error")
        );

        assertEquals(503, ex.getStatusCode());
        assertTrue(ex.getMessage().contains("temporarily unavailable"));
    }

    @Test
    @Order(29)
    @DisplayName("Problem 8: Exception Translation - Success")
    void testExceptionTranslation_Success() throws EliteExceptionTraining.ApiException {
        EliteExceptionTraining.UserService service =
            new EliteExceptionTraining.UserService();

        String result = service.getUser("123");

        assertEquals("User-123", result);
    }

    // ========================================================================
    // PROBLEM 9: Async Exception Handling Tests
    // ========================================================================

    @Test
    @Order(30)
    @DisplayName("Problem 9: Async Processing - Success")
    void testAsyncProcessing_Success() throws Exception {
        List<String> items = List.of("item1", "item2", "item3");

        CompletableFuture<List<String>> future =
            EliteExceptionTraining.AsyncProcessor.processItems(items, false);

        List<String> results = future.get(5, TimeUnit.SECONDS);

        assertEquals(3, results.size());
        assertTrue(results.get(0).contains("item1"));
    }

    @Test
    @Order(31)
    @DisplayName("Problem 9: Async Processing - With Error Recovery")
    void testAsyncProcessing_WithErrorRecovery() throws Exception {
        List<String> items = List.of("item1", "error", "item3");

        CompletableFuture<List<String>> future =
            EliteExceptionTraining.AsyncProcessor.processItems(items, true);

        List<String> results = future.get(5, TimeUnit.SECONDS);

        assertEquals(3, results.size());
        assertTrue(results.get(1).contains("Failed"));
    }

    @Test
    @Order(32)
    @DisplayName("Problem 9: Chained Processing - Success")
    void testChainedProcessing_Success() throws Exception {
        CompletableFuture<String> future =
            EliteExceptionTraining.AsyncProcessor.chainedProcessing("test");

        String result = future.get(5, TimeUnit.SECONDS);

        assertEquals("Transformed-TEST-Enriched", result);
    }

    @Test
    @Order(33)
    @DisplayName("Problem 9: Chained Processing - Error Handling")
    void testChainedProcessing_ErrorHandling() throws Exception {
        CompletableFuture<String> future =
            EliteExceptionTraining.AsyncProcessor.chainedProcessing("");

        String result = future.get(5, TimeUnit.SECONDS);

        assertTrue(result.contains("Error"));
    }

    // ========================================================================
    // PROBLEM 10: Stream Exception Handling Tests
    // ========================================================================

    @Test
    @Order(34)
    @DisplayName("Problem 10: Try Wrapper - Success")
    void testTryWrapper_Success() {
        EliteExceptionTraining.Try<String> result =
            EliteExceptionTraining.Try.of(() -> "Success");

        assertTrue(result.isSuccess());
        assertEquals("Success", result.getValue());
        assertNull(result.getException());
    }

    @Test
    @Order(35)
    @DisplayName("Problem 10: Try Wrapper - Failure")
    void testTryWrapper_Failure() {
        EliteExceptionTraining.Try<String> result =
            EliteExceptionTraining.Try.of(() -> {
                throw new Exception("Test exception");
            });

        assertFalse(result.isSuccess());
        assertNull(result.getValue());
        assertNotNull(result.getException());
        assertEquals("Test exception", result.getException().getMessage());
    }

    @Test
    @Order(36)
    @DisplayName("Problem 10: Stream Processing - Filters Failures")
    void testStreamProcessing_FiltersFailures() {
        List<String> filenames = List.of("file1.txt", "file2.txt");

        List<String> results =
            EliteExceptionTraining.processFilesInStream(filenames);

        // Since files don't exist, should return empty list
        assertNotNull(results);
    }

    // ========================================================================
    // PROBLEM 11: Global Exception Handler Tests
    // ========================================================================

    @Test
    @Order(37)
    @DisplayName("Problem 11: Global Handler - IllegalArgumentException")
    void testGlobalHandler_IllegalArgumentException() {
        EliteExceptionTraining.GlobalExceptionHandler handler =
            new EliteExceptionTraining.GlobalExceptionHandler(
                (e, ctx) -> {} // No-op reporter
            );

        String message = handler.handleException(
            new IllegalArgumentException("Test error"),
            Map.of()
        );

        assertTrue(message.contains("Validation error"));
    }

    @Test
    @Order(38)
    @DisplayName("Problem 11: Global Handler - IOException")
    void testGlobalHandler_IOException() {
        EliteExceptionTraining.GlobalExceptionHandler handler =
            new EliteExceptionTraining.GlobalExceptionHandler(
                (e, ctx) -> {}
            );

        String message = handler.handleException(
            new IOException("Test I/O error"),
            Map.of()
        );

        assertTrue(message.contains("I/O operation failed"));
    }

    @Test
    @Order(39)
    @DisplayName("Problem 11: Global Handler - Unknown Exception")
    void testGlobalHandler_UnknownException() {
        EliteExceptionTraining.GlobalExceptionHandler handler =
            new EliteExceptionTraining.GlobalExceptionHandler(
                (e, ctx) -> {}
            );

        String message = handler.handleException(
            new Exception("Unknown error"),
            Map.of()
        );

        assertTrue(message.contains("unexpected error"));
    }

    @Test
    @Order(40)
    @DisplayName("Problem 11: Global Handler - Custom Handler Registration")
    void testGlobalHandler_CustomHandlerRegistration() {
        EliteExceptionTraining.GlobalExceptionHandler handler =
            new EliteExceptionTraining.GlobalExceptionHandler(
                (e, ctx) -> {}
            );

        handler.registerHandler(
            NullPointerException.class,
            e -> "Custom NPE handler"
        );

        String message = handler.handleException(
            new NullPointerException(),
            Map.of()
        );

        assertEquals("Custom NPE handler", message);
    }

    // ========================================================================
    // PROBLEM 12: Transaction Manager Tests
    // ========================================================================

    @Test
    @Order(41)
    @DisplayName("Problem 12: Transaction - Begin and Commit")
    void testTransaction_BeginAndCommit() {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        assertFalse(tm.isInTransaction());

        tm.beginTransaction();
        assertTrue(tm.isInTransaction());

        tm.commit();
        assertFalse(tm.isInTransaction());
    }

    @Test
    @Order(42)
    @DisplayName("Problem 12: Transaction - Execute Operations")
    void testTransaction_ExecuteOperations() throws Exception {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        List<Integer> executionOrder = new ArrayList<>();

        EliteExceptionTraining.TransactionManager.Operation op1 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() { executionOrder.add(1); }
                public void rollback() { executionOrder.add(-1); }
            };

        EliteExceptionTraining.TransactionManager.Operation op2 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() { executionOrder.add(2); }
                public void rollback() { executionOrder.add(-2); }
            };

        tm.beginTransaction();
        tm.execute(op1);
        tm.execute(op2);
        tm.commit();

        assertEquals(List.of(1, 2), executionOrder);
        assertEquals(0, tm.getExecutedOperationCount());
    }

    @Test
    @Order(43)
    @DisplayName("Problem 12: Transaction - Rollback on Failure")
    void testTransaction_RollbackOnFailure() {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        List<Integer> rollbackOrder = new ArrayList<>();

        EliteExceptionTraining.TransactionManager.Operation op1 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() {}
                public void rollback() { rollbackOrder.add(1); }
            };

        EliteExceptionTraining.TransactionManager.Operation op2 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() {}
                public void rollback() { rollbackOrder.add(2); }
            };

        EliteExceptionTraining.TransactionManager.Operation op3 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() throws Exception {
                    throw new Exception("Operation 3 failed");
                }
                public void rollback() { rollbackOrder.add(3); }
            };

        assertThrows(Exception.class, () ->
            tm.executeInTransaction(List.of(op1, op2, op3))
        );

        // Should rollback in reverse order
        assertEquals(List.of(2, 1), rollbackOrder);
        assertFalse(tm.isInTransaction());
    }

    @Test
    @Order(44)
    @DisplayName("Problem 12: Transaction - Cannot Begin Twice")
    void testTransaction_CannotBeginTwice() {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        tm.beginTransaction();

        assertThrows(IllegalStateException.class, tm::beginTransaction);

        tm.rollback();
    }

    @Test
    @Order(45)
    @DisplayName("Problem 12: Transaction - Execute Without Begin Throws")
    void testTransaction_ExecuteWithoutBeginThrows() {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        EliteExceptionTraining.TransactionManager.Operation op =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() {}
                public void rollback() {}
            };

        assertThrows(IllegalStateException.class, () -> tm.execute(op));
    }

    @Test
    @Order(46)
    @DisplayName("Problem 12: Transaction - Commit Without Begin Throws")
    void testTransaction_CommitWithoutBeginThrows() {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        assertThrows(IllegalStateException.class, tm::commit);
    }

    @Test
    @Order(47)
    @DisplayName("Problem 12: Transaction - Rollback Without Begin Is Safe")
    void testTransaction_RollbackWithoutBeginIsSafe() {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        assertDoesNotThrow(tm::rollback);
        assertFalse(tm.isInTransaction());
    }

    @Test
    @Order(48)
    @DisplayName("Problem 12: Transaction - Successful Execution")
    void testTransaction_SuccessfulExecution() throws Exception {
        EliteExceptionTraining.TransactionManager tm =
            new EliteExceptionTraining.TransactionManager();

        List<String> results = new ArrayList<>();

        EliteExceptionTraining.TransactionManager.Operation op1 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() { results.add("op1"); }
                public void rollback() { results.remove("op1"); }
            };

        EliteExceptionTraining.TransactionManager.Operation op2 =
            new EliteExceptionTraining.TransactionManager.Operation() {
                public void execute() { results.add("op2"); }
                public void rollback() { results.remove("op2"); }
            };

        tm.executeInTransaction(List.of(op1, op2));

        assertEquals(List.of("op1", "op2"), results);
        assertFalse(tm.isInTransaction());
    }
}
