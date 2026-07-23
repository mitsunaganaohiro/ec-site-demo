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
     * 全会員の注文を新しい順に取得する(管理者用)。memberNameは会員名をJOINで取得する。
     */
    List<Order> findAll();

    /**
     * 全会員の注文を対象にorderIdで注文を取得する(管理者用)。memberNameは会員名をJOINで取得する。
     */
    Optional<Order> findByOrderId(@Param("orderId") int orderId);

    /**
     * 注文ステータスを更新する。
     */
    void updateStatus(@Param("orderId") int orderId, @Param("status") int status);
}
