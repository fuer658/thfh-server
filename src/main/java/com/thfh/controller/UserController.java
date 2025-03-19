package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.LoginDTO;
import com.thfh.dto.UserDTO;
import com.thfh.dto.UserQueryDTO;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户相关的API接口，包括用户登录、查询、创建、更新和删除等功能
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 用户登录
     * @param loginDTO 登录信息，包含用户名和密码
     * @return 登录结果，包含token和用户信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        return Result.success(userService.login(loginDTO));
    }

    /**
     * 获取当前登录用户信息
     * @param request HTTP请求对象，用于获取用户名
     * @return 当前用户信息
     */
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(userService.getUserInfo(username));
    }

    /**
     * 获取用户列表
     * @param queryDTO 查询条件，包含分页信息和筛选条件
     * @return 用户分页列表
     */
    @GetMapping
    public Result<Page<UserDTO>> getUsers(UserQueryDTO queryDTO) {
        return Result.success(userService.getUsers(queryDTO));
    }

    /**
     * 创建新用户
     * @param userDTO 用户信息
     * @return 创建的用户信息
     */
    @PostMapping
    public Result<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return Result.success(userService.createUser(userDTO));
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 更新的用户信息
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        return Result.success(userService.updateUser(id, userDTO));
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    /**
     * 更新个性签名
     * @param introduction 新的个性签名（限制100字以内）
     * @return 操作结果
     */
    @PutMapping("/introduction")
    public Result<Void> updateIntroduction(@RequestParam String introduction) {
        if (introduction != null && introduction.length() > 100) {
            return Result.error("个性签名不能超过100字");
        }
        userService.updateIntroduction(introduction);
        return Result.success(null);
    }

    /**
     * 切换用户状态（启用/禁用）
     * @param id 用户ID
     * @return 操作结果
     */
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleUserStatus(@PathVariable Long id) {
        userService.toggleUserStatus(id);
        return Result.success(null);
    }
}