package com.example.demo.controller.admin;

import com.example.demo.common.OrderStatus;
import com.example.demo.common.exception.InvalidOrderStatusException;
import com.example.demo.entity.Order;
import com.example.demo.service.AdminOrderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/orders")
public class AdminOrderController {

    private final AdminOrderService adminOrderService;

    public AdminOrderController(AdminOrderService adminOrderService) {
        this.adminOrderService = adminOrderService;
    }

    /**
     * API-028: 管理者向け注文一覧(全会員)。successMessageはRedirectAttributes経由の
     * フラッシュ属性が自動的にModelへ引き継がれるため、ここでの追加処理は不要。
     */
    @GetMapping
    public String list(Model model) {
        model.addAttribute("orders", adminOrderService.findAll());
        return "admin/order-list";
    }

    /**
     * API-029: 管理者向け注文詳細。存在しない場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。successMessage・errorMessageは
     * フラッシュ属性が自動的にModelへ引き継がれるため、ここでの追加処理は不要。
     */
    @GetMapping("/{orderId}")
    public String detail(@PathVariable int orderId, Model model) {
        Order order = adminOrderService.findByOrderId(orderId);
        boolean canCancel = OrderStatus.fromValue(Integer.parseInt(order.getStatus())).isCancellable();

        model.addAttribute("order", order);
        model.addAttribute("orderItems", order.getOrderItems());
        model.addAttribute("canCancel", canCancel);
        return "admin/order-detail";
    }

    /**
     * API-030: 管理者による注文キャンセル。存在しない場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。
     */
    @PostMapping("/{orderId}/cancel")
    public String cancel(@PathVariable int orderId, RedirectAttributes redirectAttributes) {
        try {
            adminOrderService.cancel(orderId);
        } catch (InvalidOrderStatusException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/orders/" + orderId;
        }

        redirectAttributes.addFlashAttribute("successMessage", "注文をキャンセルしました");
        return "redirect:/admin/orders";
    }
}
