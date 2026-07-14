package com.example.demo.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.session.HttpSessionEventPublisher;

/**
 * 会員(ROLE_MEMBER)と管理者(ROLE_ADMIN)を別々のSecurityFilterChainで扱うセキュリティ設定。
 *
 * <p>MemberUserDetailsService / AdminUserDetailsService は本クラスと同じ
 * com.example.demo.security パッケージ(または委譲先のservice層)に別途実装すること。</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final MemberUserDetailsService memberUserDetailsService;
    private final AdminUserDetailsService adminUserDetailsService;
    private final MemberAuthenticationSuccessHandler memberAuthenticationSuccessHandler;

    public SecurityConfig(MemberUserDetailsService memberUserDetailsService,
                           AdminUserDetailsService adminUserDetailsService,
                           MemberAuthenticationSuccessHandler memberAuthenticationSuccessHandler) {
        this.memberUserDetailsService = memberUserDetailsService;
        this.adminUserDetailsService = adminUserDetailsService;
        this.memberAuthenticationSuccessHandler = memberAuthenticationSuccessHandler;
    }

    /**
     * パスワードのハッシュ化・照合に使用するエンコーダー。
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 同一ユーザーの多重ログインを禁止するためのセッションレジストリ(NFR-S07)。
     * 会員・管理者の両フィルタチェーンで共有する。
     */
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    /**
     * HttpSessionの破棄イベントをSessionRegistryへ伝播させるリスナー。
     * これが無いと、ログアウトなどでセッションが破棄されてもレジストリ上は
     * セッションが有効なままとなり、maximumSessions(1)が正しく機能しない。
     */
    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    /**
     * 403(アクセス拒否)発生時に /error/403 へフォワードするハンドラー。
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
                -> request.getRequestDispatcher("/error/403").forward(request, response);
    }

    /**
     * /admin/** への未認証アクセス時に、ログイン画面へリダイレクトする代わりに
     * /error/403 へフォワードするエントリーポイント(アクセス制御マトリクスの要件)。
     */
    @Bean
    public AuthenticationEntryPoint forbiddenEntryPoint() {
        return (HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
                -> request.getRequestDispatcher("/error/403").forward(request, response);
    }

    /**
     * 会員用の認証プロバイダー。
     */
    @Bean
    public DaoAuthenticationProvider memberAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(memberUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * 管理者用の認証プロバイダー。
     */
    @Bean
    public DaoAuthenticationProvider adminAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(adminUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * /admin/** 用のフィルタチェーン。会員用チェーン(securityMatcher指定なし)より先に
     * 評価される必要があるため @Order(1) を付与する。
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .authorizeHttpRequests(auth -> auth
                        // /admin/login はゲスト(未認証)・管理者のみ許可。会員ロールは403(アクセス制御マトリクス)。
                        .requestMatchers("/admin/login").access(AuthorizationManagers.anyOf(
                                AuthenticatedAuthorizationManager.anonymous(),
                                AuthorityAuthorizationManager.hasRole("ADMIN")
                        ))
                        .anyRequest().hasRole("ADMIN")
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .loginProcessingUrl("/admin/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .failureUrl("/admin/login?error")
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessUrl("/admin/login")
                        .invalidateHttpSession(true)
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                )
                .exceptionHandling(handling -> handling
                        .authenticationEntryPoint(forbiddenEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authenticationProvider(adminAuthenticationProvider());

        return http.build();
    }

    /**
     * /admin/** 以外を扱う会員用フィルタチェーン。管理者用チェーンの後に評価される。
     */
    @Bean
    @Order(2)
    public SecurityFilterChain memberSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/register/**", "/login", "/error/**",
                                "/products/**", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/cart/**", "/orders/**").hasRole("MEMBER")
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .successHandler(memberAuthenticationSuccessHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .sessionRegistry(sessionRegistry())
                )
                .exceptionHandling(handling -> handling.accessDeniedHandler(accessDeniedHandler()))
                .authenticationProvider(memberAuthenticationProvider());

        return http.build();
    }
}
