package com.example.demo.security;

import com.example.demo.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 認証済み会員のUserDetails。AuthenticationSuccessHandlerなどでmemberIdを
 * 参照できるよう、標準のUserクラスではなくこの専用実装を使う。
 */
public class MemberPrincipal implements UserDetails {

    private final Integer memberId;
    private final String name;
    private final String email;
    private final String passwordHash;

    public MemberPrincipal(Member member) {
        this.memberId = member.getMemberId();
        this.name = member.getName();
        this.email = member.getEmail();
        this.passwordHash = member.getPasswordHash();
    }

    public Integer getMemberId() {
        return memberId;
    }

    public String getName() {
        return name;
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

    /**
     * SessionRegistryは「同一会員の別セッション」をprincipalのequals/hashCodeで判定するため、
     * ログインの度に新しいインスタンスが生成されるこのクラスでは、必ずmemberIdベースで
     * オーバーライドする必要がある(未オーバーライドだとmaximumSessions(1)が機能しない)。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MemberPrincipal other)) {
            return false;
        }
        return Objects.equals(memberId, other.memberId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberId);
    }
}
