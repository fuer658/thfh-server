package com.thfh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 应用程序入口类
 * 使用@SpringBootApplication注解标记为SpringBoot应用
 */
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.thfh.repository")
@EntityScan(basePackages = "com.thfh.model")
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.thfh.service", "com.thfh.controller", "com.thfh.config", "com.thfh.util", "com.thfh.repository", "com.thfh.exception","com.thfh.common"})
@EnableScheduling
public class ThfhAdminApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    /**
     * 主方法，应用程序启动入口
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(ThfhAdminApplication.class, args);
    }
}