package com.example.demo.service;

import com.example.demo.entity.Product;

import java.util.List;

public interface ProductService {

    /**
     * 商品を検索する。ソフトデリート済み商品はRepository側で除外済み。
     */
    List<Product> findAll(Integer categoryId, String keyword);

    /**
     * 商品IDで商品を取得する。存在しない場合、またはソフトデリート済みの場合は
     * {@link com.example.demo.common.exception.ResourceNotFoundException} をスローする。
     */
    Product findById(int productId);
}
