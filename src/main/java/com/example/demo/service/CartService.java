package com.example.demo.service;

import com.example.demo.common.exception.InsufficientStockException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.dto.CartItemDto;
import com.example.demo.entity.Cart;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.repository.CartItemRepository;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository,
                        ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * カートに商品を追加する。既に同一商品の明細がある場合は数量を加算する。
     */
    public void addItem(int memberId, int productId, int quantity) {
        Product product = productRepository.findById(productId)
                .filter(p -> p.getDeletedAt() == null)
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + productId));

        if (product.getStockQuantity() == 0) {
            throw new InsufficientStockException("在庫がありません: " + productId);
        }

        Optional<CartItem> existing = cartItemRepository.findByMemberIdAndProductId(memberId, productId);
        if (existing.isPresent()) {
            CartItem cartItem = existing.get();
            int newQuantity = cartItem.getQuantity() + quantity;
            if (newQuantity > product.getStockQuantity()) {
                throw new InsufficientStockException("在庫数を超えています: " + productId);
            }
            cartItemRepository.updateQuantity(cartItem.getCartItemId(), newQuantity);
            return;
        }

        if (quantity > product.getStockQuantity()) {
            throw new InsufficientStockException("在庫数を超えています: " + productId);
        }

        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseGet(() -> createCart(memberId));

        CartItem newItem = new CartItem();
        newItem.setCartId(cart.getCartId());
        newItem.setProductId(productId);
        newItem.setQuantity(quantity);
        newItem.setAddedAt(LocalDateTime.now());
        cartItemRepository.insert(newItem);
    }

    private Cart createCart(int memberId) {
        Cart newCart = new Cart();
        newCart.setMemberId(memberId);
        cartRepository.insert(newCart);
        return cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalStateException("カートの作成に失敗しました: memberId=" + memberId));
    }

    /**
     * 会員のカート明細一覧・合計金額・削除済み商品有無を取得する。
     */
    public CartResult getCartItems(int memberId) {
        List<CartItemDto> cartItems = cartItemRepository.findByMemberIdWithProduct(memberId);
        List<CartItemDto> allCartItems = cartItemRepository.findAllByMemberIdWithProduct(memberId);

        boolean hasDeletedItem = allCartItems.size() > cartItems.size();
        int totalAmount = cartItems.stream()
                .mapToInt(CartItemDto::getSubTotal)
                .sum();

        return new CartResult(cartItems, totalAmount, hasDeletedItem);
    }

    /**
     * カート明細の数量を更新する。明細が本人のカートに属することを確認する。
     */
    public void updateQuantity(int memberId, int cartItemId, int quantity) {
        CartItem cartItem = findOwnedCartItem(memberId, cartItemId);

        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + cartItem.getProductId()));

        if (quantity > product.getStockQuantity()) {
            throw new InsufficientStockException("在庫数を超えています: " + cartItem.getProductId());
        }

        cartItemRepository.updateQuantity(cartItemId, quantity);
    }

    /**
     * カート明細を削除する。明細が本人のカートに属することを確認する。
     */
    public void deleteItem(int memberId, int cartItemId) {
        findOwnedCartItem(memberId, cartItemId);
        cartItemRepository.deleteByCartItemId(cartItemId);
    }

    private CartItem findOwnedCartItem(int memberId, int cartItemId) {
        CartItem cartItem = cartItemRepository.findByCartItemId(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("カート明細が見つかりません: " + cartItemId));

        Cart cart = cartRepository.findByMemberId(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("カート明細が見つかりません: " + cartItemId));

        if (!cartItem.getCartId().equals(cart.getCartId())) {
            throw new ResourceNotFoundException("カート明細が見つかりません: " + cartItemId);
        }

        return cartItem;
    }

    /**
     * 会員のカート明細を全件削除する。再ログイン時(MemberAuthenticationSuccessHandler)と
     * 注文確定時(OrderService)から呼ばれる。
     */
    public void clearCart(int memberId) {
        cartItemRepository.deleteByMemberId(memberId);
    }

    @Data
    public static class CartResult {
        private final List<CartItemDto> cartItems;
        private final int totalAmount;
        private final boolean hasDeletedItem;
    }
}
