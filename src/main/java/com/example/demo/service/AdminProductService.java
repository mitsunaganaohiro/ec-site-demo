package com.example.demo.service;

import com.example.demo.common.exception.BusinessException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.dto.ProductForm;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AdminProductService {

    private final ProductRepository productRepository;

    public AdminProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * ソフトデリート済みも含めた全商品を取得する。
     */
    public List<Product> findAllIncludingDeleted() {
        return productRepository.findAllIncludingDeleted();
    }

    /**
     * ソフトデリート済みも含めてproductIdで商品を取得する。存在しない場合はResourceNotFoundException。
     */
    public Product findById(int productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + productId));
    }

    /**
     * 商品を新規登録する。
     */
    public void register(ProductForm form) {
        Product product = new Product();
        product.setName(form.getName());
        product.setPrice(form.getPrice());
        product.setCategoryId(form.getCategoryId());
        product.setStockQuantity(form.getStockQuantity());
        productRepository.insert(product);
    }

    /**
     * 商品を更新する。削除済み商品は編集できない。
     */
    public void update(int productId, ProductForm form) {
        Product product = findById(productId);

        if (product.getDeletedAt() != null) {
            throw new BusinessException("削除済み商品は編集できません");
        }

        product.setName(form.getName());
        product.setPrice(form.getPrice());
        product.setCategoryId(form.getCategoryId());
        product.setStockQuantity(form.getStockQuantity());
        productRepository.update(product);
    }

    /**
     * 商品をソフトデリートする。
     */
    public void softDelete(int productId) {
        findById(productId);
        productRepository.softDelete(productId);
    }
}
