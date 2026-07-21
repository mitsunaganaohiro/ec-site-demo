package com.example.demo.controller;

import com.example.demo.common.exception.InsufficientStockException;
import com.example.demo.common.exception.ResourceNotFoundException;
import com.example.demo.security.MemberPrincipal;
import com.example.demo.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    /**
     * API-008: カート一覧表示。
     */
    @GetMapping("/cart")
    public String list(@AuthenticationPrincipal MemberPrincipal userDetails, Model model) {
        CartService.CartResult cartResult = cartService.getCartItems(userDetails.getMemberId());
        model.addAttribute("cartItems", cartResult.getCartItems());
        model.addAttribute("totalAmount", cartResult.getTotalAmount());
        model.addAttribute("hasDeletedItem", cartResult.isHasDeletedItem());
        return "user/cart";
    }

    /**
     * API-009: カートへの商品追加。
     */
    @PostMapping("/cart/items")
    public String addItem(@AuthenticationPrincipal MemberPrincipal userDetails,
                           @RequestParam int productId,
                           @RequestParam int quantity,
                           RedirectAttributes redirectAttributes) {
        try {
            cartService.addItem(userDetails.getMemberId(), productId, quantity);
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/products/" + productId;
        } catch (ResourceNotFoundException ex) {
            return "redirect:/products/" + productId;
        }
        return "redirect:/cart";
    }

    /**
     * API-010: カート明細の数量変更。
     */
    @PostMapping("/cart/items/{cartItemId}")
    public String updateQuantity(@AuthenticationPrincipal MemberPrincipal userDetails,
                                  @PathVariable int cartItemId,
                                  @RequestParam int quantity,
                                  RedirectAttributes redirectAttributes) {
        try {
            cartService.updateQuantity(userDetails.getMemberId(), cartItemId, quantity);
        } catch (InsufficientStockException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/cart";
        } catch (ResourceNotFoundException ex) {
            return "redirect:/cart";
        }
        return "redirect:/cart";
    }

    /**
     * API-011: カート明細の削除。他会員の明細の場合はResourceNotFoundExceptionが
     * GlobalExceptionHandlerに委譲され404となる。
     */
    @PostMapping("/cart/items/{cartItemId}/delete")
    public String deleteItem(@AuthenticationPrincipal MemberPrincipal userDetails,
                              @PathVariable int cartItemId) {
        cartService.deleteItem(userDetails.getMemberId(), cartItemId);
        return "redirect:/cart";
    }
}
