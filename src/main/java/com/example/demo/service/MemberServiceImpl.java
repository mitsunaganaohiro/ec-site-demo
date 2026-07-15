package com.example.demo.service;

import com.example.demo.common.exception.DuplicateEmailException;
import com.example.demo.dto.MemberRegisterForm;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionAuthenticationStrategy sessionAuthenticationStrategy;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
                              SessionAuthenticationStrategy sessionAuthenticationStrategy) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionAuthenticationStrategy = sessionAuthenticationStrategy;
    }

    @Override
    public Member register(MemberRegisterForm form, HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        if (memberRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new DuplicateEmailException("既に登録されているメールアドレスです: " + form.getEmail());
        }

        Member member = new Member();
        member.setName(form.getName());
        member.setEmail(form.getEmail());
        member.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        member.setMemberRank("normal");
        member.setStatus("active");

        memberRepository.insert(member);

        request.login(form.getEmail(), form.getPassword());

        // request.login()はSecurityFilterChainのformLoginを経由しないため、
        // maximumSessions(1)等のセッション管理を明示的に適用する。
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        sessionAuthenticationStrategy.onAuthentication(authentication, request, response);

        return member;
    }
}
