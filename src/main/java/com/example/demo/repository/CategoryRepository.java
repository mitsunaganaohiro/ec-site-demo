package com.example.demo.repository;

import com.example.demo.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryRepository {

    /**
     * 全カテゴリをcategory_id昇順で取得する。
     */
    List<Category> findAll();
}
