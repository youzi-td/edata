package com.ruochu.edata.exception;

public class EException extends Exception {
    public EException(String message) {
        super(message);
    }

    public EException(String message, Throwable cause) {
        super(message, cause);
    }

    public EException(Throwable cause) {
        super(cause);
    }
}
