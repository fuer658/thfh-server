package com.thfh.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT黑名单服务
 * 管理已吊销的JWT令牌，使用内存Map实现
 */
@Service
public class JwtBlacklistService {
    
    // 使用ConcurrentHashMap存储令牌黑名单，键为令牌，值为过期时间
    private final Map<String, Date> blacklistedTokens = new ConcurrentHashMap<>();
    
    /**
     * 将令牌添加到黑名单
     * 
     * @param token 要加入黑名单的令牌
     * @param expirationTime 过期时间（秒）
     */
    public void addToBlacklist(String token, long expirationTime) {
        // 计算过期时间
        Date expirationDate = new Date(System.currentTimeMillis() + expirationTime * 1000);
        blacklistedTokens.put(token, expirationDate);
    }
    
    /**
     * 检查令牌是否在黑名单中
     * 
     * @param token 要检查的令牌
     * @return 是否在黑名单中
     */
    public boolean isTokenBlacklisted(String token) {
        Date expirationDate = blacklistedTokens.get(token);
        if (expirationDate == null) {
            return false;
        }
        
        // 如果过期时间已到，从黑名单中移除并返回false
        if (expirationDate.before(new Date())) {
            blacklistedTokens.remove(token);
            return false;
        }
        
        return true;
    }
    
    /**
     * 从黑名单中移除令牌
     * 
     * @param token 要移除的令牌
     */
    public void removeFromBlacklist(String token) {
        blacklistedTokens.remove(token);
    }
    
    /**
     * 清除所有黑名单令牌
     * 慎用，通常不需要调用此方法
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
    }
    
    /**
     * 定期清理过期的令牌
     * 每小时执行一次
     */
    @Scheduled(fixedRate = 3600000) // 3600000毫秒 = 1小时
    public void cleanupExpiredTokens() {
        Date now = new Date();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue().before(now));
    }
} 