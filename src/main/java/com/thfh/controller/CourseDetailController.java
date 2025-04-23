package com.thfh.controller;

import com.thfh.common.ApiResponse;
import com.thfh.model.*;
import com.thfh.service.CourseDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 课程详情控制器
 * 处理课程章节、小节等详细信息的请求
 */
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
    @GetMapping("/{courseId}")
    public ApiResponse<CourseDetail> getCourseDetail(@PathVariable Long courseId) {
        try {
            CourseDetail courseDetail = courseDetailService.getCourseDetailByCourseId(courseId);
            return ApiResponse.success(courseDetail);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取课程详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程详情
     * 
     * @param courseId 课程ID
     * @param courseDetail 课程详情数据
     * @return 更新后的课程详情
     */
    @PutMapping("/{courseId}")
    public ApiResponse<CourseDetail> updateCourseDetail(
            @PathVariable Long courseId,
            @RequestBody CourseDetail courseDetail) {
        try {
            courseDetail.setCourseId(courseId);
            CourseDetail updatedDetail = courseDetailService.saveCourseDetail(courseDetail);
            return ApiResponse.success(updatedDetail);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新课程详情失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取课程章节列表
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    @GetMapping("/{courseId}/chapters")
    public ApiResponse<List<CourseChapter>> getChapters(@PathVariable Long courseId) {
        try {
            List<CourseChapter> chapters = courseDetailService.getChaptersByCourseId(courseId);
            return ApiResponse.success(chapters);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取章节列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加课程章节
     * 
     * @param courseId 课程ID
     * @param chapter 章节数据
     * @return 创建的章节
     */
    @PostMapping("/{courseId}/chapters")
    public ApiResponse<CourseChapter> addChapter(
            @PathVariable Long courseId,
            @RequestBody CourseChapter chapter) {
        try {
            CourseChapter newChapter = courseDetailService.addChapter(courseId, chapter);
            return ApiResponse.success(newChapter);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "添加章节失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程章节
     * 
     * @param chapterId 章节ID
     * @param chapter 章节数据
     * @return 更新后的章节
     */
    @PutMapping("/chapters/{chapterId}")
    public ApiResponse<CourseChapter> updateChapter(
            @PathVariable Long chapterId,
            @RequestBody CourseChapter chapter) {
        try {
            CourseChapter updatedChapter = courseDetailService.updateChapter(chapterId, chapter);
            return ApiResponse.success(updatedChapter);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新章节失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除课程章节
     * 
     * @param chapterId 章节ID
     * @return 操作结果
     */
    @DeleteMapping("/chapters/{chapterId}")
    public ApiResponse<Map<String, Object>> deleteChapter(@PathVariable Long chapterId) {
        try {
            courseDetailService.deleteChapter(chapterId);
            Map<String, Object> result = new HashMap<>();
            result.put("deleted", true);
            return ApiResponse.success(result);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除章节失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取章节下的小节列表
     * 
     * @param chapterId 章节ID
     * @return 小节列表
     */
    @GetMapping("/chapters/{chapterId}/sections")
    public ApiResponse<List<CourseSection>> getSections(@PathVariable Long chapterId) {
        try {
            List<CourseSection> sections = courseDetailService.getSectionsByChapterId(chapterId);
            return ApiResponse.success(sections);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取小节列表失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加小节到章节
     * 
     * @param chapterId 章节ID
     * @param section 小节数据
     * @return 创建的小节
     */
    @PostMapping("/chapters/{chapterId}/sections")
    public ApiResponse<CourseSection> addSection(
            @PathVariable Long chapterId,
            @RequestBody CourseSection section) {
        try {
            CourseSection newSection = courseDetailService.addSection(chapterId, section);
            return ApiResponse.success(newSection);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "添加小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程小节
     * 
     * @param sectionId 小节ID
     * @param section 小节数据
     * @return 更新后的小节
     */
    @PutMapping("/sections/{sectionId}")
    public ApiResponse<CourseSection> updateSection(
            @PathVariable Long sectionId,
            @RequestBody CourseSection section) {
        try {
            CourseSection updatedSection = courseDetailService.updateSection(sectionId, section);
            return ApiResponse.success(updatedSection);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除课程小节
     * 
     * @param sectionId 小节ID
     * @return 操作结果
     */
    @DeleteMapping("/sections/{sectionId}")
    public ApiResponse<Map<String, Object>> deleteSection(@PathVariable Long sectionId) {
        try {
            courseDetailService.deleteSection(sectionId);
            Map<String, Object> result = new HashMap<>();
            result.put("deleted", true);
            return ApiResponse.success(result);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取小节下的子小节列表
     * 
     * @param sectionId 小节ID
     * @return 子小节列表
     */
    @GetMapping("/sections/{sectionId}/subsections")
    public ApiResponse<List<CourseSubSection>> getSubSections(@PathVariable Long sectionId) {
        try {
            List<CourseSubSection> subSections = courseDetailService.getSubSectionsBySectionId(sectionId);
            return ApiResponse.success(subSections);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "获取子小节列表失败: " + e.getMessage());
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
    public ApiResponse<CourseSubSection> addSubSection(
            @PathVariable Long sectionId,
            @RequestBody CourseSubSection subSection) {
        try {
            CourseSubSection newSubSection = courseDetailService.addSubSection(sectionId, subSection);
            return ApiResponse.success(newSubSection);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "添加子小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新课程子小节
     * 
     * @param subSectionId 子小节ID
     * @param subSection 子小节数据
     * @return 更新后的子小节
     */
    @PutMapping("/subsections/{subSectionId}")
    public ApiResponse<CourseSubSection> updateSubSection(
            @PathVariable Long subSectionId,
            @RequestBody CourseSubSection subSection) {
        try {
            CourseSubSection updatedSubSection = courseDetailService.updateSubSection(subSectionId, subSection);
            return ApiResponse.success(updatedSubSection);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "更新子小节失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除课程子小节
     * 
     * @param subSectionId 子小节ID
     * @return 操作结果
     */
    @DeleteMapping("/subsections/{subSectionId}")
    public ApiResponse<Map<String, Object>> deleteSubSection(@PathVariable Long subSectionId) {
        try {
            courseDetailService.deleteSubSection(subSectionId);
            Map<String, Object> result = new HashMap<>();
            result.put("deleted", true);
            return ApiResponse.success(result);
        } catch (EntityNotFoundException e) {
            return ApiResponse.error(404, e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(500, "删除子小节失败: " + e.getMessage());
        }
    }
} 