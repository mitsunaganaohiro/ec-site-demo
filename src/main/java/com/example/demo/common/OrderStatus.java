package com.example.demo.common;

/**
 * 注文ステータスを表す列挙型。DBのINT値(1〜4)と相互変換する。
 */
public enum OrderStatus {

    /** 注文受付。システムが自動設定する初期状態 */
    ACCEPTED(1),

    /** 発送準備。管理者のみ遷移可 */
    PREPARING(2),

    /** 発送完了。管理者のみ遷移可 */
    SHIPPED(3),

    /** キャンセル。注文受付からのみ遷移可(会員・管理者) */
    CANCELLED(4);

    private final int value;

    OrderStatus(int value) {
        this.value = value;
    }

    /**
     * DB格納用のINT値を取得する。
     */
    public int getValue() {
        return value;
    }

    /**
     * DBのINT値からOrderStatusに変換する。
     *
     * @param value DBに格納されているINT値(1〜4)
     * @return 対応するOrderStatus
     * @throws IllegalArgumentException 未定義の値が渡された場合
     */
    public static OrderStatus fromValue(int value) {
        for (OrderStatus status : values()) {
            if (status.value == value) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown OrderStatus value: " + value);
    }

    /**
     * この状態からキャンセルへ遷移可能かどうかを判定する。
     * キャンセルは注文受付(ACCEPTED)状態からのみ許可される。
     */
    public boolean isCancellable() {
        return this == ACCEPTED;
    }
}
