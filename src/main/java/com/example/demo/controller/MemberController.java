package com.example.demo.controller;

import com.example.demo.common.exception.DuplicateEmailException;
import com.example.demo.dto.MemberRegisterForm;
import com.example.demo.service.MemberService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    /**
     * API-001: 会員登録フォーム表示。ログイン済みの場合はトップへリダイレクトする。
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }

        if (!model.containsAttribute("memberRegisterForm")) {
            model.addAttribute("memberRegisterForm", new MemberRegisterForm());
        }
        return "user/register";
    }

    /**
     * API-002: 会員登録実行。
     */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberRegisterForm memberRegisterForm,
                            BindingResult bindingResult,
                            HttpServletRequest request,
                            HttpServletResponse response) throws ServletException {
        if (memberRegisterForm.getPassword() != null
                && !memberRegisterForm.getPassword().equals(memberRegisterForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "パスワードが一致しません");
        }

        if (bindingResult.hasErrors()) {
            return "user/register";
        }

        try {
            memberService.register(memberRegisterForm, request, response);
        } catch (DuplicateEmailException e) {
            bindingResult.rejectValue("email", "duplicate", e.getMessage());
            return "user/register";
        }

        return "redirect:/";
    }
}
