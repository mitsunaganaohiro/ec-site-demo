package com.example.demo.common.exception;

/**
 * 業務エラーを表す例外の基底クラス。
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }
}
