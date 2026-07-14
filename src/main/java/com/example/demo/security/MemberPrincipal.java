package com.example.demo.security;

import com.example.demo.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 認証済み会員のUserDetails。AuthenticationSuccessHandlerなどでmemberIdを
 * 参照できるよう、標準のUserクラスではなくこの専用実装を使う。
 */
public class MemberPrincipal implements UserDetails {

    private final Integer memberId;
    private final String email;
    private final String passwordHash;

    public MemberPrincipal(Member member) {
        this.memberId = member.getMemberId();
        this.email = member.getEmail();
        this.passwordHash = member.getPasswordHash();
    }

    public Integer getMemberId() {
        return memberId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_MEMBER"));
    }

    @Override
    public String getPassword() {
        return passwordHash;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
