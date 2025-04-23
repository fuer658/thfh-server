package com.thfh.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置类
 * 用于配置全局的跨域资源共享(CORS)策略
 * 通过CorsFilter实现，比WebConfig中的CORS配置优先级更高
 */
@Configuration
public class CorsConfig {

    /**
     * 创建并配置CORS过滤器
     * 该过滤器将应用于所有请求，处理跨域资源共享
     * 
     * @return 配置好的CorsFilter实例
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源的跨域请求
        config.addAllowedOriginPattern("*");
        
        // 允许跨域的请求头
        config.addAllowedHeader("*");
        
        // 允许跨域的请求方法
        config.addAllowedMethod("*");
        
        // 允许携带cookie
        config.setAllowCredentials(true);
        
        // 暴露响应头
        config.addExposedHeader("Authorization");

        // 对所有路径应用这些CORS配置
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
