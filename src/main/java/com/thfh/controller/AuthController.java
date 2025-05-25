package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.LoginDTO;
import com.thfh.dto.UserDTO;
import com.thfh.dto.JwtVerifyRequest;
import com.thfh.dto.RefreshTokenRequest;
import com.thfh.dto.ChangePasswordDTO;
import com.thfh.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;

/**
 * 认证控制器
 * 提供用户和管理员登录、登出和获取用户信息等认证功能
 */
@Tag(name = "认证管理")
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
    @Operation(summary = "用户登录", description = "提供用户名和密码，验证身份后返回Token和用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登录成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Result<Map<String, Object>> login(
            @Parameter(description = "登录信息", required = true) @Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(authService.login(loginDTO));
    }
    
    /**
     * 用户注册
     * @param userDTO 用户信息，包含用户名、密码、用户类型等
     * @return 注册结果，包含注册成功的用户信息
     */
    @Operation(summary = "用户注册", description = "提供用户基本信息进行注册，企业用户需要提供公司信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "注册成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<UserDTO> register(
            @Parameter(description = "用户信息", required = true) @Valid @RequestBody UserDTO userDTO) {
        return Result.success(authService.register(userDTO), "注册成功");
    }

    /**
     * 用户登出
     * @return 操作结果
     */
    @Operation(summary = "用户登出", description = "退出登录状态")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "登出成功")
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
    @Operation(summary = "获取当前登录用户信息", description = "根据请求头中的Token获取当前登录用户的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/info")
    public Result<Object> getUserInfo(
            @Parameter(description = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(authService.getUserInfo(username));
    }

    /**
     * 刷新JWT令牌
     * @param request 刷新令牌请求，包含刷新令牌和是否同时刷新刷新令牌的选项
     * @return 新的JWT令牌和用户类型
     */
    @Operation(summary = "刷新JWT令牌", description = "使用刷新令牌获取新的访问令牌，避免令牌过期")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "刷新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "令牌无效或已过期")
    })
    @PostMapping("/refresh-token")
    public Result<Map<String, Object>> refreshToken(
            @Parameter(description = "刷新令牌请求", required = true) @Valid @RequestBody RefreshTokenRequest request) {
        Map<String, Object> result = authService.refreshToken(request.getRefreshToken(), request.getRefreshBoth());
        return Result.success(result, "令牌刷新成功");
    }

    /**
     * 校验JWT Token是否有效
     * @param request 包含token字段的请求体
     * @return Result<Boolean> 有效true，无效false
     */
    @Operation(summary = "校验JWT Token", description = "校验前端传递的JWT Token是否有效")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "校验成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/verify-jwt")
    public Result<Boolean> verifyJwtToken(@Valid @RequestBody JwtVerifyRequest request) {
        boolean valid = authService.verifyJwtToken(request.getToken());
        return Result.success(valid, valid ? "Token有效" : "Token无效或已过期");
    }

    /**
     * 修改用户密码
     * @param changePasswordDTO 包含旧密码和新密码的DTO
     * @param request HTTP请求对象，用于获取用户名
     * @return 操作结果
     */
    @Operation(summary = "修改用户密码", description = "验证旧密码后修改为新密码")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "密码修改成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "旧密码不正确"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PostMapping("/change-password")
    public Result<String> changePassword(
            @Parameter(description = "密码修改信息", required = true) @Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            @Parameter(description = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            return Result.success(authService.changePassword(username, changePasswordDTO), "密码修改成功");
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 校验刷新Token是否有效
     * @param request 包含refreshToken字段的请求体
     * @return Result<Boolean> 有效true，无效false
     */
    @Operation(summary = "校验刷新Token", description = "校验前端传递的刷新Token是否有效")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "校验成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误")
    })
    @PostMapping("/verify-refresh-token")
    public Result<Boolean> verifyRefreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        boolean valid = authService.validateRefreshToken(request.getRefreshToken());
        return Result.success(valid, valid ? "刷新Token有效" : "刷新Token无效或已过期");
    }
}