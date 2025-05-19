package com.thfh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序主类
 * 包含主方法，启动Spring Boot应用
 */
@SpringBootApplication
@EnableScheduling
public class ThfhApplication {
    public static void main(String[] args) {
        SpringApplication.run(ThfhApplication.class, args);
    }
} 