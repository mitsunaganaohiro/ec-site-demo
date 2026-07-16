package com.example.demo.service;

import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<Product> findAll(Integer categoryId, String keyword) {
        return productRepository.findAll(categoryId, keyword);
    }

    @Override
    public Product findById(int productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + productId));

        if (product.getDeletedAt() != null) {
            throw new ResourceNotFoundException("商品が見つかりません: " + productId);
        }

        return product;
    }
}
