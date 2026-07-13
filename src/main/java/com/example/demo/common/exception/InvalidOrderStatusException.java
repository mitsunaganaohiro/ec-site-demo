package com.example.demo.common.exception;

/**
 * 不正な注文ステータス遷移が要求された場合にスローされる例外。
 */
public class InvalidOrderStatusException extends BusinessException {

    public InvalidOrderStatusException(String message) {
        super(message);
    }

    public InvalidOrderStatusException(String message, Throwable cause) {
        super(message, cause);
    }
}
