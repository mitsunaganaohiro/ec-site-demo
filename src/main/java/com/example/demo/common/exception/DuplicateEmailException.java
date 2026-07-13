package com.example.demo.common.exception;

/**
 * メールアドレスが既に登録されている場合にスローされる例外。
 */
public class DuplicateEmailException extends BusinessException {

    public DuplicateEmailException(String message) {
        super(message);
    }

    public DuplicateEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
