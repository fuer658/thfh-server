package com.thfh.controller;

import com.thfh.common.Result;
import com.thfh.model.*;
import com.thfh.service.CourseDetailService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.List;

/**
 * 课程详情控制器
 * 处理课程章节、小节等详细信息的请求
 */
@Api(tags = "课程详情管理", description = "课程详情相关的API接口，包括课程章节、小节等详细信息的管理")
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
    @ApiOperation(value = "获取课程详情", notes = "根据课程ID获取课程的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "课程不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/{courseId}")
    public Result<CourseDetail> getCourseDetail(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId) {
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
    @ApiOperation(value = "更新课程详情", notes = "根据课程ID更新课程的详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 404, message = "课程不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping("/{courseId}")
    public Result<CourseDetail> updateCourseDetail(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId,
            @ApiParam(value = "课程详情数据", required = true) @RequestBody CourseDetail courseDetail) {
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
    @ApiOperation(value = "获取课程章节列表", notes = "根据课程ID获取所有章节列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "课程不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/{courseId}/chapters")
    public Result<List<CourseChapter>> getChapters(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId) {
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
    @ApiOperation(value = "添加课程章节", notes = "向指定课程添加新的章节")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 404, message = "课程不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/{courseId}/chapters")
    public Result<CourseChapter> addChapter(
            @ApiParam(value = "课程ID", required = true) @PathVariable Long courseId,
            @ApiParam(value = "章节数据", required = true) @RequestBody CourseChapter chapter) {
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
    @ApiOperation(value = "更新课程章节", notes = "根据章节ID更新章节信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 404, message = "章节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping("/chapters/{chapterId}")
    public Result<CourseChapter> updateChapter(
            @ApiParam(value = "章节ID", required = true) @PathVariable Long chapterId,
            @ApiParam(value = "章节数据", required = true) @RequestBody CourseChapter chapter) {
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
    @ApiOperation(value = "删除课程章节", notes = "根据章节ID删除章节")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 404, message = "章节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @DeleteMapping("/chapters/{chapterId}")
    public Result<Void> deleteChapter(
            @ApiParam(value = "章节ID", required = true) @PathVariable Long chapterId) {
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
    @ApiOperation(value = "获取章节小节列表", notes = "根据章节ID获取所有小节列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "章节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/chapters/{chapterId}/sections")
    public Result<List<CourseSection>> getSections(
            @ApiParam(value = "章节ID", required = true) @PathVariable Long chapterId) {
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
    @ApiOperation(value = "获取小节详情", notes = "根据小节ID获取小节详细信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "小节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/sections/{sectionId}")
    public Result<CourseSection> getSectionDetail(
            @ApiParam(value = "小节ID", required = true) @PathVariable Long sectionId) {
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
    @ApiOperation(value = "添加小节", notes = "向指定章节添加新的小节")
    @ApiResponses({
        @ApiResponse(code = 200, message = "添加成功"),
        @ApiResponse(code = 404, message = "章节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PostMapping("/chapters/{chapterId}/sections")
    public Result<CourseSection> addSection(
            @ApiParam(value = "章节ID", required = true) @PathVariable Long chapterId,
            @ApiParam(value = "小节数据", required = true) @RequestBody CourseSection section) {
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
    @ApiOperation(value = "更新小节", notes = "根据小节ID更新小节信息")
    @ApiResponses({
        @ApiResponse(code = 200, message = "更新成功"),
        @ApiResponse(code = 404, message = "小节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @PutMapping("/sections/{sectionId}")
    public Result<CourseSection> updateSection(
            @ApiParam(value = "小节ID", required = true) @PathVariable Long sectionId,
            @ApiParam(value = "小节数据", required = true) @RequestBody CourseSection section) {
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
    @ApiOperation(value = "删除小节", notes = "根据小节ID删除小节")
    @ApiResponses({
        @ApiResponse(code = 200, message = "删除成功"),
        @ApiResponse(code = 404, message = "小节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @DeleteMapping("/sections/{sectionId}")
    public Result<Void> deleteSection(
            @ApiParam(value = "小节ID", required = true) @PathVariable Long sectionId) {
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
    @ApiOperation(value = "获取子小节列表", notes = "根据小节ID获取所有子小节列表")
    @ApiResponses({
        @ApiResponse(code = 200, message = "获取成功"),
        @ApiResponse(code = 404, message = "小节不存在"),
        @ApiResponse(code = 500, message = "服务器内部错误")
    })
    @GetMapping("/sections/{sectionId}/subsections")
    public Result<List<CourseSubSection>> getSubSections(
            @ApiParam(value = "小节ID", required = true) @PathVariable Long sectionId) {
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