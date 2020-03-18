package com.ruochu.edata.exception;

public class XmlConfigException extends RuntimeException {
    public XmlConfigException(String message) {
        super(message);
    }

    public XmlConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public XmlConfigException(Throwable cause) {
        super(cause);
    }
}
