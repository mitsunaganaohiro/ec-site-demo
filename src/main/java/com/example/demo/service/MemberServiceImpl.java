package com.example.demo.service;

import com.example.demo.common.exception.DuplicateEmailException;
import com.example.demo.dto.MemberRegisterForm;
import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberServiceImpl(MemberRepository memberRepository, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Member register(MemberRegisterForm form) {
        if (memberRepository.findByEmailAndDeletedAtIsNull(form.getEmail()).isPresent()) {
            throw new DuplicateEmailException("既に登録されているメールアドレスです: " + form.getEmail());
        }

        Member member = new Member();
        member.setName(form.getName());
        member.setEmail(form.getEmail());
        member.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        member.setMemberRank("normal");
        member.setStatus("active");

        return memberRepository.save(member);
    }
}
