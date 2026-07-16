package com.example.demo.service;

import com.example.demo.entity.Category;

import java.util.List;

public interface CategoryService {

    /**
     * 全カテゴリをcategory_id昇順で取得する。
     */
    List<Category> findAll();
}
