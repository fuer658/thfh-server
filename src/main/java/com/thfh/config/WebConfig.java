package com.thfh.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 实现WebMvcConfigurer接口，用于自定义Spring MVC的配置
 * 包括拦截器、资源处理器和跨域配置等
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    /**
     * JWT拦截器，用于验证请求中的JWT令牌
     */
    @Autowired
    private JwtInterceptor jwtInterceptor;

    /**
     * 文件上传目录，从配置文件中读取
     */
    @Value("${file.upload-dir}")
    private String uploadDir;

    /**
     * 添加拦截器配置
     * 将JWT拦截器应用于所有/api/**路径的请求
     * 排除/api/auth/login路径，因为登录请求不需要JWT验证
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/auth/login");
    }

    /**
     * 添加资源处理器配置
     * 将/uploads/**路径映射到实际的文件上传目录
     * 使客户端可以通过/uploads/路径访问上传的文件
     * @param registry 资源处理器注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");
                
        // 添加Swagger和Knife4j的静态资源映射
        registry.addResourceHandler("doc.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/swagger-resources/**")
                .addResourceLocations("classpath:/META-INF/resources/swagger-resources/");
        registry.addResourceHandler("/v2/api-docs/**")
                .addResourceLocations("classpath:/META-INF/resources/v2/api-docs/");
    }

    /**
     * 添加跨域映射配置
     * 允许来自http://localhost:8080的跨域请求
     * 支持GET、POST、PUT、DELETE和OPTIONS方法
     * 允许所有请求头，允许携带凭证，并设置预检请求的有效期为3600秒
     * @param registry 跨域注册表
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
