package com.example.demo.service;

import com.example.demo.entity.Cart;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    public CartServiceImpl(CartRepository cartRepository, CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
    }

    @Override
    @Transactional
    public void clearCart(Integer memberId) {
        Optional<Cart> cart = cartRepository.findByMemberId(memberId);
        if (cart.isPresent()) {
            cartItemRepository.deleteByCartId(cart.get().getCartId());
        }
    }
}
