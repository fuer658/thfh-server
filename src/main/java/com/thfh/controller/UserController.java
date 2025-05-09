package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserDTO;
import com.thfh.dto.UserQueryDTO;
import com.thfh.exception.ResourceNotFoundException;
import com.thfh.model.User;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 * 提供用户相关的API接口，包括用户登录、查询、创建、更新和删除等功能
 */
@Api(tags = "用户管理", description = "用户相关的API接口，包括用户登录、查询、创建、更新和删除等功能")
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;


    /**
     * 获取当前登录用户信息
     * @param request HTTP请求对象，用于获取用户名
     * @return 当前用户信息
     */
    @ApiOperation(value = "获取当前登录用户信息", notes = "根据请求头中的token获取当前登录用户的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(
            @ApiParam(value = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(userService.getUserInfo(username));
    }

    /**
     * 获取用户列表
     * @param queryDTO 查询条件，包含分页信息和筛选条件
     * @return 用户分页列表
     */
    @ApiOperation(value = "获取用户列表", notes = "根据查询条件获取用户分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<UserDTO>> getUsers(
            @ApiParam(value = "查询条件，包含分页信息和筛选条件") UserQueryDTO queryDTO) {
        return Result.success(userService.getUsers(queryDTO));
    }

    /**
     * 创建新用户
     * @param userDTO 用户信息
     * @return 创建的用户信息
     */
    @ApiOperation(value = "创建新用户", notes = "创建一个新的用户账号，需要提供用户的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 409, message = "用户名已存在")
    })
    @PostMapping
    public Result<UserDTO> createUser(
            @ApiParam(value = "用户信息", required = true) @RequestBody UserDTO userDTO) {
        return Result.success(userService.createUser(userDTO));
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 更新的用户信息
     * @return 更新后的用户信息
     */
    @ApiOperation(value = "更新用户信息", notes = "根据用户ID更新用户信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限修改该用户信息"),
        @ApiResponse(code = 404, message = "用户不存在"),
        @ApiResponse(code = 409, message = "新用户名已存在")
    })
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的用户信息", required = true) @RequestBody UserDTO userDTO) {
        return Result.success(userService.updateUser(id, userDTO));
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @ApiOperation(value = "根据ID获取用户信息", notes = "通过用户ID查询用户的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/user-info-id/{id}")
    public Result<UserDTO> getUserById(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        return Result.success(userService.getUserDTOById(id));
    }

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @ApiOperation(value = "根据用户名获取用户信息", notes = "通过用户名查询用户的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @GetMapping("/user-info-username/{username}")
    public Result<UserDTO> getUserByUsername(
            @ApiParam(value = "用户名", required = true) @PathVariable String username) {
        return Result.success(userService.getUserInfo(username));
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除用户", notes = "根据用户ID删除用户")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限删除该用户"),
        @ApiResponse(code = 404, message = "用户不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    /**
     * 更新个性签名
     * @param introduction 新的个性签名（限制100字以内）
     * @return 操作结果
     */
    @ApiOperation(value = "更新个性签名", notes = "更新当前登录用户的个性签名，限制100字以内")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "个性签名超过100字"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PutMapping("/introduction")
    public Result<Void> updateIntroduction(
            @ApiParam(value = "新的个性签名", required = true, example = "这是我的个性签名") @RequestParam String introduction) {
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
    @ApiOperation(value = "切换用户状态", notes = "启用或禁用用户账号，教员账号不能被禁用")
    @ApiResponses({
        @ApiResponse(code = 200, message = "操作成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 403, message = "没有权限修改该用户状态"),
        @ApiResponse(code = 404, message = "用户不存在"),
        @ApiResponse(code = 400, message = "教员账号不能被禁用")
    })
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleUserStatus(
            @ApiParam(value = "用户ID", required = true) @PathVariable Long id) {
        userService.toggleUserStatus(id);
        return Result.success(null);
    }

    /**
     * 根据公司ID查找企业用户
     * 
     * @param companyId 公司ID
     * @return 该公司的所有企业用户列表
     */
    @ApiOperation(value = "根据公司ID查找企业用户", notes = "通过公司ID查找属于该公司的所有企业类型用户")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 404, message = "公司不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/findByCompany/{companyId}")
    public Result<List<UserDTO>> findUsersByCompanyId(
            @ApiParam(value = "公司ID", required = true) @PathVariable Long companyId) {
        try {
            List<User> users = userService.findUsersByCompanyId(companyId);
            List<UserDTO> userDTOs = users.stream()
                    .map(user -> userService.convertToDTO(user))
                    .collect(Collectors.toList());
            return Result.success(userDTOs);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (ResourceNotFoundException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "查询用户失败: " + e.getMessage());
        }
    }

    /**
     * 根据公司ID分页查找企业用户
     * 
     * @param companyId 公司ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页后的企业用户列表
     */
    @ApiOperation(value = "根据公司ID分页查找企业用户", notes = "通过公司ID分页查找属于该公司的所有企业类型用户")
    @ApiResponses({
        @ApiResponse(code = 200, message = "查询成功"),
        @ApiResponse(code = 400, message = "参数错误"),
        @ApiResponse(code = 404, message = "公司不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/findByCompany/{companyId}/page")
    public Result<Page<UserDTO>> findUsersByCompanyIdPage(
            @ApiParam(value = "公司ID", required = true) @PathVariable Long companyId,
            @ApiParam(value = "页码", defaultValue = "1") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
            Page<User> userPage = userService.findUsersByCompanyId(companyId, pageable);
            Page<UserDTO> userDTOPage = userPage.map(user -> userService.convertToDTO(user));
            return Result.success(userDTOPage);
        } catch (IllegalArgumentException e) {
            return Result.error(400, e.getMessage());
        } catch (ResourceNotFoundException e) {
            return Result.error(404, e.getMessage());
        } catch (Exception e) {
            return Result.error(500, "查询用户失败: " + e.getMessage());
        }
    }
}