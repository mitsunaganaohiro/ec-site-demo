package com.example.demo.repository;

import com.example.demo.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderRepository {

    /**
     * 注文を新規作成する。生成されたorder_idはorder.orderIdに設定される。
     */
    void insert(Order order);

    /**
     * 自分の注文のみを取得する(注文完了画面・他会員チェック用)。
     */
    Optional<Order> findByOrderIdAndMemberId(@Param("orderId") int orderId, @Param("memberId") int memberId);

    /**
     * 会員の注文履歴を新しい順に取得する。
     */
    List<Order> findByMemberId(@Param("memberId") int memberId);

    /**
     * 注文ステータスを更新する。
     */
    void updateStatus(@Param("orderId") int orderId, @Param("status") int status);
}
