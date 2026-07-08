package com.systemdesign.realtimecollab;

public class RealtimecollabException extends RuntimeException {
    private final String errorCode;
    public RealtimecollabException(String code, String msg) { super(msg); this.errorCode = code; }
    public RealtimecollabException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}