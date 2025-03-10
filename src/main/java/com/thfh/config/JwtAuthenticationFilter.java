package com.thfh.config;

import com.thfh.util.JwtUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JWT认证过滤器
 * 继承OncePerRequestFilter，确保每个请求只执行一次过滤
 * 用于从请求中提取JWT令牌，验证其有效性，并设置Spring Security上下文
 * 在SecurityConfig中配置，应用于所有请求
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT工具类，用于验证和解析JWT令牌
     */
    private final JwtUtil jwtUtil;

    /**
     * 构造函数，注入JWT工具类
     * @param jwtUtil JWT工具类实例
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * 过滤器内部处理方法
     * 从请求中提取JWT令牌，验证其有效性，并设置认证信息
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param filterChain 过滤器链，用于继续执行后续过滤器
     * @throws ServletException 如果处理过程中发生Servlet异常
     * @throws IOException 如果处理过程中发生IO异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // 从请求中获取JWT令牌
            String jwt = getJwtFromRequest(request);

            // 验证令牌有效性并设置认证信息
            if (StringUtils.hasText(jwt) && jwtUtil.validateToken(jwt)) {
                // 从令牌中获取用户名
                String username = jwtUtil.getUsernameFromToken(jwt);

                // 创建认证令牌（不包含权限信息）
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, new ArrayList<>()
                );
                // 设置认证详情（如IP地址、会话ID等）
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证信息设置到Spring Security上下文中
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            // 记录异常但不中断请求处理
            logger.error("Could not set user authentication in security context", ex);
        }

        // 继续执行过滤器链中的下一个过滤器
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求中提取JWT令牌
     * 从Authorization请求头中获取Bearer令牌
     * 
     * @param request HTTP请求对象
     * @return 提取的JWT令牌，如果不存在则返回null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // 去除"Bearer "前缀
        }
        return null;
    }
}
