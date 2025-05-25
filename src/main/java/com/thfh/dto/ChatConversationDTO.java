package com.thfh.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ChatConversationDTO {
    private Long userId;
    private String username;
    private String avatar;
    private String lastMessage;
    private String lastMessageTime;
    private boolean hasUnread;
    private int unreadCount;
    private String messageType;
}
