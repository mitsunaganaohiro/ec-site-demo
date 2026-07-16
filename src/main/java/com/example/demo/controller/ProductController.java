package com.example.demo.controller;

import com.example.demo.service.CategoryService;
import com.example.demo.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public ProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    /**
     * API-006: トップページ/商品一覧。カテゴリ・キーワードで絞り込み可能。
     */
    @GetMapping({"/", "/products"})
    public String list(@RequestParam(required = false) Integer categoryId,
                        @RequestParam(required = false) String keyword,
                        Model model) {
        model.addAttribute("products", productService.findAll(categoryId, keyword));
        model.addAttribute("categories", categoryService.findAll());
        model.addAttribute("selectedCategoryId", categoryId);
        model.addAttribute("keyword", keyword);
        return "user/product-list";
    }

    /**
     * API-007: 商品詳細。存在しない・削除済みの場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。
     */
    @GetMapping("/products/{productId}")
    public String detail(@PathVariable int productId, Model model) {
        model.addAttribute("product", productService.findById(productId));
        return "user/product-detail";
    }
}
