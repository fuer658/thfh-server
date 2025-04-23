package com.thfh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.thfh.repository")
@EntityScan(basePackages = "com.thfh.model")
@EnableTransactionManagement
@ComponentScan(basePackages = {"com.thfh.service", "com.thfh.controller", "com.thfh.config", "com.thfh.util", "com.thfh.repository"})
public class ThfhAdminApplication {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(ThfhAdminApplication.class, args);
    }
}