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
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Api(tags = "聊天API")
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final String USER_ID_ATTR = "userId";
    private static final String MESSAGE_KEY = "message";

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
            @ApiParam(value = "消息请求体") @RequestBody ChatMessageRequest request,
            HttpServletRequest httpRequest) {

        Long senderId = (Long) httpRequest.getAttribute(USER_ID_ATTR);
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

        Long senderId = (Long) request.getAttribute(USER_ID_ATTR);
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

        Long userId = (Long) request.getAttribute(USER_ID_ATTR);
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

        Long currentUserId = (Long) httpRequest.getAttribute(USER_ID_ATTR);
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

        Long currentUserId = (Long) httpRequest.getAttribute(USER_ID_ATTR);
        chatService.markAllMessagesAsRead(currentUserId, request.getOtherUserId());
        return Result.success(null);
    }

    /**
     * 聊天图片上传接口
     * 支持单张图片上传，返回图片URL
     */
    @ApiOperation(value = "上传聊天图片", notes = "上传单张聊天图片，仅支持JPG、PNG、GIF、BMP、WEBP格式，单张图片不超过10MB")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功"),
            @ApiResponse(code = 400, message = "请求参数错误或不支持的文件类型"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/upload-image")
    public ResponseEntity<Map<String, Object>> uploadChatImage(
            @ApiParam(value = "上传的图片", required = true) @RequestParam("image") MultipartFile image,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 校验图片类型和大小
            String contentType = image.getContentType();
            List<String> allowedTypes = Arrays.asList(
                    "image/jpeg", "image/png", "image/gif", "image/bmp", "image/webp"
            );
            if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
                result.put("code", 400);
                result.put(MESSAGE_KEY, "不支持的文件类型，仅支持JPG、PNG、GIF、BMP、WEBP格式的图片");
                return ResponseEntity.badRequest().body(result);
            }
            if (image.getSize() > 10 * 1024 * 1024L) {
                result.put("code", 400);
                result.put(MESSAGE_KEY, "图片大小不能超过10MB");
                return ResponseEntity.badRequest().body(result);
            }
            // 构建存储路径
            String uploadDir = "uploads/chat_images/";
            String originalFilename = image.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                result.put("code", 400);
                result.put(MESSAGE_KEY, "文件名无效");
                return ResponseEntity.badRequest().body(result);
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + originalFilename.substring(originalFilename.lastIndexOf('.'));
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            image.transferTo(dest);
            // 构建图片URL（假设静态资源已映射）
            String url = "/" + uploadDir + fileName;
            result.put("code", 200);
            result.put("url", url);
            result.put(MESSAGE_KEY, "上传成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put(MESSAGE_KEY, "图片上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 聊天语音上传接口
     * 支持单条语音上传，返回语音URL
     */
    @ApiOperation(value = "上传聊天语音", notes = "上传单条语音，仅支持MP3、WAV、AAC格式，单条语音不超过10MB")
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功"),
            @ApiResponse(code = 400, message = "请求参数错误或不支持的文件类型"),
            @ApiResponse(code = 401, message = "未授权，请先登录"),
            @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/upload-voice")
    public ResponseEntity<Map<String, Object>> uploadChatVoice(
            @ApiParam(value = "上传的语音", required = true) @RequestParam("voice") MultipartFile voice,
            HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 校验语音类型和大小
            String contentType = voice.getContentType();
            List<String> allowedTypes = Arrays.asList(
                    "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav", "audio/aac"
            );
            if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
                result.put("code", 400);
                result.put(MESSAGE_KEY, "不支持的文件类型，仅支持MP3、WAV、AAC格式的语音");
                return ResponseEntity.badRequest().body(result);
            }
            if (voice.getSize() > 10 * 1024 * 1024L) {
                result.put("code", 400);
                result.put(MESSAGE_KEY, "语音文件大小不能超过10MB");
                return ResponseEntity.badRequest().body(result);
            }
            // 构建存储路径
            String uploadDir = "uploads/chat_voices/";
            String originalFilename = voice.getOriginalFilename();
            if (originalFilename == null || !originalFilename.contains(".")) {
                result.put("code", 400);
                result.put(MESSAGE_KEY, "文件名无效");
                return ResponseEntity.badRequest().body(result);
            }
            String fileName = UUID.randomUUID().toString().replace("-", "") + originalFilename.substring(originalFilename.lastIndexOf('.'));
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            File dest = new File(uploadDir + fileName);
            voice.transferTo(dest);
            // 构建语音URL（假设静态资源已映射）
            String url = "/" + uploadDir + fileName;
            result.put("code", 200);
            result.put("url", url);
            result.put(MESSAGE_KEY, "上传成功");
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("code", 500);
            result.put(MESSAGE_KEY, "语音上传失败: " + e.getMessage());
            return ResponseEntity.status(500).body(result);
        }
    }

    /**
     * 删除指定消息
     */
    @ApiOperation(value = "删除指定消息")
    @DeleteMapping("/message/{messageId}")
    public Result<Boolean> deleteMessage(
            @ApiParam(value = "消息ID") @PathVariable Long messageId,
            HttpServletRequest httpRequest) {

        Long currentUserId = (Long) httpRequest.getAttribute("userId");
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

        Long currentUserId = (Long) httpRequest.getAttribute("userId");
        int deletedCount = chatService.deleteAllMessagesBetweenUsers(currentUserId, otherUserId);
        return Result.success(deletedCount);
    }
}