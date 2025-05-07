package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.LoginDTO;
import com.thfh.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 * 提供用户和管理员登录、登出和获取用户信息等认证功能
 */
@Api(tags = "认证管理", description = "用户和管理员登录、登出和获取用户信息等认证功能")
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 用户登录
     * @param loginDTO 登录信息，包含用户名和密码
     * @return 登录结果，包含token和用户信息
     */
    @ApiOperation(value = "用户登录", notes = "提供用户名和密码，验证身份后返回Token和用户信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "登录成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @ApiParam(value = "登录信息", required = true) @Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }

    /**
     * 用户登出
     * @return 操作结果
     */
    @ApiOperation(value = "用户登出", notes = "退出登录状态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "登出成功")
    })
    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success(null);
    }

    /**
     * 获取当前登录用户信息
     * @param request HTTP请求对象，用于获取用户名
     * @return 当前用户信息
     */
    @ApiOperation(value = "获取当前登录用户信息", notes = "根据请求头中的Token获取当前登录用户的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/info")
    public Result<Object> getUserInfo(
            @ApiParam(value = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(authService.getUserInfo(username));
    }
}