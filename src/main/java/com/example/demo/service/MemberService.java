package com.example.demo.service;

import com.example.demo.dto.MemberRegisterForm;
import com.example.demo.entity.Member;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface MemberService {

    /**
     * 会員登録を行い、登録した会員でそのまま自動ログインさせる。
     * メールアドレスが既に登録されている場合は
     * {@link com.example.demo.common.exception.DuplicateEmailException} をスローする。
     */
    Member register(MemberRegisterForm form, HttpServletRequest request, HttpServletResponse response) throws ServletException;
}
