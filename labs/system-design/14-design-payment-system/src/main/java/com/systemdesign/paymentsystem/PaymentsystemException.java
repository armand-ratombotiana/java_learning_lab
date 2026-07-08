package com.systemdesign.paymentsystem;

public class PaymentsystemException extends RuntimeException {
    private final String errorCode;
    public PaymentsystemException(String code, String msg) { super(msg); this.errorCode = code; }
    public PaymentsystemException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}