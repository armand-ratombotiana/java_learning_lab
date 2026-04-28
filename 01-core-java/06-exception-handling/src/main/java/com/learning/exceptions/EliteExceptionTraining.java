package com.learning.exceptions;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;

/**
 * Elite Exception Handling Training - FAANG Interview Preparation
 *
 * 12 Advanced Exception Handling Problems organized by difficulty:
 * - Foundation Level (Problems 1-4): Basic exception patterns
 * - Intermediate Level (Problems 5-8): Complex exception scenarios
 * - Advanced Level (Problems 9-12): Enterprise-grade exception handling
 *
 * Each problem includes:
 * - Company attribution (Google, Amazon, Meta, Microsoft, Netflix, LinkedIn)
 * - Time/Space complexity analysis
 * - Interview tips and best practices
 * - Multiple approaches where applicable
 *
 * @author Elite Java Learning Platform
 * @version 1.0
 */
public class EliteExceptionTraining {

    /**
     * PROBLEM 1: Custom Exception Hierarchy
     * Companies: Google, Amazon
     * Difficulty: Easy-Medium
     *
     * Design a custom exception hierarchy for a banking application that can handle:
     * - Insufficient funds
     * - Invalid account
     * - Transaction limit exceeded
     *
     * Time Complexity: O(1) - exception creation and throwing
     * Space Complexity: O(1) - exception object storage
     *
     * Interview Tip: Discuss when to use checked vs unchecked exceptions.
     * Custom exceptions should extend Exception (checked) for recoverable errors
     * or RuntimeException (unchecked) for programming errors.
     */

    // Base exception for all banking errors
    public static class BankingException extends Exception {
        private final String errorCode;
        private final long timestamp;

        public BankingException(String message, String errorCode) {
            super(message);
            this.errorCode = errorCode;
            this.timestamp = System.currentTimeMillis();
        }

        public String getErrorCode() { return errorCode; }
        public long getTimestamp() { return timestamp; }
    }

    public static class InsufficientFundsException extends BankingException {
        private final double available;
        private final double requested;

        public InsufficientFundsException(double available, double requested) {
            super(String.format("Insufficient funds: available=%.2f, requested=%.2f",
                  available, requested), "INS_FUNDS_001");
            this.available = available;
            this.requested = requested;
        }

        public double getAvailable() { return available; }
        public double getRequested() { return requested; }
    }

    public static class InvalidAccountException extends BankingException {
        private final String accountId;

        public InvalidAccountException(String accountId) {
            super("Invalid account: " + accountId, "INV_ACCT_002");
            this.accountId = accountId;
        }

        public String getAccountId() { return accountId; }
    }

    public static class TransactionLimitExceededException extends BankingException {
        private final double limit;
        private final double attempted;

        public TransactionLimitExceededException(double limit, double attempted) {
            super(String.format("Transaction limit exceeded: limit=%.2f, attempted=%.2f",
                  limit, attempted), "TXN_LIMIT_003");
            this.limit = limit;
            this.attempted = attempted;
        }

        public double getLimit() { return limit; }
        public double getAttempted() { return attempted; }
    }

    /**
     * PROBLEM 2: Try-With-Resources Pattern
     * Companies: Amazon, Meta
     * Difficulty: Medium
     *
     * Implement a resource manager that properly closes multiple resources
     * using try-with-resources. Handle cases where closing fails.
     *
     * Time Complexity: O(n) - where n is number of resources
     * Space Complexity: O(1) - fixed resources
     *
     * Interview Tip: Explain AutoCloseable interface and suppressed exceptions.
     * Resources are closed in reverse order of creation.
     */

    public static class CustomResource implements AutoCloseable {
        private final String name;
        private boolean closed = false;
        private final boolean throwOnClose;

        public CustomResource(String name, boolean throwOnClose) {
            this.name = name;
            this.throwOnClose = throwOnClose;
            System.out.println("Opening resource: " + name);
        }

        public void use() throws IOException {
            if (closed) {
                throw new IOException("Resource already closed: " + name);
            }
            System.out.println("Using resource: " + name);
        }

        @Override
        public void close() throws IOException {
            if (closed) return;

            System.out.println("Closing resource: " + name);
            closed = true;

            if (throwOnClose) {
                throw new IOException("Error closing resource: " + name);
            }
        }

        public boolean isClosed() { return closed; }
        public String getName() { return name; }
    }

    /**
     * Process multiple resources with proper cleanup
     */
    public static List<String> processMultipleResources(boolean throwOnClose)
            throws IOException {
        List<String> results = new ArrayList<>();

        try (CustomResource r1 = new CustomResource("Database", throwOnClose);
             CustomResource r2 = new CustomResource("FileSystem", throwOnClose);
             CustomResource r3 = new CustomResource("Network", throwOnClose)) {

            r1.use();
            results.add(r1.getName() + " processed");

            r2.use();
            results.add(r2.getName() + " processed");

            r3.use();
            results.add(r3.getName() + " processed");

        } // Resources automatically closed in reverse order: r3, r2, r1

        return results;
    }

    /**
     * PROBLEM 3: Exception Chaining and Root Cause Analysis
     * Companies: Google, Microsoft
     * Difficulty: Medium
     *
     * Implement a method that chains exceptions properly to preserve the full
     * stack trace and implements root cause extraction.
     *
     * Time Complexity: O(d) - where d is depth of exception chain
     * Space Complexity: O(d) - call stack for recursion
     *
     * Interview Tip: Discuss exception chaining with initCause() or constructor.
     * Never swallow exceptions - always log or rethrow with context.
     */

    public static class DataAccessException extends Exception {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class ServiceException extends Exception {
        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * Simulates a layered application with exception chaining
     */
    public static void performDatabaseOperation(boolean shouldFail)
            throws ServiceException {
        try {
            databaseLayer(shouldFail);
        } catch (DataAccessException e) {
            // Chain the exception with additional context
            throw new ServiceException("Service layer failed during database operation", e);
        }
    }

    private static void databaseLayer(boolean shouldFail) throws DataAccessException {
        try {
            if (shouldFail) {
                // Simulate database error
                throw new SQLException("Connection timeout to database");
            }
        } catch (SQLException e) {
            // Wrap SQLException in application-specific exception
            throw new DataAccessException("Database access failed", e);
        }
    }

    /**
     * Extract root cause from exception chain
     */
    public static Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable;
        while (cause.getCause() != null && cause.getCause() != cause) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * PROBLEM 4: Multi-Catch and Exception Handling Best Practices
     * Companies: Amazon, Netflix
     * Difficulty: Medium
     *
     * Implement file processing with proper exception handling using multi-catch
     * and demonstrating best practices for exception handling.
     *
     * Time Complexity: O(n) - where n is file size
     * Space Complexity: O(1) - constant space
     *
     * Interview Tip: Use multi-catch for same handling logic, separate catches
     * for different handling. Order catches from specific to general.
     */

    public static class FileProcessingResult {
        private final boolean success;
        private final String content;
        private final String error;

        private FileProcessingResult(boolean success, String content, String error) {
            this.success = success;
            this.content = content;
            this.error = error;
        }

        public static FileProcessingResult success(String content) {
            return new FileProcessingResult(true, content, null);
        }

        public static FileProcessingResult failure(String error) {
            return new FileProcessingResult(false, null, error);
        }

        public boolean isSuccess() { return success; }
        public String getContent() { return content; }
        public String getError() { return error; }
    }

    /**
     * Process file with comprehensive exception handling
     */
    public static FileProcessingResult processFile(String filename) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
            return FileProcessingResult.success(content.toString());

        } catch (FileNotFoundException | NoSuchFileException e) {
            // Multi-catch for file not found scenarios
            return FileProcessingResult.failure("File not found: " + filename);

        } catch (IOException e) {
            // General I/O errors
            return FileProcessingResult.failure("I/O error reading file: " + e.getMessage());

        } catch (SecurityException e) {
            // Permission errors
            return FileProcessingResult.failure("Permission denied: " + filename);
        }
    }

    /**
     * PROBLEM 5: Exception in Constructor Pattern
     * Companies: Google, LinkedIn
     * Difficulty: Medium-Hard
     *
     * Implement proper exception handling in constructors, including
     * resource cleanup when constructor fails.
     *
     * Time Complexity: O(1) - constructor initialization
     * Space Complexity: O(1) - object allocation
     *
     * Interview Tip: If constructor throws exception, object is not created.
     * Clean up any partially initialized resources before throwing.
     */

    public static class DatabaseConnection {
        private Connection connection;
        private Statement statement;
        private final String url;

        public DatabaseConnection(String url, boolean shouldFail) throws SQLException {
            this.url = url;

            try {
                // Step 1: Establish connection
                this.connection = createConnection(url, shouldFail);

                // Step 2: Create statement (only if connection is not null)
                if (this.connection != null) {
                    this.statement = connection.createStatement();
                }

            } catch (SQLException e) {
                // Constructor failed - clean up any partially initialized resources
                cleanup();
                throw new SQLException("Failed to create database connection: " + url, e);
            }
        }

        private Connection createConnection(String url, boolean shouldFail) throws SQLException {
            if (shouldFail) {
                throw new SQLException("Connection refused");
            }
            // Return mock connection for demonstration
            return null;
        }

        private void cleanup() {
            // Close in reverse order, ignore errors during cleanup
            if (statement != null) {
                try { statement.close(); } catch (SQLException ignored) {}
            }
            if (connection != null) {
                try { connection.close(); } catch (SQLException ignored) {}
            }
        }

        public void close() throws SQLException {
            SQLException exception = null;

            // Try to close all resources, collecting exceptions
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                exception = e;
            }

            try {
                if (connection != null) connection.close();
            } catch (SQLException e) {
                if (exception != null) {
                    e.addSuppressed(exception);
                }
                throw e;
            }

            if (exception != null) throw exception;
        }

        public String getUrl() { return url; }
    }

    /**
     * PROBLEM 6: Retry Pattern with Exponential Backoff
     * Companies: Netflix, Amazon
     * Difficulty: Hard
     *
     * Implement a retry mechanism with exponential backoff for transient failures.
     * Handle different types of exceptions differently (retry vs fail fast).
     *
     * Time Complexity: O(r * t) - where r is retries, t is operation time
     * Space Complexity: O(1) - constant space
     *
     * Interview Tip: Distinguish between transient and permanent failures.
     * Use exponential backoff to avoid overwhelming failing services.
     */

    public static class RetryableOperation<T> {
        private final int maxRetries;
        private final long initialDelayMs;
        private final double backoffMultiplier;
        private final Set<Class<? extends Exception>> retryableExceptions;

        public RetryableOperation(int maxRetries, long initialDelayMs,
                                 double backoffMultiplier) {
            this.maxRetries = maxRetries;
            this.initialDelayMs = initialDelayMs;
            this.backoffMultiplier = backoffMultiplier;
            this.retryableExceptions = new HashSet<>();

            // Default retryable exceptions (transient failures)
            retryableExceptions.add(IOException.class);
            retryableExceptions.add(TimeoutException.class);
        }

        public T execute(Callable<T> operation) throws Exception {
            Exception lastException = null;
            long delay = initialDelayMs;

            for (int attempt = 0; attempt <= maxRetries; attempt++) {
                try {
                    return operation.call();

                } catch (Exception e) {
                    lastException = e;

                    // Check if exception is retryable
                    if (!isRetryable(e) || attempt == maxRetries) {
                        throw e;
                    }

                    // Wait before retry with exponential backoff
                    Thread.sleep(delay);
                    delay = (long) (delay * backoffMultiplier);
                }
            }

            // Should never reach here, but for completeness
            throw lastException;
        }

        private boolean isRetryable(Exception e) {
            for (Class<? extends Exception> retryableType : retryableExceptions) {
                if (retryableType.isInstance(e)) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * PROBLEM 7: Circuit Breaker Pattern
     * Companies: Netflix, Google
     * Difficulty: Hard
     *
     * Implement a circuit breaker pattern to prevent cascading failures.
     * Circuit has three states: CLOSED, OPEN, HALF_OPEN.
     *
     * Time Complexity: O(1) - state checking and updating
     * Space Complexity: O(n) - failure tracking window
     *
     * Interview Tip: Circuit breaker prevents calling failing services.
     * After timeout, allows test requests (HALF_OPEN) before fully recovering.
     */

    public static class CircuitBreaker {
        public enum State { CLOSED, OPEN, HALF_OPEN }

        private State state = State.CLOSED;
        private int failureCount = 0;
        private final int failureThreshold;
        private final long timeout;
        private long lastFailureTime = 0;

        public CircuitBreaker(int failureThreshold, long timeoutMs) {
            this.failureThreshold = failureThreshold;
            this.timeout = timeoutMs;
        }

        public <T> T execute(Callable<T> operation) throws Exception {
            // Check if circuit should transition to HALF_OPEN
            if (state == State.OPEN &&
                System.currentTimeMillis() - lastFailureTime >= timeout) {
                state = State.HALF_OPEN;
                failureCount = 0;
            }

            // Fail fast if circuit is OPEN
            if (state == State.OPEN) {
                throw new CircuitBreakerOpenException(
                    "Circuit breaker is OPEN - service unavailable");
            }

            try {
                T result = operation.call();

                // Success - reset on HALF_OPEN or stay CLOSED
                if (state == State.HALF_OPEN) {
                    state = State.CLOSED;
                    failureCount = 0;
                }

                return result;

            } catch (Exception e) {
                recordFailure();
                throw e;
            }
        }

        private void recordFailure() {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();

            if (failureCount >= failureThreshold) {
                state = State.OPEN;
            }
        }

        public State getState() { return state; }
        public int getFailureCount() { return failureCount; }
    }

    public static class CircuitBreakerOpenException extends Exception {
        public CircuitBreakerOpenException(String message) {
            super(message);
        }
    }

    /**
     * PROBLEM 8: Exception Translation in API Layer
     * Companies: Google, Amazon
     * Difficulty: Medium-Hard
     *
     * Implement proper exception translation from internal exceptions
     * to API-appropriate exceptions (hiding implementation details).
     *
     * Time Complexity: O(1) - exception translation
     * Space Complexity: O(1) - exception object
     *
     * Interview Tip: Don't expose internal exceptions to API clients.
     * Translate to domain-appropriate exceptions with meaningful messages.
     */

    // Internal exceptions (should not leak to API)
    private static class InternalDatabaseException extends Exception {
        public InternalDatabaseException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // API exceptions (safe to expose)
    public static class ApiException extends Exception {
        private final int statusCode;

        public ApiException(int statusCode, String message) {
            super(message);
            this.statusCode = statusCode;
        }

        public ApiException(int statusCode, String message, Throwable cause) {
            super(message, cause);
            this.statusCode = statusCode;
        }

        public int getStatusCode() { return statusCode; }
    }

    public static class UserNotFoundException extends ApiException {
        public UserNotFoundException(String userId) {
            super(404, "User not found: " + userId);
        }
    }

    public static class ServiceUnavailableException extends ApiException {
        public ServiceUnavailableException(String message) {
            super(503, "Service temporarily unavailable: " + message);
        }
    }

    /**
     * Repository layer - throws internal exceptions
     */
    private static class UserRepository {
        public String findUser(String userId) throws InternalDatabaseException {
            // Simulate database lookup
            if ("error".equals(userId)) {
                throw new InternalDatabaseException(
                    "Database query failed", new SQLException("Connection lost"));
            }
            if ("notfound".equals(userId)) {
                return null;
            }
            return "User-" + userId;
        }
    }

    /**
     * Service layer - translates internal exceptions to API exceptions
     */
    public static class UserService {
        private final UserRepository repository = new UserRepository();

        public String getUser(String userId) throws ApiException {
            try {
                String user = repository.findUser(userId);

                if (user == null) {
                    throw new UserNotFoundException(userId);
                }

                return user;

            } catch (InternalDatabaseException e) {
                // Translate internal exception to API exception
                // Log internal details, return generic message to client
                throw new ServiceUnavailableException(
                    "Unable to retrieve user data");
            }
        }
    }

    /**
     * PROBLEM 9: Async Exception Handling with CompletableFuture
     * Companies: Netflix, Microsoft
     * Difficulty: Hard
     *
     * Handle exceptions in asynchronous operations using CompletableFuture.
     * Implement proper error handling and recovery strategies.
     *
     * Time Complexity: O(n) - where n is number of async operations
     * Space Complexity: O(n) - futures storage
     *
     * Interview Tip: Use exceptionally() for recovery, handle() for both success/failure.
     * CompletableFuture wraps exceptions in CompletionException.
     */

    public static class AsyncProcessor {

        /**
         * Process items asynchronously with error handling
         */
        public static CompletableFuture<List<String>> processItems(
                List<String> items, boolean simulateError) {

            List<CompletableFuture<String>> futures = items.stream()
                .map(item -> processItemAsync(item, simulateError))
                .toList();

            // Combine all futures
            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                    .map(CompletableFuture::join)
                    .toList())
                .exceptionally(ex -> {
                    // Handle failures - return partial results or default
                    System.err.println("Batch processing failed: " + ex.getMessage());
                    return Collections.emptyList();
                });
        }

        private static CompletableFuture<String> processItemAsync(
                String item, boolean simulateError) {

            return CompletableFuture.supplyAsync(() -> {
                if (simulateError && "error".equals(item)) {
                    throw new RuntimeException("Processing failed for: " + item);
                }
                return "Processed: " + item;

            }).exceptionally(ex -> {
                // Recover from individual item failure
                return "Failed: " + item;
            });
        }

        /**
         * Chain async operations with error handling at each stage
         */
        public static CompletableFuture<String> chainedProcessing(String input) {
            return CompletableFuture.supplyAsync(() -> {
                // Stage 1: Validate
                if (input == null || input.isEmpty()) {
                    throw new IllegalArgumentException("Input cannot be empty");
                }
                return input.toUpperCase();

            }).thenApply(validated -> {
                // Stage 2: Transform
                return "Transformed-" + validated;

            }).thenApply(transformed -> {
                // Stage 3: Enrich
                return transformed + "-Enriched";

            }).exceptionally(ex -> {
                // Handle any exception in the chain
                return "Error: " + ex.getMessage();
            });
        }
    }

    /**
     * PROBLEM 10: Exception Handling in Streams
     * Companies: Google, LinkedIn
     * Difficulty: Medium-Hard
     *
     * Handle checked exceptions within Stream operations without breaking
     * the functional pipeline.
     *
     * Time Complexity: O(n) - stream processing
     * Space Complexity: O(1) - stream operations are lazy
     *
     * Interview Tip: Streams don't handle checked exceptions well.
     * Wrap in unchecked, use Try wrapper, or extract to method.
     */

    /**
     * Wrapper for operations that may throw checked exceptions
     */
    @FunctionalInterface
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Convert checked exception to unchecked for use in streams
     */
    public static <T, R> Function<T, R> unchecked(CheckedFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Try wrapper for handling exceptions in streams
     */
    public static class Try<T> {
        private final T value;
        private final Exception exception;

        private Try(T value, Exception exception) {
            this.value = value;
            this.exception = exception;
        }

        public static <T> Try<T> success(T value) {
            return new Try<>(value, null);
        }

        public static <T> Try<T> failure(Exception exception) {
            return new Try<>(null, exception);
        }

        public static <T> Try<T> of(Callable<T> callable) {
            try {
                return success(callable.call());
            } catch (Exception e) {
                return failure(e);
            }
        }

        public boolean isSuccess() { return exception == null; }
        public T getValue() { return value; }
        public Exception getException() { return exception; }
    }

    /**
     * Process files using streams with exception handling
     */
    public static List<String> processFilesInStream(List<String> filenames) {
        return filenames.stream()
            .map(filename -> Try.of(() -> Files.readString(Path.of(filename))))
            .filter(Try::isSuccess)
            .map(Try::getValue)
            .toList();
    }

    /**
     * PROBLEM 11: Global Exception Handler Pattern
     * Companies: Amazon, Meta
     * Difficulty: Medium-Hard
     *
     * Implement a global exception handler for an application with
     * proper logging, error reporting, and recovery strategies.
     *
     * Time Complexity: O(1) - exception handling
     * Space Complexity: O(1) - handler state
     *
     * Interview Tip: Centralized exception handling ensures consistent behavior.
     * Different exceptions may need different handling strategies.
     */

    public static class GlobalExceptionHandler {

        public interface ErrorReporter {
            void report(Exception e, Map<String, Object> context);
        }

        private final ErrorReporter errorReporter;
        private final Map<Class<? extends Exception>, ExceptionHandler<?>> handlers;

        public GlobalExceptionHandler(ErrorReporter errorReporter) {
            this.errorReporter = errorReporter;
            this.handlers = new HashMap<>();
            registerDefaultHandlers();
        }

        private void registerDefaultHandlers() {
            // Register specific handlers for different exception types
            registerHandler(IllegalArgumentException.class,
                e -> "Validation error: " + e.getMessage());

            registerHandler(IOException.class,
                e -> "I/O operation failed: " + e.getMessage());

            registerHandler(SQLException.class,
                e -> "Database error occurred - please try again later");
        }

        public <T extends Exception> void registerHandler(
                Class<T> exceptionType, ExceptionHandler<T> handler) {
            handlers.put(exceptionType, handler);
        }

        @SuppressWarnings("unchecked")
        public String handleException(Exception e, Map<String, Object> context) {
            // Report error for monitoring
            errorReporter.report(e, context);

            // Find specific handler
            ExceptionHandler<Exception> handler =
                (ExceptionHandler<Exception>) handlers.get(e.getClass());

            if (handler != null) {
                return handler.handle(e);
            }

            // Default handler
            return "An unexpected error occurred: " + e.getClass().getSimpleName();
        }
    }

    @FunctionalInterface
    public interface ExceptionHandler<T extends Exception> {
        String handle(T exception);
    }

    /**
     * PROBLEM 12: Transaction Exception Handling with Rollback
     * Companies: Google, Amazon
     * Difficulty: Hard
     *
     * Implement proper exception handling in transactional operations
     * with automatic rollback on failure.
     *
     * Time Complexity: O(n) - where n is number of operations
     * Space Complexity: O(n) - operation history for rollback
     *
     * Interview Tip: Ensure atomicity - all operations succeed or all rollback.
     * Track operations for rollback in reverse order.
     */

    public static class TransactionManager {

        public interface Operation {
            void execute() throws Exception;
            void rollback();
        }

        private final List<Operation> executedOperations = new ArrayList<>();
        private boolean inTransaction = false;

        public void beginTransaction() {
            if (inTransaction) {
                throw new IllegalStateException("Transaction already in progress");
            }
            inTransaction = true;
            executedOperations.clear();
        }

        public void execute(Operation operation) throws Exception {
            if (!inTransaction) {
                throw new IllegalStateException("No transaction in progress");
            }

            operation.execute();
            executedOperations.add(operation);
        }

        public void commit() {
            if (!inTransaction) {
                throw new IllegalStateException("No transaction in progress");
            }

            inTransaction = false;
            executedOperations.clear();
        }

        public void rollback() {
            if (!inTransaction) {
                return;
            }

            // Rollback operations in reverse order
            List<Operation> toRollback = new ArrayList<>(executedOperations);
            Collections.reverse(toRollback);

            for (Operation op : toRollback) {
                try {
                    op.rollback();
                } catch (Exception e) {
                    // Log but continue rolling back
                    System.err.println("Rollback failed: " + e.getMessage());
                }
            }

            inTransaction = false;
            executedOperations.clear();
        }

        /**
         * Execute operations within transaction, auto-rollback on failure
         */
        public void executeInTransaction(List<Operation> operations) throws Exception {
            beginTransaction();

            try {
                for (Operation op : operations) {
                    execute(op);
                }
                commit();

            } catch (Exception e) {
                rollback();
                throw new Exception("Transaction failed and rolled back", e);
            }
        }

        public boolean isInTransaction() { return inTransaction; }
        public int getExecutedOperationCount() { return executedOperations.size(); }
    }

    /**
     * Main method to demonstrate all exception handling patterns
     */
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║   ELITE EXCEPTION HANDLING TRAINING - 12 FAANG PROBLEMS       ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        demonstrateFoundationProblems();
        demonstrateIntermediateProblems();
        demonstrateAdvancedProblems();

        System.out.println("\n" + "═".repeat(70));
        System.out.println("🎓 ELITE EXCEPTION HANDLING TRAINING COMPLETE!");
        System.out.println("═".repeat(70));
        System.out.println("\nYou have mastered:");
        System.out.println("  ✓ Custom exception hierarchies");
        System.out.println("  ✓ Try-with-resources patterns");
        System.out.println("  ✓ Exception chaining and root cause analysis");
        System.out.println("  ✓ Multi-catch and best practices");
        System.out.println("  ✓ Constructor exception handling");
        System.out.println("  ✓ Retry patterns with exponential backoff");
        System.out.println("  ✓ Circuit breaker pattern");
        System.out.println("  ✓ Exception translation in APIs");
        System.out.println("  ✓ Async exception handling");
        System.out.println("  ✓ Stream exception handling");
        System.out.println("  ✓ Global exception handlers");
        System.out.println("  ✓ Transaction rollback patterns");
        System.out.println("\n✨ Ready for FAANG exception handling interviews!");
    }

    private static void demonstrateFoundationProblems() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("FOUNDATION LEVEL (Problems 1-4)");
        System.out.println("═".repeat(70));

        // Problem 1: Custom exceptions
        System.out.println("\n1. Custom Exception Hierarchy (Google, Amazon)");
        try {
            throw new InsufficientFundsException(100.0, 150.0);
        } catch (BankingException e) {
            System.out.println("   Caught: " + e.getMessage());
            System.out.println("   Error Code: " + e.getErrorCode());
        }

        // Problem 2: Try-with-resources
        System.out.println("\n2. Try-With-Resources Pattern (Amazon, Meta)");
        try {
            List<String> results = processMultipleResources(false);
            System.out.println("   Resources processed: " + results.size());
        } catch (IOException e) {
            System.out.println("   Error: " + e.getMessage());
        }

        // Problem 3: Exception chaining
        System.out.println("\n3. Exception Chaining (Google, Microsoft)");
        try {
            performDatabaseOperation(true);
        } catch (ServiceException e) {
            Throwable root = getRootCause(e);
            System.out.println("   Root cause: " + root.getClass().getSimpleName());
        }

        // Problem 4: Multi-catch
        System.out.println("\n4. Multi-Catch Pattern (Amazon, Netflix)");
        FileProcessingResult result = processFile("nonexistent.txt");
        System.out.println("   Success: " + result.isSuccess());
        if (!result.isSuccess()) {
            System.out.println("   Error: " + result.getError());
        }
    }

    private static void demonstrateIntermediateProblems() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("INTERMEDIATE LEVEL (Problems 5-8)");
        System.out.println("═".repeat(70));

        // Problem 5: Constructor exceptions
        System.out.println("\n5. Exception in Constructor (Google, LinkedIn)");
        try {
            new DatabaseConnection("jdbc:mysql://localhost", true);
        } catch (SQLException e) {
            System.out.println("   Constructor failed: " + e.getMessage());
        }

        // Problem 6: Retry pattern
        System.out.println("\n6. Retry Pattern with Backoff (Netflix, Amazon)");
        RetryableOperation<String> retry = new RetryableOperation<>(3, 100, 2.0);
        System.out.println("   Retry handler created with 3 attempts");

        // Problem 7: Circuit breaker
        System.out.println("\n7. Circuit Breaker Pattern (Netflix, Google)");
        CircuitBreaker breaker = new CircuitBreaker(3, 5000);
        System.out.println("   Circuit breaker state: " + breaker.getState());

        // Problem 8: Exception translation
        System.out.println("\n8. Exception Translation (Google, Amazon)");
        UserService service = new UserService();
        try {
            service.getUser("error");
        } catch (ApiException e) {
            System.out.println("   API Exception: " + e.getMessage());
            System.out.println("   Status Code: " + e.getStatusCode());
        }
    }

    private static void demonstrateAdvancedProblems() {
        System.out.println("\n" + "═".repeat(70));
        System.out.println("ADVANCED LEVEL (Problems 9-12)");
        System.out.println("═".repeat(70));

        // Problem 9: Async exceptions
        System.out.println("\n9. Async Exception Handling (Netflix, Microsoft)");
        CompletableFuture<String> future = AsyncProcessor.chainedProcessing("test");
        System.out.println("   Async result: " + future.join());

        // Problem 10: Stream exceptions
        System.out.println("\n10. Exception Handling in Streams (Google, LinkedIn)");
        List<String> files = List.of("file1.txt", "file2.txt");
        List<String> results = processFilesInStream(files);
        System.out.println("   Processed files: " + results.size());

        // Problem 11: Global handler
        System.out.println("\n11. Global Exception Handler (Amazon, Meta)");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(
            (e, ctx) -> System.out.println("   Reporting: " + e.getClass().getSimpleName())
        );
        String message = handler.handleException(
            new IllegalArgumentException("Invalid input"), Map.of()
        );
        System.out.println("   Handler message: " + message);

        // Problem 12: Transaction rollback
        System.out.println("\n12. Transaction Rollback (Google, Amazon)");
        TransactionManager tm = new TransactionManager();
        tm.beginTransaction();
        System.out.println("   Transaction started");
        tm.rollback();
        System.out.println("   Transaction rolled back");
    }
}
