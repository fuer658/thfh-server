package com.thfh.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;
    
    @Column(columnDefinition = "TEXT")
    private String content;
    
    private LocalDateTime sentTime;
    
    @Column(name = "is_read")
    private boolean read;
    
    // 消息类型：TEXT, IMAGE, FILE等
    private String messageType;
    
    // 可选的媒体URL
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