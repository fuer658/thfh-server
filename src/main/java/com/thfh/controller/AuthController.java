package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.LoginDTO;
import com.thfh.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success(null);
    }

    @GetMapping("/info")
    public Result<Object> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(authService.getUserInfo(username));
    }
}