package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.LoginDTO;
import com.thfh.dto.UserDTO;
import com.thfh.dto.JwtVerifyRequest;
import com.thfh.dto.RefreshTokenRequest;
import com.thfh.dto.ChangePasswordDTO;
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
     * @param request HTTP请求对象，用于获取用户名
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
            @ApiParam(value = "登录信息", required = true) @Valid @RequestBody LoginDTO loginDTO,
            @ApiParam(value = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        return Result.success(authService.login(loginDTO, request));
    }
    
    /**
     * 用户注册
     * @param userDTO 用户信息，包含用户名、密码、用户类型等
     * @return 注册结果，包含注册成功的用户信息
     */
    @ApiOperation(value = "用户注册", notes = "提供用户基本信息进行注册，企业用户需要提供公司信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "注册成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 409, message = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<UserDTO> register(
            @ApiParam(value = "用户信息", required = true) @Valid @RequestBody UserDTO userDTO) {
        return Result.success(authService.register(userDTO), "注册成功");
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

    /**
     * 刷新JWT令牌
     * @param request 刷新令牌请求，包含刷新令牌和是否同时刷新刷新令牌的选项
     * @return 新的JWT令牌和用户类型
     */
    @ApiOperation(value = "刷新JWT令牌", notes = "使用刷新令牌获取新的访问令牌，避免令牌过期")
    @ApiResponses({
        @ApiResponse(code = 200, message = "刷新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "令牌无效或已过期")
    })
    @PostMapping("/refresh-token")
    public Result<Map<String, Object>> refreshToken(
            @ApiParam(value = "刷新令牌请求", required = true) @Valid @RequestBody RefreshTokenRequest request) {
        try {
            Map<String, Object> result = authService.refreshToken(request.getRefreshToken(), request.getRefreshBoth());
            return Result.success(result, "令牌刷新成功");
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }

    /**
     * 校验JWT Token是否有效
     * @param request 包含token字段的请求体
     * @return Result<Boolean> 有效true，无效false
     */
    @ApiOperation(value = "校验JWT Token", notes = "校验前端传递的JWT Token是否有效")
    @ApiResponses({
        @ApiResponse(code = 200, message = "校验成功"),
        @ApiResponse(code = 400, message = "请求参数错误")
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
    @ApiOperation(value = "修改用户密码", notes = "验证旧密码后修改为新密码")
    @ApiResponses({
        @ApiResponse(code = 200, message = "密码修改成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "旧密码不正确"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @PostMapping("/change-password")
    public Result<String> changePassword(
            @ApiParam(value = "密码修改信息", required = true) @Valid @RequestBody ChangePasswordDTO changePasswordDTO,
            @ApiParam(value = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        try {
            String username = (String) request.getAttribute("username");
            return Result.success(authService.changePassword(username, changePasswordDTO), "密码修改成功");
        } catch (RuntimeException e) {
            return Result.error(401, e.getMessage());
        }
    }
}