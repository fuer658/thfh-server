package com.thfh.util;

import com.thfh.config.JwtConfig;
import com.thfh.exception.JwtException;
import com.thfh.service.JwtBlacklistService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class JwtUtil {
    // 定义常量替代重复字符串
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_TOKEN_TYPE = "tokenType";
    private static final String CLAIM_JTI = "jti";
    private static final String TOKEN_TYPE_ACCESS = "ACCESS";
    
    private final JwtConfig jwtConfig;
    private final JwtBlacklistService jwtBlacklistService;
    
    // 使用构造函数注入替代字段注入
    public JwtUtil(JwtConfig jwtConfig, JwtBlacklistService jwtBlacklistService) {
        this.jwtConfig = jwtConfig;
        this.jwtBlacklistService = jwtBlacklistService;
    }
    
    /**
     * 生成访问令牌
     * @param username 用户名
     * @return JWT访问令牌字符串
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        return createToken(claims, jwtConfig.getExpiration());
    }
    
    /**
     * 生成带有用户ID的JWT访问令牌
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT访问令牌字符串
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_TOKEN_TYPE, TOKEN_TYPE_ACCESS);
        return createToken(claims, jwtConfig.getExpiration());
    }
    
    /**
     * 生成刷新令牌
     * @param username 用户名
     * @return JWT刷新令牌字符串
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_TOKEN_TYPE, jwtConfig.getRefreshTokenType());
        claims.put(CLAIM_JTI, UUID.randomUUID().toString()); // 添加唯一标识
        return createToken(claims, jwtConfig.getRefreshExpiration());
    }
    
    /**
     * 生成带有用户ID的JWT刷新令牌
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT刷新令牌字符串
     */
    public String generateRefreshToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_USERNAME, username);
        claims.put(CLAIM_USER_ID, userId);
        claims.put(CLAIM_TOKEN_TYPE, jwtConfig.getRefreshTokenType());
        claims.put(CLAIM_JTI, UUID.randomUUID().toString()); // 添加唯一标识
        return createToken(claims, jwtConfig.getRefreshExpiration());
    }

    private String createToken(Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    /**
     * 刷新访问令牌
     * 使用刷新令牌生成新的访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     * @throws JwtException 如果令牌无效或已过期或不是刷新令牌
     */
    public String refreshAccessToken(String refreshToken) throws JwtException {
        try {
            // 校验令牌是否在黑名单中
            if (jwtBlacklistService.isTokenBlacklisted(refreshToken)) {
                throw new JwtException("令牌已被吊销");
            }
            
            // 解析刷新令牌
            final Claims claims = extractAllClaims(refreshToken);
            
            // 验证是否为刷新令牌
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (!jwtConfig.getRefreshTokenType().equals(tokenType)) {
                throw new JwtException("无效的刷新令牌类型");
            }
            
            // 获取用户名和ID
            String username = claims.get(CLAIM_USERNAME, String.class);
            Long userId = claims.get(CLAIM_USER_ID, Long.class);
            
            // 将旧的刷新令牌加入黑名单
            jwtBlacklistService.addToBlacklist(refreshToken, getExpirationFromToken(refreshToken));
            
            // 创建新的访问令牌
            if (userId != null) {
                return generateToken(username, userId);
            } else {
                return generateToken(username);
            }
        } catch (Exception e) {
            throw new JwtException("刷新令牌无效或已过期: " + e.getMessage(), e);
        }
    }
    
    /**
     * 使用刷新令牌生成新的访问令牌和刷新令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 包含新访问令牌和刷新令牌的Map
     * @throws JwtException 如果令牌无效或已过期
     */
    public Map<String, String> refreshBothTokens(String refreshToken) throws JwtException {
        try {
            // 校验令牌是否在黑名单中
            if (jwtBlacklistService.isTokenBlacklisted(refreshToken)) {
                throw new JwtException("令牌已被吊销");
            }
            
            // 解析刷新令牌
            final Claims claims = extractAllClaims(refreshToken);
            
            // 验证是否为刷新令牌
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            if (!jwtConfig.getRefreshTokenType().equals(tokenType)) {
                throw new JwtException("无效的刷新令牌类型");
            }
            
            // 获取用户名和ID
            String username = claims.get(CLAIM_USERNAME, String.class);
            Long userId = claims.get(CLAIM_USER_ID, Long.class);
            
            // 将旧的刷新令牌加入黑名单
            jwtBlacklistService.addToBlacklist(refreshToken, getExpirationFromToken(refreshToken));
            
            // 创建新的令牌
            Map<String, String> tokens = new HashMap<>();
            if (userId != null) {
                tokens.put("accessToken", generateToken(username, userId));
                tokens.put("refreshToken", generateRefreshToken(username, userId));
            } else {
                tokens.put("accessToken", generateToken(username));
                tokens.put("refreshToken", generateRefreshToken(username));
            }
            
            return tokens;
        } catch (Exception e) {
            throw new JwtException("刷新令牌无效或已过期: " + e.getMessage(), e);
        }
    }

    /**
     * 从令牌中提取所有声明
     */
    private Claims extractAllClaims(String token) throws JwtException {
        try {
            return Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new JwtException("解析令牌失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取令牌过期时间
     */
    private Date getExpirationDateFromToken(String token) throws JwtException {
        try {
            return extractAllClaims(token).getExpiration();
        } catch (Exception e) {
            throw new JwtException("获取过期时间失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 获取令牌剩余有效时间（秒）
     */
    private long getExpirationFromToken(String token) throws JwtException {
        Date expiration = getExpirationDateFromToken(token);
        return (expiration.getTime() - System.currentTimeMillis()) / 1000;
    }

    public String getUsernameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(CLAIM_USERNAME, String.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 从JWT令牌中提取用户ID
     * 
     * @param token JWT令牌
     * @return 用户ID，如果不存在则返回null
     */
    public Long getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(CLAIM_USER_ID, Long.class);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 判断令牌是否为刷新令牌
     */
    public boolean isRefreshToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            return jwtConfig.getRefreshTokenType().equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            // 检查令牌是否在黑名单中
            if (jwtBlacklistService.isTokenBlacklisted(token)) {
                return false;
            }
            
            Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 验证刷新令牌是否有效
     */
    public boolean validateRefreshToken(String token) {
        try {
            // 检查令牌是否在黑名单中
            if (jwtBlacklistService.isTokenBlacklisted(token)) {
                return false;
            }
            
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            
            // 验证令牌类型
            String tokenType = claims.get(CLAIM_TOKEN_TYPE, String.class);
            return jwtConfig.getRefreshTokenType().equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 吊销令牌（加入黑名单）
     */
    public void revokeToken(String token) {
        try {
            long expirationTime = getExpirationFromToken(token);
            jwtBlacklistService.addToBlacklist(token, expirationTime);
        } catch (Exception e) {
            // 令牌已过期或无效，无需处理
        }
    }
} 