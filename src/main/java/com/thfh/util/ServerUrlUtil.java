package com.thfh.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 服务器URL工具类
 * 提供获取服务器基础URL的方法
 * 集中管理服务器地址配置
 */
@Component
public class ServerUrlUtil {

    @Value("${server.port}")
    private String serverPort;

    @Value("${server.host:localhost}")
    private String serverHost;

    /**
     * 获取服务器基础URL
     * @return 服务器URL地址
     */
    public String getServerBaseUrl() {
        return "http://" + serverHost + ":" + serverPort;
    }
    
    /**
     * 获取上传文件的访问URL
     * @param relativePath 文件相对路径
     * @return 完整的文件访问URL
     */
    public String getFileUrl(String relativePath) {
        // 使用Nginx配置的文件服务器URL
        return getServerBaseUrl() + "/uploads/" + relativePath;
    }
} 