package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.User;
import com.thfh.service.CourseFileService;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(tags = "课程文件管理", description = "提供课程相关文件的上传、管理等功能，包括封面图片、视频、教学材料等")
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
    @ApiOperation(value = "上传课程封面图片", notes = "上传课程封面图片，返回图片访问URL")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping(value = "/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadCourseCover(
            @ApiParam(value = "图片文件", required = true) @RequestParam("file") MultipartFile file) throws Exception {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new com.thfh.exception.UserNotLoggedInException("未授权，请先登录");
        }
        String fileUrl = courseFileService.uploadCourseFile(file, "cover");
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("message", "封面图片上传成功");
        return Result.success(result);
    }
    
    /**
     * 上传课程视频
     * @param file 视频文件
     * @return 上传结果，包含视频URL
     */
    @ApiOperation(value = "上传课程视频", notes = "上传课程视频文件，返回视频访问URL")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping(value = "/video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadCourseVideo(
            @ApiParam(value = "视频文件", required = true) @RequestParam("file") MultipartFile file) throws Exception {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new com.thfh.exception.UserNotLoggedInException("未授权，请先登录");
        }
        String fileUrl = courseFileService.uploadCourseFile(file, "video");
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("message", "视频上传成功");
        return Result.success(result);
    }
    
    /**
     * 上传课程材料/资源
     * @param file 文件
     * @param description 文件描述（可选）
     * @return 上传结果，包含文件URL
     */
    @ApiOperation(value = "上传课程材料", notes = "上传课程相关的教学资料，可提供文件描述信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping(value = "/material", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, String>> uploadCourseMaterial(
            @ApiParam(value = "文件", required = true) @RequestParam("file") MultipartFile file,
            @ApiParam(value = "文件描述", required = false) @RequestParam(value = "description", required = false) String description) throws Exception {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new com.thfh.exception.UserNotLoggedInException("未授权，请先登录");
        }
        String fileUrl = courseFileService.uploadCourseFile(file, "material", description);
        Map<String, String> result = new HashMap<>();
        result.put("url", fileUrl);
        result.put("message", "课程材料上传成功");
        return Result.success(result);
    }
    
    /**
     * 批量上传课程材料/资源
     * @param files 文件列表
     * @return 上传结果，包含文件URL列表
     */
    @ApiOperation(value = "批量上传课程材料", notes = "批量上传多个课程相关的教学资料")
    @ApiResponses({
        @ApiResponse(code = 200, message = "上传成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping(value = "/materials/batch", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<Map<String, Object>> batchUploadCourseMaterials(
            @ApiParam(value = "文件列表", required = true) @RequestParam("files") MultipartFile[] files) throws Exception {
        User currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new com.thfh.exception.UserNotLoggedInException("未授权，请先登录");
        }
        if (files.length == 0) {
            throw new IllegalArgumentException("未选择任何文件");
        }
        String[] urls = new String[files.length];
        int successCount = 0;
        for (int i = 0; i < files.length; i++) {
            String fileUrl = courseFileService.uploadCourseFile(files[i], "material");
            urls[i] = fileUrl;
            successCount++;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("urls", urls);
        result.put("totalCount", files.length);
        result.put("successCount", successCount);
        result.put("message", successCount + "个文件上传成功，" + (files.length - successCount) + "个失败");
        return Result.success(result);
    }
} 