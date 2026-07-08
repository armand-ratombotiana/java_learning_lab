package com.capstone.ecommerce;

public class EcommerceException extends RuntimeException {
    private final String errorCode;
    public EcommerceException(String code, String msg) { super(msg); this.errorCode = code; }
    public EcommerceException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}