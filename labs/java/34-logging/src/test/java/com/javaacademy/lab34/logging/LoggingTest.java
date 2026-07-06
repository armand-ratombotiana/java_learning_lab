package com.javaacademy.lab34.logging;

import org.junit.jupiter.api.*;
import org.slf4j.*;
import static org.junit.jupiter.api.Assertions.*;

class LoggingTest {

    private static final Logger log = LoggerFactory.getLogger(LoggingTest.class);

    @Test
    @DisplayName("SLF4J logger is not null")
    void loggerNotNull() {
        assertNotNull(log);
    }

    @Test
    @DisplayName("Logging levels can be checked")
    void loggingLevels() {
        assertTrue(log.isInfoEnabled());
        assertTrue(log.isErrorEnabled());
    }

    @Test
    @DisplayName("LoggingExample processes order without exception")
    void processOrder() {
        LoggingExample example = new LoggingExample();
        assertDoesNotThrow(() -> example.processOrder("ORD-001", "USR-001"));
    }

    @Test
    @DisplayName("LoggingExample calculates total correctly")
    void calculateTotal() {
        LoggingExample example = new LoggingExample();
        double total = example.calculateTotal(10.0, 3);
        assertEquals(32.4, total, 0.01);
    }

    @Test
    @DisplayName("Log4j2Example demonstrates levels")
    void log4j2Levels() {
        Log4j2Example example = new Log4j2Example();
        assertDoesNotThrow(example::demonstrateLevels);
    }

    @Test
    @DisplayName("AsyncLoggerExample processes events")
    void asyncLogger() {
        AsyncLoggerExample example = new AsyncLoggerExample();
        example.logEventsAsync(100);
        assertTrue(example.getEventCount() >= 100);
        example.shutdown();
    }

    @Test
    @DisplayName("Multiple loggers can coexist")
    void multipleLoggers() {
        LoggingExample ex1 = new LoggingExample();
        Log4j2Example ex2 = new Log4j2Example();
        assertAll(
            () -> assertDoesNotThrow(() -> ex1.simulateWorkflow()),
            () -> assertDoesNotThrow(ex2::demonstrateLevels)
        );
    }

    @Test
    @DisplayName("Async logger handles backpressure simulation")
    void asyncBackpressure() {
        AsyncLoggerExample example = new AsyncLoggerExample();
        example.logWithBackpressure(500);
        assertTrue(example.getEventCount() >= 500);
    }

    @Test
    @DisplayName("Log4j2 parameterized logging works")
    void log4j2Parameterized() {
        Log4j2Example example = new Log4j2Example();
        assertDoesNotThrow(() -> example.parameterizedLogging("user1", "login", 150));
    }

    @Test
    @DisplayName("Log4j2 exception logging works")
    void log4j2Exception() {
        Log4j2Example example = new Log4j2Example();
        assertDoesNotThrow(() -> example.logException("db-query", new RuntimeException("timeout")));
    }
}
