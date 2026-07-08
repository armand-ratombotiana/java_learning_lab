package com.systemdesign.chatsystem;

public class ChatsystemException extends RuntimeException {
    private final String errorCode;
    public ChatsystemException(String code, String msg) { super(msg); this.errorCode = code; }
    public ChatsystemException(String code, String msg, Throwable c) { super(msg, c); this.errorCode = code; }
    public String getErrorCode() { return errorCode; }
}