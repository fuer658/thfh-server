package com.thfh.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册STOMP端点，允许客户端通过WebSocket连接
        registry.addEndpoint("/ws")
                .setAllowedOrigins("*") // 允许所有来源的WebSocket连接
                .withSockJS(); // 启用SockJS作为备用选项
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 配置消息代理，用户订阅消息的前缀
        registry.enableSimpleBroker("/topic", "/queue");
        // 配置应用程序目的地前缀
        registry.setApplicationDestinationPrefixes("/app");
        // 配置用户目的地前缀
        registry.setUserDestinationPrefix("/user");
    }
} 