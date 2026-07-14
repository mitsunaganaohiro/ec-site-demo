package com.example.demo.controller;

import com.example.demo.dto.MemberRegisterForm;
import com.example.demo.service.MemberService;
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
public class RegisterController {

    private final MemberService memberService;

    public RegisterController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/register")
    public String showForm(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        if (!model.containsAttribute("memberRegisterForm")) {
            model.addAttribute("memberRegisterForm", new MemberRegisterForm());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute MemberRegisterForm memberRegisterForm,
                            BindingResult bindingResult) {
        if (memberRegisterForm.getPassword() != null
                && !memberRegisterForm.getPassword().equals(memberRegisterForm.getPasswordConfirm())) {
            bindingResult.rejectValue("passwordConfirm", "mismatch", "パスワードが一致しません");
        }

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        memberService.register(memberRegisterForm);
        return "redirect:/register/complete";
    }

    @GetMapping("/register/complete")
    public String complete() {
        return "auth/register_complete";
    }
}
