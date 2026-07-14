package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * SecurityConfigのAccessDeniedHandlerがフォワードする /error/403 を処理する。
 * forwardは元リクエストのHTTPメソッドを保持するため、メソッドを限定せずマッピングする。
 */
@Controller
public class ErrorPageController {

    @RequestMapping("/error/403")
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbidden(Model model) {
        model.addAttribute("errorMessage", "アクセス権限がありません");
        return "error/403";
    }
}
