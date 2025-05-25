package com.thfh.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Getter
@Setter
@Entity
@Table(name = "chat_messages")
@Schema(description = "聊天消息实体 - 用户之间的聊天消息")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "消息ID", example = "1")
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @Schema(description = "发送者 - 发送消息的用户")
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @Schema(description = "接收者 - 接收消息的用户")
    private User receiver;
    
    @Column(columnDefinition = "TEXT")
    @Schema(description = "消息内容", example = "你好，请问有什么可以帮助你的？")
    private String content;
    
    @Schema(description = "发送时间", example = "2023-05-20T14:30:00")
    private LocalDateTime sentTime;
    
    @Column(name = "is_read")
    @Schema(description = "是否已读", example = "false")
    private boolean read;
    
    // 消息类型：TEXT, IMAGE, FILE等
    @Schema(description = "消息类型 - 可选值：TEXT, IMAGE, FILE等", example = "TEXT")
    private String messageType;
    
    // 可选的媒体URL
    @Schema(description = "媒体URL - 当消息类型为图片或文件时的媒体资源URL", example = "https://example.com/image.jpg")
    private String mediaUrl;
    
    @PrePersist
    public void prePersist() {
        if (sentTime == null) {
            sentTime = LocalDateTime.now();
        }
        if (messageType == null) {
            messageType = "TEXT";
        }
        if (read == false) {
            read = false;
        }
    }
}
