package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.AdminDTO;
import com.thfh.dto.LoginDTO;
import com.thfh.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 * 提供管理员登录、登出和获取信息等认证相关功能
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 管理员登录
     * @param loginDTO 登录信息，包含用户名和密码
     * @return 登录结果，包含token和管理员信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    /**
     * 管理员登出
     * @return 登出结果
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success(null);
    }

    /**
     * 获取当前登录管理员信息
     * @param request HTTP请求对象，用于获取用户名
     * @return 当前管理员信息
     */
    @GetMapping("/info")
    public Result<AdminDTO> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(authService.getUserInfo(username));
    }
} 