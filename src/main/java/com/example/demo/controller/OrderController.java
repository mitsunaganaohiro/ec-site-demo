package com.example.demo.controller;

import com.example.demo.common.OrderStatus;
import com.example.demo.common.exception.InsufficientStockException;
import com.example.demo.common.exception.InvalidOrderStatusException;
import com.example.demo.entity.Order;
import com.example.demo.entity.OrderItem;
import com.example.demo.repository.OrderItemRepository;
import com.example.demo.security.MemberPrincipal;
import com.example.demo.service.CartService;
import com.example.demo.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final OrderItemRepository orderItemRepository;

    public OrderController(OrderService orderService, CartService cartService,
                            OrderItemRepository orderItemRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * API-012: 注文確認画面。カートが空の場合は/cartへ戻す。
     * errorMessageは在庫不足でこの画面へリダイレクトされた場合のフラッシュメッセージで、
     * RedirectAttributesにより自動的にModelへ引き継がれるため、ここでの追加処理は不要。
     */
    @GetMapping("/orders/confirm")
    public String confirm(@AuthenticationPrincipal MemberPrincipal userDetails, Model model) {
        CartService.CartResult cartResult = cartService.getCartItems(userDetails.getMemberId());
        if (cartResult.getCartItems().isEmpty()) {
            return "redirect:/cart";
        }

        model.addAttribute("cartItems", cartResult.getCartItems());
        model.addAttribute("totalAmount", cartResult.getTotalAmount());
        return "user/order-confirm";
    }

    /**
     * API-013: 注文確定。
     */
    @PostMapping("/orders")
    public String place(@AuthenticationPrincipal MemberPrincipal userDetails, RedirectAttributes redirectAttributes) {
        try {
            int orderId = orderService.place(userDetails.getMemberId());
            return "redirect:/orders/" + orderId + "/complete";
        } catch (IllegalStateException ex) {
            return "redirect:/cart";
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/orders/confirm";
        }
    }

    /**
     * API-014: 注文完了画面。存在しない・他会員の注文の場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。
     */
    @GetMapping("/orders/{orderId}/complete")
    public String complete(@AuthenticationPrincipal MemberPrincipal userDetails, @PathVariable int orderId,
                            Model model) {
        model.addAttribute("order", orderService.findByOrderIdAndMemberId(orderId, userDetails.getMemberId()));
        return "user/order-complete";
    }

    /**
     * API-015: 注文履歴一覧。successMessageはRedirectAttributes経由のフラッシュ属性が
     * 自動的にModelへ引き継がれるため、ここでの追加処理は不要。
     */
    @GetMapping("/orders")
    public String list(@AuthenticationPrincipal MemberPrincipal userDetails, Model model) {
        List<Order> orders = orderService.findByMemberId(userDetails.getMemberId());
        model.addAttribute("orders", orders);
        return "user/order-list";
    }

    /**
     * API-016: 注文詳細。存在しない・他会員の注文の場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。successMessage・errorMessageは
     * フラッシュ属性が自動的にModelへ引き継がれるため、ここでの追加処理は不要。
     */
    @GetMapping("/orders/{orderId}")
    public String detail(@AuthenticationPrincipal MemberPrincipal userDetails, @PathVariable int orderId,
                          Model model) {
        Order order = orderService.findByOrderIdAndMemberId(orderId, userDetails.getMemberId());
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        boolean canCancel = OrderStatus.fromValue(Integer.parseInt(order.getStatus())).isCancellable();

        model.addAttribute("order", order);
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("canCancel", canCancel);
        return "user/order-detail";
    }

    /**
     * API-017: 注文キャンセル。存在しない・他会員の注文の場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。
     */
    @PostMapping("/orders/{orderId}/cancel")
    public String cancel(@AuthenticationPrincipal MemberPrincipal userDetails, @PathVariable int orderId,
                          RedirectAttributes redirectAttributes) {
        try {
            orderService.cancel(userDetails.getMemberId(), orderId);
        } catch (InvalidOrderStatusException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/orders/" + orderId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "キャンセルが完了しました");
        return "redirect:/orders/" + orderId;
    }
}
