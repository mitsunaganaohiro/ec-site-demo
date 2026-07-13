package com.example.demo.common.exception;

/**
 * 在庫が不足している場合にスローされる例外。
 */
public class InsufficientStockException extends BusinessException {

    public InsufficientStockException(String message) {
        super(message);
    }

    public InsufficientStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
