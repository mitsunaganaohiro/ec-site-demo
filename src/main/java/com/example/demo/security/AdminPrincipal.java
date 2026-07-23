package com.example.demo.security;

import com.example.demo.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * 認証済み管理者のUserDetails。Model等でadminId・nameを参照できるよう、
 * 標準のUserクラスではなくこの専用実装を使う。
 */
public class AdminPrincipal implements UserDetails {

    private final Integer adminId;
    private final String name;
    private final String email;
    private final String passwordHash;

    public AdminPrincipal(Admin admin) {
        this.adminId = admin.getAdminId();
        this.name = admin.getName();
        this.email = admin.getEmail();
        this.passwordHash = admin.getPasswordHash();
    }

    public Integer getAdminId() {
        return adminId;
    }

    public String getName() {
        return name;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
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
     * SessionRegistryは「同一管理者の別セッション」をprincipalのequals/hashCodeで判定するため、
     * MemberPrincipalと同様にadminIdベースでオーバーライドする
     * (未オーバーライドだとmaximumSessions(1)が機能しない)。
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdminPrincipal other)) {
            return false;
        }
        return Objects.equals(adminId, other.adminId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(adminId);
    }
}
