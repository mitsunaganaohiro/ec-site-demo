package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface ProductRepository {

    /**
     * ソフトデリート済みを除いた商品を検索する。
     * categoryIdが指定された場合はカテゴリで絞り込み、keywordが指定された場合は
     * 商品名の部分一致で絞り込む。新着順(product_id降順)で返す。
     */
    List<Product> findAll(@Param("categoryId") Integer categoryId, @Param("keyword") String keyword);

    /**
     * ソフトデリート状態を問わずproduct_idで商品を検索する。削除済み判定はService側で行う。
     */
    Optional<Product> findById(@Param("productId") int productId);

    /**
     * 在庫を減算する(products.stock_quantity)。quantity以上の在庫がある場合のみ減算し、
     * 更新件数を返す。0の場合は在庫不足または競合が発生したと判定する(RSK-01対策)。
     */
    int decrementStock(@Param("productId") int productId, @Param("quantity") int quantity);

    /**
     * 在庫を加算する(products.stock_quantity)。注文キャンセル・返品時の在庫戻しに使用する。
     */
    void incrementStock(@Param("productId") int productId, @Param("quantity") int quantity);

    /**
     * ソフトデリート済みも含めた全商品を新着順(product_id降順)で取得する(管理者用)。
     */
    List<Product> findAllIncludingDeleted();

    /**
     * 商品を更新する(name・price・categoryId・stockQuantity)。
     */
    void update(Product product);

    /**
     * 商品をソフトデリートする(deleted_atを設定)。
     */
    void softDelete(@Param("productId") int productId);

    /**
     * 商品を新規登録する。生成されたproduct_idはproduct.productIdに設定される。
     */
    void insert(Product product);
}
