package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.UserOnlineStatusDTO;
import com.thfh.exception.UserNotLoggedInException;
import com.thfh.model.UserOnlineStatus;
import com.thfh.service.UserOnlineStatusService;
import com.thfh.util.JwtUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 用户在线状态控制器
 * 提供用户在线状态相关的接口，包括查询状态、更新状态、心跳等
 */
@Tag(name = "用户在线状态")
@RestController
@RequestMapping("/api/user/online-status")
@Slf4j
public class UserOnlineStatusController {

    private final UserOnlineStatusService userOnlineStatusService;
    private final JwtUtil jwtUtil;
    
    public UserOnlineStatusController(UserOnlineStatusService userOnlineStatusService, JwtUtil jwtUtil) {
        this.userOnlineStatusService = userOnlineStatusService;
        this.jwtUtil = jwtUtil;
    }
    
    /**
     * 获取当前用户的在线状态
     */
    @Operation(summary = "获取当前用户的在线状态")
    @GetMapping("/my")
    public Result<UserOnlineStatusDTO> getMyStatus(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Result.success(userOnlineStatusService.getUserStatus(userId));
    }
    
    /**
     * 更新当前用户的在线状态
     */
    @Operation(summary = "更新当前用户的在线状态")
    @PutMapping("/my")
    public Result<UserOnlineStatusDTO> updateMyStatus(
            @Parameter(description = "在线状态", required = true, example = "ONLINE") 
            @RequestParam UserOnlineStatus status,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Result.success(userOnlineStatusService.updateUserStatus(userId, status));
    }
    
    /**
     * 更新用户的活跃状态（心跳接口）
     */
    @Operation(summary = "更新当前用户的活跃状态（心跳接口）")
    @PostMapping("/heartbeat")
    public Result<UserOnlineStatusDTO> heartbeat(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Result.success(userOnlineStatusService.heartbeat(userId));
    }
    
    /**
     * 获取指定用户的在线状态
     */
    @Operation(summary = "获取指定用户的在线状态")
    @GetMapping("/user/{userId}")
    public Result<UserOnlineStatusDTO> getUserStatus(
            @Parameter(description = "用户ID", required = true) 
            @PathVariable Long userId) {
        return Result.success(userOnlineStatusService.getUserStatus(userId));
    }
    
    /**
     * 获取好友在线状态列表
     */
    @Operation(summary = "获取好友在线状态列表")
    @GetMapping("/friends")
    public Result<List<UserOnlineStatusDTO>> getFriendsStatus(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return Result.success(userOnlineStatusService.getFriendsStatus(userId));
    }
    
    /**
     * WebSocket在线状态更新
     */
    @MessageMapping("/online-status")
    @SendToUser("/queue/status-result")
    public UserOnlineStatusDTO updateStatusWebSocket(
            @Payload Map<String, Object> payload,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        String statusStr = (String) payload.get("status");
        UserOnlineStatus status = UserOnlineStatus.valueOf(statusStr);
        return userOnlineStatusService.updateUserStatus(userId, status);
    }
    
    /**
     * WebSocket心跳
     */
    @MessageMapping("/heartbeat")
    @SendToUser("/queue/heartbeat-result")
    public UserOnlineStatusDTO heartbeatWebSocket(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        return userOnlineStatusService.heartbeat(userId);
    }
    
    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            return jwtUtil.getUserIdFromToken(token);
        }
        throw new UserNotLoggedInException("未登录或Token无效");
    }
} 