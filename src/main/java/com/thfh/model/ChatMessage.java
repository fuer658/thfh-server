package com.thfh.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
@ApiModel(value = "聊天消息实体", description = "用户之间的聊天消息")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty(value = "消息ID", example = "1", position = 1)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    @ApiModelProperty(value = "发送者", notes = "发送消息的用户", position = 2)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    @ApiModelProperty(value = "接收者", notes = "接收消息的用户", position = 3)
    private User receiver;
    
    @Column(columnDefinition = "TEXT")
    @ApiModelProperty(value = "消息内容", example = "你好，请问有什么可以帮助你的？", position = 4)
    private String content;
    
    @ApiModelProperty(value = "发送时间", example = "2023-05-20T14:30:00", position = 5)
    private LocalDateTime sentTime;
    
    @Column(name = "is_read")
    @ApiModelProperty(value = "是否已读", example = "false", position = 6)
    private boolean read;
    
    // 消息类型：TEXT, IMAGE, FILE等
    @ApiModelProperty(value = "消息类型", example = "TEXT", notes = "可选值：TEXT, IMAGE, FILE等", position = 7)
    private String messageType;
    
    // 可选的媒体URL
    @ApiModelProperty(value = "媒体URL", example = "https://example.com/image.jpg", notes = "当消息类型为图片或文件时的媒体资源URL", position = 8)
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