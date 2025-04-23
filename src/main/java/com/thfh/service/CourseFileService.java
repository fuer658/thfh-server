package com.thfh.service;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

/**
 * 课程文件服务接口
 * 处理课程相关文件的上传等操作
 */
public interface CourseFileService {
    
    /**
     * 上传课程相关文件（图片、视频、材料等）
     * @param file 上传的文件
     * @param fileType 文件类型（cover、video、material）
     * @return 文件URL
     * @throws IOException 如果上传过程中发生IO错误
     */
    String uploadCourseFile(MultipartFile file, String fileType) throws IOException;
    
    /**
     * 上传课程相关文件（图片、视频、材料等）
     * @param file 上传的文件
     * @param fileType 文件类型（cover、video、material）
     * @param description 文件描述（可选）
     * @return 文件URL
     * @throws IOException 如果上传过程中发生IO错误
     */
    String uploadCourseFile(MultipartFile file, String fileType, String description) throws IOException;
} 