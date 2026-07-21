package com.example.demo.repository;

import com.example.demo.entity.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface CartRepository {

    /**
     * 会員のカートを取得する。存在しない場合は空を返す。
     */
    Optional<Cart> findByMemberId(@Param("memberId") int memberId);

    /**
     * カートを新規作成する。生成されたcart_idはcart.cartIdに設定される。
     */
    void insert(Cart cart);
}
