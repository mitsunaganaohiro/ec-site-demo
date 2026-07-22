package com.example.demo.service;

import com.example.demo.common.OrderStatus;
import com.example.demo.common.exception.InsufficientStockException;
import com.example.demo.common.exception.InvalidOrderStatusException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.dto.CartItemDto;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.entity.Product;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                         ProductRepository productRepository, CartService cartService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }

    /**
     * カート内容から注文を作成する。配送料・税・クーポン・ポイントは未実装のため0固定。
     *
     * @return 作成した注文のorderId
     */
    @Transactional
    public int place(int memberId) {
        // STEP 1: カート内容を取得
        CartService.CartResult cartResult = cartService.getCartItems(memberId);
        List<CartItemDto> cartItems = cartResult.getCartItems();

        if (cartItems.isEmpty()) {
            throw new IllegalStateException("カートが空です");
        }

        // STEP 2: 各商品の最新在庫を事前チェック
        for (CartItemDto item : cartItems) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("商品が見つかりません: " + item.getProductId()));

            if (product.getStockQuantity() < item.getQuantity()) {
                throw new InsufficientStockException("在庫が不足しています：" + product.getName());
            }
        }

        // STEP 3: 在庫減算。WHERE quantity >= ? で更新されない場合は、事前チェック後の
        // 他会員との同時注文による競合が発生したとみなす(RSK-01対策)。
        for (CartItemDto item : cartItems) {
            int updated = productRepository.decrementStock(item.getProductId(), item.getQuantity());
            if (updated == 0) {
                throw new InsufficientStockException("在庫が不足しています：" + item.getProductName());
            }
        }

        // STEP 4: Orderレコードをinsert
        int totalAmount = cartResult.getTotalAmount();

        Order order = new Order();
        order.setMemberId(memberId);
        order.setStatus(String.valueOf(OrderStatus.ACCEPTED.getValue()));
        order.setSubtotal(totalAmount);
        order.setShippingFee(0);
        order.setTax(0);
        order.setCouponDiscount(0);
        order.setPointsUsed(0);
        order.setTotalAmount(totalAmount);
        orderRepository.insert(order);

        // STEP 5: OrderItemレコードをinsert(cartItem時点のスナップショット)
        for (CartItemDto item : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getOrderId());
            orderItem.setProductId(item.getProductId());
            orderItem.setProductName(item.getProductName());
            orderItem.setUnitPrice(item.getPrice());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setSubtotal(item.getSubTotal());
            orderItemRepository.insert(orderItem);
        }

        // STEP 6: カートクリア
        cartService.clearCart(memberId);

        // STEP 7: orderIdを返す
        return order.getOrderId();
    }

    /**
     * 会員の注文履歴を新しい順に取得する。
     */
    public List<Order> findByMemberId(int memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    /**
     * 自分の注文のみを取得する。存在しない・他会員の注文の場合はResourceNotFoundException。
     */
    public Order findByOrderIdAndMemberId(int orderId, int memberId) {
        return orderRepository.findByOrderIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("注文が見つかりません: " + orderId));
    }

    /**
     * 注文をキャンセルする。キャンセル済み・発送準備以降など、注文受付以外の
     * ステータスからはキャンセルできない。キャンセル時は各明細の数量分だけ在庫を戻す。
     */
    @Transactional
    public void cancel(int memberId, int orderId) {
        // STEP 1: 自分の注文のみを取得
        Order order = orderRepository.findByOrderIdAndMemberId(orderId, memberId)
                .orElseThrow(() -> new ResourceNotFoundException("注文が見つかりません: " + orderId));

        // STEP 2: キャンセル可否を確認
        OrderStatus currentStatus = OrderStatus.fromValue(Integer.parseInt(order.getStatus()));
        if (!currentStatus.isCancellable()) {
            throw new InvalidOrderStatusException("この注文はキャンセルできません");
        }

        // STEP 3: ステータスをキャンセルに更新
        orderRepository.updateStatus(orderId, OrderStatus.CANCELLED.getValue());

        // STEP 4: 注文明細を取得
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        // STEP 5: 各明細の数量分だけ在庫を加算
        for (OrderItem orderItem : orderItems) {
            productRepository.incrementStock(orderItem.getProductId(), orderItem.getQuantity());
        }
    }
}
