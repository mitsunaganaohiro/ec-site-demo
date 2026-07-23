package com.example.demo.service;

import com.example.demo.common.OrderStatus;
import com.example.demo.common.exception.InvalidOrderStatusException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminOrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

    public AdminOrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository,
                              ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
    }

    /**
     * 全会員の注文を新しい順に取得する。
     */
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    /**
     * 全会員の注文を対象にorderIdで注文を取得する。明細もあわせて取得しOrderに設定する。
     * 存在しない場合はResourceNotFoundException。
     */
    public Order findByOrderId(int orderId) {
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("注文が見つかりません: " + orderId));
        order.setOrderItems(orderItemRepository.findByOrderId(orderId));
        return order;
    }

    /**
     * 注文をキャンセルする(会員によるキャンセルと同じロジック、member_id条件なし)。
     */
    @Transactional
    public void cancel(int orderId) {
        // STEP 1: 注文を取得(全会員対象)
        Order order = orderRepository.findByOrderId(orderId)
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
