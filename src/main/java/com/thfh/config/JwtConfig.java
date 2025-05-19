package com.thfh.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 * 用于配置JWT的密钥和过期时间
 * 从application.properties或application.yml中读取jwt前缀的配置项
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    /**
     * JWT加密密钥
     * 默认值为"thfh-admin-secret-key"
     */
    private String secret = "thfh-admin-secret-key";
    
    /**
     * JWT访问令牌过期时间（秒）
     * 默认为3600秒（1小时）
     */
    private long expiration = 3600; // 1小时
    
    /**
     * JWT刷新令牌过期时间（秒）
     * 默认为2592000秒（30天）
     */
    private long refreshExpiration = 2592000; // 30天
    
    /**
     * 访问令牌前缀
     */
    private String tokenPrefix = "Bearer ";
    
    /**
     * 刷新令牌类型标识
     */
    private String refreshTokenType = "REFRESH";
} 