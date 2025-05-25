package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserDTO;
import com.thfh.dto.UserQueryDTO;
import com.thfh.model.User;
import com.thfh.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.thfh.exception.UserNotLoggedInException;

/**
 * 用户管理控制器
 * 提供用户相关的API接口，包括用户登录、查询、创建、更新和删除等功能
 */
@Tag(name = "用户管理")
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
    @Operation(summary = "获取当前登录用户信息", description = "根据请求头中的token获取当前登录用户的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/info")
    public Result<UserDTO> getUserInfo(
            @Parameter(description = "HTTP请求对象", hidden = true) HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        return Result.success(userService.getUserInfo(username));
    }

    /**
     * 获取用户列表
     * @param queryDTO 查询条件，包含分页信息和筛选条件
     * @return 用户分页列表
     */
    @Operation(summary = "获取用户列表", description = "根据查询条件获取用户分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<UserDTO>> getUsers(
            @Parameter(description = "查询条件，包含分页信息和筛选条件") UserQueryDTO queryDTO) {
        return Result.success(userService.getUsers(queryDTO));
    }

    /**
     * 创建新用户
     * @param userDTO 用户信息
     * @return 创建的用户信息
     */
    @Operation(summary = "创建新用户", description = "创建一个新的用户账号，需要提供用户的基本信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "创建成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping
    public Result<UserDTO> createUser(
            @Parameter(description = "用户信息", required = true) @RequestBody UserDTO userDTO) {
        return Result.success(userService.createUser(userDTO));
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param userDTO 更新的用户信息
     * @return 更新后的用户信息
     */
    @Operation(summary = "更新用户信息", description = "根据用户ID更新用户信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "请求参数错误"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限修改该用户信息"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "409", description = "新用户名已存在")
    })
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id,
            @Parameter(description = "更新的用户信息", required = true) @RequestBody UserDTO userDTO) {
        return Result.success(userService.updateUser(id, userDTO));
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @Operation(summary = "根据ID获取用户信息", description = "通过用户ID查询用户的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/user-info-id/{id}")
    public Result<UserDTO> getUserById(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        return Result.success(userService.getUserDTOById(id));
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 操作结果
     */
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限删除该用户"),
        @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success(null);
    }

    /**
     * 更新个性签名
     * @param introduction 新的个性签名（限制100字以内）
     * @return 操作结果
     */
    @Operation(summary = "更新个性签名", description = "更新当前登录用户的个性签名，限制100字以内")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "400", description = "个性签名超过100字"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @PutMapping("/introduction")
    public Result<Void> updateIntroduction(
            @Parameter(description = "新的个性签名", required = true, example = "这是我的个性签名") @RequestParam String introduction) {
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
    @Operation(summary = "切换用户状态", description = "启用或禁用用户账号，教员账号不能被禁用")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "操作成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录"),
        @ApiResponse(responseCode = "403", description = "没有权限修改该用户状态"),
        @ApiResponse(responseCode = "404", description = "用户不存在"),
        @ApiResponse(responseCode = "400", description = "教员账号不能被禁用")
    })
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleUserStatus(
            @Parameter(description = "用户ID", required = true) @PathVariable Long id) {
        userService.toggleUserStatus(id);
        return Result.success(null);
    }

    /**
     * 根据公司ID查找企业用户
     * 
     * @param companyId 公司ID
     * @return 该公司的所有企业用户列表
     */
    @Operation(summary = "根据公司ID查找企业用户", description = "通过公司ID查找属于该公司的所有企业类型用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "404", description = "公司不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/findByCompany/{companyId}")
    public Result<List<UserDTO>> findUsersByCompanyId(
            @Parameter(description = "公司ID", required = true) @PathVariable Long companyId) {
        List<User> users = userService.findUsersByCompanyId(companyId);
        List<UserDTO> userDTOs = users.stream()
                .map(user -> userService.convertToDTO(user))
                .collect(Collectors.toList());
        return Result.success(userDTOs);
    }

    /**
     * 根据公司ID分页查找企业用户
     * 
     * @param companyId 公司ID
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @return 分页后的企业用户列表
     */
    @Operation(summary = "根据公司ID分页查找企业用户", description = "通过公司ID分页查找属于该公司的所有企业类型用户")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "404", description = "公司不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/findByCompany/{companyId}/page")
    public Result<Page<UserDTO>> findUsersByCompanyIdPage(
            @Parameter(description = "公司ID", required = true) @PathVariable Long companyId,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") int pageSize) {
        Pageable pageable = PageRequest.of(pageNum - 1, pageSize);
        Page<User> userPage = userService.findUsersByCompanyId(companyId, pageable);
        Page<UserDTO> userDTOPage = userPage.map(user -> userService.convertToDTO(user));
        return Result.success(userDTOPage);
    }

    /**
     * 获取用户经验值信息
     * @return 用户经验值信息，包含当前经验值、用户等级和升级所需经验值
     */
    @Operation(summary = "获取用户经验值信息", description = "获取当前登录用户的经验值、等级和升级所需经验值")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "401", description = "未授权，请先登录")
    })
    @GetMapping("/experience")
    public Result<Map<String, Object>> getUserExperience() {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new UserNotLoggedInException("未授权，请先登录");
        }
        Map<String, Object> experienceInfo = new HashMap<>();
        int currentExperience = currentUser.getExperience() != null ? currentUser.getExperience() : 0;
        int currentLevel = currentUser.getLevel() != null ? currentUser.getLevel() : 1;
        experienceInfo.put("experience", currentExperience);
        experienceInfo.put("level", currentLevel);
        int nextLevelExperience = userService.getExperienceForNextLevel(currentLevel);
        int requiredExperience = 0;
        if (currentLevel < UserService.MAX_LEVEL) {
            requiredExperience = nextLevelExperience - currentExperience;
            if (requiredExperience < 0) {
                requiredExperience = 0;
            }
        }
        int baseExperience = userService.getBaseExperienceForLevel(currentLevel);
        int levelProgress = 0;
        if (currentLevel < UserService.MAX_LEVEL) {
            int levelExp = nextLevelExperience - baseExperience;
            int currentLevelExp = currentExperience - baseExperience;
            levelProgress = (int) (((float) currentLevelExp / levelExp) * 100);
        } else {
            levelProgress = 100;
        }
        experienceInfo.put("requiredExperience", requiredExperience);
        experienceInfo.put("nextLevelExperience", nextLevelExperience);
        experienceInfo.put("baseExperience", baseExperience);
        experienceInfo.put("levelProgress", levelProgress);
        return Result.success(experienceInfo);
    }

    /**
     * 通过用户名搜索用户
     * @param username 用户名
     * @return 用户信息
     */
    @Operation(summary = "通过用户名搜索用户", description = "根据用户名模糊搜索用户，返回匹配的用户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/search-by-username")
    public Result<List<UserDTO>> searchByUsername(
            @Parameter(description = "用户名，支持模糊搜索", required = true) @RequestParam String username) {
        List<UserDTO> userList = userService.searchByUsername(username);
        return Result.success(userList);
    }

    /**
     * 通过用户名或手机号搜索用户
     * @param keyword 用户名或手机号
     * @return 匹配的用户信息列表
     */
    @Operation(summary = "通过用户名或手机号搜索用户", description = "根据用户名模糊搜索或手机号精确搜索用户，返回匹配的用户列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "查询成功"),
        @ApiResponse(responseCode = "400", description = "参数错误"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/search-by-keyword")
    public Result<List<UserDTO>> searchByKeyword(
            @Parameter(description = "用户名或手机号，支持用户名模糊和手机号精确搜索", required = true) @RequestParam String keyword) {
        List<UserDTO> userList = userService.searchByKeyword(keyword);
        return Result.success(userList);
    }
}