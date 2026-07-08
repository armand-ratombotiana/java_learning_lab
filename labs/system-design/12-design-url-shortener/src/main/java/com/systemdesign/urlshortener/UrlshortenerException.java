package com.systemdesign.urlshortener;

public class UrlshortenerException extends RuntimeException {
    private final String errorCode;
    public UrlshortenerException(String code, String msg) { super(msg); this.errorCode = code; }
    public UrlshortenerException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}