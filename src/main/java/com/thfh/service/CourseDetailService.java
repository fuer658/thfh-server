package com.thfh.service;

import com.thfh.model.CourseDetail;
import com.thfh.model.CourseChapter;
import com.thfh.model.CourseSection;
import com.thfh.model.CourseSubSection;

import java.util.List;
import java.util.Optional;

/**
 * 课程详情服务接口
 */
public interface CourseDetailService {
    
    /**
     * 根据课程ID获取课程详情
     * 如果课程详情不存在，则自动创建一个空的详情
     * 
     * @param courseId 课程ID
     * @return 课程详情对象
     */
    CourseDetail getCourseDetailByCourseId(Long courseId);
    
    /**
     * 创建或更新课程详情
     * 
     * @param courseDetail 课程详情对象
     * @return 保存后的课程详情对象
     */
    CourseDetail saveCourseDetail(CourseDetail courseDetail);
    
    /**
     * 添加章节到课程详情
     * 
     * @param courseId 课程ID
     * @param chapter 章节对象
     * @return 保存后的章节对象
     */
    CourseChapter addChapter(Long courseId, CourseChapter chapter);
    
    /**
     * 更新课程章节
     * 
     * @param chapterId 章节ID
     * @param chapter 更新的章节数据
     * @return 更新后的章节对象
     */
    CourseChapter updateChapter(Long chapterId, CourseChapter chapter);
    
    /**
     * 删除课程章节
     * 
     * @param chapterId 章节ID
     */
    void deleteChapter(Long chapterId);
    
    /**
     * 添加小节到章节
     * 
     * @param chapterId 章节ID
     * @param section 小节对象
     * @return 保存后的小节对象
     */
    CourseSection addSection(Long chapterId, CourseSection section);
    
    /**
     * 更新课程小节
     * 
     * @param sectionId 小节ID
     * @param section 更新的小节数据
     * @return 更新后的小节对象
     */
    CourseSection updateSection(Long sectionId, CourseSection section);
    
    /**
     * 删除课程小节
     * 
     * @param sectionId 小节ID
     */
    void deleteSection(Long sectionId);
    
    /**
     * 添加子小节到小节
     * 
     * @param sectionId 小节ID
     * @param subSection 子小节对象
     * @return 保存后的子小节对象
     */
    CourseSubSection addSubSection(Long sectionId, CourseSubSection subSection);
    
    /**
     * 更新子小节
     * 
     * @param subSectionId 子小节ID
     * @param subSection 更新的子小节数据
     * @return 更新后的子小节对象
     */
    CourseSubSection updateSubSection(Long subSectionId, CourseSubSection subSection);
    
    /**
     * 删除子小节
     * 
     * @param subSectionId 子小节ID
     */
    void deleteSubSection(Long subSectionId);
    
    /**
     * 获取课程的所有章节
     * 
     * @param courseId 课程ID
     * @return 章节列表
     */
    List<CourseChapter> getChaptersByCourseId(Long courseId);
    
    /**
     * 获取章节的所有小节
     * 
     * @param chapterId 章节ID
     * @return 小节列表
     */
    List<CourseSection> getSectionsByChapterId(Long chapterId);
    
    /**
     * 获取小节的所有子小节
     * 
     * @param sectionId 小节ID
     * @return 子小节列表
     */
    List<CourseSubSection> getSubSectionsBySectionId(Long sectionId);
} 