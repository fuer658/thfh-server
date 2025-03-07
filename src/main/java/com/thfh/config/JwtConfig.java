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
     * JWT令牌过期时间（秒）
     * 默认为86400秒（24小时）
     */
    private long expiration = 86400; // 24小时
} 