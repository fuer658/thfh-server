package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.dto.CourseDTO;
import com.thfh.dto.CourseInteractionDTO;
import com.thfh.dto.CourseQueryDTO;
import com.thfh.dto.SimpleUserDTO;
import com.thfh.model.User;
import com.thfh.service.CourseManagementService;
import com.thfh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 课程管理控制器
 * 提供课程的增删改查和状态切换等功能
 */
@Api(tags = "课程管理")
@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private CourseManagementService courseManagementService;
    private UserService userService;

    public CourseController(CourseManagementService courseManagementService, UserService userService) {
        this.courseManagementService = courseManagementService;
        this.userService = userService;
    }

    /**
     * 获取课程列表
     * @param queryDTO 查询条件，包含课程名称、类型和分页信息等
     * @return 课程分页列表
     */
    @ApiOperation(value = "获取课程列表", notes = "根据查询条件获取课程分页列表，支持多种筛选条件")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping
    public Result<Page<CourseDTO>> getCourses(
            @ApiParam(value = "查询条件，包含课程名称、类型和分页信息等") CourseQueryDTO queryDTO) {
        return Result.success(courseManagementService.getCourses(queryDTO));
    }

    /**
     * 创建新课程
     * @param courseDTO 课程信息
     * @return 创建的课程信息
     */
    @ApiOperation(value = "创建新课程", notes = "创建一个新的课程，需要提供课程的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "创建成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @PostMapping
    public Result<CourseDTO> createCourse(
            @ApiParam(value = "课程信息", required = true) @RequestBody CourseDTO courseDTO) {
        return Result.success(courseManagementService.createCourse(courseDTO));
    }

    /**
     * 更新课程信息
     * @param id 课程ID
     * @param courseDTO 更新的课程信息
     * @return 更新后的课程信息
     */
    @ApiOperation(value = "更新课程信息", notes = "根据课程ID更新课程信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 400, message = "请求参数错误"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @PutMapping("/{id}")
    public Result<CourseDTO> updateCourse(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id,
            @ApiParam(value = "更新的课程信息", required = true) @RequestBody CourseDTO courseDTO) {
        return Result.success(courseManagementService.updateCourse(id, courseDTO));
    }

    /**
     * 删除课程
     * @param id 课程ID
     * @return 操作结果
     */
    @ApiOperation(value = "删除课程", notes = "根据课程ID删除课程")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @DeleteMapping("/{id}")
    public Result<Void> deleteCourse(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        courseManagementService.deleteCourse(id);
        return Result.success(null);
    }

    /**
     * 切换课程状态（上线/下线）
     * @param id 课程ID
     * @return 操作结果
     */
    @ApiOperation(value = "切换课程状态", notes = "上线或下线课程")
    @ApiResponses({
        @ApiResponse(code = 200, message = "操作成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @PutMapping("/{id}/toggle-status")
    public Result<Void> toggleCourseStatus(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        courseManagementService.toggleCourseStatus(id);
        return Result.success(null);
    }

    /**
     * 获取课程的学生列表
     * @param id 课程ID
     * @return 学生列表，包含基本信息（ID、姓名、头像）
     */
    @ApiOperation(value = "获取课程的学生列表", notes = "获取该课程的所有学生的基本信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @GetMapping("/{id}/students")
    public Result<List<SimpleUserDTO>> getCourseStudents(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        return Result.success(courseManagementService.getCourseStudents(id));
    }

    /**
     * 学生加入课程
     * @param id 课程ID
     * @return 加入后的课程信息
     */
    @ApiOperation(value = "学生加入课程", notes = "当前登录用户加入指定的课程")
    @ApiResponses({
        @ApiResponse(code = 200, message = "加入成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在"),
        @ApiResponse(code = 400, message = "已经加入该课程")
    })
    @PostMapping("/{id}/enroll")
    public Result<CourseDTO> enrollCourse(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        return Result.success(courseManagementService.enrollCourse(id, currentUser.getId()));
    }

    /**
     * 学生退出课程
     * @param id 课程ID
     * @return 操作结果
     */
    @ApiOperation(value = "学生退出课程", notes = "当前登录用户退出指定的课程")
    @ApiResponses({
        @ApiResponse(code = 200, message = "退出成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在或未加入该课程")
    })
    @PostMapping("/{id}/unenroll")
    public Result<Void> unenrollCourse(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        courseManagementService.unenrollCourse(id, currentUser.getId());
        return Result.success(null);
    }

    /**
     * 点赞/取消点赞课程
     * @param id 课程ID
     * @return 操作结果
     */
    @ApiOperation(value = "点赞/取消点赞课程", notes = "对指定课程进行点赞或取消点赞操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "操作成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @PostMapping("/{id}/toggle-like")
    public Result<Void> toggleCourseLike(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        courseManagementService.toggleCourseLike(id, currentUser.getId());
        return Result.success(null);
    }

    /**
     * 收藏/取消收藏课程
     * @param id 课程ID
     * @return 操作结果
     */
    @ApiOperation(value = "收藏/取消收藏课程", notes = "对指定课程进行收藏或取消收藏操作")
    @ApiResponses({
        @ApiResponse(code = 200, message = "操作成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @PostMapping("/{id}/toggle-favorite")
    public Result<Void> toggleCourseFavorite(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        courseManagementService.toggleCourseFavorite(id, currentUser.getId());
        return Result.success(null);
    }

    /**
     * 获取当前用户对课程的交互信息（点赞和收藏状态）
     * @param id 课程ID
     * @return 交互信息，包含点赞和收藏状态
     */
    @ApiOperation(value = "获取课程交互信息", notes = "获取当前用户对指定课程的交互信息，包括点赞和收藏状态")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @GetMapping("/{id}/interaction")
    public Result<CourseInteractionDTO> getCourseInteractionInfo(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        return Result.success(courseManagementService.getCourseInteractionInfo(id, currentUser.getId()));
    }

    /**
     * 获取课程的点赞和收藏用户列表
     * @param id 课程ID
     * @return 包含点赞和收藏用户列表的对象
     */
    @ApiOperation(value = "获取课程的点赞和收藏用户列表", notes = "获取对指定课程进行点赞和收藏的用户列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @GetMapping("/{id}/interaction-users")
    public Result<Map<String, List<SimpleUserDTO>>> getCourseInteractionUsers(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        return Result.success(courseManagementService.getCourseInteractionUsers(id));
    }

    /**
     * 获取当前用户收藏的课程列表
     * @param page 页码
     * @param size 每页数量
     * @return 收藏的课程列表
     */
    @ApiOperation(value = "获取收藏的课程列表", notes = "获取当前登录用户收藏的课程列表，支持分页")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/favorites")
    public Result<Page<CourseDTO>> getFavoriteCourses(
            @ApiParam(value = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer size) {
        User currentUser = userService.getCurrentUser();
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createTime"));
        return Result.success(courseManagementService.getUserFavoriteCourses(currentUser.getId(), pageRequest));
    }
    
    /**
     * 获取课程详情
     * @param id 课程ID
     * @return 课程详细信息
     */
    @ApiOperation(value = "获取课程详情", notes = "根据课程ID获取课程的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @GetMapping("/{id}")
    public Result<CourseDTO> getCourseDetail(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        return Result.success(courseManagementService.getCourseDetail(id));
    }
    
    /**
     * 发布课程
     * @param id 课程ID
     * @return 发布后的课程信息
     */
    @ApiOperation(value = "发布课程", notes = "将指定课程的状态更改为已发布")
    @ApiResponses({
        @ApiResponse(code = 200, message = "发布成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @PostMapping("/{id}/publish")
    public Result<CourseDTO> publishCourse(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        return Result.success(courseManagementService.publishCourse(id));
    }

    /**
     * 获取热门课程分页列表
     * @param page 页码（从1开始，默认1）
     * @param size 每页数量（默认10）
     * @param sortBy 排序字段（viewCount/likeCount/favoriteCount/studentCount），默认viewCount
     * @return 分页后的热门课程列表
     */
    @ApiOperation(value = "获取热门课程", notes = "分页获取热门课程，支持按浏览量、点赞数、收藏数、学习人数排序")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 401, message = "未授权，请先登录")
    })
    @GetMapping("/hot")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('USER') or hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    public Result<Page<CourseDTO>> getHotCourses(
            @ApiParam(value = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @ApiParam(value = "每页数量", example = "10") @RequestParam(defaultValue = "10") Integer size,
            @ApiParam(value = "排序字段", example = "viewCount") @RequestParam(defaultValue = "viewCount") String sortBy
    ) {
        return Result.success(courseManagementService.getHotCourses(page, size, sortBy));
    }

    /**
     * 积分购买课程
     * @param id 课程ID
     * @return 积分扣除记录
     */
    @ApiOperation(value = "积分购买课程", notes = "使用积分购买指定课程，扣除积分并加入课程")
    @ApiResponses({
        @ApiResponse(code = 200, message = "购买成功"),
        @ApiResponse(code = 400, message = "请求参数错误或积分不足"),
        @ApiResponse(code = 401, message = "未授权，请先登录"),
        @ApiResponse(code = 404, message = "课程不存在")
    })
    @PostMapping("/{id}/purchase-by-points")
    public Result<com.thfh.dto.PointsRecordDTO> purchaseCourseByPoints(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long id) {
        User currentUser = userService.getCurrentUser();
        return Result.success(courseManagementService.purchaseCourseByPoints(id, currentUser.getId()));
    }
}