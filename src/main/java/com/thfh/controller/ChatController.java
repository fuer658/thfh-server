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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Api(tags = "聊天API")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    /**
     * 发送消息 (REST API)
     */
    @ApiOperation(value = "发送消息")
    @PostMapping("/send")
    public Result<ChatMessageDTO> sendMessage(
            @ApiParam(value = "消息请求体") @RequestBody ChatMessageRequest request,
            HttpServletRequest httpRequest) {
        
        Long senderId = (Long) httpRequest.getAttribute("userId");
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
        
        Long senderId = (Long) request.getAttribute("userId");
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
        
        Long userId = (Long) request.getAttribute("userId");
        List<ChatConversationDTO> conversations = chatService.getUserConversations(userId);
        return Result.success(conversations);
    }

    /**
     * 获取与指定用户的聊天记录
     */
    @ApiOperation(value = "获取与指定用户的聊天记录")
    @PostMapping("/messages")
    public Result<List<ChatMessageDTO>> getMessages(
            @ApiParam(value = "请求体") @RequestBody GetMessagesRequest request,
            HttpServletRequest httpRequest) {
        
        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        List<ChatMessageDTO> messages = chatService.getMessagesBetweenUsers(currentUserId, request.getOtherUserId());
        
        // 标记所有消息为已读
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
            @ApiParam(value = "请求体") @RequestBody MarkReadRequest request,
            HttpServletRequest httpRequest) {
        
        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        chatService.markAllMessagesAsRead(currentUserId, request.getOtherUserId());
        return Result.success(null);
    }
} 