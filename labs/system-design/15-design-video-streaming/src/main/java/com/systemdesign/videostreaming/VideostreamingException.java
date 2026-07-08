package com.systemdesign.videostreaming;

public class VideostreamingException extends RuntimeException {
    private final String errorCode;
    public VideostreamingException(String code, String msg) { super(msg); this.errorCode = code; }
    public VideostreamingException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}