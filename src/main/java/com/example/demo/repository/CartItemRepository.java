package com.example.demo.repository;

import com.example.demo.dto.CartItemDto;
import com.example.demo.entity.CartItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CartItemRepository {

    /**
     * 会員のカート明細を、削除済み商品を除いて商品情報付きで取得する。
     */
    List<CartItemDto> findByMemberIdWithProduct(@Param("memberId") int memberId);

    /**
     * 会員のカート明細を、削除済み商品も含めて商品情報付きで取得する(hasDeletedItem判定用)。
     */
    List<CartItemDto> findAllByMemberIdWithProduct(@Param("memberId") int memberId);

    /**
     * 会員・商品の組み合わせで既存のカート明細を検索する(重複チェック用)。
     */
    Optional<CartItem> findByMemberIdAndProductId(@Param("memberId") int memberId, @Param("productId") int productId);

    /**
     * カート明細IDで単体取得する(権限チェック用)。
     */
    Optional<CartItem> findByCartItemId(@Param("cartItemId") int cartItemId);

    /**
     * カート明細を新規作成する。生成されたcart_item_idはcartItem.cartItemIdに設定される。
     */
    void insert(CartItem cartItem);

    /**
     * カート明細の数量を更新する。
     */
    void updateQuantity(@Param("cartItemId") int cartItemId, @Param("quantity") int quantity);

    /**
     * カート明細を1件削除する。
     */
    void deleteByCartItemId(@Param("cartItemId") int cartItemId);

    /**
     * 会員のカート明細を全件削除する(カートクリア用)。
     */
    void deleteByMemberId(@Param("memberId") int memberId);
}
