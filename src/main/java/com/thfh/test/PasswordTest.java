package com.thfh.test;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "admin123";
        String encodedPassword = encoder.encode(rawPassword);
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密密码: " + encodedPassword);
        System.out.println("验证结果: " + encoder.matches(rawPassword, encodedPassword));

        // 验证你提供的密文
        String yourEncodedPassword = "$2a$10$ZXx81OW5MV7lkTmGtYvwHOzfDdKlNgHp0k/S1J.h78FYCzgTJNttC";
        System.out.println("验证你的密文: " + encoder.matches(rawPassword, yourEncodedPassword));
    }
}
