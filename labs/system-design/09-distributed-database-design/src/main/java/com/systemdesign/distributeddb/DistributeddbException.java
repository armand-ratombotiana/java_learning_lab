package com.systemdesign.distributeddb;

public class DistributeddbException extends RuntimeException {
    private final String errorCode;
    public DistributeddbException(String code, String msg) { super(msg); this.errorCode = code; }
    public DistributeddbException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}