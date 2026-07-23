package com.example.demo.controller.admin;

import com.example.demo.security.AdminPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    /**
     * API-021: 管理者ダッシュボード。
     */
    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal AdminPrincipal adminDetails, Model model) {
        model.addAttribute("adminName", adminDetails.getName());
        return "admin/dashboard";
    }
}
