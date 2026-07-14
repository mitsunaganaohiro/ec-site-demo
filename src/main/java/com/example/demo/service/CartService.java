package com.example.demo.service;

public interface CartService {

    /**
     * 指定した会員のカート明細を全て削除する。カートが存在しない場合は何もしない。
     */
    void clearCart(Integer memberId);
}
