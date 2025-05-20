package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.ChatConversationDTO;
import com.thfh.dto.ChatMessageDTO;
import com.thfh.dto.ChatMessageRequest;
import com.thfh.dto.GetMessagesRequest;
import com.thfh.dto.MarkReadRequest;
import com.thfh.service.ChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import com.thfh.exception.BusinessException;
import com.thfh.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "聊天API")
@RestController
@RequestMapping("/api/chat")
@Validated
@Slf4j
public class ChatController {

    private static final String USER_ID_ATTR = "userId";

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 发送消息 (REST API)
     */
    @ApiOperation(value = "发送消息")
    @PostMapping("/send")
    public Result<ChatMessageDTO> sendMessage(
            @ApiParam(value = "消息请求体") @Valid @RequestBody ChatMessageRequest request,
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
     * 发送消息 (WebSocket)
     */
    @MessageMapping("/chat")
    @SendToUser("/queue/messages")
    public ChatMessageDTO sendMessageWebSocket(
            @Payload Map<String, Object> payload,
            HttpServletRequest request) {
        Long senderId = getUserIdFromRequest(request);
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = (String) payload.get("content");
        String messageType = payload.containsKey("messageType") ?
                (String) payload.get("messageType") : "TEXT";
        String mediaUrl = payload.containsKey("mediaUrl") ?
                (String) payload.get("mediaUrl") : null;
        return chatService.sendMessage(senderId, receiverId, content, messageType, mediaUrl);
    }

    /**
     * 获取用户的聊天会话列表
     */
    @ApiOperation(value = "获取用户的聊天会话列表")
    @GetMapping("/conversations")
    public Result<List<ChatConversationDTO>> getUserConversations(
            HttpServletRequest request) {
        Long userId = getUserIdFromRequest(request);
        List<ChatConversationDTO> conversations = chatService.getUserConversations(userId);
        return Result.success(conversations);
    }

    /**
     * 获取与指定用户的聊天记录
     */
    @ApiOperation(value = "获取与指定用户的聊天记录")
    @PostMapping("/messages")
    public Result<List<ChatMessageDTO>> getMessages(
            @ApiParam(value = "请求体") @Valid @RequestBody GetMessagesRequest request,
            HttpServletRequest httpRequest) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        List<ChatMessageDTO> messages = chatService.getMessagesBetweenUsers(currentUserId, request.getOtherUserId());
        chatService.markAllMessagesAsRead(currentUserId, request.getOtherUserId());
        return Result.success(messages);
    }

    /**
     * 将消息标记为已读
     */
    @ApiOperation(value = "将消息标记为已读")
    @PostMapping("/read/{messageId}")
    public Result<Void> markMessageAsRead(
            @ApiParam(value = "消息ID") @PathVariable Long messageId) {

        chatService.markMessageAsRead(messageId);
        return Result.success(null);
    }

    /**
     * 将与指定用户的所有消息标记为已读
     */
    @ApiOperation(value = "将与指定用户的所有消息标记为已读")
    @PostMapping("/read-all")
    public Result<Void> markAllMessagesAsRead(
            @ApiParam(value = "请求体") @Valid @RequestBody MarkReadRequest request,
            HttpServletRequest httpRequest) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        chatService.markAllMessagesAsRead(currentUserId, request.getOtherUserId());
        return Result.success(null);
    }

    /**
     * 删除指定消息
     */
    @ApiOperation(value = "删除指定消息")
    @DeleteMapping("/message/{messageId}")
    public Result<Boolean> deleteMessage(
            @ApiParam(value = "消息ID") @PathVariable Long messageId,
            HttpServletRequest httpRequest) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        boolean success = chatService.deleteMessage(messageId, currentUserId);
        return Result.success(success);
    }

    /**
     * 删除与指定用户的所有聊天记录
     */
    @ApiOperation(value = "删除与指定用户的所有聊天记录")
    @DeleteMapping("/messages/{otherUserId}")
    public Result<Integer> deleteAllMessages(
            @ApiParam(value = "对方用户ID") @PathVariable Long otherUserId,
            HttpServletRequest httpRequest) {
        Long currentUserId = getUserIdFromRequest(httpRequest);
        int deletedCount = chatService.deleteAllMessagesBetweenUsers(currentUserId, otherUserId);
        return Result.success(deletedCount);
    }

    /**
     * 从 request 获取 userId，若无则抛出业务异常
     */
    private Long getUserIdFromRequest(HttpServletRequest request) {
        Object userIdObj = request.getAttribute(USER_ID_ATTR);
        if (userIdObj == null) {
            log.warn("未获取到用户ID");
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未获取到用户ID，请重新登录");
        }
        if (!(userIdObj instanceof Long)) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户ID类型错误");
        }
        return (Long) userIdObj;
    }
}