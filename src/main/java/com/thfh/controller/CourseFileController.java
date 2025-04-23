package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.User;
import com.thfh.service.CourseFileService;
import com.thfh.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 课程文件管理控制器
 * 提供课程相关文件的上传、管理等功能
 */
@RestController
@RequestMapping("/api/course-files")
public class CourseFileController {
    
    @Autowired
    private CourseFileService courseFileService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 上传课程封面图片
     * @param file 图片文件
     * @return 上传结果，包含图片URL
     */
    @PostMapping(value = "/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadCourseCover(@RequestParam("file") MultipartFile file) {
        try {
            // 验证当前用户是否为教师
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            String fileUrl = courseFileService.uploadCourseFile(file, "cover");
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("message", "封面图片上传成功");
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传课程视频
     * @param file 视频文件
     * @return 上传结果，包含视频URL
     */
    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadCourseVideo(@RequestParam("file") MultipartFile file) {
        try {
            // 验证当前用户是否为教师
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            String fileUrl = courseFileService.uploadCourseFile(file, "video");
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("message", "视频上传成功");
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 上传课程材料/资源
     * @param file 文件
     * @param description 文件描述（可选）
     * @return 上传结果，包含文件URL
     */
    @PostMapping(value = "/material", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadCourseMaterial(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        try {
            // 验证当前用户是否为教师
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            String fileUrl = courseFileService.uploadCourseFile(file, "material", description);
            Map<String, String> result = new HashMap<>();
            result.put("url", fileUrl);
            result.put("message", "课程材料上传成功");
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 批量上传课程材料/资源
     * @param files 文件列表
     * @return 上传结果，包含文件URL列表
     */
    @PostMapping(value = "/materials/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> batchUploadCourseMaterials(
            @RequestParam("files") MultipartFile[] files) {
        try {
            // 验证当前用户是否为教师
            User currentUser = userService.getCurrentUser();
            if (currentUser == null) {
                return Result.error("未授权，请先登录");
            }
            
            if (files.length == 0) {
                return Result.error("未选择任何文件");
            }
            
            // 存储上传结果
            String[] urls = new String[files.length];
            int successCount = 0;
            
            for (int i = 0; i < files.length; i++) {
                try {
                    String fileUrl = courseFileService.uploadCourseFile(files[i], "material");
                    urls[i] = fileUrl;
                    successCount++;
                } catch (Exception e) {
                    urls[i] = "上传失败: " + e.getMessage();
                }
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("urls", urls);
            result.put("totalCount", files.length);
            result.put("successCount", successCount);
            result.put("message", successCount + "个文件上传成功，" + (files.length - successCount) + "个失败");
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
} 