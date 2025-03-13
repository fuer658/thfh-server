package com.thfh.config;

import com.thfh.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * Spring Security配置类
 * 用于配置应用的安全策略，包括认证、授权、密码加密等
 * 继承WebSecurityConfigurerAdapter以自定义安全配置
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
     * 配置认证管理器
     * 设置用户详情服务和密码编码器
     * @param auth 认证管理器构建器
     * @throws Exception 如果配置过程中发生错误
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    /**
     * 暴露认证管理器Bean
     * 用于在其他组件中进行认证操作
     * @return AuthenticationManager实例
     * @throws Exception 如果获取过程中发生错误
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
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
     * @throws Exception 如果配置过程中发生错误
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()  // 启用跨域资源共享
                .csrf().disable()  // 禁用CSRF保护，因为使用JWT进行认证
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 使用无状态会话
                .and()
                .authorizeRequests()  // 配置请求授权
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()  // 允许所有OPTIONS请求
                .antMatchers("/api/auth/**").permitAll()  // 允许所有认证相关的请求
                .antMatchers("/uploads/**").permitAll()  // 允许访问上传的文件
                .antMatchers("/doc.html").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/swagger-resources/**").permitAll()
                .antMatchers("/v2/api-docs/**").permitAll()
                .antMatchers("/api/**").authenticated()  // 所有API接口需要认证
                .anyRequest().authenticated()  // 其他所有请求都需要认证
                .and()
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);  // 添加JWT认证过滤器
    }

    /**
     * CORS配置源Bean
     * 用于配置跨域资源共享策略
     * @return CorsConfigurationSource实例
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080"));  // 允许的源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // 允许的HTTP方法
        configuration.setAllowedHeaders(Arrays.asList("*"));  // 允许的请求头
        configuration.setExposedHeaders(Arrays.asList("Authorization"));  // 暴露的响应头
        configuration.setAllowCredentials(true);  // 允许携带凭证

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);  // 对所有路径应用CORS配置
        return source;
    }
}
