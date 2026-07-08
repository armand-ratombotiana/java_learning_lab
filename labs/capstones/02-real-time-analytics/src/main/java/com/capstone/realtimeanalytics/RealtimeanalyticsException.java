package com.capstone.realtimeanalytics;

public class RealtimeanalyticsException extends RuntimeException {
    private final String errorCode;
    public RealtimeanalyticsException(String code, String msg) { super(msg); this.errorCode = code; }
    public RealtimeanalyticsException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}