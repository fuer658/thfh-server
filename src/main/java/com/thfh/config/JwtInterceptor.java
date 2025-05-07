package com.thfh.config;

import com.thfh.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * JWT拦截器
 * 实现HandlerInterceptor接口，用于拦截HTTP请求并验证JWT令牌
 * 在WebConfig中配置，应用于指定的URL路径
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {
    /**
     * JWT工具类，用于验证和解析JWT令牌
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 请求预处理方法
     * 在Controller处理请求之前执行
     * 验证请求中的JWT令牌，并将用户名和用户ID存入请求属性
     * 
     * @param request HTTP请求对象
     * @param response HTTP响应对象
     * @param handler 请求处理器
     * @return 如果验证通过返回true，否则返回false
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");
        
        // 登录接口不需要验证
        if (request.getRequestURI().equals("/api/auth/login")) {
            return true;
        }

        // 验证令牌格式
        if (token == null || !token.startsWith("Bearer ")) {
            response.setStatus(401);  // 设置未授权状态码
            return false;
        }

        // 提取并验证令牌
        token = token.substring(7);  // 去除"Bearer "前缀
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(401);  // 设置未授权状态码
            return false;
        }

        // 将用户名存入request，供后续处理使用
        String username = jwtUtil.getUsernameFromToken(token);
        request.setAttribute("username", username);
        
        // 直接从token中获取用户ID并存入request
        Long userId = jwtUtil.getUserIdFromToken(token);
        if (userId != null) {
            request.setAttribute("userId", userId);
        }
        
        return true;
    }
} 