package com.example.demo.service;

import com.example.demo.dto.MemberRegisterForm;
import com.example.demo.entity.Member;

public interface MemberService {

    /**
     * 会員登録を行う。メールアドレスが既に登録されている場合は
     * {@link com.example.demo.common.exception.DuplicateEmailException} をスローする。
     */
    Member register(MemberRegisterForm form);
}
