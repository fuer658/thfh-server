package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ChatConversationDTO;
import com.thfh.dto.ChatMessageDTO;
import com.thfh.dto.ChatMessageRequest;
import com.thfh.dto.GetMessagesRequest;
import com.thfh.dto.MarkReadRequest;
import com.thfh.service.ChatService;
import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Tag(name = "聊天API")
@RestController
@RequestMapping("/api/chat")
@Validated
@Slf4j
public class ChatController {

    private static final String USER_ID_ATTR = "userId";

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息
     */
    @Operation(summary = "发送消息")
    @PostMapping("/send")
    public Result<ChatMessageDTO> sendMessage(
            @Valid @RequestBody ChatMessageRequest request,
            HttpServletRequest httpRequest) {
        Long senderId = getUserIdFromRequest(httpRequest);
        ChatMessageDTO message = chatService.sendMessage(
                senderId,
                request.getReceiverId(),
                request.getContent(),
                request.getMessageType(),
                request.getMediaUrl()
        );
        return Result.success(message);
    }

    /**
     * WebSocket发送消息
     */
    @MessageMapping("/send")
    @SendToUser("/queue/messages")
    public ChatMessageDTO sendMessageWebSocket(
            @Payload ChatMessageRequest request,
            HttpServletRequest httpRequest) {
        Long senderId = getUserIdFromRequest(httpRequest);
        return chatService.sendMessage(
                senderId,
                request.getReceiverId(),
                request.getContent(),
                request.getMessageType(),
                request.getMediaUrl()
        );
    }

    /**
     * 获取用户的所有会话
     */
    @Operation(summary = "获取用户的所有会话")
    @GetMapping("/conversations")
    public Result<List<ChatConversationDTO>> getUserConversations(HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<ChatConversationDTO> conversations = chatService.getUserConversations(userId);
        return Result.success(conversations);
    }

    /**
     * 获取与指定用户的聊天记录
     */
    @Operation(summary = "获取与指定用户的聊天记录")
    @PostMapping("/messages")
    public Result<List<ChatMessageDTO>> getMessagesBetweenUsers(
            @Valid @RequestBody GetMessagesRequest request,
            HttpServletRequest httpRequest) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        List<ChatMessageDTO> messages = chatService.getMessagesBetweenUsers(currentUserId, request.getOtherUserId());
        chatService.markAllMessagesAsRead(currentUserId, request.getOtherUserId());
        return Result.success(messages);
    }

    /**
     * 将消息标记为已读
     */
    @Operation(summary = "将消息标记为已读")
    @PostMapping("/read/{messageId}")
    public Result<Void> markMessageAsRead(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        chatService.markMessageAsRead(messageId, userId);
        return Result.success(null);
    }

    /**
     * 将与指定用户的所有消息标记为已读
     */
    @Operation(summary = "将与指定用户的所有消息标记为已读")
    @PostMapping("/read-all")
    public Result<Void> markAllMessagesAsRead(
            @Valid @RequestBody MarkReadRequest request,
            HttpServletRequest httpRequest) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        chatService.markAllMessagesAsRead(currentUserId, request.getOtherUserId());
        return Result.success(null);
    }

    /**
     * 删除指定消息
     */
    @Operation(summary = "删除指定消息")
    @DeleteMapping("/message/{messageId}")
    public Result<Void> deleteMessage(
            @Parameter(description = "消息ID") @PathVariable Long messageId,
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        chatService.deleteMessage(messageId, userId);
        return Result.success(null);
    }

    /**
     * 从请求中获取用户ID
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute(USER_ID_ATTR);
        if (userIdObj == null) {
            log.warn("未获取到用户ID");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未获取到用户ID，请重新登录");
        }
        if (!(userIdObj instanceof Long)) {
            try {
                return Long.valueOf(userIdObj.toString());
            } catch (Exception e) {
                log.error("用户ID类型转换失败: {}", userIdObj);
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户ID类型错误");
            }
        }
        return (Long) userIdObj;
    }
}
