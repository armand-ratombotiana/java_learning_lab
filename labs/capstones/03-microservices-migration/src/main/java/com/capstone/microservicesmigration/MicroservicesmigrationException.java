package com.capstone.microservicesmigration;

public class MicroservicesmigrationException extends RuntimeException {
    private final String errorCode;
    public MicroservicesmigrationException(String code, String msg) { super(msg); this.errorCode = code; }
    public MicroservicesmigrationException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}