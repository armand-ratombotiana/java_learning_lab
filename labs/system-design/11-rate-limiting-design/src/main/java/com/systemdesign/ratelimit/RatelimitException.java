package com.systemdesign.ratelimit;

public class RatelimitException extends RuntimeException {
    private final String errorCode;
    public RatelimitException(String code, String msg) { super(msg); this.errorCode = code; }
    public RatelimitException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}