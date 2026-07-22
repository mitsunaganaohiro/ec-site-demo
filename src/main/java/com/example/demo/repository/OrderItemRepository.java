package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemRepository {

    /**
     * 注文明細を新規作成する。生成されたorder_item_idはorderItem.orderItemIdに設定される。
     */
    void insert(OrderItem orderItem);

    /**
     * 注文に紐づく明細を取得する。
     */
    List<OrderItem> findByOrderId(@Param("orderId") int orderId);
}
