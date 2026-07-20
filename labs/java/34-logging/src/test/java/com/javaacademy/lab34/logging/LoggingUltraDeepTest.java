package com.javaacademy.lab34.logging;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.logging.*;

class LoggingUltraDeepTest {

    @Test
    void loggerHierarchy() {
        Logger logger = Logger.getLogger("com.javaacademy.lab34");
        Logger parent = logger.getParent();
        assertNotNull(parent);
    }

    @Test
    void logLevelsHierarchy() {
        assertTrue(Level.SEVERE.intValue() > Level.WARNING.intValue());
        assertTrue(Level.WARNING.intValue() > Level.INFO.intValue());
        assertTrue(Level.INFO.intValue() > Level.CONFIG.intValue());
        assertTrue(Level.CONFIG.intValue() > Level.FINE.intValue());
    }

    @Test
    void loggerCreatesLogRecords() {
        Logger logger = Logger.getLogger("test.logger");
        var handler = new Handler() {
            @Override public void publish(LogRecord r) {
                assertEquals("test message", r.getMessage());
            }
            @Override public void flush() {}
            @Override public void close() {}
        };
        logger.addHandler(handler);
        logger.setLevel(Level.ALL);
        handler.setLevel(Level.ALL);
        logger.info("test message");
        logger.removeHandler(handler);
    }
}
