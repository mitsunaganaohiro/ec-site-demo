package com.example.demo.repository;

import com.example.demo.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderItemRepository {

    /**
     * 注文明細を新規作成する。生成されたorder_item_idはorderItem.orderItemIdに設定される。
     */
    void insert(OrderItem orderItem);
}
