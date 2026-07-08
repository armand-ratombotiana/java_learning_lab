package com.capstone.mlplatform;

public class MlplatformException extends RuntimeException {
    private final String errorCode;
    public MlplatformException(String code, String msg) { super(msg); this.errorCode = code; }
    public MlplatformException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}