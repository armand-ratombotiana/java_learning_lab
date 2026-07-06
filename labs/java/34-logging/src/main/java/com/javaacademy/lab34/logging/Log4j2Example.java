package com.javaacademy.lab34.logging;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

public class Log4j2Example {

    private static final Logger log = LogManager.getLogger(Log4j2Example.class);

    public void demonstrateLevels() {
        log.trace("Trace message - finest granularity");
        log.debug("Debug message - diagnostic information");
        log.info("Info message - general operational messages");
        log.warn("Warn message - potentially harmful situation");
        log.error("Error message - error event, application may continue");
        log.fatal("Fatal message - severe error, application will exit");
    }

    public void parameterizedLogging(String user, String action, long duration) {
        log.info("User '{}' performed '{}' in {}ms", user, action, duration);
    }

    public void logWithContext(String requestId) {
        ThreadContext.put("requestId", requestId);
        log.info("Processing request");
        ThreadContext.remove("requestId");
    }

    public void logException(String operation, Exception cause) {
        log.error("Operation '{}' failed", operation, cause);
    }
}
