package com.thfh.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI 3.0 接口文档配置
 * 适用于Spring Boot 3.x
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .components(new Components()
                        .addSecuritySchemes("Authorization", securityScheme()))
                .addSecurityItem(new SecurityRequirement().addList("Authorization"));
    }

    /**
     * API文档基本信息
     */
    private Info apiInfo() {
        return new Info()
                .title("THFH 后台管理系统 API文档")
                .description("使用Knife4j构建的API文档")
                .version("1.0.0")
                .contact(new Contact()
                        .name("THFH")
                        .email("admin@thfh.com")
                        .url("http://www.example.com/"));
    }

    /**
     * 安全模式，这里配置JWT Token
     */
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");
    }
} 