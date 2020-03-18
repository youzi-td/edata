package com.ruochu.edata.exception;

public class UnknownFileTypeException extends Exception {
    public UnknownFileTypeException(String message) {
        super(message);
    }

    public UnknownFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownFileTypeException(Throwable cause) {
        super(cause);
    }
}
