package com.example.demo.security;

import com.example.demo.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 会員ログイン成功時にカートをクリアしてから / へリダイレクトする。
 */
@Component
public class MemberAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String SUCCESS_URL = "/";

    private final CartService cartService;

    public MemberAuthenticationSuccessHandler(CartService cartService) {
        this.cartService = cartService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                         Authentication authentication) throws IOException {
        if (authentication.getPrincipal() instanceof MemberPrincipal memberPrincipal) {
            cartService.clearCart(memberPrincipal.getMemberId());
        }
        response.sendRedirect(request.getContextPath() + SUCCESS_URL);
    }
}
