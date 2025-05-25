package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.*;
import com.thfh.service.CourseDetailService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

/**
 * 课程详情控制器
 * 处理课程章节、小节等详细信息的请求
 */
@Tag(name = "课程详情管理", description = "课程详情相关的API接口，包括课程章节、小节等详细信息的管理")
@RestController
@RequestMapping("/api/course-details")
public class CourseDetailController {

    @Autowired
    private CourseDetailService courseDetailService;
    
    /**
     * 获取课程详情
     * 
     * @param courseId 课程ID
     * @return 课程详情
     */
    @Operation(summary = "获取课程详情", description = "根据课程ID获取课程的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "课程不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{courseId}")
    public Result<CourseDetail> getCourseDetail(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {
        try {
            CourseDetail courseDetail = courseDetailService.getCourseDetailByCourseId(courseId);
            return Result.success(courseDetail);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取课程详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程详情
     * 
     * @param courseId 课程ID
     * @param courseDetail 课程详情数据
     * @return 更新后的课程详情
     */
    @Operation(summary = "更新课程详情", description = "根据课程ID更新课程的详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "课程不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/{courseId}")
    public Result<CourseDetail> updateCourseDetail(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            @Parameter(description = "课程详情数据", required = true) @RequestBody CourseDetail courseDetail) {
        try {
            courseDetail.setCourseId(courseId);
            CourseDetail updatedDetail = courseDetailService.saveCourseDetail(courseDetail);
            return Result.success(updatedDetail);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("更新课程详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程章节列表
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    @Operation(summary = "获取课程章节列表", description = "根据课程ID获取所有章节列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "课程不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/{courseId}/chapters")
    public Result<List<CourseChapter>> getChapters(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId) {
        try {
            List<CourseChapter> chapters = courseDetailService.getChaptersByCourseId(courseId);
            return Result.success(chapters);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取章节列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加课程章节
     * 
     * @param courseId 课程ID
     * @param chapter 章节数据
     * @return 创建的章节
     */
    @Operation(summary = "添加课程章节", description = "向指定课程添加新的章节")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "404", description = "课程不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/{courseId}/chapters")
    public Result<CourseChapter> addChapter(
            @Parameter(description = "课程ID", required = true) @PathVariable Long courseId,
            @Parameter(description = "章节数据", required = true) @RequestBody CourseChapter chapter) {
        try {
            CourseChapter newChapter = courseDetailService.addChapter(courseId, chapter);
            return Result.success(newChapter);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("添加章节失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程章节
     * 
     * @param chapterId 章节ID
     * @param chapter 章节数据
     * @return 更新后的章节
     */
    @Operation(summary = "更新课程章节", description = "根据章节ID更新章节信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "章节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/chapters/{chapterId}")
    public Result<CourseChapter> updateChapter(
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId,
            @Parameter(description = "章节数据", required = true) @RequestBody CourseChapter chapter) {
        try {
            CourseChapter updatedChapter = courseDetailService.updateChapter(chapterId, chapter);
            return Result.success(updatedChapter);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("更新章节失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除课程章节
     * 
     * @param chapterId 章节ID
     * @return 操作结果
     */
    @Operation(summary = "删除课程章节", description = "根据章节ID删除章节")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "章节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/chapters/{chapterId}")
    public Result<Void> deleteChapter(
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId) {
        try {
            courseDetailService.deleteChapter(chapterId);
            return Result.success(null);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("删除章节失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取章节下的小节列表
     * 
     * @param chapterId 章节ID
     * @return 小节列表
     */
    @Operation(summary = "获取章节小节列表", description = "根据章节ID获取所有小节列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "章节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/chapters/{chapterId}/sections")
    public Result<List<CourseSection>> getSections(
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId) {
        try {
            List<CourseSection> sections = courseDetailService.getSectionsByChapterId(chapterId);
            return Result.success(sections);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取小节列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取小节详情
     * 
     * @param sectionId 小节ID
     * @return 小节详情
     */
    @Operation(summary = "获取小节详情", description = "根据小节ID获取小节详细信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "小节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/sections/{sectionId}")
    public Result<CourseSection> getSectionDetail(
            @Parameter(description = "小节ID", required = true) @PathVariable Long sectionId) {
        try {
            CourseSection section = courseDetailService.getSectionById(sectionId);
            if (section.getChapter() != null) {
                section.getChapter().setCourseDetail(null);
            }
            return Result.success(section);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取小节详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加小节到章节
     * 
     * @param chapterId 章节ID
     * @param section 小节数据
     * @return 创建的小节
     */
    @Operation(summary = "添加小节", description = "向指定章节添加新的小节")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "添加成功"),
        @ApiResponse(responseCode = "404", description = "章节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/chapters/{chapterId}/sections")
    public Result<CourseSection> addSection(
            @Parameter(description = "章节ID", required = true) @PathVariable Long chapterId,
            @Parameter(description = "小节数据", required = true) @RequestBody CourseSection section) {
        try {
            CourseSection newSection = courseDetailService.addSection(chapterId, section);
            return Result.success(newSection);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("添加小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程小节
     * 
     * @param sectionId 小节ID
     * @param section 小节数据
     * @return 更新后的小节
     */
    @Operation(summary = "更新小节", description = "根据小节ID更新小节信息")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "更新成功"),
        @ApiResponse(responseCode = "404", description = "小节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/sections/{sectionId}")
    public Result<CourseSection> updateSection(
            @Parameter(description = "小节ID", required = true) @PathVariable Long sectionId,
            @Parameter(description = "小节数据", required = true) @RequestBody CourseSection section) {
        try {
            CourseSection updatedSection = courseDetailService.updateSection(sectionId, section);
            return Result.success(updatedSection);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("更新小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除课程小节
     * 
     * @param sectionId 小节ID
     * @return 操作结果
     */
    @Operation(summary = "删除小节", description = "根据小节ID删除小节")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "删除成功"),
        @ApiResponse(responseCode = "404", description = "小节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/sections/{sectionId}")
    public Result<Void> deleteSection(
            @Parameter(description = "小节ID", required = true) @PathVariable Long sectionId) {
        try {
            courseDetailService.deleteSection(sectionId);
            return Result.success(null);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("删除小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取小节下的子小节列表
     * 
     * @param sectionId 小节ID
     * @return 子小节列表
     */
    @Operation(summary = "获取子小节列表", description = "根据小节ID获取所有子小节列表")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "获取成功"),
        @ApiResponse(responseCode = "404", description = "小节不存在"),
        @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/sections/{sectionId}/subsections")
    public Result<List<CourseSubSection>> getSubSections(
            @Parameter(description = "小节ID", required = true) @PathVariable Long sectionId) {
        try {
            List<CourseSubSection> subSections = courseDetailService.getSubSectionsBySectionId(sectionId);
            return Result.success(subSections);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("获取子小节列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加子小节到小节
     * 
     * @param sectionId 小节ID
     * @param subSection 子小节数据
     * @return 创建的子小节
     */
    @PostMapping("/sections/{sectionId}/subsections")
    public Result<CourseSubSection> addSubSection(
            @PathVariable Long sectionId,
            @RequestBody CourseSubSection subSection) {
        try {
            CourseSubSection newSubSection = courseDetailService.addSubSection(sectionId, subSection);
            return Result.success(newSubSection);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("添加子小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新子小节
     * 
     * @param subSectionId 子小节ID
     * @param subSection 子小节数据
     * @return 更新后的子小节
     */
    @PutMapping("/subsections/{subSectionId}")
    public Result<CourseSubSection> updateSubSection(
            @PathVariable Long subSectionId,
            @RequestBody CourseSubSection subSection) {
        try {
            CourseSubSection updatedSubSection = courseDetailService.updateSubSection(subSectionId, subSection);
            return Result.success(updatedSubSection);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("更新子小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除子小节
     * 
     * @param subSectionId 子小节ID
     * @return 操作结果
     */
    @DeleteMapping("/subsections/{subSectionId}")
    public Result<Void> deleteSubSection(@PathVariable Long subSectionId) {
        try {
            courseDetailService.deleteSubSection(subSectionId);
            return Result.success(null);
        } catch (EntityNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("删除子小节失败: " + e.getMessage());
        }
    }
} 