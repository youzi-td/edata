package com.ruochu.edata.exception;

public class ERuntimeException extends RuntimeException {

    public ERuntimeException(String message) {
        super(message);
    }

    public ERuntimeException(String message, Object... args) {
        super(String.format(message, args));
    }

    public ERuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ERuntimeException(Throwable cause) {
        super(cause);
    }
}
