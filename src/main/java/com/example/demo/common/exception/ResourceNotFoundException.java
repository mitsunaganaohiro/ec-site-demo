package com.example.demo.common.exception;

/**
 * 指定されたリソースが見つからない場合にスローされる例外(HTTP 404に対応)。
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
