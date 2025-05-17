package com.thfh.util;

import com.thfh.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    @Autowired
    private JwtConfig jwtConfig;

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        return createToken(claims);
    }
    
    /**
     * 生成带有用户ID的JWT令牌
     * 
     * @param username 用户名
     * @param userId 用户ID
     * @return JWT令牌字符串
     */
    public String generateToken(String username, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);
        claims.put("userId", userId);
        return createToken(claims);
    }

    private String createToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiration() * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtConfig.getSecret())
                .compact();
    }

    /**
     * 刷新令牌
     * 从原令牌中提取claims，重新生成一个新的令牌
     *
     * @param token 原令牌
     * @return 新的令牌
     * @throws Exception 如果令牌无效或已过期
     */
    public String refreshToken(String token) throws Exception {
        try {
            final Claims claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSecret())
                    .parseClaimsJws(token)
                    .getBody();
            
            // 创建新的claims，仅保留关键信息
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("username", claims.get("username"));
            if (claims.get("userId") != null) {
                newClaims.put("userId", claims.get("userId"));
            }
            
            // 创建新令牌
            return createToken(newClaims);
        } catch (Exception e) {
            throw new Exception("无效的令牌或令牌已过期");
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(jwtConfig.getSecret())
                .parseClaimsJws(token)
                .getBody();
        return claims.get("username", String.class);
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
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtConfig.getSecret()).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 