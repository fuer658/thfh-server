package com.thfh.config;

import com.thfh.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * Spring Security配置类
 * 用于配置应用的安全策略，包括认证、授权、密码加密等
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT工具类，用于生成和验证JWT令牌
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户详情服务，用于从数据库加载用户信息
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 密码编码器Bean
     * 使用BCrypt算法对密码进行加密和验证
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器Bean
     * 用于在其他组件中进行认证操作
     * @return AuthenticationManager实例
     */
    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    /**
     * JWT认证过滤器Bean
     * 用于拦截请求并验证JWT令牌
     * @return JwtAuthenticationFilter实例
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * 配置HTTP安全策略
     * 设置跨域、CSRF、会话管理、请求授权规则等
     * @param http HTTP安全构建器
     * @return SecurityFilterChain实例
     * @throws Exception 如果配置过程中发生错误
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(new AntPathRequestMatcher("/**", HttpMethod.OPTIONS.name())).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/auth/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/uploads/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/doc.html")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/webjars/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/swagger-resources/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/v2/api-docs/**")).permitAll()
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * CORS配置源Bean
     * 用于配置跨域资源共享策略
     * @return CorsConfigurationSource实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // 允许所有来源的跨域请求
        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
        
        // 允许的HTTP方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        
        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList("*"));
        
        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // 允许携带凭证
        configuration.setAllowCredentials(true);
        
        // 预检请求有效期
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 对所有路径应用CORS配置
        return source;
    }
}
